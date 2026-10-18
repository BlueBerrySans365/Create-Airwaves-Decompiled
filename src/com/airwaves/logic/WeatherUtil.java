/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package com.airwaves.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class WeatherUtil {
    private WeatherUtil() {
    }

    public static float rain(Level l) {
        return l.dimensionType().hasSkyLight() ? l.getRainLevel(1.0f) : 0.0f;
    }

    public static float thunder(Level l) {
        return l.dimensionType().hasSkyLight() ? l.getThunderLevel(1.0f) : 0.0f;
    }

    public static float penalty(float rain, float thunder, boolean fm) {
        return fm ? rain * 0.1f + thunder * 0.22f : rain * 0.22f + thunder * 0.45f;
    }

    public static float factor(Level l, BlockPos pos, boolean fm) {
        float r = WeatherUtil.rain(l);
        float t = WeatherUtil.thunder(l);
        if (r <= 0.0f && t <= 0.0f) {
            return 1.0f;
        }
        float k = l.canSeeSky(pos.above()) ? 1.0f : 0.5f;
        return Math.max(0.2f, 1.0f - WeatherUtil.penalty(r, t, fm) * k);
    }

    public static double amNightBoost(Level l) {
        if (!l.dimensionType().hasSkyLight()) {
            return 1.0;
        }
        long t = l.getDayTime() % 24000L;
        if (t > 13000L && t < 23000L) {
            return 1.6;
        }
        if (t > 12000L || t >= 23000L) {
            return 1.3;
        }
        return 1.0;
    }

    public static String describe(Level l) {
        float t = WeatherUtil.thunder(l);
        float r = WeatherUtil.rain(l);
        return t > 0.5f ? "Thunderstorm" : (r > 0.5f ? "Rain" : (r > 0.05f ? "Drizzle" : "Clear"));
    }
}
