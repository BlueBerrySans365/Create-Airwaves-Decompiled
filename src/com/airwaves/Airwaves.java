/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.registry.SimpleRegistry
 *  com.simibubi.create.api.stress.BlockStressValues
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LightningBolt
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.neoforged.fml.loading.FMLEnvironment
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.neoforged.neoforge.event.server.ServerStoppedEvent
 *  net.neoforged.neoforge.event.tick.LevelTickEvent$Post
 */
package com.airwaves;

import com.airwaves.Config;
import com.airwaves.ModRegistry;
import com.airwaves.ModSounds;
import com.airwaves.be.TransmitterBE;
import com.airwaves.cable.ForeignWires;
import com.airwaves.client.ClientInit;
import com.airwaves.compat.svc.SvcState;
import com.airwaves.logic.RadioNetwork;
import com.airwaves.logic.SoundCapture;
import com.airwaves.logic.TapeAudio;
import com.airwaves.net.Net;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@Mod(value="airwaves")
public class Airwaves {
    public static final String MODID = "airwaves";
    public static final String BUILD = "9.9";

    public Airwaves(IEventBus modBus, ModContainer container) {
        ModRegistry.register(modBus);
        ModSounds.register(modBus);
        modBus.addListener(Net::register);
        modBus.addListener(Airwaves::commonSetup);
        container.registerConfig(ModConfig.Type.COMMON, (IConfigSpec)Config.SPEC);
        NeoForge.EVENT_BUS.register((Object)this);
        NeoForge.EVENT_BUS.register(ForeignWires.class);
        NeoForge.EVENT_BUS.register(SoundCapture.class);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientInit.init(modBus);
        }
    }

    @SubscribeEvent
    public void levelTick(LevelTickEvent.Post e) {
        Level level = e.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel sl = (ServerLevel)level;
        if (sl.getGameTime() % 20L == 0L) {
            RadioNetwork.tick(sl);
        }
    }

    private static void commonSetup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            SimpleRegistry impacts = BlockStressValues.IMPACTS;
            for (int i = 0; i < ModRegistry.TRANSMITTERS.size(); ++i) {
                double v = TransmitterBE.STRESS[i];
                impacts.register((Object)((Block)ModRegistry.TRANSMITTERS.get(i).get()), () -> v);
            }
            impacts.register((Object)((Block)ModRegistry.JAMMER.get()), () -> 8.0);
            impacts.register((Object)((Block)ModRegistry.DIMENSIONAL_RELAY.get()), () -> 16.0);
            impacts.register((Object)((Block)ModRegistry.RELAY_TOWER_SMALL.get()), () -> 12.0);
            impacts.register((Object)((Block)ModRegistry.RELAY_TOWER_BIG.get()), () -> 24.0);
            impacts.register((Object)((Block)ModRegistry.RADIO_TOWER.get()), () -> 64.0);
        });
    }

    @SubscribeEvent
    public void login(PlayerEvent.PlayerLoggedInEvent e) {
        CompoundTag tag = e.getEntity().getPersistentData();
        if (tag.getBoolean("airwaves_manual")) {
            return;
        }
        tag.putBoolean("airwaves_manual", true);
        e.getEntity().getInventory().add(new ItemStack((ItemLike)ModRegistry.FIELD_MANUAL.get()));
    }

    @SubscribeEvent
    public void lightning(EntityJoinLevelEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof LightningBolt) {
            LightningBolt b = (LightningBolt)entity;
            entity = e.getLevel();
            if (entity instanceof ServerLevel) {
                ServerLevel sl = (ServerLevel)entity;
                RadioNetwork.lightning(sl, b.position());
            }
        }
    }

    @SubscribeEvent
    public void serverStopped(ServerStoppedEvent e) {
        RadioNetwork.clear();
        SoundCapture.clear();
        TapeAudio.clear();
        SvcState.USING_SVC.clear();
    }
}
