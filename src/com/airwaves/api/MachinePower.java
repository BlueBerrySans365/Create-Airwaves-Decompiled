/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.api;

public interface MachinePower {
    public int watts();

    public int ratedVolts();

    public double wantWatts();

    public void feed(double var1, long var3);

    public double storedJoules();

    public boolean running();

    public boolean onRotation();

    public boolean onElectricity();

    public float fraction();
}
