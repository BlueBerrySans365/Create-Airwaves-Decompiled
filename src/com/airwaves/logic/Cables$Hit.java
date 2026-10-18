/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 */
package com.airwaves.logic;

import com.airwaves.compat.SableCompat;
import com.airwaves.logic.Cables;
import java.util.List;
import net.minecraft.world.level.Level;

public record Cables.Hit<T>(T target, List<Cables.Seg> path) {
    public float lineQuality(Level lv) {
        double loss = 0.0;
        for (Cables.Seg s : this.path) {
            loss += s.type().loss * SableCompat.distance(lv, Cables.anchorOf(lv, s.from()), Cables.anchorOf(lv, s.to()));
        }
        return (float)Math.max(0.25, 1.0 - loss);
    }
}
