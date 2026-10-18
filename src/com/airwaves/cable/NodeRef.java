/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package com.airwaves.cable;

import net.minecraft.core.BlockPos;

public record NodeRef(BlockPos pos, int slot) {
    public NodeRef(BlockPos pos, int slot) {
        this.pos = pos = pos.immutable();
        this.slot = slot;
    }

    public long key() {
        return this.pos.asLong() * 31L + (long)this.slot;
    }
}
