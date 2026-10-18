/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Container
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package com.airwaves.menu;

import com.airwaves.ModRegistry;
import com.airwaves.menu.CircuitWorkbenchMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

class CircuitWorkbenchMenu.2
extends Slot {
    CircuitWorkbenchMenu.2(CircuitWorkbenchMenu this$0, Container arg0, int arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public boolean mayPlace(ItemStack s) {
        return s.is((Item)ModRegistry.CIRCUIT_BOARD.get());
    }
}
