/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.airwaves.item;

import net.minecraft.world.item.ItemStack;

public interface Tunable {
    default public boolean canTune(ItemStack stack) {
        return true;
    }
}
