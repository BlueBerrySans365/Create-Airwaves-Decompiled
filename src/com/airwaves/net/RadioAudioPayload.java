/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RadioAudioPayload(BlockPos sink, UUID talker, byte[] data, float quality, boolean fm, float gain) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<RadioAudioPayload> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"radio_audio"));
    public static final StreamCodec<ByteBuf, RadioAudioPayload> CODEC = StreamCodec.composite((StreamCodec)BlockPos.STREAM_CODEC, RadioAudioPayload::sink, (StreamCodec)UUIDUtil.STREAM_CODEC, RadioAudioPayload::talker, (StreamCodec)ByteBufCodecs.byteArray((int)1024), RadioAudioPayload::data, (StreamCodec)ByteBufCodecs.FLOAT, RadioAudioPayload::quality, (StreamCodec)ByteBufCodecs.BOOL, RadioAudioPayload::fm, (StreamCodec)ByteBufCodecs.FLOAT, RadioAudioPayload::gain, RadioAudioPayload::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
