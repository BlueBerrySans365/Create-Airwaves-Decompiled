/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.Sound
 *  net.minecraft.client.resources.sounds.SoundInstance$Attenuation
 *  net.minecraft.client.sounds.AudioStream
 *  net.minecraft.client.sounds.SoundBufferLibrary
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

private static final class RadioSounds.SeekingSound
extends SimpleSoundInstance {
    private final int offsetMs;

    RadioSounds.SeekingSound(ResourceLocation id, float vol, float pitch, Vec3 at, boolean handheld, int offsetMs) {
        super(id, SoundSource.RECORDS, vol, pitch, RNG, false, 0, handheld ? SoundInstance.Attenuation.NONE : SoundInstance.Attenuation.LINEAR, at == null ? 0.0 : at.x, at == null ? 0.0 : at.y, at == null ? 0.0 : at.z, handheld);
        this.offsetMs = offsetMs;
    }

    public CompletableFuture<AudioStream> getStream(SoundBufferLibrary lib, Sound sound, boolean looping) {
        CompletableFuture f = lib.getStream(sound.getPath(), looping);
        if (this.offsetMs < 250) {
            return f;
        }
        return f.thenApply(s -> {
            RadioSounds.SeekingSound.skip(s, this.offsetMs);
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
