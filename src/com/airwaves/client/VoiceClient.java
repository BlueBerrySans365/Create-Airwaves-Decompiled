/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.neoforged.neoforge.network.PacketDistributor
 */
package com.airwaves.client;

import com.airwaves.client.AudioPlayback;
import com.airwaves.client.ClientInit;
import com.airwaves.client.MuLaw;
import com.airwaves.client.VoiceConfig;
import com.airwaves.net.VoiceFramePayload;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public final class VoiceClient {
    public static final float RATE = 16000.0f;
    private static final AudioFormat[] TRY = new AudioFormat[]{new AudioFormat(16000.0f, 16, 1, true, false), new AudioFormat(48000.0f, 16, 1, true, false), new AudioFormat(44100.0f, 16, 1, true, false), new AudioFormat(48000.0f, 16, 2, true, false), new AudioFormat(44100.0f, 16, 2, true, false), new AudioFormat(32000.0f, 16, 1, true, false)};
    public static volatile float level;
    public static volatile float floor;
    public static volatile boolean speaking;
    public static volatile String error;
    public static final float[] HISTORY;
    public static final boolean[] HIST_SPEAK;
    public static volatile int histPos;
    private static final ConcurrentLinkedQueue<byte[]> OUT;
    private static volatile boolean running;
    private static TargetDataLine line;
    private static AudioFormat fmt;

    private VoiceClient() {
    }

    public static List<String> devices() {
        ArrayList<String> out = new ArrayList<String>();
        out.add("Default");
        block2: for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
            try {
                Mixer m = AudioSystem.getMixer(mi);
                for (AudioFormat f : TRY) {
                    if (!m.isLineSupported(new DataLine.Info(TargetDataLine.class, f))) continue;
                    if (out.contains(mi.getName())) continue block2;
                    out.add(mi.getName());
                    continue block2;
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return out;
    }

    private static TargetDataLine open(String name) throws LineUnavailableException {
        LineUnavailableException last = null;
        for (AudioFormat f : TRY) {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, f);
            try {
                TargetDataLine l = null;
                if (name != null && !name.isEmpty() && !name.equals("Default")) {
                    for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
                        if (!name.equals(mi.getName()) || !AudioSystem.getMixer(mi).isLineSupported(info)) continue;
                        l = (TargetDataLine)AudioSystem.getMixer(mi).getLine(info);
                    }
                }
                if (l == null && AudioSystem.isLineSupported(info)) {
                    l = (TargetDataLine)AudioSystem.getLine(info);
                }
                if (l == null) continue;
                l.open(f, (int)(f.getFrameRate() * (float)f.getFrameSize() / 5.0f));
                fmt = f;
                return l;
            }
            catch (LineUnavailableException e) {
                last = e;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        throw last != null ? last : new LineUnavailableException("no supported microphone format");
    }

    public static synchronized void start() {
        if (running) {
            return;
        }
        if (VoiceConfig.svcActive()) {
            error = null;
            return;
        }
        try {
            line = VoiceClient.open(VoiceConfig.device);
            line.start();
            error = null;
        }
        catch (Throwable t) {
            error = "No microphone (" + t.getClass().getSimpleName() + ")";
            line = null;
            return;
        }
        running = true;
        TargetDataLine l = line;
        AudioFormat f = fmt;
        Thread th = new Thread(() -> VoiceClient.loop(l, f), "Airwaves-Mic");
        th.setDaemon(true);
        th.start();
    }

    public static synchronized void stop() {
        running = false;
        if (line != null) {
            try {
                line.stop();
                line.close();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            line = null;
        }
        OUT.clear();
        level = 0.0f;
        speaking = false;
    }

    public static void restart() {
        VoiceClient.stop();
        VoiceClient.start();
    }

    public static void flush() {
        byte[] f;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) {
            OUT.clear();
            return;
        }
        byte[] pack = new byte[960];
        int n = 0;
        while ((f = OUT.poll()) != null) {
            if (n + f.length > pack.length) {
                PacketDistributor.sendToServer((CustomPacketPayload)new VoiceFramePayload(Arrays.copyOf(pack, n)), (CustomPacketPayload[])new CustomPacketPayload[0]);
                n = 0;
            }
            System.arraycopy(f, 0, pack, n, f.length);
            n += f.length;
        }
        if (n > 0) {
            PacketDistributor.sendToServer((CustomPacketPayload)new VoiceFramePayload(Arrays.copyOf(pack, n)), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
    }

    private static void loop(TargetDataLine l, AudioFormat f) {
        int ch = f.getChannels();
        double step = f.getSampleRate() / 16000.0f;
        int inFrames = (int)Math.round(320.0 * step);
        byte[] buf = new byte[inFrames * 2 * ch];
        short[] s = new short[320];
        int hang = 0;
        float agc = 1.0f;
        ArrayDeque<byte[]> preroll = new ArrayDeque<byte[]>();
        while (running) {
            float gate;
            float rms;
            int n;
            try {
                int r;
                for (n = 0; n < buf.length && running && (r = l.read(buf, n, buf.length - n)) > 0; n += r) {
                }
            }
            catch (Throwable t) {
                break;
            }
            if (n < buf.length) continue;
            double sum = 0.0;
            for (int i = 0; i < 320; ++i) {
                int a = (int)((double)i * step);
                int b = Math.max(a + 1, (int)((double)(i + 1) * step));
                long acc = 0L;
                int c = 0;
                for (int k = a; k < b && k < inFrames; ++k) {
                    for (int cc = 0; cc < ch; ++cc) {
                        int o = (k * ch + cc) * 2;
                        acc += (long)((short)(buf[o] & 0xFF | buf[o + 1] << 8));
                        ++c;
                    }
                }
                float v = c == 0 ? 0.0f : (float)acc / (float)c * VoiceConfig.input * agc;
                v = Math.max(-32768.0f, Math.min(32767.0f, v));
                s[i] = (short)v;
                sum += (double)v * (double)v;
            }
            level = rms = (float)Math.sqrt(sum / 320.0) / 32768.0f;
            if (!speaking) {
                floor = floor * 0.995f + Math.min(rms, 0.05f) * 0.005f;
            }
            if (rms > (gate = Math.max(VoiceConfig.threshold, floor * 2.6f + 0.0015f))) {
                hang = 18;
            } else if (hang > 0) {
                --hang;
            }
            boolean open = VoiceConfig.pushToTalk ? ClientInit.PTT_KEY.isDown() : hang > 0;
            boolean bl = speaking = open && !VoiceConfig.muted;
            if (speaking && rms > 5.0E-4f) {
                agc = Math.max(0.6f, Math.min(4.0f, agc + (0.12f / rms - agc) * 0.02f));
            }
            int hp = histPos;
            VoiceClient.HISTORY[hp] = rms;
            VoiceClient.HIST_SPEAK[hp] = speaking;
            histPos = (hp + 1) % HISTORY.length;
            byte[] enc = new byte[320];
            for (int i = 0; i < 320; ++i) {
                enc[i] = MuLaw.enc(s[i]);
            }
            if (VoiceConfig.monitor && (speaking || VoiceConfig.monitor)) {
                AudioPlayback.playSelf(enc);
            }
            if (speaking) {
                while (!preroll.isEmpty()) {
                    OUT.add((byte[])preroll.pollFirst());
                }
                OUT.add(enc);
                while (OUT.size() > 15) {
                    OUT.poll();
                }
                continue;
            }
            preroll.addLast(enc);
            if (preroll.size() <= 3) continue;
            preroll.pollFirst();
        }
        speaking = false;
    }

    static {
        floor = 0.002f;
        HISTORY = new float[150];
        HIST_SPEAK = new boolean[150];
        OUT = new ConcurrentLinkedQueue();
    }
}
