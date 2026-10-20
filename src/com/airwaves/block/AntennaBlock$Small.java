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

public static class AntennaBlock.Small
extends AntennaBlock {
    public static final MapCodec<AntennaBlock.Small> CODEC = AntennaBlock.Small.simpleCodec(AntennaBlock.Small::new);

    public AntennaBlock.Small(BlockBehaviour.Properties p) {
        super(p);
    }

    protected MapCodec<AntennaBlock.Small> codec() {
        return CODEC;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int tier() {
        return 1;
    }

    @Override
    public int[][] profile() {
        return new int[][]{{1, 96}};
    }
}
