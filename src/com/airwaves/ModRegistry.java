/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.valueproviders.ConstantInt
 *  net.minecraft.util.valueproviders.IntProvider
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DropExperienceBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.BlockEntityType$Builder
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.MapColor
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.common.extensions.IMenuTypeExtension
 *  net.neoforged.neoforge.registries.DeferredBlock
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.DeferredItem
 *  net.neoforged.neoforge.registries.DeferredRegister
 *  net.neoforged.neoforge.registries.DeferredRegister$Blocks
 *  net.neoforged.neoforge.registries.DeferredRegister$Items
 */
package com.airwaves;

import com.airwaves.Config;
import com.airwaves.Tier;
import com.airwaves.Tuning;
import com.airwaves.be.CircuitBoxBE;
import com.airwaves.be.CircuitWorkbenchBE;
import com.airwaves.be.CreativeAntennaBE;
import com.airwaves.be.CreativeReceiverBE;
import com.airwaves.be.CreativeTransmitterBE;
import com.airwaves.be.DimensionalRelayBE;
import com.airwaves.be.InsulatorBE;
import com.airwaves.be.JammerBE;
import com.airwaves.be.MicrophoneBE;
import com.airwaves.be.RadioBE;
import com.airwaves.be.RadioConsoleBE;
import com.airwaves.be.ReceiverBE;
import com.airwaves.be.RelayBE;
import com.airwaves.be.SpeakerBE;
import com.airwaves.be.TapePlayerBE;
import com.airwaves.be.TransmitterBE;
import com.airwaves.be.YagiAntennaBE;
import com.airwaves.block.AntennaBlock;
import com.airwaves.block.CircuitBoxBlock;
import com.airwaves.block.CircuitWorkbenchBlock;
import com.airwaves.block.ConnectedBlock;
import com.airwaves.block.CreativeAntennaBlock;
import com.airwaves.block.CreativeReceiverBlock;
import com.airwaves.block.CreativeTransmitterBlock;
import com.airwaves.block.DimensionalRelayBlock;
import com.airwaves.block.InsulatorBlock;
import com.airwaves.block.JammerBlock;
import com.airwaves.block.MastBlock;
import com.airwaves.block.MicrophoneBlock;
import com.airwaves.block.RadioBlock;
import com.airwaves.block.RadioConsoleBlock;
import com.airwaves.block.ReceiverBlock;
import com.airwaves.block.SpeakerBlock;
import com.airwaves.block.TapePlayerBlock;
import com.airwaves.block.TransmitterBlock;
import com.airwaves.block.TuningTableBlock;
import com.airwaves.block.YagiAntennaBlock;
import com.airwaves.cable.CableType;
import com.airwaves.compat.ElectricCompat;
import com.airwaves.compat.cee.CEECompat;
import com.airwaves.item.BoardItem;
import com.airwaves.item.CableItem;
import com.airwaves.item.FieldManualItem;
import com.airwaves.item.FrequencyCardItem;
import com.airwaves.item.MastItem;
import com.airwaves.item.PortableRadioItem;
import com.airwaves.item.TapeItem;
import com.airwaves.item.TunableBlockItem;
import com.airwaves.logic.BoardDesign;
import com.airwaves.logic.BoardLayout;
import com.airwaves.logic.TapeData;
import com.airwaves.menu.CircuitWorkbenchMenu;
import com.airwaves.menu.TapePlayerMenu;
import com.airwaves.menu.TuningTableMenu;
import com.airwaves.structure.KineticStructureBlock;
import com.airwaves.structure.StructureBlock;
import com.airwaves.structure.StructureItem;
import com.airwaves.structure.TowerCoreBE;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRegistry {
    private static final String ID = "airwaves";
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks((String)"airwaves");
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems((String)"airwaves");
    public static final DeferredRegister<BlockEntityType<?>> BES = DeferredRegister.create((ResourceKey)Registries.BLOCK_ENTITY_TYPE, (String)"airwaves");
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create((ResourceKey)Registries.MENU, (String)"airwaves");
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create((ResourceKey)Registries.DATA_COMPONENT_TYPE, (String)"airwaves");
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create((ResourceKey)Registries.CREATIVE_MODE_TAB, (String)"airwaves");
    public static final TagKey<Block> GROUND_PLANE = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"ground_plane"));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Tuning>> TUNING = COMPONENTS.register("tuning", () -> DataComponentType.builder().persistent(Tuning.CODEC).networkSynchronized(Tuning.STREAM).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BoardLayout>> BOARD_LAYOUT = COMPONENTS.register("board_layout", () -> DataComponentType.builder().persistent(BoardLayout.CODEC).networkSynchronized(BoardLayout.STREAM).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> WIRE_START = COMPONENTS.register("wire_start", () -> DataComponentType.builder().persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> WIRE_SLOT = COMPONENTS.register("wire_slot", () -> DataComponentType.builder().persistent((Codec)Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TapeData>> TAPE = COMPONENTS.register("tape", () -> DataComponentType.builder().persistent(TapeData.CODEC).networkSynchronized(TapeData.STREAM).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> POWERED = COMPONENTS.register("powered", () -> DataComponentType.builder().persistent((Codec)Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredBlock<RadioBlock> RADIO = BLOCKS.register("radio", () -> new RadioBlock(ModRegistry.metal()));
    public static final DeferredBlock<TuningTableBlock> TUNING_TABLE = BLOCKS.register("tuning_table", () -> new TuningTableBlock(ModRegistry.wood().strength(2.5f)));
    public static final List<DeferredBlock<TransmitterBlock>> TRANSMITTERS = new ArrayList<DeferredBlock<TransmitterBlock>>();
    public static final List<DeferredBlock<ReceiverBlock>> RECEIVERS = new ArrayList<DeferredBlock<ReceiverBlock>>();
    public static final DeferredBlock<AntennaBlock.Small> ANTENNA;
    public static final DeferredBlock<MicrophoneBlock> MICROPHONE;
    public static final DeferredBlock<SpeakerBlock> SPEAKER;
    public static final DeferredBlock<TapePlayerBlock> TAPE_PLAYER;
    public static final DeferredBlock<InsulatorBlock> INSULATOR;
    public static final DeferredBlock<JammerBlock> JAMMER;
    public static final DeferredBlock<CircuitBoxBlock> CIRCUIT_BOX;
    public static final DeferredBlock<DimensionalRelayBlock> DIMENSIONAL_RELAY;
    public static final DeferredBlock<CreativeTransmitterBlock> CREATIVE_TRANSMITTER;
    public static final DeferredBlock<CreativeReceiverBlock> CREATIVE_RECEIVER;
    public static final DeferredBlock<CreativeAntennaBlock> CREATIVE_ANTENNA;
    public static final DeferredBlock<RadioConsoleBlock> RADIO_CONSOLE;
    public static final DeferredBlock<CircuitWorkbenchBlock> CIRCUIT_WORKBENCH;
    public static final DeferredBlock<YagiAntennaBlock> YAGI_ANTENNA;
    public static final DeferredBlock<DropExperienceBlock> SILVER_ORE;
    public static final DeferredBlock<DropExperienceBlock> DEEPSLATE_SILVER_ORE;
    public static final DeferredBlock<Block> SILVER_BLOCK;
    public static final DeferredBlock<Block> RADIO_CASING;
    public static final DeferredBlock<StructureBlock> RELAY_TOWER_SMALL;
    public static final DeferredBlock<StructureBlock> RELAY_TOWER_BIG;
    public static final DeferredBlock<StructureBlock> RADIO_TOWER;
    public static final DeferredItem<CableItem> COPPER_WIRE;
    public static final DeferredItem<FrequencyCardItem> FREQUENCY_CARD;
    public static final DeferredItem<Item> SILVER_INGOT;
    public static final DeferredItem<Item> SILVER_NUGGET;
    public static final DeferredItem<Item> RAW_SILVER;
    public static final DeferredItem<Item> SILVER_SHEET;
    public static final DeferredItem<Item> SIGNAL_MECHANISM;
    public static final DeferredItem<Item> INCOMPLETE_CIRCUIT_BOARD;
    public static final DeferredItem<Item> INCOMPLETE_SIGNAL_MECHANISM;
    public static final DeferredItem<FieldManualItem> FIELD_MANUAL;
    public static final DeferredItem<Item> COPPER_COIL;
    public static final DeferredItem<Item> CRYSTAL_OSCILLATOR;
    public static final DeferredItem<Item> CIRCUIT_BOARD;
    public static final DeferredItem<Item> SPEAKER_CONE;
    public static final DeferredItem<Item> MIC_CAPSULE;
    public static final DeferredItem<Item> MEMORY_CHIP;
    public static final DeferredItem<Item> COUNTER_CHIP;
    public static final DeferredItem<Item> PULSE_CHIP;
    public static final DeferredItem<Item> LIGHT_SENSOR;
    public static final DeferredItem<Item> RAIN_SENSOR;
    public static final DeferredItem<Item> MOTION_SENSOR;
    public static final DeferredItem<Item> RESISTOR;
    public static final DeferredItem<Item> CAPACITOR;
    public static final DeferredItem<Item> LED;
    public static final DeferredItem<Item> TRANSISTOR;
    public static final Map<BoardDesign, DeferredItem<Item>> BOARDS;
    public static final DeferredItem<TunableBlockItem> RADIO_ITEM;
    public static final DeferredItem<PortableRadioItem> PORTABLE_RADIO;
    public static final DeferredItem<TunableBlockItem> CREATIVE_TRANSMITTER_ITEM;
    public static final DeferredItem<TunableBlockItem> CREATIVE_RECEIVER_ITEM;
    public static final DeferredItem<BlockItem> CREATIVE_ANTENNA_ITEM;
    public static final DeferredItem<TunableBlockItem> RADIO_CONSOLE_ITEM;
    public static final DeferredItem<BlockItem> TUNING_TABLE_ITEM;
    public static final List<DeferredItem<TunableBlockItem>> TRANSMITTER_ITEMS;
    public static final List<DeferredItem<TunableBlockItem>> RECEIVER_ITEMS;
    public static final DeferredItem<MastItem> ANTENNA_ITEM;
    public static final DeferredItem<BlockItem> YAGI_ANTENNA_ITEM;
    public static final DeferredItem<BlockItem> SILVER_ORE_ITEM;
    public static final DeferredItem<BlockItem> DEEPSLATE_SILVER_ORE_ITEM;
    public static final DeferredItem<BlockItem> SILVER_BLOCK_ITEM;
    public static final DeferredItem<BlockItem> RADIO_CASING_ITEM;
    public static final DeferredItem<BlockItem> CIRCUIT_WORKBENCH_ITEM;
    public static final DeferredItem<StructureItem> RELAY_TOWER_SMALL_ITEM;
    public static final DeferredItem<StructureItem> RELAY_TOWER_BIG_ITEM;
    public static final DeferredItem<StructureItem> RADIO_TOWER_ITEM;
    public static final DeferredItem<BlockItem> MICROPHONE_ITEM;
    public static final DeferredItem<BlockItem> SPEAKER_ITEM;
    public static final DeferredItem<BlockItem> TAPE_PLAYER_ITEM;
    public static final DeferredItem<TapeItem> TAPE_ITEM;
    public static final DeferredItem<BlockItem> INSULATOR_ITEM;
    public static final DeferredItem<BlockItem> JAMMER_ITEM;
    public static final DeferredItem<TunableBlockItem> CIRCUIT_BOX_ITEM;
    public static final DeferredItem<BlockItem> DIMENSIONAL_RELAY_ITEM;
    public static final Supplier<BlockEntityType<RadioBE>> RADIO_BE;
    public static final Supplier<BlockEntityType<CreativeTransmitterBE>> CREATIVE_TX_BE;
    public static final Supplier<BlockEntityType<CreativeReceiverBE>> CREATIVE_RX_BE;
    public static final Supplier<BlockEntityType<KineticStructureBlock.TowerShaftBE>> TOWER_SHAFT_BE;
    public static final Supplier<BlockEntityType<CreativeAntennaBE>> CREATIVE_ANTENNA_BE;
    public static final Supplier<BlockEntityType<RadioConsoleBE>> RADIO_CONSOLE_BE;
    public static final Supplier<BlockEntityType<TransmitterBE>> TRANSMITTER_BE;
    public static final Supplier<BlockEntityType<ReceiverBE>> RECEIVER_BE;
    public static final Supplier<BlockEntityType<RelayBE>> RELAY_BE;
    public static final Supplier<BlockEntityType<TowerCoreBE>> TOWER_BE;
    public static final Supplier<BlockEntityType<MicrophoneBE>> MIC_BE;
    public static final Supplier<BlockEntityType<TapePlayerBE>> TAPE_PLAYER_BE;
    public static final Supplier<BlockEntityType<SpeakerBE>> SPEAKER_BE;
    public static final Supplier<BlockEntityType<YagiAntennaBE>> YAGI_BE;
    public static final Supplier<BlockEntityType<InsulatorBE>> INSULATOR_BE;
    public static final Supplier<BlockEntityType<JammerBE>> JAMMER_BE;
    public static final Supplier<BlockEntityType<CircuitWorkbenchBE>> WORKBENCH_BE;
    public static final Supplier<BlockEntityType<CircuitBoxBE>> CIRCUIT_BOX_BE;
    public static final Supplier<BlockEntityType<DimensionalRelayBE>> DIM_RELAY_BE;
    public static final Supplier<MenuType<TuningTableMenu>> TUNING_MENU;
    public static final Supplier<MenuType<TapePlayerMenu>> TAPE_MENU;
    public static final Supplier<MenuType<CircuitWorkbenchMenu>> WORKBENCH_MENU;
    public static final Supplier<CreativeModeTab> TAB;

    private static BlockBehaviour.Properties metal() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2.0f, 6.0f).sound(SoundType.METAL).noOcclusion().requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties mast(float s) {
        return ModRegistry.metal().strength(s).isViewBlocking((a, b, c) -> false).isSuffocating((a, b, c) -> false).isRedstoneConductor((a, b, c) -> false);
    }

    private static BlockBehaviour.Properties wood() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.5f).sound(SoundType.WOOD).noOcclusion();
    }

    private static BlockBehaviour.Properties ore(boolean deep) {
        return BlockBehaviour.Properties.of().mapColor(deep ? MapColor.DEEPSLATE : MapColor.STONE).strength(deep ? 4.5f : 3.0f, 3.0f).sound(deep ? SoundType.DEEPSLATE : SoundType.STONE).requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties structure(BlockBehaviour.Properties p) {
        return p.noOcclusion().isViewBlocking((a, b, c) -> false).isSuffocating((a, b, c) -> false).isRedstoneConductor((a, b, c) -> false);
    }

    private static DeferredItem<Item> part(String n) {
        return ITEMS.register(n, () -> new Item(new Item.Properties()));
    }

    public static Item electronTube() {
        return (Item)BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath((String)"create", (String)"electron_tube"));
    }

    public static Item board(BoardDesign d) {
        return (Item)BOARDS.get((Object)d).get();
    }

    private static DeferredItem<BlockItem> blockItem(DeferredBlock<? extends Block> b) {
        return ITEMS.register(b.getId().getPath(), () -> new BlockItem((Block)b.get(), new Item.Properties()));
    }

    private static Block[] all(List<? extends DeferredBlock<? extends Block>> l) {
        return (Block[])l.stream().map(DeferredHolder::get).toArray(Block[]::new);
    }

    public static void register(IEventBus bus) {
        ElectricCompat.register();
        if (Config.ceeLoaded()) {
            CEECompat.register(bus);
        }
        COMPONENTS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BES.register(bus);
        MENUS.register(bus);
        TABS.register(bus);
    }

    private ModRegistry() {
    }

    static {
        TRANSMITTERS.add((DeferredBlock<TransmitterBlock>)BLOCKS.register("transmitter", () -> ElectricCompat.transmitter(Tier.GIANT, ModRegistry.metal().strength(6.0f))));
        RECEIVERS.add((DeferredBlock<ReceiverBlock>)BLOCKS.register("receiver", () -> new ReceiverBlock(Tier.GIANT, ModRegistry.metal().strength(5.5f))));
        ANTENNA = BLOCKS.register("antenna", () -> new AntennaBlock.Small(ModRegistry.mast(1.5f)));
        MICROPHONE = BLOCKS.register("microphone", () -> new MicrophoneBlock(ModRegistry.metal().strength(1.0f)));
        SPEAKER = BLOCKS.register("speaker", () -> new SpeakerBlock(ModRegistry.wood().strength(1.5f)));
        TAPE_PLAYER = BLOCKS.register("tape_player", () -> new TapePlayerBlock(ModRegistry.metal().strength(1.5f)));
        INSULATOR = BLOCKS.register("insulator", () -> new InsulatorBlock(ModRegistry.wood().strength(1.0f)));
        JAMMER = BLOCKS.register("jammer", () -> ElectricCompat.jammer(ModRegistry.metal().strength(3.0f).lightLevel(st -> (Boolean)st.getValue((Property)JammerBlock.POWERED) != false ? 6 : 0)));
        CIRCUIT_BOX = BLOCKS.register("circuit_box", () -> new CircuitBoxBlock(ModRegistry.metal().strength(1.5f, 6.0f).lightLevel(st -> (Boolean)st.getValue((Property)CircuitBoxBlock.LIT) != false ? 5 : 0)));
        DIMENSIONAL_RELAY = BLOCKS.register("dimensional_relay", () -> ElectricCompat.dimensionalRelay(ModRegistry.metal().strength(4.0f, 1200.0f).lightLevel(st -> (Boolean)st.getValue((Property)DimensionalRelayBlock.ACTIVE) != false ? 9 : 2)));
        CREATIVE_TRANSMITTER = BLOCKS.register("creative_transmitter", () -> new CreativeTransmitterBlock(ModRegistry.metal().strength(-1.0f, 3600000.0f).noLootTable()));
        CREATIVE_RECEIVER = BLOCKS.register("creative_receiver", () -> new CreativeReceiverBlock(ModRegistry.metal().strength(-1.0f, 3600000.0f).noLootTable()));
        CREATIVE_ANTENNA = BLOCKS.register("creative_antenna", () -> new CreativeAntennaBlock(ModRegistry.mast(1.0f).strength(-1.0f, 3600000.0f).noLootTable()));
        RADIO_CONSOLE = BLOCKS.register("radio_console", () -> new RadioConsoleBlock(ModRegistry.metal().strength(2.0f)));
        CIRCUIT_WORKBENCH = BLOCKS.register("circuit_workbench", () -> new CircuitWorkbenchBlock(ModRegistry.wood().strength(2.5f)));
        YAGI_ANTENNA = BLOCKS.register("yagi_antenna", () -> new YagiAntennaBlock(ModRegistry.mast(1.5f)));
        SILVER_ORE = BLOCKS.register("silver_ore", () -> new DropExperienceBlock((IntProvider)ConstantInt.of((int)0), ModRegistry.ore(false)));
        DEEPSLATE_SILVER_ORE = BLOCKS.register("deepslate_silver_ore", () -> new DropExperienceBlock((IntProvider)ConstantInt.of((int)0), ModRegistry.ore(true)));
        SILVER_BLOCK = BLOCKS.register("silver_block", () -> new ConnectedBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0f, 6.0f).sound(SoundType.METAL).requiresCorrectToolForDrops()));
        RADIO_CASING = BLOCKS.register("radio_casing", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(2.0f, 6.0f).sound(SoundType.COPPER)));
        RELAY_TOWER_SMALL = BLOCKS.register("relay_tower_small", () -> StructureBlock.createKinetic("relay_tower_small", ModRegistry.structure(ModRegistry.mast(4.0f)), RelayBE::new, new Property[0]));
        RELAY_TOWER_BIG = BLOCKS.register("relay_tower_big", () -> StructureBlock.createKinetic("relay_tower_big", ModRegistry.structure(ModRegistry.mast(5.0f)), RelayBE::new, new Property[0]));
        RADIO_TOWER = BLOCKS.register("radio_tower", () -> StructureBlock.createKinetic("radio_tower", ModRegistry.structure(ModRegistry.mast(6.0f)).lightLevel(st -> st.hasProperty((Property)TowerCoreBE.ACTIVE) && (Boolean)st.getValue((Property)TowerCoreBE.ACTIVE) != false ? 7 : 0), TowerCoreBE::new, new Property[]{TowerCoreBE.CRYSTAL, TowerCoreBE.ACTIVE}));
        COPPER_WIRE = ITEMS.register("copper_wire", () -> new CableItem(CableType.COPPER, new Item.Properties()));
        FREQUENCY_CARD = ITEMS.register("frequency_card", () -> new FrequencyCardItem(new Item.Properties().stacksTo(16)));
        SILVER_INGOT = ModRegistry.part("silver_ingot");
        SILVER_NUGGET = ModRegistry.part("silver_nugget");
        RAW_SILVER = ModRegistry.part("raw_silver");
        SILVER_SHEET = ModRegistry.part("silver_sheet");
        SIGNAL_MECHANISM = ModRegistry.part("signal_mechanism");
        INCOMPLETE_CIRCUIT_BOARD = ModRegistry.part("incomplete_circuit_board");
        INCOMPLETE_SIGNAL_MECHANISM = ModRegistry.part("incomplete_signal_mechanism");
        FIELD_MANUAL = ITEMS.register("field_manual", () -> new FieldManualItem(new Item.Properties().stacksTo(1)));
        COPPER_COIL = ModRegistry.part("copper_coil");
        CRYSTAL_OSCILLATOR = ModRegistry.part("crystal_oscillator");
        CIRCUIT_BOARD = ModRegistry.part("circuit_board");
        SPEAKER_CONE = ModRegistry.part("speaker_cone");
        MIC_CAPSULE = ModRegistry.part("mic_capsule");
        MEMORY_CHIP = ModRegistry.part("memory_chip");
        COUNTER_CHIP = ModRegistry.part("counter_chip");
        PULSE_CHIP = ModRegistry.part("pulse_chip");
        LIGHT_SENSOR = ModRegistry.part("light_sensor");
        RAIN_SENSOR = ModRegistry.part("rain_sensor");
        MOTION_SENSOR = ModRegistry.part("motion_sensor");
        RESISTOR = ModRegistry.part("resistor");
        CAPACITOR = ModRegistry.part("capacitor");
        LED = ModRegistry.part("led");
        TRANSISTOR = ModRegistry.part("transistor");
        BOARDS = new EnumMap<BoardDesign, DeferredItem<Item>>(BoardDesign.class);
        for (BoardDesign d : BoardDesign.values()) {
            BOARDS.put(d, (DeferredItem<Item>)ITEMS.register(d.id, () -> new BoardItem(d, new Item.Properties())));
        }
        RADIO_ITEM = ITEMS.register("radio", () -> new TunableBlockItem((Block)RADIO.get(), new Item.Properties().stacksTo(1)));
        PORTABLE_RADIO = ITEMS.register("portable_radio", () -> new PortableRadioItem(new Item.Properties().stacksTo(1)));
        CREATIVE_TRANSMITTER_ITEM = ITEMS.register("creative_transmitter", () -> new TunableBlockItem((Block)CREATIVE_TRANSMITTER.get(), new Item.Properties().rarity(Rarity.EPIC)));
        CREATIVE_RECEIVER_ITEM = ITEMS.register("creative_receiver", () -> new TunableBlockItem((Block)CREATIVE_RECEIVER.get(), new Item.Properties().rarity(Rarity.EPIC)));
        CREATIVE_ANTENNA_ITEM = ITEMS.register("creative_antenna", () -> new BlockItem((Block)CREATIVE_ANTENNA.get(), new Item.Properties().rarity(Rarity.EPIC)));
        RADIO_CONSOLE_ITEM = ITEMS.register("radio_console", () -> new TunableBlockItem((Block)RADIO_CONSOLE.get(), new Item.Properties().stacksTo(1)));
        TUNING_TABLE_ITEM = ITEMS.register("tuning_table", () -> new BlockItem((Block)TUNING_TABLE.get(), new Item.Properties()));
        TRANSMITTER_ITEMS = new ArrayList<DeferredItem<TunableBlockItem>>();
        RECEIVER_ITEMS = new ArrayList<DeferredItem<TunableBlockItem>>();
        TRANSMITTER_ITEMS.add((DeferredItem<TunableBlockItem>)ITEMS.register("transmitter", () -> new TunableBlockItem((Block)TRANSMITTERS.get(0).get(), new Item.Properties())));
        RECEIVER_ITEMS.add((DeferredItem<TunableBlockItem>)ITEMS.register("receiver", () -> new TunableBlockItem((Block)RECEIVERS.get(0).get(), new Item.Properties())));
        ANTENNA_ITEM = ITEMS.register("antenna", () -> new MastItem((MastBlock)((Object)((Object)ANTENNA.get())), new Item.Properties()));
        YAGI_ANTENNA_ITEM = ModRegistry.blockItem(YAGI_ANTENNA);
        SILVER_ORE_ITEM = ModRegistry.blockItem(SILVER_ORE);
        DEEPSLATE_SILVER_ORE_ITEM = ModRegistry.blockItem(DEEPSLATE_SILVER_ORE);
        SILVER_BLOCK_ITEM = ModRegistry.blockItem(SILVER_BLOCK);
        RADIO_CASING_ITEM = ModRegistry.blockItem(RADIO_CASING);
        CIRCUIT_WORKBENCH_ITEM = ModRegistry.blockItem(CIRCUIT_WORKBENCH);
        RELAY_TOWER_SMALL_ITEM = ITEMS.register("relay_tower_small", () -> new StructureItem.Tuned((StructureBlock)((Object)((Object)RELAY_TOWER_SMALL.get())), new Item.Properties().stacksTo(1)));
        RELAY_TOWER_BIG_ITEM = ITEMS.register("relay_tower_big", () -> new StructureItem.Tuned((StructureBlock)((Object)((Object)RELAY_TOWER_BIG.get())), new Item.Properties().stacksTo(1)));
        RADIO_TOWER_ITEM = ITEMS.register("radio_tower", () -> new StructureItem.Tuned((StructureBlock)((Object)((Object)RADIO_TOWER.get())), new Item.Properties().stacksTo(1)));
        MICROPHONE_ITEM = ITEMS.register("microphone", () -> new BlockItem((Block)MICROPHONE.get(), new Item.Properties()));
        SPEAKER_ITEM = ITEMS.register("speaker", () -> new BlockItem((Block)SPEAKER.get(), new Item.Properties()));
        TAPE_PLAYER_ITEM = ITEMS.register("tape_player", () -> new BlockItem((Block)TAPE_PLAYER.get(), new Item.Properties()));
        TAPE_ITEM = ITEMS.register("tape", () -> new TapeItem(new Item.Properties().stacksTo(1)));
        INSULATOR_ITEM = ITEMS.register("insulator", () -> new BlockItem((Block)INSULATOR.get(), new Item.Properties()));
        JAMMER_ITEM = ITEMS.register("jammer", () -> new BlockItem((Block)JAMMER.get(), new Item.Properties()));
        CIRCUIT_BOX_ITEM = ITEMS.register("circuit_box", () -> new TunableBlockItem((Block)CIRCUIT_BOX.get(), new Item.Properties()));
        DIMENSIONAL_RELAY_ITEM = ITEMS.register("dimensional_relay", () -> new BlockItem((Block)DIMENSIONAL_RELAY.get(), new Item.Properties()));
        RADIO_BE = BES.register("radio", () -> BlockEntityType.Builder.of(RadioBE::new, (Block[])new Block[]{(Block)RADIO.get()}).build(null));
        CREATIVE_TX_BE = BES.register("creative_transmitter", () -> BlockEntityType.Builder.of(CreativeTransmitterBE::new, (Block[])new Block[]{(Block)CREATIVE_TRANSMITTER.get()}).build(null));
        CREATIVE_RX_BE = BES.register("creative_receiver", () -> BlockEntityType.Builder.of(CreativeReceiverBE::new, (Block[])new Block[]{(Block)CREATIVE_RECEIVER.get()}).build(null));
        TOWER_SHAFT_BE = BES.register("tower_shaft", () -> BlockEntityType.Builder.of(KineticStructureBlock.TowerShaftBE::new, (Block[])new Block[]{(Block)RADIO_TOWER.get(), (Block)RELAY_TOWER_SMALL.get(), (Block)RELAY_TOWER_BIG.get()}).build(null));
        CREATIVE_ANTENNA_BE = BES.register("creative_antenna", () -> BlockEntityType.Builder.of(CreativeAntennaBE::new, (Block[])new Block[]{(Block)CREATIVE_ANTENNA.get()}).build(null));
        RADIO_CONSOLE_BE = BES.register("radio_console", () -> BlockEntityType.Builder.of(RadioConsoleBE::new, (Block[])new Block[]{(Block)RADIO_CONSOLE.get()}).build(null));
        TRANSMITTER_BE = BES.register("transmitter", () -> BlockEntityType.Builder.of(ElectricCompat.transmitterBE(), (Block[])ModRegistry.all(TRANSMITTERS)).build(null));
        RECEIVER_BE = BES.register("receiver", () -> BlockEntityType.Builder.of(ReceiverBE::new, (Block[])ModRegistry.all(RECEIVERS)).build(null));
        RELAY_BE = BES.register("relay", () -> BlockEntityType.Builder.of(RelayBE::new, (Block[])new Block[]{(Block)RELAY_TOWER_SMALL.get(), (Block)RELAY_TOWER_BIG.get()}).build(null));
        TOWER_BE = BES.register("radio_tower", () -> BlockEntityType.Builder.of(TowerCoreBE::new, (Block[])new Block[]{(Block)RADIO_TOWER.get()}).build(null));
        MIC_BE = BES.register("microphone", () -> BlockEntityType.Builder.of(MicrophoneBE::new, (Block[])new Block[]{(Block)MICROPHONE.get()}).build(null));
        TAPE_PLAYER_BE = BES.register("tape_player", () -> BlockEntityType.Builder.of(TapePlayerBE::new, (Block[])new Block[]{(Block)TAPE_PLAYER.get()}).build(null));
        SPEAKER_BE = BES.register("speaker", () -> BlockEntityType.Builder.of(SpeakerBE::new, (Block[])new Block[]{(Block)SPEAKER.get()}).build(null));
        YAGI_BE = BES.register("yagi_antenna", () -> BlockEntityType.Builder.of((p, st) -> new YagiAntennaBE(YAGI_BE.get(), p, st), (Block[])new Block[]{(Block)YAGI_ANTENNA.get()}).build(null));
        INSULATOR_BE = BES.register("insulator", () -> BlockEntityType.Builder.of(InsulatorBE::new, (Block[])new Block[]{(Block)INSULATOR.get()}).build(null));
        JAMMER_BE = BES.register("jammer", () -> BlockEntityType.Builder.of(ElectricCompat.jammerBE(), (Block[])new Block[]{(Block)JAMMER.get()}).build(null));
        WORKBENCH_BE = BES.register("circuit_workbench", () -> BlockEntityType.Builder.of(CircuitWorkbenchBE::new, (Block[])new Block[]{(Block)CIRCUIT_WORKBENCH.get()}).build(null));
        CIRCUIT_BOX_BE = BES.register("circuit_box", () -> BlockEntityType.Builder.of(CircuitBoxBE::new, (Block[])new Block[]{(Block)CIRCUIT_BOX.get()}).build(null));
        DIM_RELAY_BE = BES.register("dimensional_relay", () -> BlockEntityType.Builder.of(ElectricCompat.dimensionalRelayBE(), (Block[])new Block[]{(Block)DIMENSIONAL_RELAY.get()}).build(null));
        TUNING_MENU = MENUS.register("tuning_table", () -> IMenuTypeExtension.create((id, inv, buf) -> new TuningTableMenu(id, inv)));
        TAPE_MENU = MENUS.register("tape_player", () -> IMenuTypeExtension.create((id, inv, buf) -> new TapePlayerMenu(id, inv)));
        WORKBENCH_MENU = MENUS.register("circuit_workbench", () -> IMenuTypeExtension.create((id, inv, buf) -> new CircuitWorkbenchMenu(id, inv)));
        TAB = TABS.register("main", () -> CreativeModeTab.builder().title((Component)Component.translatable((String)"itemGroup.airwaves")).icon(() -> new ItemStack((ItemLike)RADIO_ITEM.get())).displayItems((p, out) -> {
            out.accept((ItemLike)FIELD_MANUAL.get());
            out.accept((ItemLike)RADIO_ITEM.get());
            out.accept((ItemLike)PORTABLE_RADIO.get());
            out.accept((ItemLike)TUNING_TABLE_ITEM.get());
            out.accept((ItemLike)MICROPHONE_ITEM.get());
            out.accept((ItemLike)TAPE_PLAYER_ITEM.get());
            out.accept((ItemLike)TAPE_ITEM.get());
            out.accept((ItemLike)SPEAKER_ITEM.get());
            TRANSMITTER_ITEMS.forEach(i -> out.accept((ItemLike)i.get()));
            RECEIVER_ITEMS.forEach(i -> out.accept((ItemLike)i.get()));
            out.accept((ItemLike)ANTENNA_ITEM.get());
            out.accept((ItemLike)YAGI_ANTENNA_ITEM.get());
            out.accept((ItemLike)RELAY_TOWER_SMALL_ITEM.get());
            out.accept((ItemLike)RELAY_TOWER_BIG_ITEM.get());
            out.accept((ItemLike)RADIO_TOWER_ITEM.get());
            out.accept((ItemLike)JAMMER_ITEM.get());
            out.accept((ItemLike)DIMENSIONAL_RELAY_ITEM.get());
            out.accept((ItemLike)CIRCUIT_BOX_ITEM.get());
            out.accept((ItemLike)RADIO_CONSOLE_ITEM.get());
            out.accept((ItemLike)COPPER_WIRE.get());
            out.accept((ItemLike)INSULATOR_ITEM.get());
            out.accept((ItemLike)FREQUENCY_CARD.get());
            out.accept((ItemLike)RADIO_CASING_ITEM.get());
            out.accept((ItemLike)CIRCUIT_WORKBENCH_ITEM.get());
            out.accept((ItemLike)CIRCUIT_BOARD.get());
            out.accept((ItemLike)RESISTOR.get());
            out.accept((ItemLike)CAPACITOR.get());
            out.accept((ItemLike)LED.get());
            out.accept((ItemLike)TRANSISTOR.get());
            out.accept((ItemLike)MEMORY_CHIP.get());
            out.accept((ItemLike)COUNTER_CHIP.get());
            out.accept((ItemLike)PULSE_CHIP.get());
            out.accept((ItemLike)LIGHT_SENSOR.get());
            out.accept((ItemLike)RAIN_SENSOR.get());
            out.accept((ItemLike)MOTION_SENSOR.get());
            out.accept((ItemLike)COPPER_COIL.get());
            out.accept((ItemLike)CRYSTAL_OSCILLATOR.get());
            BOARDS.values().forEach(b -> out.accept((ItemLike)b.get()));
            out.accept((ItemLike)SIGNAL_MECHANISM.get());
            out.accept((ItemLike)SPEAKER_CONE.get());
            out.accept((ItemLike)MIC_CAPSULE.get());
            out.accept((ItemLike)SILVER_ORE_ITEM.get());
            out.accept((ItemLike)DEEPSLATE_SILVER_ORE_ITEM.get());
            out.accept((ItemLike)RAW_SILVER.get());
            out.accept((ItemLike)SILVER_INGOT.get());
            out.accept((ItemLike)SILVER_NUGGET.get());
            out.accept((ItemLike)SILVER_SHEET.get());
            out.accept((ItemLike)SILVER_BLOCK_ITEM.get());
            out.accept((ItemLike)CREATIVE_TRANSMITTER_ITEM.get());
            out.accept((ItemLike)CREATIVE_RECEIVER_ITEM.get());
            out.accept((ItemLike)CREATIVE_ANTENNA_ITEM.get());
        }).build());
    }
}
