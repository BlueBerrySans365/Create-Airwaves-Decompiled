/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.Style
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
 */
package com.airwaves.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class ShiftTooltips {
    private static final int WIDTH = 38;
    private static final int HL = 15653496;
    private static final int TEXT = 13079883;
    private static final int DIM = 0x9B9B9B;

    private ShiftTooltips() {
    }

    public static void onTooltip(ItemTooltipEvent e) {
        ItemStack st = e.getItemStack();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey((Object)st.getItem());
        if (!"airwaves".equals(id.getNamespace())) {
            return;
        }
        String base = st.getDescriptionId() + ".tooltip";
        if (!I18n.exists((String)(base + ".summary"))) {
            return;
        }
        List tip = e.getToolTip();
        ArrayList<Object> add = new ArrayList<Object>();
        boolean shift = Screen.hasShiftDown();
        add.add(Component.literal((String)"Hold [").withStyle(ChatFormatting.DARK_GRAY).append((Component)Component.literal((String)"Shift").withStyle(shift ? ChatFormatting.WHITE : ChatFormatting.GRAY)).append((Component)Component.literal((String)"] for Summary").withStyle(ChatFormatting.DARK_GRAY)));
        if (shift) {
            add.add(Component.empty());
            add.addAll(ShiftTooltips.wrap(I18n.get((String)(base + ".summary"), (Object[])new Object[0]), 13079883, 0));
            for (int i = 1; i <= 3 && I18n.exists((String)(base + ".condition" + i)); ++i) {
                add.add(Component.empty());
                add.add(Component.literal((String)I18n.get((String)(base + ".condition" + i), (Object[])new Object[0])).withStyle(Style.EMPTY.withColor(0x9B9B9B)));
                if (!I18n.exists((String)(base + ".behaviour" + i))) continue;
                add.addAll(ShiftTooltips.wrap(I18n.get((String)(base + ".behaviour" + i), (Object[])new Object[0]), 13079883, 1));
            }
        }
        tip.addAll(Math.min(1, tip.size()), add);
    }

    static List<Component> wrap(String text, int color, int indent) {
        ArrayList<Component> out = new ArrayList<Component>();
        String pad = " ".repeat(indent);
        StringBuilder line = new StringBuilder();
        ArrayList<String> lines = new ArrayList<String>();
        for (String w : text.split(" ")) {
            int vis = w.replace("_", "").length();
            if (line.length() > 0 && line.toString().replace("_", "").length() + 1 + vis > 38 - indent) {
                lines.add(line.toString());
                line.setLength(0);
            }
            if (line.length() > 0) {
                line.append(' ');
            }
            line.append(w);
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        boolean hl = false;
        for (String l : lines) {
            MutableComponent c = Component.literal((String)pad);
            StringBuilder seg = new StringBuilder();
            for (char ch : l.toCharArray()) {
                if (ch == '_') {
                    if (seg.length() > 0) {
                        c.append((Component)Component.literal((String)seg.toString()).withStyle(Style.EMPTY.withColor(hl ? 15653496 : color)));
                    }
                    seg.setLength(0);
                    hl = !hl;
                    continue;
                }
                seg.append(ch);
            }
            if (seg.length() > 0) {
                c.append((Component)Component.literal((String)seg.toString()).withStyle(Style.EMPTY.withColor(hl ? 15653496 : color)));
            }
            out.add((Component)c);
        }
        return out;
    }
}
