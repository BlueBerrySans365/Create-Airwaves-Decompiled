/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.airwaves.be;

import com.airwaves.ModRegistry;
import com.airwaves.be.TransmitterBE;
import com.airwaves.block.TransmitterBlock;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class CreativeTransmitterBE
extends TransmitterBE {
    public CreativeTransmitterBE(BlockPos p, BlockState s) {
        super(ModRegistry.CREATIVE_TX_BE.get(), p, s);
    }

    @Override
    public void refresh() {
        if (this.level == null) {
            return;
        }
        BlockState st = this.getBlockState();
        if (!(st.getBlock() instanceof TransmitterBlock)) {
            return;
        }
        boolean held = this.level.getGameTime() - this.consoleHold < 40L;
        this.standby = this.level.hasNeighborSignal(this.worldPosition) || held;
        boolean bl = this.active = !this.standby;
        this.problem = this.standby ? (held ? "standby (radio console)" : "standby (redstone)") : "";
        this.range = this.active ? 1.0E8 : 0.0;
        this.baseQ = 1.0f;
        this.crystal = 2;
        this.antennaHeight = 0;
        boolean onAir = this.active && this.level.getGameTime() - this.lastAir < 40L;
        BlockState ns = (BlockState)((BlockState)st.setValue((Property)TransmitterBlock.ACTIVE, (Comparable)Boolean.valueOf(this.active))).setValue((Property)TransmitterBlock.ON_AIR, (Comparable)Boolean.valueOf(onAir));
        if (ns != st) {
            this.level.setBlock(this.worldPosition, ns, 3);
        }
        this.sync();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tip, boolean sneaking) {
        tip.add((Component)Component.literal((String)"    Creative Transmitter").withStyle(ChatFormatting.LIGHT_PURPLE));
        tip.add((Component)Component.literal((String)("    " + this.tuning.display() + (String)(this.stationName != null ? "  " + this.stationName.getString() : ""))).withStyle(ChatFormatting.AQUA));
        tip.add((Component)Component.literal((String)(this.active ? "    on the air, heard everywhere" : "    " + this.problem)).withStyle(this.active ? ChatFormatting.GREEN : ChatFormatting.GOLD));
        tip.add((Component)Component.literal((String)("    " + (this.wired ? "microphone cabled" : "built-in mic"))).withStyle(ChatFormatting.GRAY));
        return true;
    }
}
