/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.airwaves.be;

import com.airwaves.ModRegistry;
import com.airwaves.be.ReceiverBE;
import com.airwaves.block.ReceiverBlock;
import com.airwaves.compat.SableCompat;
import com.airwaves.logic.Emitter;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class CreativeReceiverBE
extends ReceiverBE {
    public CreativeReceiverBE(BlockPos p, BlockState s) {
        super(ModRegistry.CREATIVE_RX_BE.get(), p, s);
    }

    @Override
    public void refresh() {
        BlockState st;
        if (this.level == null) {
            return;
        }
        boolean was = this.ok;
        this.ok = true;
        this.crystal = 2;
        this.grounded = false;
        this.antennaHeight = 0;
        if (!was) {
            this.sync();
        }
        if ((st = this.getBlockState()).getBlock() instanceof ReceiverBlock && !((Boolean)st.getValue((Property)ReceiverBlock.ACTIVE)).booleanValue()) {
            this.level.setBlock(this.worldPosition, (BlockState)st.setValue((Property)ReceiverBlock.ACTIVE, (Comparable)Boolean.valueOf(true)), 3);
        }
    }

    @Override
    public void updateSignal(List<Emitter> emitters, List<BlockPos> jam) {
        BlockPos me = SableCompat.globalPos(this.level, this.worldPosition);
        Emitter pick = null;
        double bd = Double.MAX_VALUE;
        for (Emitter e : emitters) {
            double d;
            if (!e.tuning().matches(this.tuning) || !((d = me.distSqr((Vec3i)e.pos())) < bd)) continue;
            bd = d;
            pick = e;
        }
        boolean c = pick != null;
        boolean changed = c != this.carrier || c && this.quality < 1.0f || c && this.bestRoot != pick.root();
        this.carrier = c;
        this.quality = c ? 1.0f : 0.0f;
        long l = this.bestRoot = c ? pick.root() : 0L;
        if (changed) {
            this.sync();
        }
        this.afterSignal();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tip, boolean sneaking) {
        tip.add((Component)Component.literal((String)"    Creative Receiver").withStyle(ChatFormatting.LIGHT_PURPLE));
        tip.add((Component)Component.literal((String)("    " + this.tuning.display() + (String)(this.stationName != null ? "  " + this.stationName.getString() : ""))).withStyle(ChatFormatting.AQUA));
        tip.add((Component)Component.literal((String)(this.carrier ? "    \u25ae\u25ae\u25ae\u25ae\u25ae  100% signal" : "    no station on this frequency")).withStyle(this.carrier ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
        tip.add((Component)Component.literal((String)("    " + (String)(this.links().isEmpty() ? "built-in speaker" : this.links().size() + " cable" + (this.links().size() > 1 ? "s" : "")))).withStyle(ChatFormatting.GRAY));
        return true;
    }
}
