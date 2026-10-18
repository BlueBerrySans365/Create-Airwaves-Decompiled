/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.resources.ResourceLocation
 */
package com.airwaves.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record HandheldStatePayload(boolean on, boolean carrier, float quality, boolean fm, String label) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<HandheldStatePayload> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"handheld_state"));
    public static final StreamCodec<ByteBuf, HandheldStatePayload> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, HandheldStatePayload::on, (StreamCodec)ByteBufCodecs.BOOL, HandheldStatePayload::carrier, (StreamCodec)ByteBufCodecs.FLOAT, HandheldStatePayload::quality, (StreamCodec)ByteBufCodecs.BOOL, HandheldStatePayload::fm, (StreamCodec)ByteBufCodecs.stringUtf8((int)64), HandheldStatePayload::label, HandheldStatePayload::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
