/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 */
package com.airwaves.structure;

import com.airwaves.ModRegistry;
import com.airwaves.Tuning;
import com.airwaves.item.Tunable;
import com.airwaves.structure.StructureBlock;
import com.airwaves.structure.StructureItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public static class StructureItem.Tuned
extends StructureItem
implements Tunable {
    public StructureItem.Tuned(StructureBlock b, Item.Properties p) {
        super(b, p);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        tip.add((Component)Component.literal((String)((Tuning)stack.getOrDefault((DataComponentType)ModRegistry.TUNING.get(), (Object)Tuning.DEFAULT)).display()).withStyle(ChatFormatting.AQUA));
    }
}
