/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual
 *  dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
 *  net.createmod.ponder.api.registration.PonderPlugin
 *  net.createmod.ponder.foundation.PonderIndex
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 *  net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent$LoggingIn
 *  net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Post
 *  net.neoforged.neoforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.neoforged.neoforge.client.event.ModelEvent$RegisterAdditional
 *  net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
 *  net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
 *  net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent$Stage
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 *  net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.network.PacketDistributor
 *  net.neoforged.neoforge.registries.DeferredHolder
 */
package com.airwaves.client;

import com.airwaves.ModRegistry;
import com.airwaves.cable.NodeRef;
import com.airwaves.client.AirwavesSpriteShifts;
import com.airwaves.client.AudioPlayback;
import com.airwaves.client.BoardItemRenderer;
import com.airwaves.client.CableRenderer;
import com.airwaves.client.CircuitWorkbenchScreen;
import com.airwaves.client.ClientData;
import com.airwaves.client.InsulatorRenderer;
import com.airwaves.client.RadioSounds;
import com.airwaves.client.ShiftTooltips;
import com.airwaves.client.TapePlayerScreen;
import com.airwaves.client.TuningTableScreen;
import com.airwaves.client.VoiceClient;
import com.airwaves.client.VoiceConfig;
import com.airwaves.client.VoiceMenuScreen;
import com.airwaves.client.YagiRenderer;
import com.airwaves.client.ponder.AirwavesPonderPlugin;
import com.airwaves.item.CableItem;
import com.airwaves.kinetic.KineticDeviceRenderer;
import com.airwaves.logic.Cables;
import com.airwaves.logic.ClientSinks;
import com.airwaves.net.VoiceModePayload;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import java.util.List;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ClientInit {
    public static final KeyMapping VOICE_KEY = new KeyMapping("key.airwaves.voice_menu", 86, "key.categories.airwaves");
    public static final KeyMapping PTT_KEY = new KeyMapping("key.airwaves.push_to_talk", 280, "key.categories.airwaves");
    private static int ticks;

    private ClientInit() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientInit::keys);
        modBus.addListener(ClientInit::screens);
        modBus.addListener(ClientInit::renderers);
        modBus.addListener(ClientInit::layers);
        modBus.addListener(ClientInit::models);
        modBus.addListener(ClientInit::setup);
        modBus.addListener(ClientInit::itemExtensions);
        modBus.addListener(e -> e.register(YagiRenderer.ARMS));
        AirwavesSpriteShifts.init(modBus);
        NeoForge.EVENT_BUS.addListener(ClientInit::tick);
        NeoForge.EVENT_BUS.addListener(ClientInit::login);
        NeoForge.EVENT_BUS.addListener(ClientInit::logout);
        NeoForge.EVENT_BUS.addListener(ClientInit::renderWorld);
        NeoForge.EVENT_BUS.addListener(ShiftTooltips::onTooltip);
    }

    private static void keys(RegisterKeyMappingsEvent e) {
        e.register(VOICE_KEY);
        e.register(PTT_KEY);
    }

    private static void screens(RegisterMenuScreensEvent e) {
        e.register(ModRegistry.TUNING_MENU.get(), TuningTableScreen::new);
        e.register(ModRegistry.WORKBENCH_MENU.get(), CircuitWorkbenchScreen::new);
        e.register(ModRegistry.TAPE_MENU.get(), TapePlayerScreen::new);
    }

    private static void renderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerBlockEntityRenderer(ModRegistry.MIC_BE.get(), ctx -> new CableRenderer());
        e.registerBlockEntityRenderer(ModRegistry.SPEAKER_BE.get(), ctx -> new CableRenderer());
        e.registerBlockEntityRenderer(ModRegistry.TRANSMITTER_BE.get(), KineticDeviceRenderer::new);
        e.registerBlockEntityRenderer(ModRegistry.RECEIVER_BE.get(), ctx -> new CableRenderer());
        e.registerBlockEntityRenderer(ModRegistry.CREATIVE_TX_BE.get(), ctx -> new CableRenderer());
        e.registerBlockEntityRenderer(ModRegistry.CREATIVE_RX_BE.get(), ctx -> new CableRenderer());
        e.registerBlockEntityRenderer(ModRegistry.INSULATOR_BE.get(), ctx -> new InsulatorRenderer());
        e.registerBlockEntityRenderer(ModRegistry.YAGI_BE.get(), YagiRenderer::new);
        e.registerBlockEntityRenderer(ModRegistry.JAMMER_BE.get(), KineticDeviceRenderer::new);
        e.registerBlockEntityRenderer(ModRegistry.DIM_RELAY_BE.get(), KineticDeviceRenderer::new);
        e.registerBlockEntityRenderer(ModRegistry.RELAY_BE.get(), KineticDeviceRenderer::new);
        e.registerBlockEntityRenderer(ModRegistry.TOWER_BE.get(), KineticDeviceRenderer::new);
        e.registerBlockEntityRenderer(ModRegistry.TOWER_SHAFT_BE.get(), KineticDeviceRenderer::new);
    }

    private static void setup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            SimpleBlockEntityVisualizer.builder(ModRegistry.TRANSMITTER_BE.get()).factory(SingleAxisRotatingVisual::shaft).skipVanillaRender(be -> false).apply();
            SimpleBlockEntityVisualizer.builder(ModRegistry.JAMMER_BE.get()).factory(SingleAxisRotatingVisual::shaft).skipVanillaRender(be -> false).apply();
            SimpleBlockEntityVisualizer.builder(ModRegistry.DIM_RELAY_BE.get()).factory(SingleAxisRotatingVisual::shaft).skipVanillaRender(be -> false).apply();
            SimpleBlockEntityVisualizer.builder(ModRegistry.TOWER_BE.get()).factory(SingleAxisRotatingVisual::shaft).skipVanillaRender(be -> false).apply();
            SimpleBlockEntityVisualizer.builder(ModRegistry.RELAY_BE.get()).factory(SingleAxisRotatingVisual::shaft).skipVanillaRender(be -> false).apply();
            SimpleBlockEntityVisualizer.builder(ModRegistry.TOWER_SHAFT_BE.get()).factory(SingleAxisRotatingVisual::shaft).skipVanillaRender(be -> false).apply();
            PonderIndex.addPlugin((PonderPlugin)new AirwavesPonderPlugin());
        });
    }

    private static void models(ModelEvent.RegisterAdditional e) {
        e.register(InsulatorRenderer.SPOOL);
        e.register(InsulatorRenderer.SPOOL_WOUND);
    }

    private static void layers(RegisterGuiLayersEvent e) {
        e.registerAboveAll(ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"radio_hud"), (g, dt) -> ClientInit.hud(g));
    }

    private static void pill(GuiGraphics g, int x, int y, int w, int h, int accent) {
        g.fill(x, y, x + w, y + h, -1072688618);
        g.fill(x, y, x + 2, y + h, accent);
        g.fill(x, y, x + w, y + 1, 0x40FFFFFF);
    }

    private static void hud(GuiGraphics g) {
        ItemStack st;
        NodeRef start;
        int c;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.screen != null) {
            return;
        }
        Font font = mc.font;
        boolean ptt = VoiceConfig.pushToTalk;
        int n = VoiceClient.error != null ? -2056144 : (VoiceConfig.muted ? -2078648 : (c = VoiceClient.speaking ? -11214736 : -7695456));
        String t = VoiceClient.error != null ? "No mic" : (VoiceConfig.muted ? "Muted" : (VoiceClient.speaking ? "Live" : (ptt ? "PTT: " + PTT_KEY.getTranslatedKeyMessage().getString() : "Voice")));
        int w = font.width(t) + 30;
        ClientInit.pill(g, 4, 4, w, 14, c);
        g.fill(10, 6, 14, 12, c);
        g.fill(9, 11, 15, 12, c);
        g.fill(11, 12, 13, 15, c);
        g.fill(9, 15, 15, 16, c);
        if (VoiceConfig.muted) {
            for (int i = 0; i < 9; ++i) {
                g.fill(8 + i, 6 + i, 9 + i, 7 + i, -1);
            }
        }
        g.drawString(font, t, 20, 7, -1512208, false);
        if (VoiceClient.speaking) {
            float lv = Math.min(1.0f, VoiceClient.level * 6.0f);
            g.fill(20, 16, 20 + (int)((float)(w - 24) * lv), 17, c);
        }
        int y = 22;
        if (ClientData.hhOn) {
            String s = ClientData.hhLabel;
            int bars = ClientData.hhCarrier ? 1 + Math.round(ClientData.hhQuality * 4.0f) : 0;
            int ww = font.width(s) + 40;
            ClientInit.pill(g, 4, y, ww, 14, ClientData.hhCarrier ? -11220768 : -9800832);
            g.drawString(font, s, 10, y + 3, -1512208, false);
            for (int i = 0; i < 5; ++i) {
                g.fill(ww - 26 + i * 4, y + 10 - i * 2, ww - 23 + i * 4, y + 11, i < bars ? -11214736 : -12959670);
            }
            y += 18;
        }
        NodeRef nodeRef = start = (st = mc.player.getMainHandItem()).getItem() instanceof CableItem ? CableItem.start(st) : null;
        if (start != null) {
            int max = ((CableItem)st.getItem()).type.maxLen;
            double len = CableRenderer.anchor((Level)mc.level, start.pos(), start.slot()).distanceTo(ClientInit.cursor(mc));
            boolean far = len > (double)max;
            int cx = mc.getWindow().getGuiScaledWidth() / 2;
            int cy = mc.getWindow().getGuiScaledHeight() / 2 + 12;
            ClientInit.pill(g, cx - 34, cy, 68, 8, far ? -2078648 : -2588598);
            int fillW = (int)(60.0 * Math.min(1.0, len / (double)max));
            g.fill(cx - 30, cy + 3, cx - 30 + fillW, cy + 5, far ? -40864 : -1533344);
            g.fill(cx - 30 + fillW, cy + 3, cx + 30, cy + 5, -14013388);
        }
    }

    private static Vec3 cursor(Minecraft mc) {
        HitResult h = mc.hitResult;
        if (h != null && h.getType() == HitResult.Type.BLOCK) {
            NodeRef r = CableItem.refAt((Level)mc.level, ((BlockHitResult)h).getBlockPos(), h.getLocation());
            return r != null ? CableRenderer.anchor((Level)mc.level, r.pos(), r.slot()) : h.getLocation();
        }
        return mc.player.getEyePosition().add(mc.player.getLookAngle().scale(2.5));
    }

    private static void renderWorld(RenderLevelStageEvent e) {
        Vec3 b;
        NodeRef tgt;
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }
        ItemStack st = mc.player.getMainHandItem();
        Item item = st.getItem();
        if (!(item instanceof CableItem)) {
            return;
        }
        CableItem ci = (CableItem)item;
        NodeRef start = CableItem.start(st);
        if (start == null || !mc.level.isLoaded(start.pos())) {
            return;
        }
        Vec3 a = CableRenderer.anchor((Level)mc.level, start.pos(), start.slot());
        int col = ci.type.color;
        HitResult h = mc.hitResult;
        NodeRef nodeRef = tgt = h != null && h.getType() == HitResult.Type.BLOCK ? CableItem.refAt((Level)mc.level, ((BlockHitResult)h).getBlockPos(), h.getLocation()) : null;
        if (tgt != null) {
            b = CableRenderer.anchor((Level)mc.level, tgt.pos(), tgt.slot());
            col = tgt.equals(start) ? ci.type.color : (Cables.canLink((Level)mc.level, start, tgt, ci.type) ? -8323184 : -40864);
        } else {
            Vec3 vec3 = b = h != null && h.getType() == HitResult.Type.BLOCK ? h.getLocation() : mc.player.getEyePosition().add(mc.player.getLookAngle().scale(2.5));
            if (a.distanceTo(b) > (double)ci.type.maxLen) {
                col = -40864;
            }
        }
        Vec3 cam = e.getCamera().getPosition();
        PoseStack ps = e.getPoseStack();
        ps.pushPose();
        ps.translate(a.x - cam.x, a.y - cam.y, a.z - cam.z);
        MultiBufferSource.BufferSource bs = mc.renderBuffers().bufferSource();
        int light = LevelRenderer.getLightColor((BlockAndTintGetter)mc.level, (BlockPos)BlockPos.containing((Position)a));
        CableRenderer.line(ps, (MultiBufferSource)bs, Vec3.ZERO, b.subtract(a), light, light, col, ci.type.width);
        bs.endBatch(RenderType.entitySolid((ResourceLocation)CableRenderer.WHITE));
        ps.popPose();
    }

    private static void tick(ClientTickEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        while (VOICE_KEY.consumeClick()) {
            if (mc.screen != null || mc.player == null) continue;
            mc.setScreen((Screen)new VoiceMenuScreen());
        }
        VoiceClient.flush();
        if (++ticks % 2 == 0) {
            AudioPlayback.tick(mc);
        }
        if (ticks % 10 == 0) {
            RadioSounds.tick(mc);
        }
    }

    private static void login(ClientPlayerNetworkEvent.LoggingIn e) {
        VoiceConfig.load();
        VoiceClient.start();
        ClientSinks.GONE = RadioSounds::stopAt;
        ClientInit.sendVoiceMode();
    }

    public static void sendVoiceMode() {
        try {
            PacketDistributor.sendToServer((CustomPacketPayload)new VoiceModePayload(VoiceConfig.svcActive()), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void logout(ClientPlayerNetworkEvent.LoggingOut e) {
        VoiceClient.stop();
        AudioPlayback.stopAll();
        RadioSounds.stopAll();
        ClientData.hhOn = false;
        ClientData.spectrum = List.of();
    }

    private static void itemExtensions(RegisterClientExtensionsEvent e) {
        IClientItemExtensions ext = new IClientItemExtensions(){
            private BoardItemRenderer renderer;

            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new BoardItemRenderer();
                }
                return this.renderer;
            }
        };
        e.registerItem(ext, (Item[])ModRegistry.BOARDS.values().stream().map(DeferredHolder::get).toArray(Item[]::new));
    }
}
