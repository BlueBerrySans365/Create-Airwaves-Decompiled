/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.be;

import com.airwaves.ModRegistry;
import com.airwaves.be.AudioSink;
import com.airwaves.be.CableNode;
import com.airwaves.be.NodeBE;
import com.airwaves.logic.ClientSinks;
import com.airwaves.logic.RadioNetwork;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class SpeakerBE
extends NodeBE
implements AudioSink {
    public boolean on;
    public boolean carrier;
    public boolean fm;
    public float quality;

    public SpeakerBE(BlockPos p, BlockState s) {
        super(ModRegistry.SPEAKER_BE.get(), p, s);
    }

    @Override
    public int maxLinks(int slot) {
        return 2;
    }

    @Override
    public Vec3 anchor(int slot) {
        return CableNode.facing(this, 8.0, 2.0, 14.0);
    }

    public void onLoad() {
        super.onLoad();
        if (this.level == null) {
            return;
        }
        if (this.level.isClientSide) {
            ClientSinks.SINKS.add(this);
        } else {
            RadioNetwork.register(this);
        }
    }

    public void setRemoved() {
        super.setRemoved();
        ClientSinks.removed(this, this);
        if (this.level != null && !this.level.isClientSide) {
            RadioNetwork.unregister(this);
        }
    }

    public void setState(boolean on, boolean carrier, float q, boolean fm) {
        boolean ch = on != this.on || carrier != this.carrier || fm != this.fm || Math.round(q * 50.0f) != Math.round(this.quality * 50.0f);
        this.on = on;
        this.carrier = carrier;
        this.quality = q;
        this.fm = fm;
        if (ch) {
            this.sync();
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tip, boolean sneaking) {
        super.addToGoggleTooltip(tip, sneaking);
        tip.add((Component)Component.literal((String)(!this.on ? "    not driven by a receiver" : (this.carrier ? "    playing, " + Math.round(this.quality * 100.0f) + "% signal" : "    receiver has no station"))).withStyle(this.on && this.carrier ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
        return true;
    }

    @Override
    public BlockPos sinkPos() {
        return this.worldPosition;
    }

    @Override
    public boolean sinkOn() {
        return this.on;
    }

    @Override
    public boolean carrier() {
        return this.carrier;
    }

    @Override
    public float quality() {
        return this.quality;
    }

    @Override
    public boolean fm() {
        return this.fm;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider r) {
        super.saveAdditional(tag, r);
        tag.putBoolean("on", this.on);
        tag.putBoolean("carrier", this.carrier);
        tag.putBoolean("fm", this.fm);
        tag.putFloat("q", this.quality);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider r) {
        super.loadAdditional(tag, r);
        this.on = tag.getBoolean("on");
        this.carrier = tag.getBoolean("carrier");
        this.fm = tag.getBoolean("fm");
        this.quality = tag.getFloat("q");
    }
}
