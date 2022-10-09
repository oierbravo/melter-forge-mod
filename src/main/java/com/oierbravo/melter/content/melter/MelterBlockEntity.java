package com.oierbravo.melter.content.melter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

public class MelterBlockEntity extends BlockEntity  {


    private final int FLUID_CAPACITY = MelterConfig.MELTER_CAPACITY.get();
    private static final int FLIUD_PER_TICK = MelterConfig.MELTER_FLUID_PER_TICK.get();
    private CompoundTag updateTag;
    public final ItemStackHandler inputItems = createInputItemHandler();
    private final LazyOptional<IItemHandler> inputItemHandler = LazyOptional.of(() -> inputItems);

    private final FluidTank fluidTankHandler = createFluidTank();
    //private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> fluidTankHandler);
    private LazyOptional<IFluidHandler> outputFluidHandler = LazyOptional.of(() -> fluidTankHandler);
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
                //clientSync();
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
               // clientSync();
            }
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return canProcess(stack) && super.isItemValid(slot, stack);
            }
        };
    }
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inputItemHandler.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return outputFluidHandler.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
            return outputFluidHandler.cast();
        }
        return super.getCapability(cap, side);
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
    public void invalidateCaps() {
        super.invalidateCaps();
        inputItemHandler.invalidate();
        outputFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {

        tag.put("input", inputItems.serializeNBT());
        fluidTankHandler.writeToNBT(tag);
        tag.putInt("output", fluidTankHandler.getCapacity());
        tag.putInt("melter.progress", progress);
        tag.putInt("melter.maxProgress", maxProgress);
        super.saveAdditional(tag);
        updateTag = tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("input")) {
            inputItems.deserializeNBT(tag.getCompound("input"));
        }
        if (tag.contains("output")) {
            fluidTankHandler.readFromNBT(tag);
            fluidTankHandler.setCapacity(tag.getInt("output"));
        }

        if (tag.contains("melter.progress")) {
            progress = tag.getInt("melter.progress");
        }
        if (tag.contains("melter.maxProgress")) {
            maxProgress = tag.getInt("melter.maxProgress");
        }


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
        SimpleContainer inputInventory = new SimpleContainer(pBlockEntity.inputItems.getSlots());
        inputInventory.setItem(0, pBlockEntity.inputItems.getStackInSlot(0));

        return level.getRecipeManager().getRecipeFor(MeltingRecipe.Type.INSTANCE, inputInventory, level).map(MeltingRecipe::getProcessingTime).orElse(200);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, MelterBlockEntity pBlockEntity) {

        if(pLevel.isClientSide()) {
            return;
        }
        pBlockEntity.updateBlockStateFromNeighborUpdate(pState);


        if (canCraftFluid(pBlockEntity)) {
            pBlockEntity.progress += 1 * pBlockEntity.getHeatSourceMultiplier();
            BlockEntity.setChanged(pLevel, pPos, pState);
           // pBlockEntity.clientSync();
            pBlockEntity.maxProgress = pBlockEntity.getProcessingTime(pBlockEntity);
            if (pBlockEntity.progress > pBlockEntity.maxProgress) {
                MelterBlockEntity.craftFluid(pBlockEntity);
            }
        } else {
            pBlockEntity.resetProgress();
            BlockEntity.setChanged(pLevel, pPos, pState);
            //pBlockEntity.clientSync();
        }


    }

    public int getHeatSourceMultiplier() {
        return this.getBlockState().getValue(MelterBlock.HEAT_SOURCE).getMultiplier();
    }
    public String getHeatSourceDisplayName() {
        return this.getBlockState().getValue(MelterBlock.HEAT_SOURCE).getDisplayName();
    }



    public void updateBlockStateFromNeighborUpdate(BlockState pLastState){
        BlockPos pos = this.getBlockPos();
        BlockState below = this.getLevel().getBlockState(pos.below());

        BlockState newState = this.getBlockState().setValue(MelterBlock.HEAT_SOURCE,HeatSources.get(below));
        if(!pLastState.equals(newState)){
            this.getLevel().setBlock(pos,newState,Block.UPDATE_ALL);
            //clientSync();
        }
    }
    private static void craftFluid(MelterBlockEntity pBlockEntity) {

        Level level = pBlockEntity.getLevel();
        SimpleContainer inputInventory = new SimpleContainer(pBlockEntity.inputItems.getSlots());
        inputInventory.setItem(0, pBlockEntity.inputItems.getStackInSlot(0));

        Optional<MeltingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(MeltingRecipe.Type.INSTANCE, inputInventory, level);

        if(recipe.isPresent()){
            int ingredientAmount = recipe.get().getIngredients().get(0).getItems()[0].getCount();
            pBlockEntity.inputItems.extractItem(0, ingredientAmount, false);

            FluidStack output = recipe.get().getOutputFluidStack();
            //pBlockEntity.fluidTankHandler.fill( new FluidStack(output, output.getAmount()), IFluidHandler.FluidAction.EXECUTE);
            pBlockEntity.fluidTankHandler.fill( new FluidStack(output.getFluid(),output.getAmount()),IFluidHandler.FluidAction.EXECUTE);
        }

        pBlockEntity.resetProgress();
        pBlockEntity.setChanged();
        //pBlockEntity.clientSync();
    }


    static boolean canCraftFluid(MelterBlockEntity pBlockEntity) {
        Level level = pBlockEntity.getLevel();
        SimpleContainer inputInventory = new SimpleContainer(pBlockEntity.inputItems.getSlots());
        inputInventory.setItem(0, pBlockEntity.inputItems.getStackInSlot(0));


        Optional<MeltingRecipe> match = level.getRecipeManager()
                .getRecipeFor(MeltingRecipe.Type.INSTANCE, inputInventory, level);
        return match.isPresent()
                && MelterBlockEntity.hasEnoughInputItems(inputInventory,match.get().getIngredients().get(0).getItems()[0].getCount())
                && MelterBlockEntity.canInsertFluidAmountIntoOutput(pBlockEntity.fluidTankHandler, match.get().getOutputFluidStack(),match.get().getOutputFluidAmount())
                && MelterBlockEntity.hasEnoughOutputSpace(pBlockEntity.fluidTankHandler,match.get().getOutputFluidAmount())
                && MelterBlockEntity.hasHeatSourceBelow(pBlockEntity)
;

    }

    private boolean canProcess(ItemStack stack) {

        SimpleContainer inputInventory = new SimpleContainer(1);
        inputInventory.setItem(0, stack);

        return level.getRecipeManager().getRecipeFor(MeltingRecipe.Type.INSTANCE, inputInventory, level).isPresent();
    }
    protected static boolean hasEnoughInputItems(SimpleContainer inventory, int count){
        return inventory.getItem(0).getCount() >= count;
    }


    protected static boolean canInsertFluidAmountIntoOutput(FluidTank tank, FluidStack fluidStack, int amount) {
        return tank.isEmpty() || tank.isFluidValid(amount,fluidStack);
    }
    protected static boolean hasEnoughOutputSpace(FluidTank tank,int amount){
        return tank.isEmpty() ||  amount <= tank.getSpace();
    }

    protected static boolean hasHeatSourceBelow(MelterBlockEntity pBlockEntity){
        BlockPos pos = pBlockEntity.getBlockPos();
        BlockState below = pBlockEntity.getLevel().getBlockState(pos.below());
        return HeatSources.isHeatSource(below);
    }

    @Override
    public CompoundTag getUpdateTag() {
        this.saveAdditional(updateTag);
        return updateTag;
    }


    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    /*public void clientSync() {
        if (Objects.requireNonNull(this.getLevel()).isClientSide) {
            return;
        }
        ServerLevel world = (ServerLevel) this.getLevel();
        Stream<ServerPlayer> entities = world.getChunkSource().chunkMap.getPlayers(new ChunkPos(this.worldPosition), false).stream();
        Packet<ClientGamePacketListener> updatePacket = this.getUpdatePacket();
        entities.forEach(e -> {
            if (updatePacket != null) {
                e.connection.send(updatePacket);
            }
        });
    }*/

    public IFluidHandler getFluidHandler() {
        return fluidTankHandler;
    }

    public int getProgressPercent() {
        return this.progress * 100 / this.maxProgress;
    }


}
