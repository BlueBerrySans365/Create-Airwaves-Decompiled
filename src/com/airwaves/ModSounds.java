/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredRegister
 */
package com.airwaves;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create((ResourceKey)Registries.SOUND_EVENT, (String)"airwaves");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRYSTAL_IN = ModSounds.s("crystal_in");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRYSTAL_OUT = ModSounds.s("crystal_out");
    public static final DeferredHolder<SoundEvent, SoundEvent> KNOB = ModSounds.s("knob");
    public static final DeferredHolder<SoundEvent, SoundEvent> TUNE = ModSounds.s("tune");
    public static final DeferredHolder<SoundEvent, SoundEvent> SEEK = ModSounds.s("seek");
    public static final DeferredHolder<SoundEvent, SoundEvent> POWER_ON = ModSounds.s("power_on");
    public static final DeferredHolder<SoundEvent, SoundEvent> POWER_OFF = ModSounds.s("power_off");
    public static final DeferredHolder<SoundEvent, SoundEvent> CABLE_GRAB = ModSounds.s("cable_grab");
    public static final DeferredHolder<SoundEvent, SoundEvent> CABLE_ATTACH = ModSounds.s("cable_attach");
    public static final DeferredHolder<SoundEvent, SoundEvent> CABLE_HEAVY = ModSounds.s("cable_heavy");
    public static final DeferredHolder<SoundEvent, SoundEvent> CABLE_DETACH = ModSounds.s("cable_detach");
    public static final DeferredHolder<SoundEvent, SoundEvent> CABLE_REJECT = ModSounds.s("cable_reject");
    public static final DeferredHolder<SoundEvent, SoundEvent> STRUCTURE = ModSounds.s("structure");
    public static final DeferredHolder<SoundEvent, SoundEvent> SWITCH = ModSounds.s("switch");
    public static final DeferredHolder<SoundEvent, SoundEvent> PROBE = ModSounds.s("probe");
    public static final DeferredHolder<SoundEvent, SoundEvent> BOOK = ModSounds.s("book");

    private static DeferredHolder<SoundEvent, SoundEvent> s(String n) {
        return SOUNDS.register(n, () -> SoundEvent.createVariableRangeEvent((ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)n)));
    }

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }

    private ModSounds() {
    }
}
