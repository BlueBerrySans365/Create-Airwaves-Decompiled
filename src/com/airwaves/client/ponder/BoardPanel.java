/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.ponder.api.element.PonderElement
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.element.AnimatedOverlayElementBase
 *  net.createmod.ponder.foundation.instruction.FadeInOutInstruction
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.createmod.ponder.foundation.ui.PonderUI
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.phys.Vec2
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.client.ponder;

import com.airwaves.ModRegistry;
import com.airwaves.logic.BoardAnalysis;
import com.airwaves.logic.BoardDesign;
import com.airwaves.logic.BoardLayout;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.PonderElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.AnimatedOverlayElementBase;
import net.createmod.ponder.foundation.instruction.FadeInOutInstruction;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class BoardPanel
extends AnimatedOverlayElementBase {
    private static final int BOX_W = 214;
    private static final int BOX_H = 104;
    private static final int CELL = 10;
    private static final int BX = 13;
    private static final int BY = 9;
    private static final int PART_TICKS = 6;
    private static final int TRACE_TICKS = 2;
    private static final int START = 4;
    private static final int PCB = -14718150;
    private static final int PCB_HI = -13727154;
    private static final int PCB_LO = -15448278;
    private static final int PAD = -3634628;
    private static final int PAD_LIVE = -15254;
    private static final int TRACE = -5214162;
    private static final int HOLE = -15184083;
    private static final int UNDER = -14016234;
    private static final int LABEL = -6248272;
    private static final int PROGRAM = -10434320;
    private final Vec3 anchor;
    private final int[] parts;
    private final boolean[] traces;
    private final List<Integer> partOrder = new ArrayList<Integer>();
    private final List<Integer> traceOrder = new ArrayList<Integer>();
    private final boolean build;
    private Pointing pointing = Pointing.RIGHT;
    int age;
    private int cachedKey = -1;
    private BoardAnalysis.Result cached;

    public BoardPanel(Vec3 anchor, boolean build, String ... rows) {
        this.anchor = anchor;
        this.build = build;
        StringBuilder b = new StringBuilder();
        for (String r : rows) {
            b.append(r);
        }
        BoardLayout l = new BoardLayout(b.toString());
        this.parts = l.parts();
        this.traces = l.traces();
        for (int c = 0; c < 8; ++c) {
            for (int r = 0; r < 5; ++r) {
                int i = r * 8 + c;
                if (this.parts[i] >= 0) {
                    this.partOrder.add(i);
                    continue;
                }
                if (!this.traces[i]) continue;
                this.traceOrder.add(i);
            }
        }
    }

    public static BoardPanel show(SceneBuilder builder, Vec3 anchor, int ticks, boolean build, String ... rows) {
        return BoardPanel.show(builder, anchor, Pointing.RIGHT, ticks, build, rows);
    }

    public static BoardPanel show(SceneBuilder builder, Vec3 anchor, Pointing pointing, int ticks, boolean build, String ... rows) {
        BoardPanel p = new BoardPanel(anchor, build, rows);
        p.pointing = pointing;
        builder.addInstruction((PonderInstruction)new Show(p, ticks));
        return p;
    }

    public static BoardPanel show(SceneBuilder builder, Vec3 anchor, int ticks, boolean build, BoardDesign design) {
        int[] e = design.example();
        String cells = BoardLayout.of(e).cells();
        String[] rows = new String[5];
        for (int r = 0; r < 5; ++r) {
            rows[r] = cells.substring(r * 8, (r + 1) * 8);
        }
        return BoardPanel.show(builder, anchor, ticks, build, rows);
    }

    public static int buildTicks(String ... rows) {
        return new BoardPanel(Vec3.ZERO, true, rows).doneAt();
    }

    private int partsShown() {
        return this.build ? Math.max(0, Math.min(this.partOrder.size(), (this.age - 4) / 6 + 1)) : this.partOrder.size();
    }

    private int tracesShown() {
        if (!this.build) {
            return this.traceOrder.size();
        }
        int t0 = 4 + this.partOrder.size() * 6;
        return Math.max(0, Math.min(this.traceOrder.size(), (this.age - t0) / 2 + 1));
    }

    private int doneAt() {
        return 4 + this.partOrder.size() * 6 + this.traceOrder.size() * 2;
    }

    private BoardAnalysis.Result analysis(int np, int nt) {
        int key = np * 100 + nt;
        if (key != this.cachedKey) {
            int k;
            int[] p = new int[40];
            boolean[] t = new boolean[40];
            Arrays.fill(p, -1);
            for (k = 0; k < np; ++k) {
                p[this.partOrder.get((int)k).intValue()] = this.parts[this.partOrder.get(k)];
            }
            for (k = 0; k < nt; ++k) {
                t[this.traceOrder.get((int)k).intValue()] = true;
            }
            this.cached = BoardAnalysis.analyse(p, t);
            this.cachedKey = key;
        }
        return this.cached;
    }

    public void render(PonderScene scene, PonderUI screen, GuiGraphics g, float pt, float fade) {
        int placedAt;
        float k;
        int i;
        int k2;
        if (fade < 0.0625f) {
            return;
        }
        Font font = screen.getFontRenderer();
        Vec2 at = scene.getTransform().sceneToScreen(this.anchor, pt);
        float sx = at.x;
        float sy = at.y;
        float dx = 0.0f;
        float dy = 0.0f;
        if (this.pointing == Pointing.RIGHT) {
            sx = Math.max(sx, 230.0f);
            sy = Math.max(sy, 56.0f);
            dx = -10.0f * (1.0f - fade);
        } else {
            sy = Math.max(sy, 118.0f);
            sx = Math.max(sx, 111.0f);
            dy = -10.0f * (1.0f - fade);
        }
        PoseStack ps = g.pose();
        ps.pushPose();
        ps.translate(sx + dx, sy + dy, 400.0f);
        PonderUI.renderSpeechBox((GuiGraphics)g, (int)0, (int)0, (int)214, (int)104, (boolean)false, (Pointing)this.pointing, (boolean)true);
        ps.translate(0.0f, 0.0f, 100.0f);
        int np = this.partsShown();
        int nt = this.tracesShown();
        BoardAnalysis.Result r = this.analysis(np, nt);
        boolean works = r.works();
        float time = (float)this.age + pt;
        int x0 = 4;
        int y0 = 4;
        int x1 = 102;
        int y1 = 64;
        BoardPanel.bevel(g, x0, y0, x1, y1, -14718150, -13727154, -15448278);
        for (int row = 0; row < 5; ++row) {
            int a = row * 8;
            int b = a + 8 - 1;
            int cy = 9 + row * 10;
            g.fill(6, cy + 3, 11, cy + 7, r.live()[a] ? -15254 : -3634628);
            g.fill(95, cy + 3, 100, cy + 7, works && r.live()[b] ? -15254 : -3634628);
        }
        boolean[] partOn = new boolean[40];
        boolean[] traceOn = new boolean[40];
        for (k2 = 0; k2 < np; ++k2) {
            partOn[this.partOrder.get((int)k2).intValue()] = true;
        }
        for (k2 = 0; k2 < nt; ++k2) {
            traceOn[this.traceOrder.get((int)k2).intValue()] = true;
        }
        for (i = 0; i < 40; ++i) {
            int cx0 = 13 + i % 8 * 10;
            int sy2 = 9 + i / 8 * 10;
            g.fill(cx0 + 1, sy2 + 1, cx0 + 10 - 1, sy2 + 10 - 1, partOn[i] ? -14016234 : -15184083);
            g.fill(cx0 + 1, sy2 + 1, cx0 + 2, sy2 + 2, -3634628);
        }
        for (i = 0; i < 40; ++i) {
            if (!partOn[i] && !traceOn[i]) continue;
            int col = i % 8;
            int row = i / 8;
            int cx = 13 + col * 10 + 5 - 1;
            int cy = 9 + row * 10 + 5 - 1;
            int cc = BoardPanel.copper(r.live()[i], r.dist()[i], works, time);
            if (col == 0) {
                g.fill(11, cy, cx, cy + 2, cc);
            }
            if (col == 7) {
                g.fill(cx, cy, 95, cy + 2, cc);
            }
            if (col < 7 && (partOn[i + 1] || traceOn[i + 1])) {
                g.fill(cx, cy, cx + 10, cy + 2, cc);
            }
            if (row < 4 && (partOn[i + 8] || traceOn[i + 8])) {
                g.fill(cx, cy, cx + 2, cy + 10, cc);
            }
            if (!traceOn[i]) continue;
            g.fill(cx - 1, cy - 1, cx + 3, cy + 3, r.live()[i] ? -15254 : -3634628);
        }
        for (k2 = 0; k2 < np; ++k2) {
            int i2 = this.partOrder.get(k2);
            int cx0 = 13 + i2 % 8 * 10;
            int sy2 = 9 + i2 / 8 * 10;
            BoardPanel.item(g, new ItemStack((ItemLike)BoardDesign.Part.values()[this.parts[i2]].item()), cx0, sy2, 0.625f);
        }
        if (this.build && np > 0 && np <= this.partOrder.size() && nt == 0 && (k = (time - (float)(placedAt = 4 + (np - 1) * 6)) / 6.0f) < 1.0f) {
            int i3 = this.partOrder.get(np - 1);
            int cx0 = 13 + i3 % 8 * 10;
            int sy2 = 9 + i3 / 8 * 10;
            int a = (int)(255.0f * (1.0f - k));
            BoardPanel.outline(g, cx0 - 1, sy2 - 1, cx0 + 10 + 1, sy2 + 10 + 1, a << 24 | 0xFFE8A0);
        }
        int rx = x1 + 8;
        int ry = 5;
        BoardPanel.small(g, font, Component.translatable((String)"gui.airwaves.workbench.stages").getString(), rx, ry, -6248272);
        ry += 10;
        if (works) {
            int shown = 0;
            for (Map.Entry<BoardAnalysis.Stage, Integer> e : r.counts().entrySet()) {
                if (shown != 6) {
                    g.fill(rx, ry + 1, rx + 4, ry + 5, e.getKey().color());
                    String name = Component.translatable((String)e.getKey().key()).getString() + (String)(e.getValue() > 1 ? " x" + String.valueOf(e.getValue()) : "");
                    BoardPanel.small(g, font, name, rx + 7, ry, -1);
                    ry += 8;
                    ++shown;
                    continue;
                }
                break;
            }
        } else {
            BoardPanel.small(g, font, np < this.partOrder.size() ? "..." : Component.translatable((String)"gui.airwaves.workbench.open").getString(), rx, ry, -6643544);
        }
        int by = y1 + 6;
        if (works) {
            int bx = 4;
            List<BoardDesign> ds = r.designs();
            int[] e = new int[40];
            for (int i4 = 0; i4 < e.length; ++i4) {
                e[i4] = partOn[i4] ? this.parts[i4] : (traceOn[i4] ? -2 : -1);
            }
            BoardLayout layout = BoardLayout.of(e);
            for (int k3 = 0; k3 < ds.size() && k3 < 6; ++k3) {
                float pop = Math.min(1.0f, (time - (float)this.doneAt() - (float)(k3 * 3)) / 4.0f);
                if (pop <= 0.0f) continue;
                ItemStack st = new ItemStack((ItemLike)ds.get(k3).item());
                st.set((DataComponentType)ModRegistry.BOARD_LAYOUT.get(), (Object)layout);
                if (k3 == 0) {
                    BoardPanel.bevel(g, bx - 1, by - 1, bx + 17, by + 17, -1522584, -3920, -6657520);
                }
                BoardPanel.item(g, st, (float)bx + 8.0f * (1.0f - pop), (float)by + 8.0f * (1.0f - pop), pop);
                bx += 20;
            }
            if (!ds.isEmpty()) {
                BoardPanel.small(g, font, new ItemStack((ItemLike)ds.get(0).item()).getHoverName().getString(), bx + 2, by + 5, -5984);
            }
            Object prog = BoardAnalysis.describe(BoardAnalysis.program(r));
            int maxW = 274;
            if (font.width((String)prog) > maxW) {
                prog = font.plainSubstrByWidth((String)prog, maxW - font.width("...")) + "...";
            }
            BoardPanel.small(g, font, (String)prog, 4.0f, by + 21, -10434320);
        }
        ps.popPose();
    }

    private static int copper(boolean live, int dist, boolean works, float time) {
        if (!live) {
            return -5214162;
        }
        if (!works) {
            return -3110336;
        }
        double ph = (double)time * 0.5 - (double)dist * 2.2;
        int k = (int)(Math.pow(0.5 + 0.5 * Math.sin(ph), 6.0) * 110.0);
        return 0xFF000000 | Math.min(255, 216 + k / 3) << 16 | Math.min(255, 150 + k) << 8 | Math.min(255, 64 + k);
    }

    private static void bevel(GuiGraphics g, int x0, int y0, int x1, int y1, int fill, int hi, int lo) {
        g.fill(x0, y0, x1, y1, fill);
        g.fill(x0, y0, x1, y0 + 1, hi);
        g.fill(x0, y0, x0 + 1, y1, hi);
        g.fill(x0, y1 - 1, x1, y1, lo);
        g.fill(x1 - 1, y0, x1, y1, lo);
    }

    private static void outline(GuiGraphics g, int x0, int y0, int x1, int y1, int c) {
        g.fill(x0, y0, x1, y0 + 1, c);
        g.fill(x0, y1 - 1, x1, y1, c);
        g.fill(x0, y0, x0 + 1, y1, c);
        g.fill(x1 - 1, y0, x1, y1, c);
    }

    private static void item(GuiGraphics g, ItemStack st, float x, float y, float scale) {
        PoseStack ps = g.pose();
        ps.pushPose();
        ps.translate(x, y, 0.0f);
        ps.scale(scale, scale, 1.0f);
        g.renderItem(st, 0, 0);
        ps.popPose();
    }

    private static void small(GuiGraphics g, Font font, String s, float x, float y, int color) {
        PoseStack ps = g.pose();
        ps.pushPose();
        ps.translate(x, y, 50.0f);
        ps.scale(0.75f, 0.75f, 1.0f);
        g.drawString(font, s, 0, 0, color, false);
        ps.popPose();
    }

    public static class Show
    extends FadeInOutInstruction {
        private final BoardPanel panel;

        public Show(BoardPanel panel, int ticks) {
            super(ticks);
            this.panel = panel;
        }

        protected void show(PonderScene scene) {
            this.panel.age = 0;
            scene.addElement((PonderElement)this.panel);
            this.panel.setVisible(true);
        }

        protected void hide(PonderScene scene) {
            this.panel.setVisible(false);
        }

        protected void applyFade(PonderScene scene, float fade) {
            this.panel.setFade(fade);
        }

        public void tick(PonderScene scene) {
            super.tick(scene);
            this.panel.age = Math.max(0, this.totalTicks - this.remainingTicks - 5);
        }
    }
}
