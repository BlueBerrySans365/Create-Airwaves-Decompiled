/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.logic;

import com.airwaves.logic.BoardAnalysis;
import com.airwaves.logic.BoardDesign;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

public record BoardAnalysis.Result(boolean works, String status, boolean[] live, int[] dist, EnumMap<BoardAnalysis.Stage, Integer> counts, List<BoardDesign> designs, List<BoardAnalysis.Stage> program, EnumMap<BoardAnalysis.Stage, Set<Integer>> members) {
    public BoardAnalysis.Result(boolean works, String status, boolean[] live, int[] dist, EnumMap<BoardAnalysis.Stage, Integer> counts, List<BoardDesign> designs, List<BoardAnalysis.Stage> program) {
        this(works, status, live, dist, counts, designs, program, new EnumMap<BoardAnalysis.Stage, Set<Integer>>(BoardAnalysis.Stage.class));
    }

    public int count(BoardAnalysis.Stage s) {
        return this.counts.getOrDefault((Object)s, 0);
    }

    public Set<Integer> cells(BoardAnalysis.Stage s) {
        return this.members.getOrDefault((Object)s, Set.of());
    }
}
