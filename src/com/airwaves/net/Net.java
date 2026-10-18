/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.neoforge.network.PacketDistributor
 *  net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 *  net.neoforged.neoforge.network.registration.PayloadRegistrar
 */
package com.airwaves.net;

import com.airwaves.ModRegistry;
import com.airwaves.Tuning;
import com.airwaves.client.ClientHandlers;
import com.airwaves.compat.svc.SvcState;
import com.airwaves.logic.RadioNetwork;
import com.airwaves.menu.TuningTableMenu;
import com.airwaves.net.CablePulsePayload;
import com.airwaves.net.HandheldAudioPayload;
import com.airwaves.net.HandheldStatePayload;
import com.airwaves.net.RadioAudioPayload;
import com.airwaves.net.RadioSoundPayload;
import com.airwaves.net.SpectrumPayload;
import com.airwaves.net.SpectrumRequestPayload;
import com.airwaves.net.StaticBurstPayload;
import com.airwaves.net.TunePayload;
import com.airwaves.net.VoiceFramePayload;
import com.airwaves.net.VoiceModePayload;
import com.airwaves.net.VoiceRelayPayload;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class Net {
    private Net() {
    }

    public static void register(RegisterPayloadHandlersEvent e) {
        PayloadRegistrar r = e.registrar("3");
        r.playToServer(TunePayload.TYPE, TunePayload.CODEC, Net::onTune);
        r.playToServer(VoiceFramePayload.TYPE, VoiceFramePayload.CODEC, Net::onVoice);
        r.playToClient(VoiceRelayPayload.TYPE, VoiceRelayPayload.CODEC, ClientHandlers::onVoiceRelay);
        r.playToClient(RadioAudioPayload.TYPE, RadioAudioPayload.CODEC, ClientHandlers::onRadioAudio);
        r.playToServer(SpectrumRequestPayload.TYPE, SpectrumRequestPayload.CODEC, Net::onSpectrumRequest);
        r.playToClient(SpectrumPayload.TYPE, SpectrumPayload.CODEC, ClientHandlers::onSpectrum);
        r.playToClient(HandheldAudioPayload.TYPE, HandheldAudioPayload.CODEC, ClientHandlers::onHandheldAudio);
        r.playToClient(HandheldStatePayload.TYPE, HandheldStatePayload.CODEC, ClientHandlers::onHandheldState);
        r.playToClient(CablePulsePayload.TYPE, CablePulsePayload.CODEC, ClientHandlers::onPulse);
        r.playToClient(StaticBurstPayload.TYPE, StaticBurstPayload.CODEC, ClientHandlers::onBurst);
        r.playToClient(RadioSoundPayload.TYPE, RadioSoundPayload.CODEC, ClientHandlers::onRadioSound);
        r.playToServer(VoiceModePayload.TYPE, VoiceModePayload.CODEC, Net::onVoiceMode);
    }

    private static void onTune(TunePayload p, IPayloadContext ctx) {
        Player player;
        block5: {
            block4: {
                player = ctx.player();
                if (!(player instanceof ServerPlayer)) break block4;
                ServerPlayer sp = (ServerPlayer)player;
                player = sp.containerMenu;
                if (player instanceof TuningTableMenu) break block5;
            }
            return;
        }
        TuningTableMenu m = (TuningTableMenu)player;
        ItemStack s = m.getSlot(0).getItem();
        if (s.isEmpty()) {
            return;
        }
        s.set((DataComponentType)ModRegistry.TUNING.get(), (Object)new Tuning(p.fm(), p.freq()).clamped());
        m.broadcastChanges();
    }

    private static void onSpectrumRequest(SpectrumRequestPayload p, IPayloadContext ctx) {
        Player player = ctx.player();
        if (player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)player;
            PacketDistributor.sendToPlayer((ServerPlayer)sp, (CustomPacketPayload)new SpectrumPayload(RadioNetwork.spectrum(sp)), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
    }

    private static void onVoiceMode(VoiceModePayload p, IPayloadContext ctx) {
        Player player = ctx.player();
        if (player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)player;
            SvcState.set(sp.getUUID(), p.simpleVoiceChat());
        }
    }

    private static void onVoice(VoiceFramePayload p, IPayloadContext ctx) {
        ServerPlayer sp;
        Player player = ctx.player();
        if (!(player instanceof ServerPlayer) || (sp = (ServerPlayer)player).isSpectator()) {
            return;
        }
        if (SvcState.usesSvc(sp.getUUID())) {
            return;
        }
        byte[] d = p.data();
        if (d.length < 16 || d.length > 960) {
            return;
        }
        PacketDistributor.sendToPlayersNear((ServerLevel)sp.serverLevel(), (ServerPlayer)sp, (double)sp.getX(), (double)sp.getY(), (double)sp.getZ(), (double)48.0, (CustomPacketPayload)new VoiceRelayPayload(sp.getUUID(), d), (CustomPacketPayload[])new CustomPacketPayload[0]);
        RadioNetwork.onVoice(sp, d);
    }
}
