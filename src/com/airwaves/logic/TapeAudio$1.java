/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.logic;

import com.airwaves.logic.TapeAudio;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class TapeAudio.1
extends LinkedHashMap<String, List<TapeAudio.Frame>> {
    TapeAudio.1(int arg0, float arg1, boolean arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, List<TapeAudio.Frame>> e) {
        return this.size() > 12;
    }
}
