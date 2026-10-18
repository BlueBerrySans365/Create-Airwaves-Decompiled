/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ContainerLevelAccess
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package com.airwaves.menu;

import com.airwaves.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

class CircuitWorkbenchMenu.3
extends Slot {
    final /* synthetic */ ContainerLevelAccess val$access;

    CircuitWorkbenchMenu.3(Container arg0, int arg1, int arg2, int arg3, ContainerLevelAccess containerLevelAccess) {
        this.val$access = containerLevelAccess;
        super(arg0, arg1, arg2, arg3);
    }

    public boolean mayPlace(ItemStack s) {
        return false;
    }

    public void onTake(Player p, ItemStack s) {
        CircuitWorkbenchMenu.this.consumeInputs();
        this.val$access.execute((l, pos) -> l.playSound(null, pos, (SoundEvent)ModSounds.SWITCH.get(), SoundSource.BLOCKS, 0.6f, 1.6f));
        super.onTake(p, s);
    }
}
