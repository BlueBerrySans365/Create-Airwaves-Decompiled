/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.component.DataComponentMap$Builder
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntity$DataComponentInput
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.airwaves.be;

import com.airwaves.ModRegistry;
import com.airwaves.Tuning;
import com.airwaves.be.Tuned;
import com.airwaves.compat.SableCompat;
import com.airwaves.logic.Emitter;
import com.airwaves.logic.RadioNetwork;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TunableBE
extends BlockEntity
implements Tuned,
IHaveGoggleInformation {
    public Tuning tuning = Tuning.DEFAULT;
    public float quality;
    public boolean carrier;
    public long bestRoot;
    public Component stationName;

    protected TunableBE(BlockEntityType<?> t, BlockPos p, BlockState s) {
        super(t, p, s);
    }

    @Override
    public Tuning getTuning() {
        return this.tuning;
    }

    @Override
    public void setTuning(Tuning t) {
        this.tuning = t;
    }

    @Override
    public Component getStationName() {
        return this.stationName;
    }

    public void onLoad() {
        super.onLoad();
        if (this.level != null && !this.level.isClientSide) {
            RadioNetwork.register(this);
        }
    }

    public void setRemoved() {
        super.setRemoved();
        if (this.level != null && !this.level.isClientSide) {
            RadioNetwork.unregister(this);
        }
    }

    @Override
    public void sync() {
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public String stationLabel() {
        return this.stationName == null ? "" : this.stationName.getString();
    }

    public boolean addToGoggleTooltip(List<Component> tip, boolean sneaking) {
        tip.add((Component)Component.literal((String)"    ").append((Component)this.getBlockState().getBlock().getName()).withStyle(ChatFormatting.WHITE));
        tip.add((Component)Component.literal((String)("    " + this.tuning.display() + (String)(this.stationName != null ? "  " + this.stationName.getString() : ""))).withStyle(ChatFormatting.AQUA));
        int bars = this.carrier ? 1 + Math.round(this.quality * 4.0f) : 0;
        tip.add((Component)Component.literal((String)("    " + "\u25ae".repeat(bars) + "\u25af".repeat(5 - bars) + "  " + (String)(this.carrier ? Math.round(this.quality * 100.0f) + "% signal" : "no signal"))).withStyle(this.carrier ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
        return true;
    }

    public void refresh() {
    }

    public double rxGain() {
        return 1.0;
    }

    public Float rxBeam() {
        return null;
    }

    public double antennaTop() {
        return SableCompat.global((Level)this.level, (BlockPos)this.worldPosition).y + 0.5;
    }

    protected void afterSignal() {
    }

    public void updateSignal(List<Emitter> emitters, List<BlockPos> jam) {
        RadioNetwork.Best b = RadioNetwork.best(this.level, SableCompat.globalPos(this.level, this.worldPosition), this.antennaTop(), this.tuning, this.rxGain(), emitters, jam, this.rxBeam());
        float best = b.q();
        boolean c = best > 0.04f;
        boolean changed = c != this.carrier || Math.round(best * 50.0f) != Math.round(this.quality * 50.0f);
        this.bestRoot = b.root();
        this.quality = best;
        this.carrier = c;
        if (changed) {
            this.sync();
        }
        this.afterSignal();
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider r) {
        super.saveAdditional(tag, r);
        tag.putBoolean("fm", this.tuning.fm());
        tag.putDouble("freq", this.tuning.freq());
        tag.putFloat("q", this.quality);
        tag.putBoolean("carrier", this.carrier);
        if (this.stationName != null) {
            tag.putString("station", Component.Serializer.toJson((Component)this.stationName, (HolderLookup.Provider)r));
        }
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider r) {
        super.loadAdditional(tag, r);
        if (tag.contains("freq")) {
            this.tuning = new Tuning(tag.getBoolean("fm"), tag.getDouble("freq")).clamped();
        }
        this.quality = tag.getFloat("q");
        this.carrier = tag.getBoolean("carrier");
        this.stationName = tag.contains("station") ? Component.Serializer.fromJson((String)tag.getString("station"), (HolderLookup.Provider)r) : null;
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider r) {
        return this.saveWithoutMetadata(r);
    }

    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create((BlockEntity)this);
    }

    protected void applyImplicitComponents(BlockEntity.DataComponentInput in) {
        Component n;
        Tuning t = (Tuning)in.get((DataComponentType)ModRegistry.TUNING.get());
        if (t != null) {
            this.tuning = t.clamped();
        }
        if ((n = (Component)in.get(DataComponents.CUSTOM_NAME)) != null) {
            this.stationName = n;
        }
    }

    protected void collectImplicitComponents(DataComponentMap.Builder b) {
        b.set((DataComponentType)ModRegistry.TUNING.get(), (Object)this.tuning);
        if (this.stationName != null) {
            b.set(DataComponents.CUSTOM_NAME, (Object)this.stationName);
        }
    }

    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("fm");
        tag.remove("freq");
        tag.remove("station");
    }
}
