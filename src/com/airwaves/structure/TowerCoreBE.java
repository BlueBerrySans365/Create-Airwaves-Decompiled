/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.structure;

import com.airwaves.ModRegistry;
import com.airwaves.ModSounds;
import com.airwaves.be.TransmitterBE;
import com.airwaves.cable.CableType;
import com.airwaves.logic.AntennaUtil;
import com.airwaves.logic.WeatherUtil;
import com.airwaves.structure.StructureNodes;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class TowerCoreBE
extends TransmitterBE {
    public static final IntegerProperty CRYSTAL = IntegerProperty.create((String)"crystal", (int)0, (int)2);
    public static final BooleanProperty ACTIVE = BooleanProperty.create((String)"active");
    public static final int HEIGHT = 27;
    public static final double STRESS = 64.0;

    public TowerCoreBE(BlockPos p, BlockState s) {
        super(ModRegistry.TOWER_BE.get(), p, s);
    }

    @Override
    public int tier() {
        return 4;
    }

    @Override
    public double stressImpact() {
        return 64.0;
    }

    @Override
    public int ratedVolts() {
        return 240;
    }

    @Override
    public boolean accepts(int slot, CableType t) {
        return true;
    }

    @Override
    public Vec3 anchor(int slot) {
        return StructureNodes.anchor((BlockEntity)this, slot);
    }

    @Override
    public double antennaTop() {
        return this.radioPos().getY() + 27;
    }

    @Override
    public void refresh() {
        if (this.level == null) {
            return;
        }
        BlockState st = this.getBlockState();
        if (!st.hasProperty((Property)CRYSTAL)) {
            return;
        }
        this.crystal = (Integer)st.getValue((Property)CRYSTAL);
        this.standby = this.level.hasNeighborSignal(this.worldPosition);
        this.antennaHeight = 27;
        this.problem = this.crystal == 0 ? "no amethyst in the chamber" : (this.standby ? "standby (redstone)" : (!this.turning() ? "needs power (a shaft on the drive shaft at the base edge, or electricity)" : ""));
        this.active = this.problem.isEmpty();
        AntennaUtil.Info a = new AntennaUtil.Info(96, 4);
        this.range = this.active ? AntennaUtil.range(a, this.tuning.fm()) * 1.4 * this.powerFactor() * this.altitudeFactor() : 0.0;
        this.baseQ = Math.min(1.0f, (AntennaUtil.quality(a) + 0.08f) * (this.crystal == 2 ? 1.1f : 1.0f) * (float)Math.sqrt(WeatherUtil.factor(this.level, this.radioPos().above(27), this.tuning.fm())));
        if ((Boolean)st.getValue((Property)ACTIVE) != this.active) {
            this.level.playSound(null, this.worldPosition, this.active ? (SoundEvent)ModSounds.POWER_ON.get() : (SoundEvent)ModSounds.POWER_OFF.get(), SoundSource.BLOCKS, 1.0f, 0.8f);
            this.level.setBlock(this.worldPosition, (BlockState)st.setValue((Property)ACTIVE, (Comparable)Boolean.valueOf(this.active)), 3);
        }
        this.sync();
    }

    @Override
    public void onAir(ServerLevel lv) {
        this.lastAir = lv.getGameTime();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tip, boolean sneaking) {
        boolean r = super.addToGoggleTooltip(tip, sneaking);
        for (int i = 0; i < tip.size(); ++i) {
            if (!tip.get(i).getString().trim().equals("Transmitter")) continue;
            tip.set(i, (Component)Component.literal((String)"    Radio Tower").withStyle(ChatFormatting.WHITE));
        }
        return r;
    }
}
