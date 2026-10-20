/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.cable;

public enum CableType {
    COPPER("copper", 16, 0.004, 0.06f, true, -3641281);

    public final String id;
    public final int maxLen;
    public final double loss;
    public final float width;
    public final boolean light;
    public final int color;

    private CableType(String id, int maxLen, double loss, float width, boolean light, int color) {
        this.id = id;
        this.maxLen = maxLen;
        this.loss = loss;
        this.width = width;
        this.light = light;
        this.color = color;
    }

    public static CableType byId(String s) {
        for (CableType t : CableType.values()) {
            if (!t.id.equals(s)) continue;
            return t;
        }
        return COPPER;
    }
}
