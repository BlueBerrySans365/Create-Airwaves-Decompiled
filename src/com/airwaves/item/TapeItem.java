/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 */
package com.airwaves.item;

import com.airwaves.ModRegistry;
import com.airwaves.logic.TapeData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class TapeItem
extends Item {
    public TapeItem(Item.Properties p) {
        super(p);
    }

    public static TapeData data(ItemStack s) {
        return (TapeData)s.getOrDefault((DataComponentType)ModRegistry.TAPE.get(), (Object)TapeData.EMPTY);
    }

    public void appendHoverText(ItemStack s, Item.TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        TapeData d = TapeItem.data(s);
        if (d.blank()) {
            tip.add((Component)Component.literal((String)"Blank").withStyle(ChatFormatting.GRAY));
        } else {
            tip.add((Component)Component.literal((String)("Length " + TapeData.time(d.length()))).withStyle(ChatFormatting.GOLD));
            tip.add((Component)Component.literal((String)(d.cues().size() + " sounds recorded")).withStyle(ChatFormatting.DARK_GRAY));
            if (d.hasVoice()) {
                tip.add((Component)Component.literal((String)("Voice: " + TapeData.time(d.voiceSeconds() * 20))).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    public boolean isFoil(ItemStack s) {
        return false;
    }
}
