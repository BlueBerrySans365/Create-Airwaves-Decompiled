/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package com.airwaves.api;

import com.airwaves.Tuning;
import com.airwaves.logic.Emitter;
import net.minecraft.core.BlockPos;

public record RadioStation(long id, BlockPos pos, double antennaTop, Tuning tuning, double range, float quality, String name) {
    public static final double CREATIVE_RANGE = 1.0E8;

    public RadioStation {
        tuning = tuning.clamped();
        quality = Math.max(0.0f, Math.min(1.0f, quality));
        range = Math.max(0.0, range);
        name = name == null ? "" : name;
    }

    public Emitter toEmitter() {
        return new Emitter(this.id, this.pos, this.antennaTop, this.tuning, this.range, this.quality, this.name);
    }

    public static RadioStation of(Emitter e) {
        return new RadioStation(e.root(), e.pos(), e.topY(), e.tuning(), e.range(), e.baseQ(), e.name());
    }
}
