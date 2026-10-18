/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.client;

import com.airwaves.client.AudioPlayback;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

static final class AudioPlayback.Output
implements Runnable {
    SourceDataLine line;
    float outRate = 16000.0f;
    volatile boolean alive = true;
    long failedAt;
    private Thread thread;
    private float prevL;
    private float prevR;

    AudioPlayback.Output() {
        for (float r : OUT_RATES) {
            try {
                AudioFormat f = new AudioFormat(r, 16, 2, true, false);
                this.line = AudioSystem.getSourceDataLine(f);
                this.line.open(f, (int)((double)(r * 4.0f) * 0.1));
                this.outRate = r;
                break;
            }
            catch (Throwable t) {
                this.line = null;
            }
        }
        if (this.line == null) {
            this.alive = false;
            this.failedAt = System.currentTimeMillis();
            return;
        }
        this.thread = new Thread((Runnable)this, "Airwaves-Audio");
        this.thread.setDaemon(true);
        this.thread.setPriority(9);
        this.thread.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try {
            this.line.start();
            double step = 16000.0f / this.outRate;
            int outFrames = (int)Math.round(320.0 / step);
            float[] mix = new float[640];
            byte[] b = new byte[outFrames * 4];
            while (this.alive) {
                Arrays.fill(mix, 0.0f);
                for (AudioPlayback.Source s : SOURCES.values()) {
                    s.mixInto(mix);
                }
                for (int o = 0; o < outFrames; ++o) {
                    double pos = (double)o * step;
                    int i0 = (int)pos;
                    double t = pos - (double)i0;
                    float l0 = i0 == 0 ? this.prevL : mix[2 * (i0 - 1)];
                    float r0 = i0 == 0 ? this.prevR : mix[2 * (i0 - 1) + 1];
                    float l1 = mix[2 * Math.min(i0, 319)];
                    float r1 = mix[2 * Math.min(i0, 319) + 1];
                    float L = (float)((double)l0 + (double)(l1 - l0) * t);
                    float R = (float)((double)r0 + (double)(r1 - r0) * t);
                    L /= 1.0f + Math.abs(L) * 0.6f;
                    R /= 1.0f + Math.abs(R) * 0.6f;
                    short sl = (short)(L * 30000.0f);
                    short sr = (short)(R * 30000.0f);
                    b[4 * o] = (byte)sl;
                    b[4 * o + 1] = (byte)(sl >> 8);
                    b[4 * o + 2] = (byte)sr;
                    b[4 * o + 3] = (byte)(sr >> 8);
                }
                this.prevL = mix[638];
                this.prevR = mix[639];
                this.line.write(b, 0, b.length);
            }
        }
        catch (Throwable throwable) {
        }
        finally {
            try {
                this.line.stop();
                this.line.close();
            }
            catch (Throwable throwable) {}
            this.alive = false;
            this.failedAt = System.currentTimeMillis();
        }
    }

    void close() {
        this.alive = false;
        if (this.thread != null) {
            this.thread.interrupt();
        }
    }
}
