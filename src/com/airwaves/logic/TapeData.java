/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 */
package com.airwaves.logic;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record TapeData(int length, List<Cue> cues, String audio, int voiceSeconds) {
    public static final int MAX_TICKS = 6000;
    public static final int MAX_CUES = 4000;
    public static final TapeData EMPTY = new TapeData(0, List.of(), "", 0);
    public static final Codec<TapeData> CODEC = RecordCodecBuilder.create(i -> i.group((App)Codec.INT.fieldOf("length").forGetter(TapeData::length), (App)Cue.CODEC.listOf().fieldOf("cues").forGetter(TapeData::cues), (App)Codec.STRING.optionalFieldOf("audio", (Object)"").forGetter(TapeData::audio), (App)Codec.INT.optionalFieldOf("voice", (Object)0).forGetter(TapeData::voiceSeconds)).apply((Applicative)i, TapeData::new));
    public static final StreamCodec<ByteBuf, TapeData> STREAM = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, TapeData::length, (StreamCodec)Cue.STREAM.apply(ByteBufCodecs.list()), TapeData::cues, (StreamCodec)ByteBufCodecs.STRING_UTF8, TapeData::audio, (StreamCodec)ByteBufCodecs.VAR_INT, TapeData::voiceSeconds, TapeData::new);

    public boolean blank() {
        return this.length <= 0;
    }

    public static String time(int ticks) {
        int s = Math.max(0, ticks) / 20;
        return String.format(Locale.ROOT, "%d:%02d", s / 60, s % 60);
    }

    public boolean hasVoice() {
        return !this.audio.isEmpty();
    }

    public static TapeData of(int length, List<Cue> cues, String audio, int voiceSeconds) {
        return new TapeData(length, new ArrayList<Cue>(cues), audio, voiceSeconds);
    }

    public record Cue(int tick, ResourceLocation sound, float volume, float pitch, byte kind) {
        public static final Codec<Cue> CODEC = RecordCodecBuilder.create(i -> i.group((App)Codec.INT.fieldOf("t").forGetter(Cue::tick), (App)ResourceLocation.CODEC.fieldOf("s").forGetter(Cue::sound), (App)Codec.FLOAT.fieldOf("v").forGetter(Cue::volume), (App)Codec.FLOAT.fieldOf("p").forGetter(Cue::pitch), (App)Codec.BYTE.optionalFieldOf("k", (Object)0).forGetter(Cue::kind)).apply((Applicative)i, Cue::new));
        public static final StreamCodec<ByteBuf, Cue> STREAM = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, Cue::tick, (StreamCodec)ResourceLocation.STREAM_CODEC, Cue::sound, (StreamCodec)ByteBufCodecs.FLOAT, Cue::volume, (StreamCodec)ByteBufCodecs.FLOAT, Cue::pitch, (StreamCodec)ByteBufCodecs.BYTE, Cue::kind, Cue::new);
    }
}
