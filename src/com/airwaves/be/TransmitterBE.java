/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.be;

import com.airwaves.ModRegistry;
import com.airwaves.ModSounds;
import com.airwaves.be.CableNode;
import com.airwaves.be.KineticTunableBE;
import com.airwaves.be.LinkSet;
import com.airwaves.be.Tuned;
import com.airwaves.block.TransmitterBlock;
import com.airwaves.logic.AntennaUtil;
import com.airwaves.logic.WeatherUtil;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class TransmitterBE
extends KineticTunableBE
implements CableNode {
    private static final double[][] JACK = new double[][]{{5.0, 3.0, 3.0}, {4.0, 3.0, 2.0}, {3.0, 2.0, 0.0}, {4.0, 3.0, 0.0}};
    public static final double[] STRESS = new double[]{4.0, 8.0, 16.0, 32.0};
    public boolean active;
    public boolean standby;
    public boolean wired;
    public int antennaHeight;
    public Direction beam;
    public double range;
    public float baseQ;
    protected int crystal;
    protected long lastAir;
    public long consoleHold = -1000L;
    protected String problem = "";
    protected final LinkSet links = new LinkSet();
    private static final int[] VOLTS = new int[]{12, 24, 120, 120};

    public TransmitterBE(BlockPos p, BlockState s) {
        this(ModRegistry.TRANSMITTER_BE.get(), p, s);
    }

    protected TransmitterBE(BlockEntityType<?> type, BlockPos p, BlockState s) {
        super(type, p, s);
    }

    public int tier() {
        int n;
        Block block = this.getBlockState().getBlock();
        if (block instanceof TransmitterBlock) {
            TransmitterBlock b = (TransmitterBlock)block;
            n = b.tier.level;
        } else {
            n = 1;
        }
        return n;
    }

    @Override
    public double stressImpact() {
        return STRESS[Math.max(0, Math.min(STRESS.length - 1, this.tier() - 1))];
    }

    @Override
    protected boolean usesPower() {
        return this.active;
    }

    @Override
    public int ratedVolts() {
        return VOLTS[Math.max(0, Math.min(VOLTS.length - 1, this.tier() - 1))];
    }

    @Override
    public LinkSet links() {
        return this.links;
    }

    @Override
    public BlockPos nodePos() {
        return this.worldPosition;
    }

    @Override
    public int maxLinks(int slot) {
        return 4;
    }

    @Override
    public Vec3 anchor(int slot) {
        return CableNode.facing((BlockEntity)this, JACK[this.tier() - 1][0], JACK[this.tier() - 1][1], JACK[this.tier() - 1][2]);
    }

    @Override
    public void cablesChanged() {
        this.sync();
    }

    @Override
    public double antennaTop() {
        return super.antennaTop() + (double)this.antennaHeight;
    }

    @Override
    public void refresh() {
        BlockState ns;
        boolean onAir;
        if (this.level == null) {
            return;
        }
        BlockState st = this.getBlockState();
        if (!(st.getBlock() instanceof TransmitterBlock)) {
            return;
        }
        Direction f = (Direction)st.getValue(TransmitterBlock.HORIZONTAL_FACING);
        BlockPos back = this.worldPosition.relative(f.getOpposite());
        this.crystal = (Integer)st.getValue((Property)TransmitterBlock.CRYSTAL);
        boolean ground = this.level.isLoaded(back) && this.level.getBlockState(back).is(ModRegistry.GROUND_PLANE);
        AntennaUtil.Info a = ground ? AntennaUtil.scan(this.level, back.above(), this.tier()) : AntennaUtil.Info.NONE;
        boolean match = a.tier() == this.tier();
        this.antennaHeight = match ? a.height() : 0;
        this.beam = match ? a.beam() : null;
        boolean held = this.level.getGameTime() - this.consoleHold < 40L;
        boolean bl = this.standby = this.level.hasNeighborSignal(this.worldPosition) || held;
        this.problem = this.crystal == 0 ? "no amethyst in the chamber" : (!ground ? "needs a copper block behind it" : (!match ? "needs a " + AntennaUtil.tierName(this.tier()).toLowerCase() + " antenna on the copper" : (this.antennaHeight < 4 ? "antenna too short (" + this.antennaHeight + "/4)" : (this.standby ? (held ? "standby (radio console)" : "standby (redstone)") : (!this.turning() ? "needs power" : "")))));
        this.active = this.problem.isEmpty();
        this.range = this.active ? (a.creative() ? a.fixedRange() : AntennaUtil.range(a, this.tuning.fm()) * this.powerFactor() * this.altitudeFactor()) : 0.0;
        this.baseQ = Math.min(1.0f, AntennaUtil.quality(a) * (this.crystal == 2 ? 1.1f : 1.0f) * (float)Math.sqrt(WeatherUtil.factor(this.level, this.radioPos().above(1 + this.antennaHeight), this.tuning.fm())));
        boolean bl2 = onAir = this.active && this.level.getGameTime() - this.lastAir < 40L;
        if (this.active != (Boolean)st.getValue((Property)TransmitterBlock.ACTIVE)) {
            this.level.playSound(null, this.worldPosition, this.active ? (SoundEvent)ModSounds.POWER_ON.get() : (SoundEvent)ModSounds.POWER_OFF.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
        }
        if ((ns = (BlockState)((BlockState)st.setValue((Property)TransmitterBlock.ACTIVE, (Comparable)Boolean.valueOf(this.active))).setValue((Property)TransmitterBlock.ON_AIR, (Comparable)Boolean.valueOf(onAir))) != st) {
            this.level.setBlock(this.worldPosition, ns, 3);
        }
        this.sync();
    }

    public void onAir(ServerLevel lv) {
        this.lastAir = lv.getGameTime();
        BlockState st = this.getBlockState();
        if (st.getBlock() instanceof TransmitterBlock && !((Boolean)st.getValue((Property)TransmitterBlock.ON_AIR)).booleanValue()) {
            lv.setBlock(this.worldPosition, (BlockState)st.setValue((Property)TransmitterBlock.ON_AIR, (Comparable)Boolean.valueOf(true)), 3);
        }
    }

    public boolean addToGoggleTooltip(List<Component> tip, boolean sneaking) {
        String pr;
        super.addToGoggleTooltip(tip, sneaking);
        tip.add((Component)Component.literal((String)"    Transmitter").withStyle(ChatFormatting.WHITE));
        int cr = this.getBlockState().getBlock() instanceof TransmitterBlock ? (Integer)this.getBlockState().getValue((Property)TransmitterBlock.CRYSTAL) : this.crystal;
        tip.add(Tuned.chamberLine(cr));
        this.radioTooltip(tip, this.active, this.range);
        String string = cr > 0 && this.problem.startsWith("no amethyst") ? "" : (pr = cr == 0 ? "no amethyst in the chamber" : this.problem);
        if (!(pr.isEmpty() || pr.startsWith("needs power") || pr.startsWith("needs rotation"))) {
            tip.add((Component)Component.literal((String)("    " + pr)).withStyle(ChatFormatting.GOLD));
        }
        if (this.active) {
            tip.add((Component)Component.literal((String)("    antenna " + this.antennaHeight + " tall, " + (this.wired ? "microphone cabled" : "built-in mic"))).withStyle(ChatFormatting.GRAY));
        }
        if (this.active && this.beam != null) {
            tip.add((Component)Component.literal((String)("    Yagi beam to the " + this.beam.getName() + ": x1.9 ahead, nothing to the sides or behind")).withStyle(ChatFormatting.AQUA));
        }
        return true;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider r, boolean clientPacket) {
        super.write(tag, r, clientPacket);
        this.links.save(tag);
        tag.putBoolean("active", this.active);
        tag.putDouble("range", this.range);
        tag.putString("problem", this.problem);
        tag.putInt("ant", this.antennaHeight);
        tag.putBoolean("wired", this.wired);
        tag.putInt("beam", this.beam == null ? -1 : this.beam.get2DDataValue());
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider r, boolean clientPacket) {
        super.read(tag, r, clientPacket);
        this.links.load(tag);
        if (clientPacket) {
            this.active = tag.getBoolean("active");
            this.range = tag.getDouble("range");
            this.problem = tag.getString("problem");
            this.antennaHeight = tag.getInt("ant");
            this.wired = tag.getBoolean("wired");
            int bm = tag.contains("beam") ? tag.getInt("beam") : -1;
            this.beam = bm < 0 ? null : Direction.from2DDataValue((int)bm);
        }
    }
}
