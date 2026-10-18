/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.client;

import com.airwaves.client.AudioPlayback;
import com.airwaves.client.MuLaw;
import java.util.ArrayDeque;
import java.util.Random;

static final class AudioPlayback.Source {
    private final ArrayDeque<float[]> q = new ArrayDeque();
    private float[] pending = new float[640];
    private int pendingN = 0;
    volatile long last = System.currentTimeMillis();
    private boolean primed;
    private float lastL;
    private float lastR;
    private float hpX;
    private float hpY;
    private float lp;
    private float lp2;
    private float crackle;
    private final Random rnd = new Random();

    AudioPlayback.Source() {
    }

    private float weatherNoise(int mode) {
        float am = mode == 1 ? 1.0f : 0.3f;
        float bl = AudioPlayback.burstLevel() * am;
        float y = (this.rnd.nextFloat() * 2.0f - 1.0f) * (rainLevel * 0.01f + thunderLevel * 0.025f) * am;
        if (this.rnd.nextFloat() < rainLevel * 0.003f) {
            this.crackle = (this.rnd.nextFloat() - 0.5f) * 0.35f * am;
        }
        if (bl > 0.0f && this.rnd.nextFloat() < 0.012f) {
            this.crackle = (this.rnd.nextFloat() - 0.5f) * 0.9f * bl;
        }
        float f = bl > 0.0f ? (this.rnd.nextFloat() * 2.0f - 1.0f) * bl * 0.25f : 0.0f;
        this.crackle *= 0.86f;
        return y += this.crackle + f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void push(byte[] mu, float vol, float pan, float qual, int mode) {
        this.last = System.currentTimeMillis();
        float lg = pan <= 0.0f ? 1.0f : 1.0f - pan;
        float rg = pan >= 0.0f ? 1.0f : 1.0f + pan;
        float inv = 1.0f - qual;
        boolean drop = mode == 2 && qual < 0.3f && this.rnd.nextFloat() < (0.3f - qual) * 1.4f;
        for (byte b : mu) {
            float x = (float)MuLaw.dec(b) / 32768.0f;
            if (mode == 1) {
                this.hpY = 0.96f * (this.hpY + x - this.hpX);
                this.hpX = x;
                x = this.hpY;
                this.lp += 0.6f * (x - this.lp);
                this.lp2 += 0.6f * (this.lp - this.lp2);
                x = this.lp2;
                x = (float)Math.tanh(x * 1.7f) * 0.8f;
                x = x * (0.45f + 0.55f * qual) + (this.rnd.nextFloat() * 2.0f - 1.0f) * (0.004f + inv * inv * 0.07f);
                x += this.weatherNoise(1);
            } else if (mode == 2) {
                this.lp += 0.88f * (x - this.lp);
                x = drop ? this.lp * 0.1f : this.lp;
                x = x * (0.6f + 0.4f * qual) + (this.rnd.nextFloat() * 2.0f - 1.0f) * (inv * inv * inv * 0.06f);
                x += this.weatherNoise(2);
            }
            float y = x * vol;
            this.pending[2 * this.pendingN] = y * lg;
            this.pending[2 * this.pendingN + 1] = y * rg;
            if (++this.pendingN != 320) continue;
            ArrayDeque<float[]> arrayDeque = this.q;
            synchronized (arrayDeque) {
                this.q.addLast(this.pending);
                while (this.q.size() > 12) {
                    this.q.pollFirst();
                }
            }
            this.pending = new float[640];
            this.pendingN = 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void mixInto(float[] mix) {
        float[] f;
        ArrayDeque<float[]> arrayDeque = this.q;
        synchronized (arrayDeque) {
            if (!this.primed && this.q.size() >= 3) {
                this.primed = true;
            }
            float[] fArray = f = this.primed ? this.q.pollFirst() : null;
            if (f == null) {
                this.primed = false;
            }
        }
        if (f == null) {
            if (this.lastL != 0.0f || this.lastR != 0.0f) {
                for (int i = 0; i < 80; ++i) {
                    float k = 1.0f - (float)i / 80.0f;
                    int n = 2 * i;
                    mix[n] = mix[n] + this.lastL * k;
                    int n2 = 2 * i + 1;
                    mix[n2] = mix[n2] + this.lastR * k;
                }
                this.lastR = 0.0f;
                this.lastL = 0.0f;
            }
            return;
        }
        boolean fadeIn = this.lastL == 0.0f && this.lastR == 0.0f;
        for (int i = 0; i < 320; ++i) {
            float k = fadeIn && i < 80 ? (float)i / 80.0f : 1.0f;
            int n = 2 * i;
            mix[n] = mix[n] + f[2 * i] * k;
            int n3 = 2 * i + 1;
            mix[n3] = mix[n3] + f[2 * i + 1] * k;
        }
        this.lastL = f[638];
        this.lastR = f[639];
        if (this.lastL == 0.0f && this.lastR == 0.0f) {
            this.lastL = 1.0E-9f;
        }
    }
}
