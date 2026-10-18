/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.george_vi.electroenergetics.CEEDataComponents
 *  com.george_vi.electroenergetics.CEEWireTypes
 *  com.george_vi.electroenergetics.foundation.device.ElectricalDeviceBlock
 *  com.george_vi.electroenergetics.foundation.nodes.InWorldNode
 *  com.george_vi.electroenergetics.simulation.WireType
 *  com.george_vi.electroenergetics.simulation.infrastructure.InfrastructureSavedData
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.compat.cee;

import com.airwaves.cable.CableType;
import com.airwaves.compat.cee.CEEMachine;
import com.airwaves.power.Terminals;
import com.george_vi.electroenergetics.CEEDataComponents;
import com.george_vi.electroenergetics.CEEWireTypes;
import com.george_vi.electroenergetics.foundation.device.ElectricalDeviceBlock;
import com.george_vi.electroenergetics.foundation.nodes.InWorldNode;
import com.george_vi.electroenergetics.simulation.WireType;
import com.george_vi.electroenergetics.simulation.infrastructure.InfrastructureSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class CEEWires {
    private CEEWires() {
    }

    private static WireType wireFor(CableType t) {
        return t == CableType.COPPER ? (WireType)CEEWireTypes.COPPER.get() : (WireType)CEEWireTypes.ELECTRUM.get();
    }

    public static InteractionResult use(UseOnContext ctx, CableType type) {
        if (type == CableType.FIBER) {
            return InteractionResult.PASS;
        }
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Player player = ctx.getPlayer();
        ItemStack held = ctx.getItemInHand();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (!(block instanceof ElectricalDeviceBlock)) {
            return InteractionResult.PASS;
        }
        ElectricalDeviceBlock db = (ElectricalDeviceBlock)block;
        boolean selecting = held.has(CEEDataComponents.SELECTED_NODE);
        if (state.getBlock() instanceof CEEMachine && !selecting) {
            Vec3 local = ctx.getClickLocation().subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
            if (!Terminals.has(state) || Terminals.at(state, local) < 0) {
                return InteractionResult.PASS;
            }
        }
        if (player != null && player.isShiftKeyDown()) {
            if (!selecting) {
                return InteractionResult.PASS;
            }
            held.remove(CEEDataComponents.SELECTED_NODE);
            return InteractionResult.sidedSuccess((boolean)level.isClientSide);
        }
        InWorldNode hovered = InWorldNode.closestNode((Level)level, (Vec3)ctx.getClickLocation(), (float)1.5f, (BlockPos)pos);
        if (hovered == null) {
            hovered = InWorldNode.closestNode((Level)level, (BlockPos)pos, (BlockState)state, (float)1.5f, (Vec3)ctx.getClickLocation());
        }
        if (!selecting) {
            if (hovered == null) {
                return InteractionResult.PASS;
            }
            held.set(CEEDataComponents.SELECTED_NODE, (Object)hovered);
            level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.6f, 1.4f);
            return InteractionResult.sidedSuccess((boolean)level.isClientSide);
        }
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel sl = (ServerLevel)level;
        InWorldNode original = (InWorldNode)held.get(CEEDataComponents.SELECTED_NODE);
        WireType wire = CEEWires.wireFor(type);
        InfrastructureSavedData sd = InfrastructureSavedData.load((ServerLevel)sl);
        if (hovered == null || original == null || hovered.equals((Object)original) || hovered.sourcePos().equals((Object)original.sourcePos()) && !db.canSelfConnect(level, pos, state, hovered.id(), original.id()) || sd.isConnected(hovered, original) || Math.sqrt(original.sableSourcePos(level).distSqr((Vec3i)hovered.sableSourcePos(level))) > (double)Math.max(wire.getMaxLength(), (float)type.maxLen)) {
            level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 0.4f, 1.6f);
            return InteractionResult.FAIL;
        }
        BlockState os = level.getBlockState(original.sourcePos());
        BlockState hs = level.getBlockState(hovered.sourcePos());
        Block block2 = os.getBlock();
        if (block2 instanceof ElectricalDeviceBlock) {
            ElectricalDeviceBlock e1 = (ElectricalDeviceBlock)block2;
            e1.ensureNodesExist(sl, original.sourcePos(), os);
        }
        if ((block2 = hs.getBlock()) instanceof ElectricalDeviceBlock) {
            ElectricalDeviceBlock e2 = (ElectricalDeviceBlock)block2;
            e2.ensureNodesExist(sl, hovered.sourcePos(), hs);
        }
        if (!sd.getNodes().contains(hovered) || !sd.getNodes().contains(original)) {
            return InteractionResult.FAIL;
        }
        sd.connect(original, hovered, wire);
        level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.8f, 1.0f);
        if (player == null || !player.isCreative()) {
            held.shrink(1);
        }
        held.remove(CEEDataComponents.SELECTED_NODE);
        return InteractionResult.SUCCESS;
    }
}
