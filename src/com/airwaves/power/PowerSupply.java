/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.airwaves.power;

import com.airwaves.Config;
import com.airwaves.api.MachinePower;
import com.airwaves.be.CreativeTransmitterBE;
import com.airwaves.be.DimensionalRelayBE;
import com.airwaves.be.JammerBE;
import com.airwaves.be.KineticTunableBE;
import com.airwaves.structure.StructureBlock;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class PowerSupply
implements MachinePower {
    private static final float MIN_FRACTION = 0.25f;
    private static final double BANK_SECONDS = 2.0;
    private final DoubleSupplier stress;
    private final IntSupplier volts;
    private double stored;
    private double lastUsed;
    private Source source = Source.NONE;
    private float fraction;
    private long lastFed = -1000L;
    private boolean wired;

    public PowerSupply(DoubleSupplier stress, IntSupplier volts) {
        this.stress = stress;
        this.volts = volts;
    }

    public static int watts(double stress) {
        return Math.max(1, (int)Math.round(stress * (Double)Config.WATTS_PER_STRESS.get()));
    }

    @Override
    public int watts() {
        return PowerSupply.watts(this.stress.getAsDouble());
    }

    @Override
    public int ratedVolts() {
        return this.volts.getAsInt();
    }

    private double bank() {
        return (double)this.watts() * 2.0;
    }

    public Source source() {
        return this.source;
    }

    @Override
    public boolean running() {
        return this.source != Source.NONE;
    }

    @Override
    public boolean onRotation() {
        return this.source == Source.ROTATION;
    }

    @Override
    public boolean onElectricity() {
        return this.source == Source.ELECTRIC;
    }

    @Override
    public float fraction() {
        return this.fraction;
    }

    public boolean wired() {
        return this.wired;
    }

    @Override
    public double storedJoules() {
        return this.stored;
    }

    @Override
    public double wantWatts() {
        double room = this.bank() - this.stored;
        if (room <= 1.0E-6) {
            return 0.0;
        }
        return Math.min((double)this.watts() * 1.5, room * 20.0);
    }

    @Override
    public void feed(double joules, long gameTime) {
        if (joules > 0.0) {
            this.stored = Math.min(this.bank(), this.stored + joules);
        }
        this.lastFed = gameTime;
    }

    public boolean update(boolean rotationOk, long gameTime) {
        Source was = this.source;
        float wasF = this.fraction;
        boolean wasW = this.wired;
        this.wired = gameTime - this.lastFed < 40L;
        this.lastUsed = 0.0;
        if (rotationOk) {
            this.source = Source.ROTATION;
            this.fraction = 1.0f;
        } else if (Config.electricEnabled() && this.stored > 0.0) {
            double need = this.watts();
            double use = Math.min(this.stored, need);
            float f = (float)(use / need);
            if (f >= 0.25f) {
                this.stored -= use;
                this.lastUsed = use;
                this.source = Source.ELECTRIC;
                this.fraction = f;
            } else {
                this.source = Source.NONE;
                this.fraction = f;
            }
        } else {
            this.source = Source.NONE;
            this.fraction = 0.0f;
        }
        return was != this.source || wasW != this.wired || Math.abs(wasF - this.fraction) > 0.04f;
    }

    public void refund() {
        if (this.lastUsed > 0.0) {
            this.stored = Math.min(this.bank(), this.stored + this.lastUsed);
            this.lastUsed = 0.0;
        }
    }

    public double electricFactor() {
        return 0.5 + 0.5 * (double)Math.min(1.0f, this.fraction);
    }

    public void tooltip(List<Component> tip, boolean wantsPower, boolean overstressed, float rpm) {
        boolean elec = Config.electricEnabled();
        switch (this.source.ordinal()) {
            case 1: {
                tip.add((Component)Component.literal((String)("    \u27f3 rotation, " + Math.round(rpm) + " RPM")).withStyle(ChatFormatting.YELLOW));
                break;
            }
            case 2: {
                boolean full = this.fraction >= 0.99f;
                tip.add((Component)Component.literal((String)("    \u26a1 electric, " + this.watts() + " W" + (String)(full ? "" : " (weak: " + Math.round(this.fraction * 100.0f) + "%)"))).withStyle(full ? ChatFormatting.AQUA : ChatFormatting.GOLD));
                if (full) break;
                tip.add((Component)Component.literal((String)("    the grid needs " + this.ratedVolts() + " V")).withStyle(ChatFormatting.GRAY));
                break;
            }
            case 0: {
                if (!wantsPower) {
                    return;
                }
                if (overstressed) {
                    tip.add((Component)Component.literal((String)"    overstressed").withStyle(ChatFormatting.RED));
                } else {
                    tip.add((Component)Component.literal((String)(elec ? "    needs " + String.valueOf(Config.MIN_RPM.get()) + " RPM or " + this.watts() + " W at " + this.ratedVolts() + " V" : "    needs " + String.valueOf(Config.MIN_RPM.get()) + " RPM")).withStyle(ChatFormatting.GOLD));
                }
                if (!elec || !this.wired || !(this.fraction > 0.0f)) break;
                tip.add((Component)Component.literal((String)("    grid too weak: needs " + this.ratedVolts() + " V")).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public String summary() {
        return switch (this.source.ordinal()) {
            default -> throw new MatchException(null, null);
            case 1 -> "rotation";
            case 2 -> "electric " + this.watts() + " W" + (String)(this.fraction < 0.99f ? " (" + Math.round(this.fraction * 100.0f) + "%)" : "");
            case 0 -> "unpowered";
        };
    }

    public void save(CompoundTag tag) {
        tag.putDouble("bankJ", this.stored);
        tag.putByte("psrc", (byte)this.source.ordinal());
        tag.putFloat("pfrac", this.fraction);
        tag.putBoolean("pwired", this.wired);
    }

    public void load(CompoundTag tag) {
        this.stored = tag.getDouble("bankJ");
        byte s = tag.getByte("psrc");
        this.source = s >= 0 && s < Source.values().length ? Source.values()[s] : Source.NONE;
        this.fraction = tag.getFloat("pfrac");
        this.wired = tag.getBoolean("pwired");
    }

    public static PowerSupply at(Level lv, BlockPos pos) {
        BlockPos blockPos;
        BlockState st = lv.getBlockState(pos);
        Block block = st.getBlock();
        if (block instanceof StructureBlock) {
            StructureBlock sb = (StructureBlock)block;
            blockPos = sb.corePos(st, pos);
        } else {
            blockPos = pos;
        }
        BlockPos p = blockPos;
        BlockEntity be = lv.getBlockEntity(p);
        if (be instanceof KineticTunableBE) {
            KineticTunableBE k = (KineticTunableBE)be;
            if (!(be instanceof CreativeTransmitterBE)) {
                return k.power;
            }
        }
        if (be instanceof JammerBE) {
            JammerBE j = (JammerBE)be;
            return j.power;
        }
        if (be instanceof DimensionalRelayBE) {
            DimensionalRelayBE d = (DimensionalRelayBE)be;
            return d.power;
        }
        return null;
    }

    public static enum Source {
        NONE,
        ROTATION,
        ELECTRIC;

    }
}
