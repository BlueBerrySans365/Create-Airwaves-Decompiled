/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  de.maxhenkel.voicechat.api.ForgeVoicechatPlugin
 *  de.maxhenkel.voicechat.api.VoicechatApi
 *  de.maxhenkel.voicechat.api.VoicechatConnection
 *  de.maxhenkel.voicechat.api.VoicechatPlugin
 *  de.maxhenkel.voicechat.api.events.EventRegistration
 *  de.maxhenkel.voicechat.api.events.MicrophonePacketEvent
 *  de.maxhenkel.voicechat.api.opus.OpusDecoder
 *  de.maxhenkel.voicechat.api.packets.MicrophonePacket
 *  net.minecraft.server.level.ServerPlayer
 */
package com.airwaves.compat.svc;

import com.airwaves.compat.svc.SvcState;
import com.airwaves.logic.RadioNetwork;
import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;

@ForgeVoicechatPlugin
public class AirwavesVoicechatPlugin
implements VoicechatPlugin {
    private static VoicechatApi api;
    private static final Map<UUID, OpusDecoder> DECODERS;
    private static final Map<UUID, Long> LAST;

    public String getPluginId() {
        return "airwaves";
    }

    public void initialize(VoicechatApi a) {
        api = a;
    }

    public void registerEvents(EventRegistration reg) {
        reg.registerEvent(MicrophonePacketEvent.class, AirwavesVoicechatPlugin::onMic);
    }

    private static void onMic(MicrophonePacketEvent e) {
        short[] pcm48;
        VoicechatConnection con = e.getSenderConnection();
        if (con == null || api == null) {
            return;
        }
        Object object = con.getPlayer().getPlayer();
        if (!(object instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer sp = (ServerPlayer)object;
        UUID id = sp.getUUID();
        if (!SvcState.usesSvc(id)) {
            return;
        }
        byte[] opus = ((MicrophonePacket)e.getPacket()).getOpusEncodedData();
        if (opus == null || opus.length == 0) {
            return;
        }
        long now = System.currentTimeMillis();
        Long last = LAST.put(id, now);
        OpusDecoder dec = DECODERS.computeIfAbsent(id, k -> api.createDecoder());
        if (last != null && now - last > 500L) {
            dec.resetState();
        }
        if ((pcm48 = dec.decode(opus)) == null || pcm48.length < 3) {
            return;
        }
        byte[] frame = AirwavesVoicechatPlugin.toAirwaves(pcm48);
        sp.getServer().execute(() -> {
            if (!sp.isRemoved() && !sp.isSpectator()) {
                RadioNetwork.onVoice(sp, frame);
            }
        });
    }

    private static byte[] toAirwaves(short[] pcm48) {
        int n = pcm48.length / 3;
        byte[] out = new byte[n];
        for (int i = 0; i < n; ++i) {
            int s = (pcm48[i * 3] + pcm48[i * 3 + 1] + pcm48[i * 3 + 2]) / 3;
            out[i] = AirwavesVoicechatPlugin.muLaw((short)Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, s)));
        }
        return out;
    }

    private static byte muLaw(short pcm) {
        int s = pcm;
        int sign = s >> 8 & 0x80;
        if (sign != 0) {
            s = -s;
        }
        if (s > 32635) {
            s = 32635;
        }
        s += 132;
        int exp = 7;
        int mask = 16384;
        while ((s & mask) == 0 && exp > 0) {
            --exp;
            mask >>= 1;
        }
        int mant = s >> exp + 3 & 0xF;
        return (byte)(~(sign | exp << 4 | mant));
    }

    public static void forget(UUID id) {
        OpusDecoder d = DECODERS.remove(id);
        if (d != null) {
            d.close();
        }
        LAST.remove(id);
    }

    static {
        DECODERS = new ConcurrentHashMap<UUID, OpusDecoder>();
        LAST = new ConcurrentHashMap<UUID, Long>();
    }
}
