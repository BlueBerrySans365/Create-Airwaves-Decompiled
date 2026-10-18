/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.SimpleContainer
 */
package com.airwaves.be;

import net.minecraft.world.SimpleContainer;

class TapePlayerBE.1
extends SimpleContainer {
    TapePlayerBE.1(int arg0) {
        super(arg0);
    }

    public void setChanged() {
        super.setChanged();
        TapePlayerBE.this.onTapeChanged();
    }

    public int getMaxStackSize() {
        return 1;
    }
}
