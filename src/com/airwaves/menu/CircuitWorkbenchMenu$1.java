/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package com.airwaves.menu;

import com.airwaves.logic.BoardDesign;
import com.airwaves.menu.CircuitWorkbenchMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

class CircuitWorkbenchMenu.1
extends Slot {
    CircuitWorkbenchMenu.1(CircuitWorkbenchMenu this$0, Container arg0, int arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public boolean mayPlace(ItemStack s) {
        return BoardDesign.Part.of(s) != null;
    }

    public int getMaxStackSize() {
        return 1;
    }

    public int getMaxStackSize(ItemStack s) {
        return 1;
    }
}
