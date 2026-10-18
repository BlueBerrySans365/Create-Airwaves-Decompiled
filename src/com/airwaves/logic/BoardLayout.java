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
 */
package com.airwaves.logic;

import com.airwaves.logic.BoardAnalysis;
import com.airwaves.logic.BoardDesign;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BoardLayout(String cells) {
    public static final Codec<BoardLayout> CODEC = RecordCodecBuilder.create(i -> i.group((App)Codec.STRING.fieldOf("cells").forGetter(BoardLayout::cells)).apply((Applicative)i, BoardLayout::new));
    public static final StreamCodec<ByteBuf, BoardLayout> STREAM = StreamCodec.composite((StreamCodec)ByteBufCodecs.STRING_UTF8, BoardLayout::cells, BoardLayout::new);

    public static BoardLayout of(int[] entries) {
        StringBuilder b = new StringBuilder(40);
        for (int i = 0; i < 40; ++i) {
            int e;
            int n = e = i < entries.length ? entries[i] : -1;
            b.append((char)(e == -2 ? 45 : (e < 0 || e >= BoardDesign.Part.values().length ? 46 : (int)BoardDesign.Part.values()[e].letter)));
        }
        return new BoardLayout(b.toString());
    }

    public int[] parts() {
        int[] out = new int[40];
        for (int i = 0; i < 40; ++i) {
            char c = i < this.cells.length() ? (char)this.cells.charAt(i) : (char)'.';
            int p = -1;
            for (BoardDesign.Part part : BoardDesign.Part.values()) {
                if (part.letter != c) continue;
                p = part.ordinal();
            }
            out[i] = p;
        }
        return out;
    }

    public boolean[] traces() {
        boolean[] out = new boolean[40];
        for (int i = 0; i < 40; ++i) {
            out[i] = i < this.cells.length() && this.cells.charAt(i) == '-';
        }
        return out;
    }

    public BoardAnalysis.Result analyse() {
        return BoardAnalysis.analyse(this.parts(), this.traces());
    }
}
