/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.logic;

import com.airwaves.Tuning;
import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public static final class MobileRadios.Mobile {
    public final UUID id;
    public final int kind;
    public ResourceKey<Level> dim;
    public Vec3 pos = Vec3.ZERO;
    public Tuning tuning = Tuning.DEFAULT;
    public int contraption;
    public boolean on;
    public double range;
    public double rxGain = 1.0;
    public long seen;
    public long root;
    public float q;

    MobileRadios.Mobile(UUID id, int kind) {
        this.id = id;
        this.kind = kind;
        this.root = id.getMostSignificantBits() ^ id.getLeastSignificantBits() ^ 0x4D0B11E5L;
    }

    public boolean listens() {
        return this.kind == 1 || this.kind == 2 || this.kind == 3;
    }

    public boolean station() {
        return this.kind == 5 || this.kind == 3;
    }
}
