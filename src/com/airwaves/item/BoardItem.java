/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 */
package com.airwaves.item;

import com.airwaves.ModRegistry;
import com.airwaves.logic.BoardAnalysis;
import com.airwaves.logic.BoardDesign;
import com.airwaves.logic.BoardLayout;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class BoardItem
extends Item {
    public final BoardDesign design;

    public BoardItem(BoardDesign design, Item.Properties p) {
        super(p);
        this.design = design;
    }

    public static BoardLayout layout(ItemStack st) {
        BoardLayout boardLayout;
        BoardLayout l = (BoardLayout)st.get((DataComponentType)ModRegistry.BOARD_LAYOUT.get());
        if (l != null) {
            return l;
        }
        Item item = st.getItem();
        if (item instanceof BoardItem) {
            BoardItem b = (BoardItem)item;
            boardLayout = b.design.exampleLayout();
        } else {
            boardLayout = BoardDesign.LOGIC.exampleLayout();
        }
        return boardLayout;
    }

    public void appendHoverText(ItemStack st, Item.TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        BoardAnalysis.Result r = BoardItem.layout(st).analyse();
        if (!r.works()) {
            return;
        }
        MutableComponent stages = Component.empty();
        boolean firstOne = true;
        for (Map.Entry<BoardAnalysis.Stage, Integer> e : r.counts().entrySet()) {
            if (!firstOne) {
                stages.append((Component)Component.literal((String)", "));
            }
            stages.append((Component)Component.literal((String)(String.valueOf(e.getValue()) + " "))).append((Component)Component.translatable((String)e.getKey().key()));
            firstOne = false;
        }
        if (!firstOne) {
            tip.add((Component)stages.withStyle(ChatFormatting.GRAY));
        }
        tip.add((Component)Component.translatable((String)"tooltip.airwaves.board.program", (Object[])new Object[]{BoardAnalysis.describe(BoardAnalysis.program(r))}).withStyle(ChatFormatting.DARK_AQUA));
        tip.add((Component)Component.translatable((String)("tooltip.airwaves.board." + this.design.id)).withStyle(ChatFormatting.DARK_GRAY));
    }
}
