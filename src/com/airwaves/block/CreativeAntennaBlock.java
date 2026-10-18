/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.airwaves.block;

import com.airwaves.ModRegistry;
import com.airwaves.be.CreativeAntennaBE;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CreativeAntennaBlock
extends Block
implements IBE<CreativeAntennaBE> {
    private static final VoxelShape SHAPE = Shapes.or((VoxelShape)Block.box((double)3.0, (double)0.0, (double)3.0, (double)13.0, (double)4.0, (double)13.0), (VoxelShape)Block.box((double)6.0, (double)4.0, (double)6.0, (double)10.0, (double)16.0, (double)10.0));

    public CreativeAntennaBlock(BlockBehaviour.Properties p) {
        super(p);
    }

    protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return SHAPE;
    }

    public Class<CreativeAntennaBE> getBlockEntityClass() {
        return CreativeAntennaBE.class;
    }

    public BlockEntityType<? extends CreativeAntennaBE> getBlockEntityType() {
        return ModRegistry.CREATIVE_ANTENNA_BE.get();
    }
}
