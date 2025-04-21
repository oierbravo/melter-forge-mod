package com.oierbravo.melter.content.melter;

import com.oierbravo.melter.content.melter.heatsource.HeatSources;
import com.oierbravo.melter.network.packets.data.FluidSyncPayload;
import com.oierbravo.melter.network.packets.data.ItemSyncPayload;
import com.oierbravo.melter.registrate.ModMessages;
import com.oierbravo.melter.registrate.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

public class MelterBlockEntity extends BlockEntity  {

    private final int FLUID_CAPACITY = MelterConfig.MELTER_CAPACITY.get();

    private CompoundTag updateTag;
    public final ItemStackHandler inputItems = createInputItemHandler();
    private final Lazy<IItemHandler> inputItemHandler = Lazy.of(() -> inputItems);

    private final FluidTank fluidTankHandler = createFluidTank();
    private Lazy<IFluidHandler> outputFluidHandler = Lazy.of(() -> fluidTankHandler);
    public int progress = 0;
    public int maxProgress = 200;
    private BlockState lastBlockState;

    public MelterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        updateTag = getPersistentData();
        lastBlockState = this.getBlockState();
    }

    private FluidTank createFluidTank() {

        return new FluidTank(FLUID_CAPACITY) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                if(!level.isClientSide()) {
                    ModMessages.sendToAllClients(new FluidSyncPayload(getFluid(), worldPosition));
                }
            }

        };
    }
    @NotNull
    @Nonnull
    private ItemStackHandler createInputItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if(!level.isClientSide()) {
                    ModMessages.sendToAllClients(new ItemSyncPayload(this.getStackInSlot(0),worldPosition));
                }
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return canProcess(stack) && super.isItemValid(slot, stack);
            }
        };
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inputItemHandler.invalidate();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void invalidateCapabilities() {
        super.invalidateCapabilities();
        inputItemHandler.invalidate();
        outputFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("input", inputItems.serializeNBT(registries));
        fluidTankHandler.writeToNBT(registries, tag);
        tag.putInt("output", fluidTankHandler.getCapacity());
        tag.putInt("melter.progress", progress);
        tag.putInt("melter.maxProgress", maxProgress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inputItems.deserializeNBT(registries, tag.getCompound("input"));
        fluidTankHandler.readFromNBT(registries, tag);
        fluidTankHandler.setCapacity(tag.getInt("output"));
        progress = tag.getInt("melter.progress");
        maxProgress = tag.getInt("melter.maxProgress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(inputItems.getSlots());
        for (int i = 0; i < inputItems.getSlots(); i++) {
            inventory.setItem(i, inputItems.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }


    public void resetProgress() {
        this.progress = 0;
        this.maxProgress = 200;
    }


    protected int getProcessingTime(MelterBlockEntity pBlockEntity) {
        Level level = pBlockEntity.getLevel();
        SingleRecipeInput input = new SingleRecipeInput(pBlockEntity.inputItems.getStackInSlot(0));
        return level.getRecipeManager().getRecipeFor(ModRecipes.MELTING_TYPE.get(), input, level).get().value().getProcessingTime();
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, MelterBlockEntity pBlockEntity) {
        if(pLevel.isClientSide()) {
            return;
        }

        pBlockEntity.updateBlockStateFromNeighborUpdate(pState);

        if (canCraftFluid(pBlockEntity)) {
            // get heat source
            int heatSource = pBlockEntity.getBlockState().getValue(MelterBlock.HEAT_SOURCE);

            int processingTime = pBlockEntity.getProcessingTime(pBlockEntity);

            if (pBlockEntity.isCreative()) {
                pBlockEntity.maxProgress = processingTime;
                MelterBlockEntity.craftFluid(pBlockEntity);
                return;
            }

            SingleRecipeInput input = new SingleRecipeInput(pBlockEntity.inputItems.getStackInSlot(0));
            Optional<RecipeHolder<MeltingRecipe>> match = ModRecipes.find(input, pLevel);
            int heatLevel = match.get().value().getHeatLevel();
            int diff = heatSource - heatLevel;
            int bonus = Math.max(diff, 0) * 2; // 2 ticks bonus per level above needed

            pBlockEntity.progress += 1 + bonus;

            BlockEntity.setChanged(pLevel, pPos, pState);
            pBlockEntity.maxProgress = processingTime;

            if (pBlockEntity.progress >= pBlockEntity.maxProgress) {
                MelterBlockEntity.craftFluid(pBlockEntity);
            }
        } else {
            pBlockEntity.resetProgress();
            BlockEntity.setChanged(pLevel, pPos, pState);
        }
    }

    public int getHeatLevel() {
        return this.getBlockState().getValue(MelterBlock.HEAT_SOURCE);
    }

    public boolean isCreative() {
        return this.getBlockState().getValue(MelterBlock.CREATIVE);
    }

    public void updateBlockStateFromNeighborUpdate(BlockState pLastState){
        BlockPos pos = this.getBlockPos();
        BlockState below = this.getLevel().getBlockState(pos.below());
        BlockInWorld belowInWorld = new BlockInWorld(getLevel(),pos.below(), false);

        BlockState newState = this.getBlockState()
            .setValue(MelterBlock.HEAT_SOURCE, HeatSources.getHeatSource(this.getLevel(), belowInWorld))
            .setValue(MelterBlock.CREATIVE, HeatSources.isCreative(getLevel(), pos.below()));
        if(!pLastState.equals(newState)){
            this.getLevel().setBlock(pos,newState,Block.UPDATE_ALL);
        }
    }
    private static void craftFluid(MelterBlockEntity pBlockEntity) {

        Level level = pBlockEntity.getLevel();
        SingleRecipeInput input = new SingleRecipeInput(pBlockEntity.inputItems.getStackInSlot(0));

        Optional<RecipeHolder<MeltingRecipe>> recipe = ModRecipes.find(input, level);

        if(recipe.isPresent()){
            int ingredientAmount = recipe.get().value().getIngredient().getItems()[0].getCount();
            pBlockEntity.inputItems.extractItem(0, ingredientAmount, false);

            FluidStack output = recipe.get().value().getOutput();
            pBlockEntity.fluidTankHandler.fill( new FluidStack(output.getFluid(),output.getAmount()),IFluidHandler.FluidAction.EXECUTE);
        }

        pBlockEntity.resetProgress();
        pBlockEntity.setChanged();
    }


    static boolean canCraftFluid(MelterBlockEntity pBlockEntity) {
        Level level = pBlockEntity.getLevel();
        if(level == null)
            return false;
        SingleRecipeInput input = new SingleRecipeInput(pBlockEntity.inputItems.getStackInSlot(0));
        Optional<RecipeHolder<MeltingRecipe>> match = ModRecipes.find(input,level);
        if(match.isEmpty()) {
            return false;
        }

        int heatLevel = match.get().value().getHeatLevel();

        return MelterBlockEntity.hasEnoughInputItems(input,match.get().value().getIngredient().getItems()[0].getCount())
                && MelterBlockEntity.canInsertFluidAmountIntoOutput(pBlockEntity.fluidTankHandler, match.get().value().getOutput(),match.get().value().getOutputFluidAmount())
                && MelterBlockEntity.hasEnoughOutputSpace(pBlockEntity.fluidTankHandler,match.get().value().getOutputFluidAmount())
                && MelterBlockEntity.isEmptyOrHasSameFluid(pBlockEntity.fluidTankHandler,  match.get().value().getOutput())
                && MelterBlockEntity.hasHeatSourceBelow(pBlockEntity)
                && MelterBlockEntity.hasMinimumHeatSource(heatLevel, pBlockEntity);
    }

    private boolean canProcess(ItemStack stack) {
        SingleRecipeInput input = new SingleRecipeInput(stack);
        return ModRecipes.find(input,level).isPresent();
    }
    protected static boolean hasEnoughInputItems(SingleRecipeInput input, int count){
        return input.getItem(0).getCount() >= count;
    }


    protected static boolean canInsertFluidAmountIntoOutput(FluidTank tank, FluidStack fluidStack, int amount) {
        return tank.isEmpty() || tank.isFluidValid(amount,fluidStack);
    }
    protected static boolean hasEnoughOutputSpace(FluidTank tank,int amount){
        return amount <= tank.getSpace();
    }

    protected static boolean isEmptyOrHasSameFluid(FluidTank tank, FluidStack fluidStack) {
        return tank.isEmpty() || tank.getFluid().is(fluidStack.getFluidType());
    }

    protected static boolean hasHeatSourceBelow(MelterBlockEntity pBlockEntity){
        BlockPos pos = pBlockEntity.getBlockPos();
        //BlockState below = Objects.requireNonNull(pBlockEntity.getLevel()).getBlockState(pos.below());
        BlockInWorld belowInWorld = new BlockInWorld(pBlockEntity.getLevel(),pos.below(), false);

        return HeatSources.isHeatSource(pBlockEntity.getLevel(), belowInWorld);
    }

    protected static boolean hasMinimumHeatSource(int minimum, MelterBlockEntity melter) {
        if(melter.isCreative())
            return true;
        BlockPos pos = melter.getBlockPos();
        BlockState below = melter.getLevel().getBlockState(pos.below());
        BlockInWorld belowInWorld = new BlockInWorld(melter.getLevel(),pos.below(), false);
        int sourceHeatLevel = HeatSources.getHeatSource(melter.getLevel(), belowInWorld);
        return minimum <= sourceHeatLevel;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);

    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public IFluidHandler getFluidHandler() {
        return fluidTankHandler;
    }
    public IItemHandler getItemHandler() {
        return inputItems;
    }
    public void setFluid(FluidStack fluidStack) {
        fluidTankHandler.setFluid(fluidStack);
    }

    public int getProgressPercent() {
        return this.progress * 100 / this.maxProgress;
    }


    public void setItemStack(ItemStack itemStack) {
        inputItems.setStackInSlot(0,itemStack);
    }

    public Lazy<IItemHandler> getItemHandlerCapability() {
        return inputItemHandler;
    }
}
