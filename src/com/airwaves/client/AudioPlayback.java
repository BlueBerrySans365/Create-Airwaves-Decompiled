/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.client;

import com.airwaves.client.MuLaw;
import com.airwaves.client.VoiceConfig;
import com.airwaves.compat.SableCompat;
import com.airwaves.net.HandheldAudioPayload;
import com.airwaves.net.RadioAudioPayload;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class AudioPlayback {
    static final float SRC_RATE = 16000.0f;
    static final int FRAME = 320;
    static final int PREBUFFER = 3;
    static final int MAX_QUEUE = 12;
    private static final float[] OUT_RATES = new float[]{48000.0f, 44100.0f, 16000.0f, 32000.0f};
    static final Map<String, Source> SOURCES = new ConcurrentHashMap<String, Source>();
    static volatile float rainLevel;
    static volatile float thunderLevel;
    static volatile float burstAmp;
    static volatile long burstStart;
    static volatile long burstUntil;
    private static volatile Output output;

    private AudioPlayback() {
    }

    static float pan(Player me, double x, double z) {
        double dz;
        double dx = x - me.getX();
        double len = Math.hypot(dx, dz = z - me.getZ());
        if (len < 0.5) {
            return 0.0f;
        }
        double yaw = Math.toRadians(me.getYRot());
        double rx = -Math.cos(yaw);
        double rz = -Math.sin(yaw);
        return (float)Math.max(-1.0, Math.min(1.0, (dx * rx + dz * rz) / len)) * 0.85f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static Source source(String key) {
        Output o = output;
        if (o != null && o.alive) return SOURCES.computeIfAbsent(key, k -> new Source());
        Class<AudioPlayback> clazz = AudioPlayback.class;
        synchronized (AudioPlayback.class) {
            if (output == null || !AudioPlayback.output.alive) {
                if (output != null && System.currentTimeMillis() - AudioPlayback.output.failedAt < 5000L) {
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return null;
                }
                output = new Output();
            }
            o = output;
            // ** MonitorExit[var2_2] (shouldn't be in output)
            if (o.alive) return SOURCES.computeIfAbsent(key, k -> new Source());
            return null;
        }
    }

    static float burstLevel() {
        long now = System.currentTimeMillis();
        if (now >= burstUntil) {
            return 0.0f;
        }
        return burstAmp * (float)(burstUntil - now) / (float)Math.max(1L, burstUntil - burstStart);
    }

    public static void burst(float strength) {
        long now;
        burstStart = now = System.currentTimeMillis();
        burstUntil = now + (long)(300.0f + strength * 600.0f);
        burstAmp = strength;
    }

    public static void playProximity(UUID id, byte[] data) {
        if (VoiceConfig.deafened) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        Player p = mc.level.getPlayerByUUID(id);
        if (p == null || p == mc.player) {
            return;
        }
        double d = p.getEyePosition().distanceTo(mc.player.getEyePosition());
        if (d > 48.0) {
            return;
        }
        float vol = (float)Math.pow(1.0 - d / 48.0, 1.6) * VoiceConfig.voice * 1.3f;
        Source s = AudioPlayback.source("v:" + String.valueOf(id));
        if (s != null) {
            s.push(data, vol, AudioPlayback.pan((Player)mc.player, p.getX(), p.getZ()), 1.0f, 0);
        }
    }

    public static void playRadio(RadioAudioPayload pl) {
        if (VoiceConfig.deafened) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        Vec3 c = SableCompat.rendered(Vec3.atCenterOf((Vec3i)pl.sink()), 1.0f);
        double d = mc.player.getEyePosition().distanceTo(c);
        if (d > 24.0) {
            return;
        }
        float vol = (float)Math.pow(1.0 - d / 24.0, 1.6) * pl.gain() * 1.1f * VoiceConfig.radio;
        Source s = AudioPlayback.source("r:" + pl.sink().asLong() + ":" + String.valueOf(pl.talker()));
        if (s != null) {
            s.push(pl.data(), vol, AudioPlayback.pan((Player)mc.player, c.x, c.z), pl.quality(), pl.fm() ? 2 : 1);
        }
    }

    public static void playHandheld(HandheldAudioPayload p) {
        if (VoiceConfig.deafened) {
            return;
        }
        Source s = AudioPlayback.source("h:" + String.valueOf(p.talker()));
        if (s != null) {
            s.push(p.data(), 0.9f * p.gain() * VoiceConfig.radio, 0.0f, p.quality(), p.fm() ? 2 : 1);
        }
    }

    public static void playSelf(byte[] enc) {
        Source s = AudioPlayback.source("self");
        if (s != null) {
            s.push(enc, 0.8f * VoiceConfig.voice, 0.0f, 1.0f, 0);
        }
    }

    public static void tick(Minecraft mc) {
        if (mc.level == null || mc.player == null) {
            AudioPlayback.stopAll();
            return;
        }
        rainLevel = mc.level.getRainLevel(1.0f);
        thunderLevel = mc.level.getThunderLevel(1.0f);
        long now = System.currentTimeMillis();
        SOURCES.entrySet().removeIf(e -> now - ((Source)e.getValue()).last > 4000L);
    }

    public static void stopSink(BlockPos pos) {
        String prefix = "r:" + pos.asLong() + ":";
        SOURCES.keySet().removeIf(k -> k.startsWith(prefix));
    }

    public static void stopAll() {
        SOURCES.clear();
        Output o = output;
        output = null;
        if (o != null) {
            o.close();
        }
    }

    static final class Output
    implements Runnable {
        SourceDataLine line;
        float outRate = 16000.0f;
        volatile boolean alive = true;
        long failedAt;
        private Thread thread;
        private float prevL;
        private float prevR;

        Output() {
            for (float r : OUT_RATES) {
                try {
                    AudioFormat f = new AudioFormat(r, 16, 2, true, false);
                    this.line = AudioSystem.getSourceDataLine(f);
                    this.line.open(f, (int)((double)(r * 4.0f) * 0.1));
                    this.outRate = r;
                    break;
                }
                catch (Throwable t) {
                    this.line = null;
                }
            }
            if (this.line == null) {
                this.alive = false;
                this.failedAt = System.currentTimeMillis();
                return;
            }
            this.thread = new Thread((Runnable)this, "Airwaves-Audio");
            this.thread.setDaemon(true);
            this.thread.setPriority(9);
            this.thread.start();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                this.line.start();
                double step = 16000.0f / this.outRate;
                int outFrames = (int)Math.round(320.0 / step);
                float[] mix = new float[640];
                byte[] b = new byte[outFrames * 4];
                while (this.alive) {
                    Arrays.fill(mix, 0.0f);
                    for (Source s : SOURCES.values()) {
                        s.mixInto(mix);
                    }
                    for (int o = 0; o < outFrames; ++o) {
                        double pos = (double)o * step;
                        int i0 = (int)pos;
                        double t = pos - (double)i0;
                        float l0 = i0 == 0 ? this.prevL : mix[2 * (i0 - 1)];
                        float r0 = i0 == 0 ? this.prevR : mix[2 * (i0 - 1) + 1];
                        float l1 = mix[2 * Math.min(i0, 319)];
                        float r1 = mix[2 * Math.min(i0, 319) + 1];
                        float L = (float)((double)l0 + (double)(l1 - l0) * t);
                        float R = (float)((double)r0 + (double)(r1 - r0) * t);
                        L /= 1.0f + Math.abs(L) * 0.6f;
                        R /= 1.0f + Math.abs(R) * 0.6f;
                        short sl = (short)(L * 30000.0f);
                        short sr = (short)(R * 30000.0f);
                        b[4 * o] = (byte)sl;
                        b[4 * o + 1] = (byte)(sl >> 8);
                        b[4 * o + 2] = (byte)sr;
                        b[4 * o + 3] = (byte)(sr >> 8);
                    }
                    this.prevL = mix[638];
                    this.prevR = mix[639];
                    this.line.write(b, 0, b.length);
                }
            }
            catch (Throwable throwable) {
            }
            finally {
                try {
                    this.line.stop();
                    this.line.close();
                }
                catch (Throwable throwable) {}
                this.alive = false;
                this.failedAt = System.currentTimeMillis();
            }
        }

        void close() {
            this.alive = false;
            if (this.thread != null) {
                this.thread.interrupt();
            }
        }
    }

    static final class Source {
        private final ArrayDeque<float[]> q = new ArrayDeque();
        private float[] pending = new float[640];
        private int pendingN = 0;
        volatile long last = System.currentTimeMillis();
        private boolean primed;
        private float lastL;
        private float lastR;
        private float hpX;
        private float hpY;
        private float lp;
        private float lp2;
        private float crackle;
        private final Random rnd = new Random();

        Source() {
        }

        private float weatherNoise(int mode) {
            float am = mode == 1 ? 1.0f : 0.3f;
            float bl = AudioPlayback.burstLevel() * am;
            float y = (this.rnd.nextFloat() * 2.0f - 1.0f) * (rainLevel * 0.01f + thunderLevel * 0.025f) * am;
            if (this.rnd.nextFloat() < rainLevel * 0.003f) {
                this.crackle = (this.rnd.nextFloat() - 0.5f) * 0.35f * am;
            }
            if (bl > 0.0f && this.rnd.nextFloat() < 0.012f) {
                this.crackle = (this.rnd.nextFloat() - 0.5f) * 0.9f * bl;
            }
            float f = bl > 0.0f ? (this.rnd.nextFloat() * 2.0f - 1.0f) * bl * 0.25f : 0.0f;
            this.crackle *= 0.86f;
            return y += this.crackle + f;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void push(byte[] mu, float vol, float pan, float qual, int mode) {
            this.last = System.currentTimeMillis();
            float lg = pan <= 0.0f ? 1.0f : 1.0f - pan;
            float rg = pan >= 0.0f ? 1.0f : 1.0f + pan;
            float inv = 1.0f - qual;
            boolean drop = mode == 2 && qual < 0.3f && this.rnd.nextFloat() < (0.3f - qual) * 1.4f;
            for (byte b : mu) {
                float x = (float)MuLaw.dec(b) / 32768.0f;
                if (mode == 1) {
                    this.hpY = 0.96f * (this.hpY + x - this.hpX);
                    this.hpX = x;
                    x = this.hpY;
                    this.lp += 0.6f * (x - this.lp);
                    this.lp2 += 0.6f * (this.lp - this.lp2);
                    x = this.lp2;
                    x = (float)Math.tanh(x * 1.7f) * 0.8f;
                    x = x * (0.45f + 0.55f * qual) + (this.rnd.nextFloat() * 2.0f - 1.0f) * (0.004f + inv * inv * 0.07f);
                    x += this.weatherNoise(1);
                } else if (mode == 2) {
                    this.lp += 0.88f * (x - this.lp);
                    x = drop ? this.lp * 0.1f : this.lp;
                    x = x * (0.6f + 0.4f * qual) + (this.rnd.nextFloat() * 2.0f - 1.0f) * (inv * inv * inv * 0.06f);
                    x += this.weatherNoise(2);
                }
                float y = x * vol;
                this.pending[2 * this.pendingN] = y * lg;
                this.pending[2 * this.pendingN + 1] = y * rg;
                if (++this.pendingN != 320) continue;
                ArrayDeque<float[]> arrayDeque = this.q;
                synchronized (arrayDeque) {
                    this.q.addLast(this.pending);
                    while (this.q.size() > 12) {
                        this.q.pollFirst();
                    }
                }
                this.pending = new float[640];
                this.pendingN = 0;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void mixInto(float[] mix) {
            float[] f;
            ArrayDeque<float[]> arrayDeque = this.q;
            synchronized (arrayDeque) {
                if (!this.primed && this.q.size() >= 3) {
                    this.primed = true;
                }
                float[] fArray = f = this.primed ? this.q.pollFirst() : null;
                if (f == null) {
                    this.primed = false;
                }
            }
            if (f == null) {
                if (this.lastL != 0.0f || this.lastR != 0.0f) {
                    for (int i = 0; i < 80; ++i) {
                        float k = 1.0f - (float)i / 80.0f;
                        int n = 2 * i;
                        mix[n] = mix[n] + this.lastL * k;
                        int n2 = 2 * i + 1;
                        mix[n2] = mix[n2] + this.lastR * k;
                    }
                    this.lastR = 0.0f;
                    this.lastL = 0.0f;
                }
                return;
            }
            boolean fadeIn = this.lastL == 0.0f && this.lastR == 0.0f;
            for (int i = 0; i < 320; ++i) {
                float k = fadeIn && i < 80 ? (float)i / 80.0f : 1.0f;
                int n = 2 * i;
                mix[n] = mix[n] + f[2 * i] * k;
                int n3 = 2 * i + 1;
                mix[n3] = mix[n3] + f[2 * i + 1] * k;
            }
            this.lastL = f[638];
            this.lastR = f[639];
            if (this.lastL == 0.0f && this.lastR == 0.0f) {
                this.lastL = 1.0E-9f;
            }
        }
    }
}
