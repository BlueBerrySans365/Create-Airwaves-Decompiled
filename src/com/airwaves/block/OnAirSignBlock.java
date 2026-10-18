/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.airwaves.block;

import com.airwaves.logic.RadioNetwork;
import com.airwaves.logic.ShapeUtil;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OnAirSignBlock
extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final int RADIUS = 12;
    private static final Map<Direction, VoxelShape> SHAPES = ShapeUtil.make(1.0, 3.0, 13.0, 15.0, 13.0, 16.0);

    public OnAirSignBlock(BlockBehaviour.Properties p) {
        super(p);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH)).setValue((Property)LIT, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(new Property[]{FACING, LIT});
    }

    public BlockState getStateForPlacement(BlockPlaceContext c) {
        Direction d = c.getClickedFace().getAxis().isHorizontal() ? c.getClickedFace() : c.getHorizontalDirection().getOpposite();
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)d);
    }

    protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return SHAPES.get(s.getValue((Property)FACING));
    }

    protected BlockState rotate(BlockState s, Rotation r) {
        return (BlockState)s.setValue((Property)FACING, (Comparable)r.rotate((Direction)s.getValue((Property)FACING)));
    }

    protected BlockState mirror(BlockState s, Mirror m) {
        return s.rotate(m.getRotation((Direction)s.getValue((Property)FACING)));
    }

    protected void onPlace(BlockState s, Level lv, BlockPos p, BlockState old, boolean moved) {
        if (!lv.isClientSide) {
            lv.scheduleTick(p, (Block)this, 10);
        }
    }

    protected void tick(BlockState s, ServerLevel lv, BlockPos p, RandomSource r) {
        boolean on = OnAirSignBlock.onAirNear((Level)lv, p);
        if (on != (Boolean)s.getValue((Property)LIT)) {
            lv.setBlock(p, (BlockState)s.setValue((Property)LIT, (Comparable)Boolean.valueOf(on)), 3);
        }
        lv.scheduleTick(p, (Block)this, 10);
    }

    private static boolean onAirNear(Level lv, BlockPos pos) {
        return RadioNetwork.onAirNear(lv, pos, 12);
    }
}
