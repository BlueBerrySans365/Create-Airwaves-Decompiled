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

import com.airwaves.be.MicrophoneBE;
import com.airwaves.block.DeviceBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MicrophoneBlock
extends DeviceBlock {
    public static final MapCodec<MicrophoneBlock> CODEC = MicrophoneBlock.simpleCodec(MicrophoneBlock::new);

    public MicrophoneBlock(BlockBehaviour.Properties p) {
        super(p, 4.0, 0.0, 4.0, 12.0, 16.0, 13.0);
    }

    protected MapCodec<MicrophoneBlock> codec() {
        return CODEC;
    }

    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        return new MicrophoneBE(p, s);
    }
}
