/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package com.airwaves.menu;

import com.airwaves.item.Tunable;
import com.airwaves.menu.TuningTableMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

class TuningTableMenu.1
extends Slot {
    TuningTableMenu.1(TuningTableMenu this$0, Container arg0, int arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public boolean mayPlace(ItemStack s) {
        return s.getItem() instanceof Tunable;
    }

    public int getMaxStackSize() {
        return 1;
    }
}
