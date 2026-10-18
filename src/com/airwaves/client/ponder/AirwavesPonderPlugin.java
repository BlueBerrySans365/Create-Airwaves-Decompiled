/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.registration.PonderPlugin
 *  net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
 *  net.createmod.ponder.api.registration.PonderTagRegistrationHelper
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.ItemLike
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredItem
 */
package com.airwaves.client.ponder;

import com.airwaves.ModRegistry;
import com.airwaves.client.ponder.AirwavesScenes;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class AirwavesPonderPlugin
implements PonderPlugin {
    public static final ResourceLocation TAG = ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"radio");

    public String getModId() {
        return "airwaves";
    }

    private static ResourceLocation id(DeferredHolder<?, ?> h) {
        return h.getId();
    }

    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> h) {
        Object[] tx = new ResourceLocation[]{AirwavesPonderPlugin.id((DeferredHolder)ModRegistry.TRANSMITTER_ITEMS.get(0)), AirwavesPonderPlugin.id((DeferredHolder)ModRegistry.TRANSMITTER_ITEMS.get(1)), AirwavesPonderPlugin.id((DeferredHolder)ModRegistry.TRANSMITTER_ITEMS.get(2)), AirwavesPonderPlugin.id((DeferredHolder)ModRegistry.TRANSMITTER_ITEMS.get(3))};
        Object[] rx = new ResourceLocation[]{AirwavesPonderPlugin.id((DeferredHolder)ModRegistry.RECEIVER_ITEMS.get(0)), AirwavesPonderPlugin.id((DeferredHolder)ModRegistry.RECEIVER_ITEMS.get(1)), AirwavesPonderPlugin.id((DeferredHolder)ModRegistry.RECEIVER_ITEMS.get(2)), AirwavesPonderPlugin.id((DeferredHolder)ModRegistry.RECEIVER_ITEMS.get(3))};
        h.forComponents(tx).addStoryBoard("transmitter/setup", AirwavesScenes::transmitter, new ResourceLocation[]{TAG}).addStoryBoard("transmitter/microphone", AirwavesScenes::microphone, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.MULTIMETER)}).addStoryBoard("transmitter/setup", AirwavesScenes::transmitter, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.MICROPHONE_ITEM)}).addStoryBoard("transmitter/microphone", AirwavesScenes::microphone, new ResourceLocation[]{TAG});
        h.forComponents(rx).addStoryBoard("receiver/setup", AirwavesScenes::receiver, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.SPEAKER_ITEM), AirwavesPonderPlugin.id(ModRegistry.HORN_SPEAKER_ITEM)}).addStoryBoard("receiver/setup", AirwavesScenes::receiver, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.ANTENNA_ITEM), AirwavesPonderPlugin.id(ModRegistry.ANTENNA_MEDIUM_ITEM), AirwavesPonderPlugin.id(ModRegistry.ANTENNA_BIG_ITEM), AirwavesPonderPlugin.id(ModRegistry.ANTENNA_GIANT_ITEM)}).addStoryBoard("antennas/stacking", AirwavesScenes::antennas, new ResourceLocation[]{TAG}).addStoryBoard("antennas/yagi", AirwavesScenes::yagi, new ResourceLocation[]{TAG}).addStoryBoard("transmitter/setup", AirwavesScenes::transmitter, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.COPPER_WIRE), AirwavesPonderPlugin.id(ModRegistry.GOLD_WIRE), AirwavesPonderPlugin.id(ModRegistry.SILVER_WIRE), AirwavesPonderPlugin.id(ModRegistry.FIBER_CABLE)}).addStoryBoard("transmitter/microphone", AirwavesScenes::microphone, new ResourceLocation[]{TAG}).addStoryBoard("cables/insulators", AirwavesScenes::insulators, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.INSULATOR_ITEM)}).addStoryBoard("cables/insulators", AirwavesScenes::insulators, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.RADIO_TOWER_ITEM)}).addStoryBoard("towers/radio_tower", AirwavesScenes::radioTower, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.RELAY_TOWER_SMALL_ITEM), AirwavesPonderPlugin.id(ModRegistry.RELAY_TOWER_BIG_ITEM)}).addStoryBoard("towers/relay", AirwavesScenes::relayTower, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.JAMMER_ITEM)}).addStoryBoard("jammer/basics", AirwavesScenes::jammer, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.CIRCUIT_WORKBENCH_ITEM), AirwavesPonderPlugin.id(ModRegistry.CIRCUIT_BOARD), AirwavesPonderPlugin.id(ModRegistry.RESISTOR), AirwavesPonderPlugin.id(ModRegistry.CAPACITOR), AirwavesPonderPlugin.id(ModRegistry.LED), AirwavesPonderPlugin.id(ModRegistry.TRANSISTOR), AirwavesPonderPlugin.id(ModRegistry.VACUUM_TUBE), AirwavesPonderPlugin.id(ModRegistry.COPPER_COIL), AirwavesPonderPlugin.id(ModRegistry.CRYSTAL_OSCILLATOR)}).addStoryBoard("workbench/boards", AirwavesScenes::workbench, new ResourceLocation[]{TAG}).addStoryBoard("workbench/circuit_box", AirwavesScenes::circuitBox, new ResourceLocation[]{TAG});
        for (DeferredItem<Item> b : ModRegistry.BOARDS.values()) {
            h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(b)}).addStoryBoard("workbench/boards", AirwavesScenes::workbench, new ResourceLocation[]{TAG}).addStoryBoard("workbench/circuit_box", AirwavesScenes::circuitBox, new ResourceLocation[]{TAG});
        }
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.CIRCUIT_BOX_ITEM)}).addStoryBoard("workbench/circuit_box", AirwavesScenes::circuitBox, new ResourceLocation[]{TAG}).addStoryBoard("workbench/boards", AirwavesScenes::workbench, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.TAPE_PLAYER_ITEM), AirwavesPonderPlugin.id(ModRegistry.TAPE_ITEM)}).addStoryBoard("tape/player", AirwavesScenes::tapePlayer, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.YAGI_ANTENNA_ITEM)}).addStoryBoard("antennas/yagi", AirwavesScenes::yagi, new ResourceLocation[]{TAG}).addStoryBoard("antennas/stacking", AirwavesScenes::antennas, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.DIMENSIONAL_RELAY_ITEM)}).addStoryBoard("dimensional/relay", AirwavesScenes::dimensionalRelay, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.RADIO_ITEM), AirwavesPonderPlugin.id(ModRegistry.PORTABLE_RADIO), AirwavesPonderPlugin.id(ModRegistry.TUNING_TABLE_ITEM), AirwavesPonderPlugin.id(ModRegistry.FREQUENCY_CARD)}).addStoryBoard("radio/basics", AirwavesScenes::radio, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.RADIO_CONSOLE_ITEM)}).addStoryBoard("airship/console", AirwavesScenes::airship, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.ON_AIR_SIGN_ITEM)}).addStoryBoard("studio/extras", AirwavesScenes::studio, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.HORN_SPEAKER_ITEM)}).addStoryBoard("studio/extras", AirwavesScenes::studio, new ResourceLocation[]{TAG});
        h.forComponents((Object[])new ResourceLocation[]{AirwavesPonderPlugin.id(ModRegistry.CREATIVE_TRANSMITTER_ITEM), AirwavesPonderPlugin.id(ModRegistry.CREATIVE_RECEIVER_ITEM), AirwavesPonderPlugin.id(ModRegistry.CREATIVE_ANTENNA_ITEM)}).addStoryBoard("creative/blocks", AirwavesScenes::creative, new ResourceLocation[]{TAG});
    }

    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> h) {
        h.registerTag(TAG).addToIndex().item((ItemLike)ModRegistry.RADIO_ITEM.get(), true, false).title("Airwaves Radio").description("Transmitters, receivers and everything that carries a voice over the air").register();
        h.addToTag(TAG).add((Object)AirwavesPonderPlugin.id(ModRegistry.RADIO_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.MICROPHONE_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.TAPE_PLAYER_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.SPEAKER_ITEM)).add((Object)AirwavesPonderPlugin.id((DeferredHolder)ModRegistry.TRANSMITTER_ITEMS.get(0))).add((Object)AirwavesPonderPlugin.id((DeferredHolder)ModRegistry.RECEIVER_ITEMS.get(0))).add((Object)AirwavesPonderPlugin.id(ModRegistry.ANTENNA_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.YAGI_ANTENNA_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.JAMMER_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.INSULATOR_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.CIRCUIT_WORKBENCH_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.CIRCUIT_BOX_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.DIMENSIONAL_RELAY_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.RADIO_TOWER_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.RELAY_TOWER_SMALL_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.RADIO_CONSOLE_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.ON_AIR_SIGN_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.HORN_SPEAKER_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.TUNING_TABLE_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.CREATIVE_TRANSMITTER_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.CREATIVE_RECEIVER_ITEM)).add((Object)AirwavesPonderPlugin.id(ModRegistry.CREATIVE_ANTENNA_ITEM));
    }
}
