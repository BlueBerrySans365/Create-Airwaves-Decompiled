/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.Sound
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.client.sounds.AudioStream
 *  net.minecraft.client.sounds.SoundBufferLibrary
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.client;

import com.airwaves.client.AudioPlayback;
import com.airwaves.client.VoiceConfig;
import com.airwaves.compat.SableCompat;
import com.airwaves.net.RadioSoundPayload;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public final class RadioSounds {
    private static final RandomSource RNG = RandomSource.create();
    private static final long TIMEOUT_MS = 3500L;
    private static final double MUSIC_RANGE = 72.0;
    private static final Map<String, Track> MUSIC = new HashMap<String, Track>();

    private RadioSounds() {
    }

    public static void play(RadioSoundPayload p) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        String key = (p.handheld() ? "h" : Long.toString(p.sink().asLong())) + ":" + p.key() + ":" + String.valueOf(p.sound());
        if (p.kind() == 2) {
            RadioSounds.stop(key);
            return;
        }
        if (VoiceConfig.deafened) {
            RadioSounds.stop(key);
            return;
        }
        float q = p.quality();
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        Vec3 at = null;
        if (!p.handheld()) {
            at = SableCompat.rendered(Vec3.atCenterOf((Vec3i)p.sink()), 1.0f);
            x = at.x;
            y = at.y;
            z = at.z;
        }
        if (p.kind() == 1) {
            RadioSounds.music(mc, p, key, at, q);
            return;
        }
        if (q < 0.04f) {
            return;
        }
        if (at != null && mc.player.getEyePosition().distanceTo(at) > 32.0) {
            return;
        }
        if (RNG.nextFloat() > 0.3f + q) {
            return;
        }
        SimpleSoundInstance inst = new SimpleSoundInstance(p.sound(), SoundSource.BLOCKS, RadioSounds.volume(p, q), RadioSounds.pitch(p, q), RNG, false, 0, p.handheld() ? SoundInstance.Attenuation.NONE : SoundInstance.Attenuation.LINEAR, x, y, z, p.handheld());
        mc.getSoundManager().play((SoundInstance)inst);
        if (q < 0.55f) {
            AudioPlayback.burst(0.12f * (1.0f - q));
        }
    }

    private static void music(Minecraft mc, RadioSoundPayload p, String key, Vec3 at, float q) {
        boolean inRange;
        Track t = MUSIC.get(key);
        boolean bl = inRange = q >= 0.04f && (at == null || mc.player.getEyePosition().distanceTo(at) <= 72.0);
        if (!inRange) {
            RadioSounds.stop(key);
            return;
        }
        if (t != null) {
            t.seen = System.currentTimeMillis();
            return;
        }
        t = new Track();
        t.seen = System.currentTimeMillis();
        t.at = at;
        t.inst = new SeekingSound(p.sound(), RadioSounds.volume(p, q), RadioSounds.pitch(p, q), at, p.handheld(), p.offsetMs());
        MUSIC.put(key, t);
        mc.getSoundManager().play(t.inst);
        if (q < 0.55f) {
            AudioPlayback.burst(0.12f * (1.0f - q));
        }
    }

    private static float volume(RadioSoundPayload p, float q) {
        return Math.max(0.02f, p.volume() * VoiceConfig.radio * (0.35f + 0.65f * q));
    }

    private static float pitch(RadioSoundPayload p, float q) {
        return p.pitch() * (p.fm() ? 1.0f : 1.0f + (RNG.nextFloat() - 0.5f) * 0.05f * (1.0f - q));
    }

    private static void stop(String key) {
        Track old = MUSIC.remove(key);
        if (old != null) {
            Minecraft.getInstance().getSoundManager().stop(old.inst);
        }
    }

    public static void tick(Minecraft mc) {
        if (mc.level == null || mc.player == null) {
            RadioSounds.stopAll();
            return;
        }
        long now = System.currentTimeMillis();
        Vec3 eye = mc.player.getEyePosition();
        Iterator<Map.Entry<String, Track>> it = MUSIC.entrySet().iterator();
        while (it.hasNext()) {
            Track t = it.next().getValue();
            if (now - t.seen <= 3500L && (t.at == null || !(eye.distanceTo(t.at) > 80.0))) continue;
            mc.getSoundManager().stop(t.inst);
            it.remove();
        }
    }

    public static void stopAt(BlockPos pos) {
        String prefix = pos.asLong() + ":";
        Minecraft mc = Minecraft.getInstance();
        Iterator<Map.Entry<String, Track>> it = MUSIC.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Track> e = it.next();
            if (!e.getKey().startsWith(prefix)) continue;
            mc.getSoundManager().stop(e.getValue().inst);
            it.remove();
        }
        AudioPlayback.stopSink(pos);
    }

    public static void stopAll() {
        Minecraft mc = Minecraft.getInstance();
        for (Track t : MUSIC.values()) {
            mc.getSoundManager().stop(t.inst);
        }
        MUSIC.clear();
    }

    private static final class Track {
        SoundInstance inst;
        long seen;
        Vec3 at;

        private Track() {
        }
    }

    private static final class SeekingSound
    extends SimpleSoundInstance {
        private final int offsetMs;

        SeekingSound(ResourceLocation id, float vol, float pitch, Vec3 at, boolean handheld, int offsetMs) {
            super(id, SoundSource.RECORDS, vol, pitch, RNG, false, 0, handheld ? SoundInstance.Attenuation.NONE : SoundInstance.Attenuation.LINEAR, at == null ? 0.0 : at.x, at == null ? 0.0 : at.y, at == null ? 0.0 : at.z, handheld);
            this.offsetMs = offsetMs;
        }

        public CompletableFuture<AudioStream> getStream(SoundBufferLibrary lib, Sound sound, boolean looping) {
            CompletableFuture f = lib.getStream(sound.getPath(), looping);
            if (this.offsetMs < 250) {
                return f;
            }
            return f.thenApply(s -> {
                SeekingSound.skip(s, this.offsetMs);
                return s;
            });
        }

        private static void skip(AudioStream s, int ms) {
            try {
                ByteBuffer b;
                AudioFormat fmt = s.getFormat();
                for (long bytes = (long)((double)ms / 1000.0 * (double)fmt.getSampleRate()) * (long)fmt.getChannels() * (long)Math.max(1, fmt.getSampleSizeInBits() / 8); bytes > 0L && (b = s.read((int)Math.min(bytes, 32768L))) != null && b.remaining() != 0; bytes -= (long)b.remaining()) {
                }
            }
            catch (IOException | RuntimeException exception) {
                // empty catch block
            }
        }
    }
}
