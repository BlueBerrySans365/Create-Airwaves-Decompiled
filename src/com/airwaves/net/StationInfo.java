/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package com.airwaves.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record StationInfo(boolean fm, double freq, float quality, String name) {
    public static final StreamCodec<ByteBuf, StationInfo> STREAM = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, StationInfo::fm, (StreamCodec)ByteBufCodecs.DOUBLE, StationInfo::freq, (StreamCodec)ByteBufCodecs.FLOAT, StationInfo::quality, (StreamCodec)ByteBufCodecs.stringUtf8((int)64), StationInfo::name, StationInfo::new);
}
