/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.UUIDUtil
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 */
package com.airwaves.net;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record HandheldAudioPayload(UUID talker, byte[] data, float quality, boolean fm, float gain) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<HandheldAudioPayload> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"handheld_audio"));
    public static final StreamCodec<ByteBuf, HandheldAudioPayload> CODEC = StreamCodec.composite((StreamCodec)UUIDUtil.STREAM_CODEC, HandheldAudioPayload::talker, (StreamCodec)ByteBufCodecs.byteArray((int)1024), HandheldAudioPayload::data, (StreamCodec)ByteBufCodecs.FLOAT, HandheldAudioPayload::quality, (StreamCodec)ByteBufCodecs.BOOL, HandheldAudioPayload::fm, (StreamCodec)ByteBufCodecs.FLOAT, HandheldAudioPayload::gain, HandheldAudioPayload::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
