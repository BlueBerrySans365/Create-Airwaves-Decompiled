/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.logic;

import com.airwaves.logic.BoardAnalysis;
import java.util.List;

public record BoardAnalysis.Program(BoardAnalysis.Source source, List<BoardAnalysis.Step> steps, boolean lamp, int clockTicks) {
}
