/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.client;

import com.airwaves.client.AudioPlayback;
import com.airwaves.client.VoiceConfig;
import com.airwaves.compat.SableCompat;
import com.airwaves.net.RadioSoundPayload;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public final class RadioSounds {
    private static final RandomSource RNG = RandomSource.create();
    private static final Map<String, SoundInstance> MUSIC = new HashMap<String, SoundInstance>();

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
            return;
        }
        float q = p.quality();
        if (q < 0.04f) {
            return;
        }
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        if (!p.handheld()) {
            Vec3 c = SableCompat.rendered(Vec3.atCenterOf((Vec3i)p.sink()), 1.0f);
            double d = mc.player.getEyePosition().distanceTo(c);
            int n = p.kind() == 0 ? 32 : 72;
            if (d > (double)n) {
                return;
            }
            x = c.x;
            y = c.y;
            z = c.z;
        }
        if (p.kind() == 0 && RNG.nextFloat() > 0.3f + q) {
            return;
        }
        float vol = Math.max(0.02f, p.volume() * VoiceConfig.radio * (0.35f + 0.65f * q));
        float pitch = p.pitch() * (p.fm() ? 1.0f : 1.0f + (RNG.nextFloat() - 0.5f) * 0.05f * (1.0f - q));
        SoundInstance.Attenuation att = p.handheld() ? SoundInstance.Attenuation.NONE : SoundInstance.Attenuation.LINEAR;
        SoundSource src = p.kind() == 0 ? SoundSource.BLOCKS : SoundSource.RECORDS;
        SimpleSoundInstance inst = new SimpleSoundInstance(p.sound(), src, vol, pitch, RNG, false, 0, att, x, y, z, p.handheld());
        if (p.kind() == 1) {
            RadioSounds.stop(key);
            MUSIC.put(key, (SoundInstance)inst);
        }
        mc.getSoundManager().play((SoundInstance)inst);
        if (q < 0.55f) {
            AudioPlayback.burst(0.12f * (1.0f - q));
        }
    }

    private static void stop(String key) {
        SoundInstance old = MUSIC.remove(key);
        if (old != null) {
            Minecraft.getInstance().getSoundManager().stop(old);
        }
    }

    public static void tick(Minecraft mc) {
        if (mc.level == null) {
            RadioSounds.stopAll();
            return;
        }
        Iterator<Map.Entry<String, SoundInstance>> it = MUSIC.entrySet().iterator();
        while (it.hasNext()) {
            if (mc.getSoundManager().isActive(it.next().getValue())) continue;
            it.remove();
        }
    }

    public static void stopAll() {
        Minecraft mc = Minecraft.getInstance();
        for (SoundInstance s : MUSIC.values()) {
            mc.getSoundManager().stop(s);
        }
        MUSIC.clear();
    }
}
