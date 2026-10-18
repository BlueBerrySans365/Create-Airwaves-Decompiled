/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package com.airwaves.be;

import net.minecraft.core.BlockPos;

public interface AudioSink {
    public BlockPos sinkPos();

    public boolean sinkOn();

    public boolean carrier();

    public float quality();

    public boolean fm();

    default public boolean ambientOn() {
        return this.sinkOn();
    }
}
