/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package com.airwaves.cable;

import com.airwaves.cable.CableType;
import com.airwaves.cable.NodeRef;
import net.minecraft.core.BlockPos;

public record Link(int slot, BlockPos other, int otherSlot, CableType type) {
    public NodeRef far() {
        return new NodeRef(this.other, this.otherSlot);
    }
}
