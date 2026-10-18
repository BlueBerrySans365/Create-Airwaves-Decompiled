/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ContainerLevelAccess
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.airwaves.block;

import com.airwaves.be.CircuitWorkbenchBE;
import com.airwaves.menu.CircuitWorkbenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CircuitWorkbenchBlock
extends Block
implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)15.0, (double)16.0);

    public CircuitWorkbenchBlock(BlockBehaviour.Properties p) {
        super(p);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(new Property[]{FACING});
    }

    public BlockState getStateForPlacement(BlockPlaceContext c) {
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)c.getHorizontalDirection().getOpposite());
    }

    protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return SHAPE;
    }

    protected InteractionResult useWithoutItem(BlockState st, Level lv, BlockPos pos, Player pl, BlockHitResult hit) {
        if (!lv.isClientSide) {
            pl.openMenu(st.getMenuProvider(lv, pos));
        }
        return InteractionResult.sidedSuccess((boolean)lv.isClientSide);
    }

    protected MenuProvider getMenuProvider(BlockState st, Level lv, BlockPos pos) {
        CircuitWorkbenchBE b;
        BlockEntity blockEntity = lv.getBlockEntity(pos);
        CircuitWorkbenchBE be = blockEntity instanceof CircuitWorkbenchBE ? (b = (CircuitWorkbenchBE)blockEntity) : null;
        return new SimpleMenuProvider((id, inv, p) -> new CircuitWorkbenchMenu(id, inv, ContainerLevelAccess.create((Level)lv, (BlockPos)pos), be), (Component)Component.translatable((String)"container.airwaves.circuit_workbench"));
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState st) {
        return new CircuitWorkbenchBE(pos, st);
    }

    protected void onRemove(BlockState st, Level lv, BlockPos pos, BlockState now, boolean moved) {
        BlockEntity blockEntity;
        if (!st.is(now.getBlock()) && (blockEntity = lv.getBlockEntity(pos)) instanceof CircuitWorkbenchBE) {
            CircuitWorkbenchBE be = (CircuitWorkbenchBE)blockEntity;
            Containers.dropContents((Level)lv, (BlockPos)pos, (Container)be.items);
        }
        super.onRemove(st, lv, pos, now, moved);
    }
}
