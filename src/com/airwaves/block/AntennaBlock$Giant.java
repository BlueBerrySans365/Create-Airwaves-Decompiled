/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package com.airwaves.block;

import com.airwaves.block.AntennaBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;

public static class AntennaBlock.Giant
extends AntennaBlock {
    public static final MapCodec<AntennaBlock.Giant> CODEC = AntennaBlock.Giant.simpleCodec(AntennaBlock.Giant::new);

    public AntennaBlock.Giant(BlockBehaviour.Properties p) {
        super(p);
    }

    protected MapCodec<AntennaBlock.Giant> codec() {
        return CODEC;
    }

    @Override
    public int size() {
        return 8;
    }

    @Override
    public int tier() {
        return 4;
    }

    @Override
    public int[][] profile() {
        return new int[][]{{8, 32}, {4, 32}, {2, 32}};
    }
}
