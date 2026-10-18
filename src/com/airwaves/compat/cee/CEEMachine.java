/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.george_vi.electroenergetics.devices.device.SimulatedDeviceType
 *  com.george_vi.electroenergetics.foundation.device.ElectricalDeviceBlock
 *  com.george_vi.electroenergetics.foundation.nodes.InWorldNodeConnection
 *  com.george_vi.electroenergetics.simulation.infrastructure.InWorldNodeData
 *  com.george_vi.electroenergetics.simulation.infrastructure.InfrastructureSavedData
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.compat.cee;

import com.airwaves.compat.cee.CEECompat;
import com.airwaves.compat.cee.MachineDevice;
import com.airwaves.power.Terminals;
import com.george_vi.electroenergetics.devices.device.SimulatedDeviceType;
import com.george_vi.electroenergetics.foundation.device.ElectricalDeviceBlock;
import com.george_vi.electroenergetics.foundation.nodes.InWorldNodeConnection;
import com.george_vi.electroenergetics.simulation.infrastructure.InWorldNodeData;
import com.george_vi.electroenergetics.simulation.infrastructure.InfrastructureSavedData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface CEEMachine
extends ElectricalDeviceBlock<MachineDevice> {
    default public SimulatedDeviceType<MachineDevice> getDevice() {
        return (SimulatedDeviceType)CEECompat.MACHINE.get();
    }

    default public Map<Integer, Vec3> getNodePositions(Level level, BlockPos pos, BlockState state) {
        HashMap<Integer, Vec3> m = new HashMap<Integer, Vec3>();
        m.put(0, this.getNodePosition(level, pos, state, 0));
        m.put(1, this.getNodePosition(level, pos, state, 1));
        return m;
    }

    default public Vec3 getNodePosition(Level level, BlockPos pos, BlockState state, int id) {
        if (id < 0 || id > 1) {
            return null;
        }
        return Terminals.point(state, id);
    }

    default public MutableComponent getNodeLabel(Level level, BlockPos pos, BlockState state, int id) {
        return id == 0 ? Component.literal((String)"+").withStyle(ChatFormatting.RED) : Component.literal((String)"-").withStyle(ChatFormatting.BLUE);
    }

    default public float getNodeSize(Level level, BlockPos pos, BlockState state, int id) {
        return 0.1875f;
    }

    public static void placed(Level level, BlockPos pos, BlockState state, Block block) {
        if (!level.getBlockTicks().hasScheduledTick(pos, (Object)block)) {
            level.scheduleTick(pos, block, 1);
        }
    }

    public static void ticked(CEEMachine m, ServerLevel level, BlockPos pos, BlockState state) {
        ArrayList<Integer> nodes = new ArrayList<Integer>(m.getNodePositions((Level)level, pos, state).keySet());
        InfrastructureSavedData.load((ServerLevel)level).registerOrUpdateNodes(pos, nodes);
    }

    public static void creativeBreak(Level level, BlockPos pos, Player player) {
        if (level instanceof ServerLevel) {
            ServerLevel sl = (ServerLevel)level;
            if (player.isCreative()) {
                InfrastructureSavedData sd = InfrastructureSavedData.load((ServerLevel)sl);
                for (InWorldNodeData nodeData : sd.getNodesAt(pos)) {
                    for (InWorldNodeConnection connection : sd.getConnections(nodeData)) {
                        sd.removeConnection(connection);
                    }
                }
            }
        }
    }
}
