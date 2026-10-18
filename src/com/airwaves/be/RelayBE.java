/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.be;

import com.airwaves.ModRegistry;
import com.airwaves.be.CableNode;
import com.airwaves.be.KineticTunableBE;
import com.airwaves.be.LinkSet;
import com.airwaves.logic.AntennaUtil;
import com.airwaves.structure.StructureBlock;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class RelayBE
extends KineticTunableBE
implements CableNode {
    public static final double STRESS_SMALL = 12.0;
    public static final double STRESS_BIG = 24.0;
    public boolean active;
    public boolean relaying;
    public double range;
    public float antQ;
    private final LinkSet links = new LinkSet();

    public RelayBE(BlockPos p, BlockState s) {
        super(ModRegistry.RELAY_BE.get(), p, s);
    }

    public boolean big() {
        return this.getBlockState().getBlock() == ModRegistry.RELAY_TOWER_BIG.get();
    }

    public int height() {
        return this.big() ? 28 : 16;
    }

    public int tier() {
        return this.big() ? 3 : 2;
    }

    @Override
    public double stressImpact() {
        return this.big() ? 24.0 : 12.0;
    }

    @Override
    protected boolean usesPower() {
        return this.active;
    }

    @Override
    public int ratedVolts() {
        return this.big() ? 240 : 120;
    }

    @Override
    public void refresh() {
        if (this.level == null) {
            return;
        }
        AntennaUtil.Info info = new AntennaUtil.Info(this.height(), this.tier());
        boolean bl = this.active = this.turning() && !this.level.hasNeighborSignal(this.worldPosition);
        this.range = this.active ? AntennaUtil.range(info, this.tuning.fm()) * (this.big() ? 1.8 : 1.5) * this.powerFactor() * this.altitudeFactor() : 0.0;
        this.antQ = Math.min(1.0f, AntennaUtil.quality(info) + 0.1f);
        this.sync();
    }

    @Override
    public double rxGain() {
        return AntennaUtil.rxGain(new AntennaUtil.Info(this.height(), this.tier())) * 1.2;
    }

    @Override
    public double antennaTop() {
        return this.radioPos().getY() + this.height();
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
        Block block = this.getBlockState().getBlock();
        if (block instanceof StructureBlock) {
            StructureBlock b = (StructureBlock)block;
            return b.def.connector(this.worldPosition, 0, (Direction.Axis)this.getBlockState().getValue(StructureBlock.AXIS));
        }
        return Vec3.atCenterOf((Vec3i)this.worldPosition);
    }

    @Override
    public void cablesChanged() {
        this.sync();
    }

    public boolean addToGoggleTooltip(List<Component> tip, boolean sneaking) {
        super.addToGoggleTooltip(tip, sneaking);
        tip.add((Component)Component.literal((String)("    " + (this.big() ? "Big Relay Tower" : "Relay Tower"))).withStyle(ChatFormatting.WHITE));
        this.radioTooltip(tip, this.active, this.range);
        if (this.active) {
            tip.add((Component)Component.literal((String)(this.relaying ? "    relaying a station" : "    listening, nothing to relay")).withStyle(this.relaying ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        }
        return true;
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider r, boolean clientPacket) {
        super.write(tag, r, clientPacket);
        this.links.save(tag);
        tag.putBoolean("active", this.active);
        tag.putBoolean("relaying", this.relaying);
        tag.putDouble("range", this.range);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider r, boolean clientPacket) {
        super.read(tag, r, clientPacket);
        this.links.load(tag);
        if (clientPacket) {
            this.active = tag.getBoolean("active");
            this.relaying = tag.getBoolean("relaying");
            this.range = tag.getDouble("range");
        }
    }
}
