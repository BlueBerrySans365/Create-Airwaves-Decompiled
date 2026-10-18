/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package com.airwaves.block;

import com.airwaves.ModRegistry;
import com.airwaves.ModSounds;
import com.airwaves.Tuning;
import com.airwaves.be.CircuitBoxBE;
import com.airwaves.item.BoardItem;
import com.airwaves.item.Tunable;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CircuitBoxBlock
extends BaseEntityBlock {
    public static final MapCodec<CircuitBoxBlock> CODEC = CircuitBoxBlock.simpleCodec(CircuitBoxBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty BOARD = BooleanProperty.create((String)"board");
    private static final VoxelShape SHAPE = Block.box((double)1.0, (double)0.0, (double)1.0, (double)15.0, (double)8.0, (double)15.0);

    public CircuitBoxBlock(BlockBehaviour.Properties p) {
        super(p);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH)).setValue((Property)LIT, (Comparable)Boolean.valueOf(false))).setValue((Property)BOARD, (Comparable)Boolean.valueOf(false)));
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(new Property[]{FACING, LIT, BOARD});
    }

    protected RenderShape getRenderShape(BlockState s) {
        return RenderShape.MODEL;
    }

    protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return SHAPE;
    }

    public BlockState getStateForPlacement(BlockPlaceContext c) {
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)c.getHorizontalDirection().getOpposite());
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        return new CircuitBoxBE(p, s);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lv, BlockState s, BlockEntityType<T> t) {
        return lv.isClientSide ? null : CircuitBoxBlock.createTickerHelper(t, ModRegistry.CIRCUIT_BOX_BE.get(), (l, p, st, be) -> be.serverTick());
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState st, Level lv, BlockPos pos, Player pl, InteractionHand h, BlockHitResult hit) {
        Tuning t;
        Tunable tn;
        BlockEntity blockEntity = lv.getBlockEntity(pos);
        if (!(blockEntity instanceof CircuitBoxBE)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        CircuitBoxBE be = (CircuitBoxBE)blockEntity;
        if (stack.getItem() instanceof BoardItem && be.board().isEmpty()) {
            if (!lv.isClientSide) {
                be.setBoard(stack.copyWithCount(1));
                if (!pl.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                lv.playSound(null, pos, (SoundEvent)ModSounds.SWITCH.get(), SoundSource.BLOCKS, 0.6f, 1.4f);
            }
            return ItemInteractionResult.sidedSuccess((boolean)lv.isClientSide);
        }
        Item item = stack.getItem();
        if (item instanceof Tunable && (tn = (Tunable)item).canTune(stack) && !pl.isSecondaryUseActive() && !(t = (Tuning)stack.getOrDefault((DataComponentType)ModRegistry.TUNING.get(), (Object)Tuning.DEFAULT)).matches(be.getTuning())) {
            if (!lv.isClientSide) {
                be.setTuning(t);
                be.sync();
                lv.playSound(null, pos, (SoundEvent)ModSounds.TUNE.get(), SoundSource.BLOCKS, 0.4f, 1.8f);
            }
            return ItemInteractionResult.sidedSuccess((boolean)lv.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected InteractionResult useWithoutItem(BlockState st, Level lv, BlockPos pos, Player pl, BlockHitResult hit) {
        CircuitBoxBE be;
        BlockEntity blockEntity;
        if (!pl.isSecondaryUseActive() || !((blockEntity = lv.getBlockEntity(pos)) instanceof CircuitBoxBE) || (be = (CircuitBoxBE)blockEntity).board().isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!lv.isClientSide) {
            ItemStack b = be.board();
            be.setBoard(ItemStack.EMPTY);
            if (!pl.getInventory().add(b)) {
                Block.popResource((Level)lv, (BlockPos)pos.above(), (ItemStack)b);
            }
            lv.playSound(null, pos, (SoundEvent)ModSounds.SWITCH.get(), SoundSource.BLOCKS, 0.6f, 0.9f);
        }
        return InteractionResult.sidedSuccess((boolean)lv.isClientSide);
    }

    protected boolean isSignalSource(BlockState s) {
        return true;
    }

    protected int getSignal(BlockState s, BlockGetter l, BlockPos p, Direction side) {
        int n;
        BlockEntity blockEntity;
        if (side == s.getValue((Property)FACING) && (blockEntity = l.getBlockEntity(p)) instanceof CircuitBoxBE) {
            CircuitBoxBE be = (CircuitBoxBE)blockEntity;
            n = be.output();
        } else {
            n = 0;
        }
        return n;
    }

    protected int getDirectSignal(BlockState s, BlockGetter l, BlockPos p, Direction side) {
        return this.getSignal(s, l, p, side);
    }

    public boolean canConnectRedstone(BlockState s, BlockGetter l, BlockPos p, @Nullable Direction side) {
        return side != null && side.getAxis() == ((Direction)s.getValue((Property)FACING)).getAxis();
    }

    public static void updateFront(Level lv, BlockPos pos, BlockState s) {
        Direction out = ((Direction)s.getValue((Property)FACING)).getOpposite();
        BlockPos front = pos.relative(out);
        lv.neighborChanged(front, s.getBlock(), pos);
        lv.updateNeighborsAtExceptFromFacing(front, s.getBlock(), out.getOpposite());
    }

    protected void onRemove(BlockState s, Level lv, BlockPos pos, BlockState ns, boolean moved) {
        CircuitBoxBE be;
        BlockEntity blockEntity;
        if (!s.is(ns.getBlock()) && (blockEntity = lv.getBlockEntity(pos)) instanceof CircuitBoxBE && !(be = (CircuitBoxBE)blockEntity).board().isEmpty()) {
            Block.popResource((Level)lv, (BlockPos)pos, (ItemStack)be.board());
        }
        super.onRemove(s, lv, pos, ns, moved);
        if (!s.is(ns.getBlock()) && !moved) {
            CircuitBoxBlock.updateFront(lv, pos, s);
        }
    }
}
