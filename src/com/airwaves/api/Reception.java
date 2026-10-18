/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.api;

public record Reception(float quality, long stationId, String name) {
    public static final Reception NOTHING = new Reception(0.0f, 0L, "");
    public static final float THRESHOLD = 0.02f;

    public boolean heard() {
        return this.quality >= 0.02f && this.stationId != 0L;
    }
}
