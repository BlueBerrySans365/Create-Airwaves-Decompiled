/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.logic;

public record AntennaUtil.Info(int height, int tier, double fixedRange, Float beam) {
    public static final AntennaUtil.Info NONE = new AntennaUtil.Info(0, 0);

    public AntennaUtil.Info(int height, int tier) {
        this(height, tier, -1.0, null);
    }

    public AntennaUtil.Info(int height, int tier, double fixedRange) {
        this(height, tier, fixedRange, null);
    }

    public boolean creative() {
        return this.fixedRange >= 0.0;
    }
}
