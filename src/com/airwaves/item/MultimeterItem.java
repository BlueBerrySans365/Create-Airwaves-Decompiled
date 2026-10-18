/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.airwaves.item;

import com.airwaves.Config;
import com.airwaves.ModSounds;
import com.airwaves.be.CableNode;
import com.airwaves.be.JammerBE;
import com.airwaves.be.KineticTunableBE;
import com.airwaves.be.RelayBE;
import com.airwaves.be.TransmitterBE;
import com.airwaves.be.TunableBE;
import com.airwaves.be.Tuned;
import com.airwaves.cable.Link;
import com.airwaves.item.CableItem;
import com.airwaves.power.PowerSupply;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MultimeterItem
extends Item {
    public MultimeterItem(Item.Properties p) {
        super(p);
    }

    public InteractionResult useOn(UseOnContext ctx) {
        CableNode n;
        PowerSupply ps;
        Level lv = ctx.getLevel();
        if (lv.isClientSide || ctx.getPlayer() == null) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = CableItem.nodePos(lv, ctx.getClickedPos());
        BlockEntity be = lv.getBlockEntity(pos);
        if (be == null) {
            return InteractionResult.PASS;
        }
        MutableComponent out = Component.empty();
        if (be instanceof KineticBlockEntity) {
            KineticBlockEntity k = (KineticBlockEntity)be;
            out.append((Component)Component.literal((String)("\u27f3 " + Math.round(Math.abs(k.getSpeed())) + " RPM" + (k.isOverStressed() ? " overstressed" : ""))).withStyle(k.isOverStressed() ? ChatFormatting.RED : ChatFormatting.YELLOW));
        }
        if (be instanceof KineticTunableBE) {
            KineticTunableBE kt = (KineticTunableBE)be;
            v0 = kt.power;
        } else if (be instanceof JammerBE) {
            JammerBE j = (JammerBE)be;
            v0 = j.power;
        } else {
            v0 = ps = null;
        }
        if (ps != null && Config.electricEnabled()) {
            out.append((Component)Component.literal((String)("   \u26a1 " + ps.summary() + (String)(ps.wired() ? ", bank " + Math.round(ps.storedJoules()) + " J" : ""))).withStyle(ps.source() == PowerSupply.Source.ELECTRIC ? ChatFormatting.AQUA : ChatFormatting.GRAY));
        }
        if (be instanceof CableNode && !(n = (CableNode)be).links().isEmpty()) {
            double loss = 0.0;
            for (Link l : n.links().list()) {
                loss = Math.max(loss, l.type().loss);
            }
            out.append((Component)Component.literal((String)("   \u2194 " + n.links().size() + " cable" + (n.links().size() == 1 ? "" : "s"))).withStyle(ChatFormatting.GOLD));
        }
        if (be instanceof Tuned) {
            Tuned t = (Tuned)be;
            out.append((Component)Component.literal((String)("   " + t.getTuning().display())).withStyle(ChatFormatting.AQUA));
            if (be instanceof TransmitterBE) {
                TransmitterBE tx = (TransmitterBE)be;
                out.append((Component)Component.literal((String)("   \u25b2 " + (int)tx.range + " m")).withStyle(tx.active ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
            } else if (be instanceof RelayBE) {
                RelayBE r = (RelayBE)be;
                out.append((Component)Component.literal((String)("   \u25b2 " + (int)r.range + " m")).withStyle(r.relaying ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
            } else if (be instanceof TunableBE) {
                TunableBE tb = (TunableBE)be;
                out.append((Component)Component.literal((String)("   \u25ae " + Math.round(tb.quality * 100.0f) + "%")).withStyle(tb.carrier ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
            }
        }
        if (out.getSiblings().isEmpty()) {
            return InteractionResult.PASS;
        }
        ctx.getPlayer().displayClientMessage((Component)out, true);
        lv.playSound(null, pos, (SoundEvent)ModSounds.PROBE.get(), SoundSource.PLAYERS, 0.3f, 2.0f);
        return InteractionResult.CONSUME;
    }
}
