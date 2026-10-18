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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record TapeData.Cue(int tick, ResourceLocation sound, float volume, float pitch, byte kind) {
    public static final Codec<TapeData.Cue> CODEC = RecordCodecBuilder.create(i -> i.group((App)Codec.INT.fieldOf("t").forGetter(TapeData.Cue::tick), (App)ResourceLocation.CODEC.fieldOf("s").forGetter(TapeData.Cue::sound), (App)Codec.FLOAT.fieldOf("v").forGetter(TapeData.Cue::volume), (App)Codec.FLOAT.fieldOf("p").forGetter(TapeData.Cue::pitch), (App)Codec.BYTE.optionalFieldOf("k", (Object)0).forGetter(TapeData.Cue::kind)).apply((Applicative)i, TapeData.Cue::new));
    public static final StreamCodec<ByteBuf, TapeData.Cue> STREAM = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, TapeData.Cue::tick, (StreamCodec)ResourceLocation.STREAM_CODEC, TapeData.Cue::sound, (StreamCodec)ByteBufCodecs.FLOAT, TapeData.Cue::volume, (StreamCodec)ByteBufCodecs.FLOAT, TapeData.Cue::pitch, (StreamCodec)ByteBufCodecs.BYTE, TapeData.Cue::kind, TapeData.Cue::new);
}
