/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.client;

import com.airwaves.net.CablePulsePayload;
import java.util.HashMap;
import java.util.Map;

public final class CablePulses {
    private static final Map<String, Long> LIVE = new HashMap<String, Long>();

    private CablePulses() {
    }

    static String key(long a, int as, long b, int bs) {
        return a + ":" + as + ">" + b + ":" + bs;
    }

    public static synchronized void add(CablePulsePayload p) {
        long now = System.currentTimeMillis();
        for (int i = 0; i < p.from().length; ++i) {
            LIVE.put(CablePulses.key(p.from()[i], p.fromSlot()[i], p.to()[i], p.toSlot()[i]), now);
        }
        if (LIVE.size() > 4096) {
            LIVE.clear();
        }
    }

    public static synchronized int dir(long a, int as, long b, int bs) {
        long now = System.currentTimeMillis();
        Long f = LIVE.get(CablePulses.key(a, as, b, bs));
        if (f != null && now - f < 700L) {
            return 1;
        }
        Long r = LIVE.get(CablePulses.key(b, bs, a, as));
        if (r != null && now - r < 700L) {
            return -1;
        }
        return 0;
    }
}
