/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package com.airwaves.item;

import com.airwaves.ModSounds;
import com.airwaves.client.ManualScreen;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FieldManualItem
extends Item {
    public FieldManualItem(Item.Properties p) {
        super(p);
    }

    public InteractionResultHolder<ItemStack> use(Level lv, Player pl, InteractionHand hand) {
        if (lv.isClientSide) {
            ManualScreen.open();
        } else {
            lv.playSound(null, pl.blockPosition(), (SoundEvent)ModSounds.BOOK.get(), SoundSource.PLAYERS, 0.7f, 1.0f);
        }
        return InteractionResultHolder.sidedSuccess((Object)pl.getItemInHand(hand), (boolean)lv.isClientSide);
    }
}
