/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 */
package com.airwaves.be;

import com.airwaves.be.CableNode;
import com.airwaves.be.LinkSet;
import com.airwaves.cable.CableType;
import com.airwaves.cable.Link;
import com.airwaves.logic.Cables;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public abstract class NodeBE
extends BlockEntity
implements CableNode,
IHaveGoggleInformation {
    protected final LinkSet links = new LinkSet();
    public long changedAt;

    protected NodeBE(BlockEntityType<?> t, BlockPos p, BlockState s) {
        super(t, p, s);
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
    public void cablesChanged() {
        this.sync();
    }

    public void sync() {
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider r) {
        super.saveAdditional(tag, r);
        this.links.save(tag);
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider r) {
        int before = this.links.size();
        super.loadAdditional(tag, r);
        this.links.load(tag);
        if (this.level != null && this.level.isClientSide && this.links.size() != before) {
            this.changedAt = System.currentTimeMillis();
        }
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider r) {
        return this.saveWithoutMetadata(r);
    }

    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create((BlockEntity)this);
    }

    public boolean addToGoggleTooltip(List<Component> tip, boolean sneaking) {
        tip.add((Component)Component.literal((String)"    ").append((Component)this.getBlockState().getBlock().getName()).withStyle(ChatFormatting.WHITE));
        if (this.links.isEmpty()) {
            tip.add((Component)Component.literal((String)"    no cables").withStyle(ChatFormatting.DARK_GRAY));
            return true;
        }
        EnumMap<CableType, Integer> n = new EnumMap<CableType, Integer>(CableType.class);
        for (Link link : this.links.list()) {
            n.merge(link.type(), 1, Integer::sum);
        }
        for (Map.Entry entry : n.entrySet()) {
            tip.add((Component)Component.literal((String)("    " + String.valueOf(entry.getValue()) + " x ")).append(new ItemStack((ItemLike)Cables.item((CableType)((Object)entry.getKey()))).getHoverName()).withStyle(ChatFormatting.GOLD));
        }
        if (this.junction(0)) {
            tip.add((Component)Component.literal((String)"    junction: passes the signal on").withStyle(ChatFormatting.GRAY));
        }
        return true;
    }

    public AABB cableBounds() {
        AABB b = new AABB(this.worldPosition);
        for (Link l : this.links.list()) {
            b = b.minmax(new AABB(l.other()));
        }
        return b.inflate(1.0);
    }
}
