/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.be;

import com.airwaves.logic.BoardAnalysis;

static class CircuitBoxBE.1 {
    static final /* synthetic */ int[] $SwitchMap$com$airwaves$logic$BoardAnalysis$Source;
    static final /* synthetic */ int[] $SwitchMap$com$airwaves$logic$BoardAnalysis$Step;

    static {
        $SwitchMap$com$airwaves$logic$BoardAnalysis$Step = new int[BoardAnalysis.Step.values().length];
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Step[BoardAnalysis.Step.INVERT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Step[BoardAnalysis.Step.AMPLIFY.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Step[BoardAnalysis.Step.BLINK.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Step[BoardAnalysis.Step.ONLY_ON_AIR.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Step[BoardAnalysis.Step.HOLD.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Step[BoardAnalysis.Step.TOGGLE.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Step[BoardAnalysis.Step.COUNT.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Step[BoardAnalysis.Step.PULSE.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Step[BoardAnalysis.Step.ONLY_DAY.ordinal()] = 9;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Step[BoardAnalysis.Step.ONLY_RAIN.ordinal()] = 10;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Step[BoardAnalysis.Step.ONLY_PLAYER.ordinal()] = 11;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        $SwitchMap$com$airwaves$logic$BoardAnalysis$Source = new int[BoardAnalysis.Source.values().length];
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Source[BoardAnalysis.Source.RADIO.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Source[BoardAnalysis.Source.CLOCK.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Source[BoardAnalysis.Source.REDSTONE.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Source[BoardAnalysis.Source.DAYLIGHT.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Source[BoardAnalysis.Source.RAIN.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            CircuitBoxBE.1.$SwitchMap$com$airwaves$logic$BoardAnalysis$Source[BoardAnalysis.Source.MOTION.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
