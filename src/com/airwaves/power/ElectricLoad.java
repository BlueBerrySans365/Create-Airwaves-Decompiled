/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 */
package com.airwaves.power;

import com.airwaves.Config;
import com.airwaves.power.PowerSupply;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public final class ElectricLoad {
    public static final double OPEN = 100000.0;
    private double resistance = 100000.0;
    public float volts;
    public float watts;
    public Status status = Status.IDLE;

    public double resistance() {
        return this.resistance;
    }

    public void update(PowerSupply s, double v, double joules, long gameTime) {
        this.volts = (float)Math.abs(v);
        this.watts = (float)(joules * 20.0);
        double r = 100000.0;
        if (!Config.electricEnabled()) {
            this.status = Status.DISABLED;
        } else if (s == null) {
            this.status = Status.NO_MACHINE;
        } else {
            if (joules > 0.0) {
                s.feed(joules, gameTime);
            } else {
                s.feed(0.0, gameTime);
            }
            double want = s.wantWatts();
            int rated = s.ratedVolts();
            if ((double)this.volts > (double)rated * 2.2) {
                this.status = Status.OVERVOLTAGE;
            } else if (want <= 0.0) {
                this.status = Status.IDLE;
            } else {
                double vv = Math.max(this.volts, (float)rated);
                r = Mth.clamp((double)(vv * vv / want), (double)0.05, (double)100000.0);
                this.status = Status.DRAWING;
            }
        }
        this.resistance = r;
    }

    public void tooltip(List<Component> tip, PowerSupply s) {
        if (this.volts < 0.05f && this.status != Status.OVERVOLTAGE) {
            return;
        }
        tip.add((Component)Component.literal((String)("    \u26a1 " + String.format("%.1f", Float.valueOf(this.volts)) + " V, " + String.format("%.1f", Float.valueOf(this.watts)) + " W")).withStyle(ChatFormatting.AQUA));
        if (this.status == Status.OVERVOLTAGE && s != null) {
            tip.add((Component)Component.literal((String)("    too much voltage: max " + Math.round((double)s.ratedVolts() * 2.2) + " V")).withStyle(ChatFormatting.RED));
        }
    }

    public static enum Status {
        DRAWING,
        NO_MACHINE,
        OVERVOLTAGE,
        IDLE,
        DISABLED;

    }
}
