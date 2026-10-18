/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.client;

public final class MuLaw {
    private MuLaw() {
    }

    public static byte enc(short pcm) {
        int s = pcm;
        int sign = s >> 8 & 0x80;
        if (sign != 0) {
            s = -s;
        }
        if (s > 32635) {
            s = 32635;
        }
        s += 132;
        int exp = 7;
        int mask = 16384;
        while ((s & mask) == 0 && exp > 0) {
            --exp;
            mask >>= 1;
        }
        int mant = s >> exp + 3 & 0xF;
        return (byte)(~(sign | exp << 4 | mant));
    }

    public static short dec(byte u) {
        int v = ~u & 0xFF;
        int sign = v & 0x80;
        int exp = v >> 4 & 7;
        int mant = v & 0xF;
        int s = (mant << 3) + 132 << exp;
        return (short)(sign != 0 ? -s : (s -= 132));
    }
}
