/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.logic;

import com.airwaves.logic.BoardDesign;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;

public final class BoardAnalysis {
    private static final int R = 0;
    private static final int C = 1;
    private static final int L = 2;
    private static final int T = 3;
    private static final int U = 4;
    private static final int K = 5;
    private static final int X = 6;

    private static Stage single(int part) {
        if (part < 0 || part >= BoardDesign.Part.values().length) {
            return null;
        }
        return switch (BoardDesign.Part.values()[part]) {
            case BoardDesign.Part.MEMORY_CHIP -> Stage.MEMORY;
            case BoardDesign.Part.COUNTER_CHIP -> Stage.COUNTER;
            case BoardDesign.Part.PULSE_CHIP -> Stage.PULSE;
            case BoardDesign.Part.LIGHT_SENSOR -> Stage.DAYLIGHT;
            case BoardDesign.Part.RAIN_SENSOR -> Stage.RAIN;
            case BoardDesign.Part.MOTION_SENSOR -> Stage.MOTION;
            default -> null;
        };
    }

    private BoardAnalysis() {
    }

    private static int[] nb(int i) {
        int c = i % 8;
        int r = i / 8;
        int W = 8;
        return new int[]{c > 0 ? i - 1 : -1, c < W - 1 ? i + 1 : -1, r > 0 ? i - W : -1, r < 4 ? i + W : -1};
    }

    private static Stage pair(int a, int b) {
        int lo = Math.min(a, b);
        int hi = Math.max(a, b);
        if (lo == 1 && hi == 5) {
            return Stage.TANK;
        }
        if (lo == 1 && hi == 6) {
            return Stage.OSCILLATOR;
        }
        if (lo == 0 && hi == 4) {
            return Stage.TUBE_AMP;
        }
        if (lo == 0 && hi == 3) {
            return Stage.TRANSISTOR_AMP;
        }
        if (lo == 3 && hi == 3) {
            return Stage.LOGIC;
        }
        if (lo == 0 && hi == 1) {
            return Stage.TIMER;
        }
        if (lo == 0 && hi == 2) {
            return Stage.INDICATOR;
        }
        return null;
    }

    private static int anchor(Stage s) {
        return switch (s.ordinal()) {
            case 0 -> 5;
            case 1 -> 6;
            case 2 -> 4;
            case 3, 4 -> 3;
            case 5 -> 1;
            case 6 -> 2;
            default -> -1;
        };
    }

    public static Result analyse(int[] parts, boolean[] traces) {
        int N = 40;
        int W = 8;
        int H = 5;
        boolean[] cond = new boolean[N];
        int[] dist = new int[N];
        Arrays.fill(dist, -1);
        int nparts = 0;
        for (int i = 0; i < N; ++i) {
            boolean bl = cond[i] = parts[i] >= 0 || traces[i];
            if (parts[i] < 0) continue;
            ++nparts;
        }
        ArrayDeque<Integer> q = new ArrayDeque<Integer>();
        for (int r = 0; r < H; ++r) {
            if (!cond[r * W]) continue;
            dist[r * W] = 0;
            q.add(r * W);
        }
        while (!q.isEmpty()) {
            int i = (Integer)q.poll();
            for (int n : BoardAnalysis.nb(i)) {
                if (n < 0 || !cond[n] || dist[n] >= 0) continue;
                dist[n] = dist[i] + 1;
                q.add(n);
            }
        }
        boolean[] live = new boolean[N];
        for (int i = 0; i < N; ++i) {
            live[i] = dist[i] >= 0;
        }
        EnumMap<Stage, Integer> counts = new EnumMap<Stage, Integer>(Stage.class);
        if (nparts == 0) {
            return new Result(false, "gui.airwaves.workbench.empty", live, dist, counts, List.of(), List.of());
        }
        boolean out = false;
        for (int r = 0; r < H; ++r) {
            out |= live[r * W + W - 1];
        }
        if (!out) {
            return new Result(false, "gui.airwaves.workbench.open", live, dist, counts, List.of(), List.of());
        }
        for (int i = 0; i < N; ++i) {
            if (parts[i] < 0 || live[i]) continue;
            return new Result(false, "gui.airwaves.workbench.loose", live, dist, counts, List.of(), List.of());
        }
        TreeSet<Long> links = new TreeSet<Long>();
        for (int a = 0; a < N; ++a) {
            if (parts[a] < 0) continue;
            HashSet<Integer> seen = new HashSet<Integer>();
            ArrayDeque<Integer> tq = new ArrayDeque<Integer>();
            for (int n2 : BoardAnalysis.nb(a)) {
                if (n2 < 0) continue;
                if (parts[n2] >= 0) {
                    links.add(BoardAnalysis.key(a, n2));
                    continue;
                }
                if (!traces[n2]) continue;
                seen.add(n2);
                tq.add(n2);
            }
            while (!tq.isEmpty()) {
                int i = (Integer)tq.poll();
                int[] nArray = BoardAnalysis.nb(i);
                int n = nArray.length;
                for (int n2 = 0; n2 < n; ++n2) {
                    int n3 = nArray[n2];
                    if (n3 < 0 || n3 == a || seen.contains(n3)) continue;
                    if (parts[n3] >= 0) {
                        links.add(BoardAnalysis.key(a, n3));
                        continue;
                    }
                    if (!traces[n3]) continue;
                    seen.add(n3);
                    tq.add(n3);
                }
            }
        }
        EnumMap<Stage, Set> anchors = new EnumMap<Stage, Set>(Stage.class);
        EnumMap<Stage, Integer> first = new EnumMap<Stage, Integer>(Stage.class);
        EnumMap<Stage, Set<Integer>> members = new EnumMap<Stage, Set<Integer>>(Stage.class);
        Iterator i = links.iterator();
        while (i.hasNext()) {
            int b;
            long k = (Long)i.next();
            int a = (int)(k >> 16);
            Stage s = BoardAnalysis.pair(parts[a], parts[b = (int)(k & 0xFFFFL)]);
            if (s == null) continue;
            Set mem = members.computeIfAbsent(s, x -> new HashSet());
            mem.add(a);
            mem.add(b);
            Set an = anchors.computeIfAbsent(s, x -> new HashSet());
            if (parts[a] == BoardAnalysis.anchor(s)) {
                an.add(a);
            }
            if (parts[b] == BoardAnalysis.anchor(s)) {
                an.add(b);
            }
            first.merge(s, Math.min(dist[a], dist[b]), Math::min);
        }
        for (int i2 = 0; i2 < N; ++i2) {
            Stage s = BoardAnalysis.single(parts[i2]);
            if (s == null || !live[i2]) continue;
            anchors.computeIfAbsent(s, x -> new HashSet()).add(i2);
            members.computeIfAbsent(s, x -> new HashSet()).add(i2);
            first.merge(s, dist[i2], Math::min);
        }
        for (Map.Entry e : anchors.entrySet()) {
            int n = e.getKey() == Stage.LOGIC ? (((Set)e.getValue()).size() + 1) / 2 : ((Set)e.getValue()).size();
            if (n <= 0) continue;
            counts.put(e.getKey(), n);
        }
        List<BoardDesign> designs = BoardAnalysis.designs(counts);
        ArrayList<Stage> program = new ArrayList<Stage>(first.keySet());
        program.sort(Comparator.comparingInt(first::get).thenComparingInt(Enum::ordinal));
        return new Result(true, "", live, dist, counts, designs, program, members);
    }

    private static long key(int a, int b) {
        return (long)Math.min(a, b) << 16 | (long)Math.max(a, b);
    }

    public static List<BoardDesign> designs(EnumMap<Stage, Integer> c) {
        ToIntFunction<Stage> g = s -> c.getOrDefault(s, 0);
        ArrayList<BoardDesign> d = new ArrayList<BoardDesign>();
        if (g.applyAsInt(Stage.TANK) > 0 && g.applyAsInt(Stage.OSCILLATOR) > 0 && g.applyAsInt(Stage.TRANSISTOR_AMP) > 0 && g.applyAsInt(Stage.INDICATOR) > 0) {
            d.add(BoardDesign.RADIO);
        }
        if (g.applyAsInt(Stage.OSCILLATOR) > 0 && g.applyAsInt(Stage.TUBE_AMP) > 0 && g.applyAsInt(Stage.TANK) > 0) {
            d.add(BoardDesign.RF);
        }
        if (g.applyAsInt(Stage.TANK) > 0 && g.applyAsInt(Stage.OSCILLATOR) > 0) {
            d.add(BoardDesign.TUNER);
        }
        if (g.applyAsInt(Stage.TUBE_AMP) > 0 && g.applyAsInt(Stage.TANK) == 0 && g.applyAsInt(Stage.OSCILLATOR) == 0) {
            d.add(BoardDesign.AMPLIFIER);
        }
        if (g.applyAsInt(Stage.TRANSISTOR_AMP) > 0 && g.applyAsInt(Stage.TIMER) > 0 && g.applyAsInt(Stage.TUBE_AMP) == 0 && g.applyAsInt(Stage.OSCILLATOR) == 0) {
            d.add(BoardDesign.AUDIO);
        }
        if (g.applyAsInt(Stage.INDICATOR) >= 3) {
            d.add(BoardDesign.DISPLAY);
        }
        if (g.applyAsInt(Stage.LOGIC) > 0 && g.applyAsInt(Stage.INDICATOR) > 0) {
            d.add(BoardDesign.CONTROL);
        }
        if (g.applyAsInt(Stage.MEMORY) + g.applyAsInt(Stage.COUNTER) + g.applyAsInt(Stage.PULSE) > 0) {
            d.add(BoardDesign.COMPUTER);
        }
        if (g.applyAsInt(Stage.DAYLIGHT) + g.applyAsInt(Stage.RAIN) + g.applyAsInt(Stage.MOTION) > 0) {
            d.add(BoardDesign.SENSOR);
        }
        d.add(BoardDesign.LOGIC);
        return d;
    }

    public static Program program(Result r) {
        Source source;
        if (!r.works()) {
            return new Program(Source.REDSTONE, List.of(), false, 20);
        }
        Enum src = null;
        for (Stage s : r.program()) {
            if (s != Stage.TANK && s != Stage.OSCILLATOR && !s.sensor()) continue;
            src = s;
            break;
        }
        if (src == null) {
            source = Source.REDSTONE;
        } else {
            switch (src.ordinal()) {
                case 0: {
                    source = Source.RADIO;
                    break;
                }
                case 1: {
                    source = Source.CLOCK;
                    break;
                }
                case 10: {
                    source = Source.DAYLIGHT;
                    break;
                }
                case 11: {
                    source = Source.RAIN;
                    break;
                }
                case 12: {
                    source = Source.MOTION;
                    break;
                }
                default: {
                    source = Source.REDSTONE;
                }
            }
        }
        Source source2 = source;
        ArrayList<Step> steps = new ArrayList<Step>();
        boolean usedSrc = false;
        boolean lamp = false;
        block22: for (Stage s : r.program()) {
            int n = r.count(s);
            if (s == src && !usedSrc) {
                usedSrc = true;
                --n;
                if (s == Stage.TANK || s == Stage.OSCILLATOR) {
                    n = 0;
                }
            }
            switch (s.ordinal()) {
                case 4: {
                    steps.add(Step.INVERT);
                    break;
                }
                case 5: {
                    int k;
                    for (k = 0; k < n; ++k) {
                        steps.add(Step.HOLD);
                    }
                    continue block22;
                }
                case 2: 
                case 3: {
                    if (steps.contains((Object)Step.AMPLIFY)) break;
                    steps.add(Step.AMPLIFY);
                    break;
                }
                case 1: {
                    if (s == src) break;
                    steps.add(Step.BLINK);
                    break;
                }
                case 0: {
                    if (s == src) break;
                    steps.add(Step.ONLY_ON_AIR);
                    break;
                }
                case 6: {
                    lamp = true;
                    break;
                }
                case 7: {
                    int k;
                    for (k = 0; k < n; ++k) {
                        steps.add(Step.TOGGLE);
                    }
                    continue block22;
                }
                case 8: {
                    int k;
                    for (k = 0; k < n; ++k) {
                        steps.add(Step.COUNT);
                    }
                    continue block22;
                }
                case 9: {
                    int k;
                    for (k = 0; k < n; ++k) {
                        steps.add(Step.PULSE);
                    }
                    continue block22;
                }
                case 10: {
                    if (n <= 0) break;
                    steps.add(Step.ONLY_DAY);
                    break;
                }
                case 11: {
                    if (n <= 0) break;
                    steps.add(Step.ONLY_RAIN);
                    break;
                }
                case 12: {
                    if (n <= 0) break;
                    steps.add(Step.ONLY_PLAYER);
                }
            }
        }
        int clock = 10 * (1 + r.count(Stage.OSCILLATOR));
        return new Program(source2, steps, lamp, clock);
    }

    public static String stepName(Step s) {
        return switch (s.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> "invert";
            case 1 -> "hold 1 s";
            case 2 -> "amplify";
            case 3 -> "blink";
            case 4 -> "only on air";
            case 5 -> "toggle";
            case 6 -> "count";
            case 7 -> "pulse";
            case 8 -> "only by day";
            case 9 -> "only in rain";
            case 10 -> "only with a player near";
        };
    }

    public static String describe(Program p) {
        StringBuilder b = new StringBuilder((String)(switch (p.source().ordinal()) {
            default -> throw new MatchException(null, null);
            case 1 -> "station heard";
            case 2 -> "clock " + (double)p.clockTicks() / 20.0 + " s";
            case 0 -> "redstone in";
            case 3 -> "daylight";
            case 4 -> "rain";
            case 5 -> "player near";
        }));
        int hold = 0;
        for (Step s : p.steps()) {
            if (s == Step.HOLD) {
                ++hold;
                continue;
            }
            if (hold > 0) {
                b.append(" > hold ").append(hold).append(" s");
                hold = 0;
            }
            b.append(" > ").append(BoardAnalysis.stepName(s));
        }
        if (hold > 0) {
            b.append(" > hold ").append(hold).append(" s");
        }
        return b.append(" > out").toString();
    }

    public static enum Stage {
        TANK,
        OSCILLATOR,
        TUBE_AMP,
        TRANSISTOR_AMP,
        LOGIC,
        TIMER,
        INDICATOR,
        MEMORY,
        COUNTER,
        PULSE,
        DAYLIGHT,
        RAIN,
        MOTION;


        public boolean chip() {
            return this == MEMORY || this == COUNTER || this == PULSE;
        }

        public boolean sensor() {
            return this == DAYLIGHT || this == RAIN || this == MOTION;
        }

        public String key() {
            return "gui.airwaves.stage." + this.name().toLowerCase(Locale.ROOT);
        }

        public int color() {
            return switch (this.ordinal()) {
                default -> throw new MatchException(null, null);
                case 0 -> -3641288;
                case 1 -> -6262560;
                case 2 -> -26048;
                case 3 -> -12566456;
                case 4 -> -11509568;
                case 5 -> -12818232;
                case 6 -> -2081744;
                case 7 -> -11760400;
                case 8 -> -1523648;
                case 9 -> -1023920;
                case 10 -> -995232;
                case 11 -> -11493136;
                case 12 -> -2039584;
            };
        }
    }

    public record Result(boolean works, String status, boolean[] live, int[] dist, EnumMap<Stage, Integer> counts, List<BoardDesign> designs, List<Stage> program, EnumMap<Stage, Set<Integer>> members) {
        public Result(boolean works, String status, boolean[] live, int[] dist, EnumMap<Stage, Integer> counts, List<BoardDesign> designs, List<Stage> program) {
            this(works, status, live, dist, counts, designs, program, new EnumMap<Stage, Set<Integer>>(Stage.class));
        }

        public int count(Stage s) {
            return this.counts.getOrDefault((Object)s, 0);
        }

        public Set<Integer> cells(Stage s) {
            return this.members.getOrDefault((Object)s, Set.of());
        }
    }

    public record Program(Source source, List<Step> steps, boolean lamp, int clockTicks) {
    }

    public static enum Source {
        REDSTONE,
        RADIO,
        CLOCK,
        DAYLIGHT,
        RAIN,
        MOTION;

    }

    public static enum Step {
        INVERT,
        HOLD,
        AMPLIFY,
        BLINK,
        ONLY_ON_AIR,
        TOGGLE,
        COUNT,
        PULSE,
        ONLY_DAY,
        ONLY_RAIN,
        ONLY_PLAYER;

    }
}
