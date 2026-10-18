/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.george_vi.electroenergetics.CEERegistries
 *  com.george_vi.electroenergetics.devices.device.SimulatedDeviceType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.airwaves.compat.cee;

import com.airwaves.Tier;
import com.airwaves.block.DimensionalRelayBlock;
import com.airwaves.block.JammerBlock;
import com.airwaves.block.TransmitterBlock;
import com.airwaves.compat.cee.CEEBlocks;
import com.airwaves.compat.cee.CEETowerBlock;
import com.airwaves.compat.cee.MachineDevice;
import com.airwaves.structure.KineticStructureBlock;
import com.airwaves.structure.StructureDef;
import com.george_vi.electroenergetics.CEERegistries;
import com.george_vi.electroenergetics.devices.device.SimulatedDeviceType;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CEECompat {
    private static final DeferredRegister<SimulatedDeviceType<?>> DEVICES = DeferredRegister.create((Registry)CEERegistries.SIMULATED_DEVICE_TYPE, (String)"airwaves");
    public static final DeferredHolder<SimulatedDeviceType<?>, SimulatedDeviceType<MachineDevice>> MACHINE = DEVICES.register("machine", () -> new SimulatedDeviceType(ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"machine"), (type, level, pos, sd) -> new MachineDevice(level, pos, sd, type)));

    private CEECompat() {
    }

    public static void register(IEventBus modBus) {
        DEVICES.register(modBus);
    }

    public static KineticStructureBlock tower(StructureDef d, BlockBehaviour.Properties p, BiFunction<BlockPos, BlockState, BlockEntity> be) {
        return new CEETowerBlock(d, p, be);
    }

    public static TransmitterBlock transmitter(Tier t, BlockBehaviour.Properties p) {
        return new CEEBlocks.Transmitter(t, p);
    }

    public static JammerBlock jammer(BlockBehaviour.Properties p) {
        return new CEEBlocks.Jammer(p);
    }

    public static DimensionalRelayBlock dimensionalRelay(BlockBehaviour.Properties p) {
        return new CEEBlocks.DimRelay(p);
    }
}
