/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour
 *  com.simibubi.create.foundation.block.connected.AllCTTypes
 *  com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry
 *  com.simibubi.create.foundation.block.connected.CTSpriteShifter
 *  com.simibubi.create.foundation.block.connected.CTType
 *  com.simibubi.create.foundation.data.CreateRegistrate
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.registries.RegisterEvent
 */
package com.airwaves.client;

import com.airwaves.ModRegistry;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class AirwavesSpriteShifts {
    public static final CTSpriteShiftEntry RADIO_CASING = CTSpriteShifter.getCT((CTType)AllCTTypes.OMNIDIRECTIONAL, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"block/radio_casing"), (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"block/radio_casing_connected"));

    private AirwavesSpriteShifts() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(EventPriority.LOWEST, RegisterEvent.class, e -> {
            if (!e.getRegistryKey().equals(Registries.BLOCK)) {
                return;
            }
            CreateRegistrate.connectedTextures(() -> new EncasedCTBehaviour(RADIO_CASING)).accept(ModRegistry.RADIO_CASING.get());
        });
    }
}
