/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.AbstractSliderButton
 *  net.minecraft.network.chat.Component
 */
package com.airwaves.client;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

private static class VoiceMenuScreen.Slider
extends AbstractSliderButton {
    private final String label;
    private final DoubleConsumer cb;
    private final DoubleFunction<String> fmt;

    VoiceMenuScreen.Slider(int x, int y, int w, String label, double v, DoubleFunction<String> fmt, DoubleConsumer cb) {
        super(x, y, w, 20, (Component)Component.empty(), v);
        this.label = label;
        this.cb = cb;
        this.fmt = fmt;
        this.updateMessage();
    }

    protected void updateMessage() {
        this.setMessage((Component)Component.literal((String)(this.label + ": " + this.fmt.apply(this.value))));
    }

    protected void applyValue() {
        this.cb.accept(this.value);
    }
}
