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

public record VoiceRelayPayload(UUID talker, byte[] data) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<VoiceRelayPayload> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"voice_relay"));
    public static final StreamCodec<ByteBuf, VoiceRelayPayload> CODEC = StreamCodec.composite((StreamCodec)UUIDUtil.STREAM_CODEC, VoiceRelayPayload::talker, (StreamCodec)ByteBufCodecs.byteArray((int)1024), VoiceRelayPayload::data, VoiceRelayPayload::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
