/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.logic;

import java.util.Locale;

public static enum BoardAnalysis.Stage {
    TANK,
    OSCILLATOR,
    TUBE_AMP,
    TRANSISTOR_AMP,
    LOGIC,
    TIMER,
    INDICATOR,
    MEMORY,
    COUNTER,
    PULSE,
    DAYLIGHT,
    RAIN,
    MOTION;


    public boolean chip() {
        return this == MEMORY || this == COUNTER || this == PULSE;
    }

    public boolean sensor() {
        return this == DAYLIGHT || this == RAIN || this == MOTION;
    }

    public String key() {
        return "gui.airwaves.stage." + this.name().toLowerCase(Locale.ROOT);
    }

    public int color() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> -3641288;
            case 1 -> -6262560;
            case 2 -> -26048;
            case 3 -> -12566456;
            case 4 -> -11509568;
            case 5 -> -12818232;
            case 6 -> -2081744;
            case 7 -> -11760400;
            case 8 -> -1523648;
            case 9 -> -1023920;
            case 10 -> -995232;
            case 11 -> -11493136;
            case 12 -> -2039584;
        };
    }
}
