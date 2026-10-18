/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.Level
 */
package com.airwaves.item;

import com.airwaves.ModRegistry;
import com.airwaves.ModSounds;
import com.airwaves.Tuning;
import com.airwaves.item.Tunable;
import com.airwaves.logic.Emitter;
import com.airwaves.logic.RadioNetwork;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class PortableRadioItem
extends Item
implements Tunable {
    public PortableRadioItem(Item.Properties p) {
        super(p);
    }

    public static boolean isOn(ItemStack s) {
        return (Boolean)s.getOrDefault((DataComponentType)ModRegistry.POWERED.get(), (Object)false);
    }

    public InteractionResultHolder<ItemStack> use(Level lv, Player pl, InteractionHand h) {
        ItemStack st = pl.getItemInHand(h);
        if (lv instanceof ServerLevel) {
            ServerLevel sl = (ServerLevel)lv;
            if (pl instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer)pl;
                Tuning cur = (Tuning)st.getOrDefault((DataComponentType)ModRegistry.TUNING.get(), (Object)Tuning.DEFAULT);
                if (pl.isShiftKeyDown()) {
                    Emitter e = RadioNetwork.seek(sl, pl.blockPosition(), pl.getEyeY(), cur, 0.9);
                    st.set((DataComponentType)ModRegistry.POWERED.get(), (Object)true);
                    if (e != null) {
                        st.set((DataComponentType)ModRegistry.TUNING.get(), (Object)e.tuning());
                        lv.playSound(null, pl.blockPosition(), (SoundEvent)ModSounds.SEEK.get(), SoundSource.PLAYERS, 0.5f, 1.6f);
                    } else {
                        lv.playSound(null, pl.blockPosition(), (SoundEvent)ModSounds.SEEK.get(), SoundSource.PLAYERS, 0.5f, 0.7f);
                    }
                    float q = RadioNetwork.refreshHandheld(sp);
                    Tuning t = (Tuning)st.getOrDefault((DataComponentType)ModRegistry.TUNING.get(), (Object)Tuning.DEFAULT);
                    pl.displayClientMessage((Component)(e != null ? Component.literal((String)(t.display() + (String)(e.name() != null && !e.name().isEmpty() ? "  " + e.name() : ""))).withStyle(ChatFormatting.AQUA) : Component.literal((String)("No stations in range on " + (cur.fm() ? "FM" : "AM"))).withStyle(ChatFormatting.GRAY)), true);
                } else {
                    boolean on = !PortableRadioItem.isOn(st);
                    st.set((DataComponentType)ModRegistry.POWERED.get(), (Object)on);
                    lv.playSound(null, pl.blockPosition(), (SoundEvent)ModSounds.SWITCH.get(), SoundSource.PLAYERS, 0.6f, on ? 1.3f : 0.9f);
                    float q = RadioNetwork.refreshHandheld(sp);
                    if (on) {
                        pl.displayClientMessage((Component)Component.literal((String)(cur.display() + (q > 0.04f ? "" : "  (no signal - sneak + right-click to seek)"))).withStyle(q > 0.04f ? ChatFormatting.AQUA : ChatFormatting.GRAY), true);
                    }
                }
            }
        }
        return InteractionResultHolder.sidedSuccess((Object)st, (boolean)lv.isClientSide);
    }

    public boolean isFoil(ItemStack s) {
        return PortableRadioItem.isOn(s);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        tip.add((Component)Component.literal((String)((Tuning)stack.getOrDefault((DataComponentType)ModRegistry.TUNING.get(), (Object)Tuning.DEFAULT)).display()).withStyle(ChatFormatting.AQUA));
        tip.add((Component)Component.translatable((String)"tooltip.airwaves.portable_radio.use").withStyle(ChatFormatting.GRAY));
    }
}
