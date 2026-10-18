/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 */
package com.airwaves.logic;

import com.airwaves.Tuning;
import com.airwaves.logic.AntennaUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record Emitter(long root, BlockPos pos, double topY, Tuning tuning, double range, float baseQ, String name, Direction beam) {
    public static final double CREATIVE = 1.0E8;

    public Emitter(long root, BlockPos pos, double topY, Tuning tuning, double range, float baseQ, String name) {
        this(root, pos, topY, tuning, range, baseQ, name, null);
    }

    public double rangeTo(BlockPos at) {
        return this.beam == null ? this.range : this.range * AntennaUtil.pattern(this.beam, at.getX() - this.pos.getX(), at.getZ() - this.pos.getZ());
    }

    public boolean creative() {
        return this.range >= 1.0E8;
    }
}
