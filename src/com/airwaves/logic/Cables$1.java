/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.logic;

import com.airwaves.cable.CableType;

static class Cables.1 {
    static final /* synthetic */ int[] $SwitchMap$com$airwaves$cable$CableType;

    static {
        $SwitchMap$com$airwaves$cable$CableType = new int[CableType.values().length];
        try {
            Cables.1.$SwitchMap$com$airwaves$cable$CableType[CableType.GOLD.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Cables.1.$SwitchMap$com$airwaves$cable$CableType[CableType.SILVER.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            Cables.1.$SwitchMap$com$airwaves$cable$CableType[CableType.FIBER.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
