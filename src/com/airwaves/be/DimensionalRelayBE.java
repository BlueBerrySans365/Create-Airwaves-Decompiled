/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.airwaves.be;

import com.airwaves.Config;
import com.airwaves.ModRegistry;
import com.airwaves.ModSounds;
import com.airwaves.block.DimensionalRelayBlock;
import com.airwaves.compat.SableCompat;
import com.airwaves.logic.AntennaUtil;
import com.airwaves.logic.RadioNetwork;
import com.airwaves.power.PowerSupply;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class DimensionalRelayBE
extends KineticBlockEntity {
    public static final double STRESS = 16.0;
    public final PowerSupply power = new PowerSupply(() -> 16.0, () -> 120);
    public boolean active;
    public int sending;
    public int bringing;
    public List<String> linked = new ArrayList<String>();
    private boolean forced;

    public DimensionalRelayBE(BlockPos p, BlockState s) {
        super(ModRegistry.DIM_RELAY_BE.get(), p, s);
    }

    public BlockPos radioPos() {
        return SableCompat.globalPos(this.level, this.worldPosition);
    }

    public double antennaTop() {
        return SableCompat.global((Level)this.level, (BlockPos)this.worldPosition).y + 2.0;
    }

    public double range(boolean fm) {
        double f = this.power.source() == PowerSupply.Source.ELECTRIC ? this.power.electricFactor() : (!Config.rotation() ? 1.0 : 0.5 + 0.5 * Math.min(2.0, (double)(Math.abs(this.getSpeed()) / (float)((Integer)Config.FULL_RPM.get()).intValue())));
        return AntennaUtil.range(new AntennaUtil.Info(16, 2), fm) * 1.2 * f;
    }

    public double rxGain() {
        return AntennaUtil.rxGain(new AntennaUtil.Info(16, 2)) * 1.2;
    }

    public boolean rotationOk() {
        return Math.abs(this.getSpeed()) >= (float)((Integer)Config.MIN_RPM.get()).intValue() && !this.isOverStressed();
    }

    public void refresh() {
        BlockState st;
        if (this.level == null) {
            return;
        }
        if (Config.rotation()) {
            this.power.update(this.rotationOk(), this.level.getGameTime());
        }
        boolean was = this.active;
        boolean bl = this.active = (!Config.rotation() || this.power.running()) && !this.level.hasNeighborSignal(this.worldPosition);
        if (!this.active) {
            this.power.refund();
        }
        if ((st = this.getBlockState()).hasProperty((Property)DimensionalRelayBlock.ACTIVE) && (Boolean)st.getValue((Property)DimensionalRelayBlock.ACTIVE) != this.active) {
            this.level.setBlock(this.worldPosition, (BlockState)st.setValue((Property)DimensionalRelayBlock.ACTIVE, (Comparable)Boolean.valueOf(this.active)), 3);
            this.level.playSound(null, this.worldPosition, this.active ? (SoundEvent)ModSounds.POWER_ON.get() : (SoundEvent)ModSounds.POWER_OFF.get(), SoundSource.BLOCKS, 0.7f, 0.6f);
        }
        this.keepLoaded((Boolean)Config.DIM_RELAY_CHUNK.get());
        if (was != this.active || this.level.getGameTime() % 40L == 0L) {
            this.sendData();
        }
    }

    public void keepLoaded(boolean on) {
        ServerLevel sl;
        block3: {
            block2: {
                Level level = this.level;
                if (!(level instanceof ServerLevel)) break block2;
                sl = (ServerLevel)level;
                if (this.forced != on) break block3;
            }
            return;
        }
        ChunkPos c = new ChunkPos(this.worldPosition);
        sl.setChunkForced(c.x, c.z, on);
        this.forced = on;
        this.setChanged();
    }

    public void initialize() {
        super.initialize();
        if (this.level != null && !this.level.isClientSide) {
            RadioNetwork.register((BlockEntity)this);
        }
    }

    public void invalidate() {
        super.invalidate();
        if (this.level != null && !this.level.isClientSide) {
            RadioNetwork.unregister((BlockEntity)this);
        }
    }

    public boolean addToGoggleTooltip(List<Component> tip, boolean sneaking) {
        super.addToGoggleTooltip(tip, sneaking);
        tip.add((Component)Component.literal((String)"    Dimensional Relay").withStyle(ChatFormatting.WHITE));
        if (!this.active) {
            boolean red = this.level != null && this.level.hasNeighborSignal(this.worldPosition);
            tip.add((Component)Component.literal((String)(red ? "    off (redstone)" : "    needs power")).withStyle(ChatFormatting.GOLD));
        } else if (this.linked.isEmpty()) {
            tip.add((Component)Component.literal((String)"    no partner: build another one").withStyle(ChatFormatting.GOLD));
            tip.add((Component)Component.literal((String)"    in the Nether or the End").withStyle(ChatFormatting.GOLD));
        } else {
            tip.add((Component)Component.literal((String)("    linked to " + String.join((CharSequence)", ", this.linked))).withStyle(ChatFormatting.LIGHT_PURPLE));
            tip.add((Component)Component.literal((String)("    sending " + this.sending + " station" + (this.sending == 1 ? "" : "s") + ", bringing in " + this.bringing)).withStyle(ChatFormatting.GREEN));
        }
        if (Config.rotation()) {
            this.power.tooltip(tip, this.level == null || !this.level.hasNeighborSignal(this.worldPosition), this.isOverStressed(), Math.abs(this.getSpeed()));
        }
        return true;
    }

    protected void write(CompoundTag tag, HolderLookup.Provider r, boolean clientPacket) {
        super.write(tag, r, clientPacket);
        this.power.save(tag);
        tag.putBoolean("forced", this.forced);
        if (clientPacket) {
            tag.putBoolean("active", this.active);
            tag.putInt("sending", this.sending);
            tag.putInt("bringing", this.bringing);
            ListTag l = new ListTag();
            for (String s : this.linked) {
                l.add((Object)StringTag.valueOf((String)s));
            }
            tag.put("linked", (Tag)l);
        }
    }

    protected void read(CompoundTag tag, HolderLookup.Provider r, boolean clientPacket) {
        super.read(tag, r, clientPacket);
        this.power.load(tag);
        this.forced = tag.getBoolean("forced");
        if (clientPacket) {
            this.active = tag.getBoolean("active");
            this.sending = tag.getInt("sending");
            this.bringing = tag.getInt("bringing");
            this.linked = new ArrayList<String>();
            for (Tag t : tag.getList("linked", 8)) {
                this.linked.add(t.getAsString());
            }
        }
    }
}
