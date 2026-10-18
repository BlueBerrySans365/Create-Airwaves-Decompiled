/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.airwaves.block;

import com.airwaves.be.InsulatorBE;
import com.airwaves.block.DeviceBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class InsulatorBlock
extends DeviceBlock {
    public static final MapCodec<InsulatorBlock> CODEC = InsulatorBlock.simpleCodec(InsulatorBlock::new);

    public InsulatorBlock(BlockBehaviour.Properties p) {
        super(p, 2.5, 0.0, 4.5, 13.5, 11.0, 11.5);
    }

    protected MapCodec<InsulatorBlock> codec() {
        return CODEC;
    }

    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        return new InsulatorBE(p, s);
    }
}
