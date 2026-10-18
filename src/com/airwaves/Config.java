/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.fml.ModList
 *  net.neoforged.neoforge.common.ModConfigSpec
 *  net.neoforged.neoforge.common.ModConfigSpec$BooleanValue
 *  net.neoforged.neoforge.common.ModConfigSpec$Builder
 *  net.neoforged.neoforge.common.ModConfigSpec$DoubleValue
 *  net.neoforged.neoforge.common.ModConfigSpec$IntValue
 */
package com.airwaves;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue REQUIRE_ROTATION;
    public static final ModConfigSpec.IntValue MIN_RPM;
    public static final ModConfigSpec.IntValue FULL_RPM;
    public static final ModConfigSpec.DoubleValue ALTITUDE_BONUS;
    public static final ModConfigSpec.BooleanValue ELECTRIC;
    public static final ModConfigSpec.DoubleValue WATTS_PER_STRESS;
    public static final ModConfigSpec.BooleanValue DIM_RELAY_CHUNK;
    private static Boolean powerGrid;
    private static Boolean cee;

    public static boolean rotation() {
        return (Boolean)REQUIRE_ROTATION.get();
    }

    public static boolean powerGridLoaded() {
        if (powerGrid == null) {
            powerGrid = ModList.get() != null && ModList.get().isLoaded("powergrid");
        }
        return powerGrid;
    }

    public static boolean ceeLoaded() {
        if (cee == null) {
            cee = ModList.get() != null && ModList.get().isLoaded("electroenergetics");
        }
        return cee;
    }

    public static boolean electricEnabled() {
        if (!Config.powerGridLoaded() && !Config.ceeLoaded()) {
            return false;
        }
        return !SPEC.isLoaded() || (Boolean)ELECTRIC.get() != false;
    }

    private Config() {
    }

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        REQUIRE_ROTATION = b.comment("Transmitters, relay towers, radio towers and jammers need power (rotation, or electricity, see below) to run").define("requireRotation", true);
        MIN_RPM = b.comment("Slowest shaft speed that still runs a powered device").defineInRange("minRpm", 16, 1, 256);
        FULL_RPM = b.comment("Shaft speed for full transmit power; faster shafts give more range, up to twice this speed").defineInRange("fullRpm", 64, 1, 256);
        ALTITUDE_BONUS = b.comment("Range bonus per 100 blocks a transmitter or relay flies above the ground (Create: Aeronautics airships)").defineInRange("altitudeBonus", 0.5, 0.0, 4.0);
        b.push("electricity");
        ELECTRIC = b.comment(new String[]{"With Create: Power Grid or Create: Electro Energetics installed, powered machines can also run on electricity through their built-in terminals.", "Rotation is always used first."}).define("electricPower", true);
        WATTS_PER_STRESS = b.comment("Watts a machine draws on electricity per stress unit (SU per RPM) it takes on rotation").defineInRange("wattsPerStress", 2.5, 0.01, 1000.0);
        b.pop();
        DIM_RELAY_CHUNK = b.comment("A running Dimensional Relay keeps its own chunk loaded, so the link to the other dimension never drops").define("dimensionalRelayLoadsChunk", true);
        SPEC = b.build();
    }
}
