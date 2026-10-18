/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.airwaves.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class YagiAntennaBlock
extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape NS = Shapes.or((VoxelShape)YagiAntennaBlock.box((double)6.0, (double)0.0, (double)6.0, (double)10.0, (double)9.0, (double)10.0), (VoxelShape[])new VoxelShape[]{YagiAntennaBlock.box((double)7.0, (double)8.0, (double)0.0, (double)9.0, (double)11.0, (double)16.0), YagiAntennaBlock.box((double)0.0, (double)9.0, (double)13.0, (double)16.0, (double)10.0, (double)15.0)});
    private static final VoxelShape EW = Shapes.or((VoxelShape)YagiAntennaBlock.box((double)6.0, (double)0.0, (double)6.0, (double)10.0, (double)9.0, (double)10.0), (VoxelShape[])new VoxelShape[]{YagiAntennaBlock.box((double)0.0, (double)8.0, (double)7.0, (double)16.0, (double)11.0, (double)9.0), YagiAntennaBlock.box((double)13.0, (double)9.0, (double)0.0, (double)15.0, (double)10.0, (double)16.0)});
    private static final VoxelShape WE = Shapes.or((VoxelShape)YagiAntennaBlock.box((double)6.0, (double)0.0, (double)6.0, (double)10.0, (double)9.0, (double)10.0), (VoxelShape[])new VoxelShape[]{YagiAntennaBlock.box((double)0.0, (double)8.0, (double)7.0, (double)16.0, (double)11.0, (double)9.0), YagiAntennaBlock.box((double)1.0, (double)9.0, (double)0.0, (double)3.0, (double)10.0, (double)16.0)});
    private static final VoxelShape SN = Shapes.or((VoxelShape)YagiAntennaBlock.box((double)6.0, (double)0.0, (double)6.0, (double)10.0, (double)9.0, (double)10.0), (VoxelShape[])new VoxelShape[]{YagiAntennaBlock.box((double)7.0, (double)8.0, (double)0.0, (double)9.0, (double)11.0, (double)16.0), YagiAntennaBlock.box((double)0.0, (double)9.0, (double)1.0, (double)16.0, (double)10.0, (double)3.0)});

    public YagiAntennaBlock(BlockBehaviour.Properties p) {
        super(p);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(new Property[]{FACING});
    }

    public BlockState getStateForPlacement(BlockPlaceContext c) {
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)c.getHorizontalDirection());
    }

    protected BlockState rotate(BlockState s, Rotation r) {
        return (BlockState)s.setValue((Property)FACING, (Comparable)r.rotate((Direction)s.getValue((Property)FACING)));
    }

    protected BlockState mirror(BlockState s, Mirror m) {
        return s.rotate(m.getRotation((Direction)s.getValue((Property)FACING)));
    }

    protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return switch ((Direction)s.getValue((Property)FACING)) {
            case Direction.SOUTH -> SN;
            case Direction.EAST -> WE;
            case Direction.WEST -> EW;
            default -> NS;
        };
    }
}
