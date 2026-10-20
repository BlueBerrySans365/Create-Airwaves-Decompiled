/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.PipeBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.airwaves.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class ConnectedBlock
extends Block {
    public ConnectedBlock(BlockBehaviour.Properties p) {
        super(p);
        BlockState s = (BlockState)this.stateDefinition.any();
        for (BooleanProperty b : PipeBlock.PROPERTY_BY_DIRECTION.values()) {
            s = (BlockState)s.setValue((Property)b, (Comparable)Boolean.valueOf(false));
        }
        this.registerDefaultState(s);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(new Property[]{PipeBlock.NORTH, PipeBlock.EAST, PipeBlock.SOUTH, PipeBlock.WEST, PipeBlock.UP, PipeBlock.DOWN});
    }

    private BlockState connect(BlockState s, BlockGetter lv, BlockPos pos) {
        for (Direction d : Direction.values()) {
            s = (BlockState)s.setValue((Property)PipeBlock.PROPERTY_BY_DIRECTION.get(d), (Comparable)Boolean.valueOf(lv.getBlockState(pos.relative(d)).is((Block)this)));
        }
        return s;
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.connect(this.defaultBlockState(), (BlockGetter)ctx.getLevel(), ctx.getClickedPos());
    }

    protected BlockState updateShape(BlockState s, Direction d, BlockState other, LevelAccessor lv, BlockPos pos, BlockPos otherPos) {
        return (BlockState)s.setValue((Property)PipeBlock.PROPERTY_BY_DIRECTION.get(d), (Comparable)Boolean.valueOf(other.is((Block)this)));
    }
}
