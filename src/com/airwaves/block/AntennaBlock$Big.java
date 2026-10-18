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

public static class AntennaBlock.Big
extends AntennaBlock {
    public static final MapCodec<AntennaBlock.Big> CODEC = AntennaBlock.Big.simpleCodec(AntennaBlock.Big::new);

    public AntennaBlock.Big(BlockBehaviour.Properties p) {
        super(p);
    }

    protected MapCodec<AntennaBlock.Big> codec() {
        return CODEC;
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public int tier() {
        return 3;
    }

    @Override
    public int[][] profile() {
        return new int[][]{{4, 32}, {2, 32}};
    }
}
