/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.StringRepresentable
 */
package com.airwaves.block;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public static enum TuningTableBlock.Part implements StringRepresentable
{
    LEFT,
    CENTER,
    RIGHT;


    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
