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

import com.airwaves.net.StationInfo;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SpectrumPayload(List<StationInfo> stations) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<SpectrumPayload> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"spectrum"));
    public static final StreamCodec<ByteBuf, SpectrumPayload> CODEC = StreamCodec.composite((StreamCodec)StationInfo.STREAM.apply(ByteBufCodecs.list((int)64)), SpectrumPayload::stations, SpectrumPayload::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
