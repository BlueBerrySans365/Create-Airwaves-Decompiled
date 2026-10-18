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

public static class AntennaBlock.Medium
extends AntennaBlock {
    public static final MapCodec<AntennaBlock.Medium> CODEC = AntennaBlock.Medium.simpleCodec(AntennaBlock.Medium::new);

    public AntennaBlock.Medium(BlockBehaviour.Properties p) {
        super(p);
    }

    protected MapCodec<AntennaBlock.Medium> codec() {
        return CODEC;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public int tier() {
        return 2;
    }

    @Override
    public int[][] profile() {
        return new int[][]{{2, 40}};
    }
}
