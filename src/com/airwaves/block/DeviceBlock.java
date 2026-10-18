/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.airwaves.block;

import com.airwaves.ModRegistry;
import com.airwaves.ModSounds;
import com.airwaves.Tuning;
import com.airwaves.be.CableNode;
import com.airwaves.be.TunableBE;
import com.airwaves.cable.Link;
import com.airwaves.item.CableItem;
import com.airwaves.item.Tunable;
import com.airwaves.logic.Cables;
import com.airwaves.logic.ShapeUtil;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class DeviceBlock
extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private final Map<Direction, VoxelShape> shapes;

    protected DeviceBlock(BlockBehaviour.Properties p, double x1, double y1, double z1, double x2, double y2, double z2) {
        super(p);
        this.shapes = ShapeUtil.make(x1, y1, z1, x2, y2, z2);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH));
    }

    protected DeviceBlock(BlockBehaviour.Properties p, double[] b) {
        this(p, b[0], b[1], b[2], b[3], b[4], b[5]);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(new Property[]{FACING});
    }

    public BlockState getStateForPlacement(BlockPlaceContext c) {
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)c.getHorizontalDirection().getOpposite());
    }

    protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return this.shapes.get(s.getValue((Property)FACING));
    }

    protected RenderShape getRenderShape(BlockState s) {
        return RenderShape.MODEL;
    }

    protected BlockState rotate(BlockState s, Rotation r) {
        return (BlockState)s.setValue((Property)FACING, (Comparable)r.rotate((Direction)s.getValue((Property)FACING)));
    }

    protected BlockState mirror(BlockState s, Mirror m) {
        return s.rotate(m.getRotation((Direction)s.getValue((Property)FACING)));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState st, Level lv, BlockPos pos, Player pl, InteractionHand h, BlockHitResult hit) {
        Tunable tn;
        if (stack.getItem() instanceof CableItem) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        Item item = stack.getItem();
        if (item instanceof Tunable && (tn = (Tunable)item).canTune(stack) && (item = lv.getBlockEntity(pos)) instanceof TunableBE) {
            Tuning t;
            TunableBE be = (TunableBE)item;
            if (!pl.isSecondaryUseActive() && !(t = (Tuning)stack.getOrDefault((DataComponentType)ModRegistry.TUNING.get(), (Object)Tuning.DEFAULT)).matches(be.tuning)) {
                if (!lv.isClientSide) {
                    be.tuning = t;
                    be.sync();
                    lv.playSound(null, pos, (SoundEvent)ModSounds.TUNE.get(), SoundSource.BLOCKS, 0.4f, 1.8f);
                }
                return ItemInteractionResult.sidedSuccess((boolean)lv.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected void onRemove(BlockState s, Level lv, BlockPos p, BlockState ns, boolean moved) {
        BlockEntity blockEntity;
        if (!s.is(ns.getBlock()) && !lv.isClientSide && (blockEntity = lv.getBlockEntity(p)) instanceof CableNode) {
            CableNode n = (CableNode)blockEntity;
            Cables.broken(lv, p, new ArrayList<Link>(n.links().list()));
        }
        super.onRemove(s, lv, p, ns, moved);
    }

    protected static boolean handEmpty(Player p) {
        return p.getMainHandItem().isEmpty();
    }

    protected ItemInteractionResult crystalInsert(ItemStack stack, BlockState st, Level lv, BlockPos pos, Player pl, IntegerProperty prop) {
        int v;
        int n = stack.is(Items.AMETHYST_SHARD) ? 1 : (v = stack.is(Items.AMETHYST_BLOCK) ? 2 : 0);
        if (v == 0) {
            return null;
        }
        if ((Integer)st.getValue((Property)prop) != 0) {
            return ItemInteractionResult.CONSUME;
        }
        if (!lv.isClientSide) {
            lv.setBlock(pos, (BlockState)st.setValue((Property)prop, (Comparable)Integer.valueOf(v)), 3);
            BlockEntity blockEntity = lv.getBlockEntity(pos);
            if (blockEntity instanceof TunableBE) {
                TunableBE be = (TunableBE)blockEntity;
                be.refresh();
                be.sync();
            }
            if (!pl.getAbilities().instabuild) {
                stack.shrink(1);
            }
            lv.playSound(null, pos, (SoundEvent)ModSounds.CRYSTAL_IN.get(), SoundSource.BLOCKS, 1.0f, v == 2 ? 0.7f : 1.0f);
        }
        return ItemInteractionResult.sidedSuccess((boolean)lv.isClientSide);
    }

    protected boolean crystalRemove(BlockState st, Level lv, BlockPos pos, IntegerProperty prop) {
        int v = (Integer)st.getValue((Property)prop);
        if (v == 0) {
            return false;
        }
        lv.setBlock(pos, (BlockState)st.setValue((Property)prop, (Comparable)Integer.valueOf(0)), 3);
        BlockEntity blockEntity = lv.getBlockEntity(pos);
        if (blockEntity instanceof TunableBE) {
            TunableBE be = (TunableBE)blockEntity;
            be.refresh();
            be.sync();
        }
        lv.addFreshEntity((Entity)new ItemEntity(lv, (double)pos.getX() + 0.5, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5, new ItemStack((ItemLike)(v == 2 ? Items.AMETHYST_BLOCK : Items.AMETHYST_SHARD))));
        lv.playSound(null, pos, (SoundEvent)ModSounds.CRYSTAL_OUT.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        return true;
    }
}
