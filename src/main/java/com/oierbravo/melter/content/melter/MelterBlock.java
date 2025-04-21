package com.oierbravo.melter.content.melter;

import com.mojang.serialization.MapCodec;
import com.oierbravo.melter.content.melter.heatsource.HeatSources;
import com.oierbravo.melter.foundation.block.ITE;
import com.oierbravo.melter.registrate.ModBlockEntities;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;


public class MelterBlock extends BaseEntityBlock implements ITE<MelterBlockEntity> {
    public static final IntegerProperty HEAT_SOURCE = IntegerProperty.create("heatsource", 0, HeatSources.MAX_LEVEL);
    public static final BooleanProperty CREATIVE = BooleanProperty.create("creative");

    private static final VoxelShape RENDER_SHAPE = Shapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return RENDER_SHAPE;
    }

    public MelterBlock(Properties pProperties) {
        super(pProperties.strength(0.6f));
        registerDefaultState(getStateDefinition().any().setValue(HEAT_SOURCE, 0).setValue(CREATIVE, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HEAT_SOURCE);
        builder.add(CREATIVE);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
            .setValue(HEAT_SOURCE, HeatSources.fromLevel(context.getLevel(), context.getClickedPos().below()))
            .setValue(CREATIVE, HeatSources.isCreative(context.getLevel(), context.getClickedPos().below()));
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if(pos.below().equals(neighbor)){
            MelterBlockEntity melter = (MelterBlockEntity) level.getBlockEntity(pos);
            if(melter != null){
                melter.updateBlockStateFromNeighborUpdate(state);
            }
        }
        super.onNeighborChange(state, level, pos, neighbor);
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        if(!pLevel.isClientSide()) {
            if(pEntity instanceof LivingEntity) {
                LivingEntity entity = ((LivingEntity) pEntity);
                if(pState.hasProperty(MelterBlock.HEAT_SOURCE) && !pState.getValue(MelterBlock.HEAT_SOURCE).equals(0) && !pState.getValue(MelterBlock.CREATIVE)) {
                    entity.hurt(this.getTileEntity(pLevel,pPos).getLevel().damageSources().hotFloor(),0.4f* pState.getValue(MelterBlock.HEAT_SOURCE));
                }
            }
        }

        super.stepOn(pLevel, pPos, pState, pEntity);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);

            ItemStack heldItem = pPlayer.getItemInHand(pHand);
            boolean isEmptyHanded = heldItem.isEmpty();

            if(entity instanceof MelterBlockEntity melter) {
                ItemStack itemInMelter = melter.getItemHandler().getStackInSlot(0);

                // if the hand is empty (and there is items in the melter), take the items out
                if (isEmptyHanded) {
                    if (!itemInMelter.isEmpty()) {
                        pPlayer.getInventory().placeItemBackInInventory(itemInMelter);
                        melter.setItemStack(ItemStack.EMPTY);
                        pLevel.playSound(null, pPos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f, 1f);
                        melter.setChanged();
                    }
                }
                // if a player clicks on a melter with a tank or a bucket
                else if (FluidUtil.interactWithFluidHandler(pPlayer, pHand, melter.getFluidHandler())) {
                    melter.setChanged();
                }
                // try and put the items in the melter (only items with valid recipe are allowed and up to 64)
                else {
                    IItemHandler itemHandler = melter.getItemHandler();
                    ItemStack remainder = itemHandler
                            .insertItem(0, heldItem, false);
                    if (remainder.isEmpty()) {
                        pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
                        melter.setChanged();
                    }
                    else if (remainder.getCount() < heldItem.getCount()) {
                        pPlayer.setItemInHand(pHand, remainder);
                        melter.setChanged();
                    }
                }
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.MELTER_BLOCK_ENTITY.create(pPos, pState);
    }



    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.MELTER_BLOCK_ENTITY.get(),
                MelterBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof MelterBlockEntity) {
                ((MelterBlockEntity) blockEntity).drops();
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);

        if (entityIn.level().isClientSide)
            return;
        if (!(entityIn instanceof ItemEntity))
            return;
        if (!entityIn.isAlive())
            return;

        MelterBlockEntity melter = null;
        for (BlockPos pos : Iterate.hereAndBelow(entityIn.blockPosition()))
            if (melter == null)
                melter = getTileEntity(worldIn, pos);

        if (melter == null)
            return;

        ItemEntity itemEntity = (ItemEntity) entityIn;
        Lazy<IItemHandler> itemHandler = melter.getItemHandlerCapability();

        ItemStack remainder = itemHandler
                .get().insertItem(0, itemEntity.getItem(), false);
        if (remainder.isEmpty())
            itemEntity.discard();
        int count = remainder.getCount();
        int iCount = itemEntity.getItem().getCount();
        if (remainder.getCount() < itemEntity.getItem()
                .getCount())
            itemEntity.setItem(remainder);
    }

    @Override
    public Class<MelterBlockEntity> getTileEntityClass() {
        return MelterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MelterBlockEntity> getTileEntityType() {
        return ModBlockEntities.MELTER_BLOCK_ENTITY.get();
    }
}
