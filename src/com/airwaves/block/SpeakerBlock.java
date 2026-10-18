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

import com.airwaves.be.SpeakerBE;
import com.airwaves.block.DeviceBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SpeakerBlock
extends DeviceBlock {
    public static final MapCodec<SpeakerBlock> CODEC = SpeakerBlock.simpleCodec(SpeakerBlock::new);

    public SpeakerBlock(BlockBehaviour.Properties p) {
        super(p, 2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    }

    protected SpeakerBlock(BlockBehaviour.Properties p, double[] box) {
        super(p, box);
    }

    protected MapCodec<SpeakerBlock> codec() {
        return CODEC;
    }

    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        return new SpeakerBE(p, s);
    }
}
