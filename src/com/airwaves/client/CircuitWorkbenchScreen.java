/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package com.airwaves.client;

import com.airwaves.ModRegistry;
import com.airwaves.client.ProgramFlow;
import com.airwaves.logic.BoardAnalysis;
import com.airwaves.logic.BoardDesign;
import com.airwaves.logic.BoardLayout;
import com.airwaves.menu.CircuitWorkbenchMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class CircuitWorkbenchScreen
extends AbstractContainerScreen<CircuitWorkbenchMenu> {
    private static final int BG = -3750202;
    private static final int HI = -1;
    private static final int LO = -11184811;
    private static final int SLOT = -7631989;
    private static final int PCB = -14718150;
    private static final int PCB_HI = -13727154;
    private static final int PCB_LO = -15448278;
    private static final int PAD = -3634628;
    private static final int PAD_LIVE = -15254;
    private static final int TRACE = -5214162;
    private static final int TEXT = -12959928;
    private static final int OK = -13727170;
    private static final int WARN = -6661616;
    private static final int PANEL = -4934476;
    private static final int W = 8;
    private static final int H = 5;
    private static final int RX = 186;
    private static final int LIST_Y = 18;
    private static final int ROW = 10;
    private static final int CHIPS_Y = 129;
    private static final int STATUS_Y = 118;
    private static final int PANEL_Y = 168;
    private static final int PANEL_W = 88;
    private static final int BTN_Y = 149;
    private int ticks;
    private BoardDesign selected;
    private boolean painting;
    private int paintMode;
    private int lastCell = -1;
    private Button autoBtn;
    private Button clearBtn;
    private final List<int[]> stageRows = new ArrayList<int[]>();

    public CircuitWorkbenchScreen(CircuitWorkbenchMenu menu, Inventory inv, Component title) {
        super((AbstractContainerMenu)menu, inv, title);
        this.imageWidth = 280;
        this.imageHeight = 242;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 148;
    }

    protected void init() {
        super.init();
        this.autoBtn = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.airwaves.workbench.auto"), b -> {
            if (this.selected != null) {
                this.send(200 + this.selected.ordinal(), false);
            }
        }).bounds(this.leftPos + 186 - 2, this.topPos + 149, 45, 14).build());
        this.clearBtn = (Button)this.addRenderableWidget((GuiEventListener)Button.builder((Component)Component.translatable((String)"gui.airwaves.workbench.clear"), b -> this.send(300, true)).bounds(this.leftPos + 186 + 45, this.topPos + 149, 44, 14).build());
    }

    protected void containerTick() {
        super.containerTick();
        ++this.ticks;
        this.autoBtn.active = this.selected != null;
        boolean any = false;
        for (boolean t : ((CircuitWorkbenchMenu)this.menu).traces()) {
            any |= t;
        }
        this.clearBtn.active = any;
    }

    private void send(int id, boolean predict) {
        if (predict && this.minecraft.player != null) {
            ((CircuitWorkbenchMenu)this.menu).clickMenuButton((Player)this.minecraft.player, id);
        }
        if (this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(((CircuitWorkbenchMenu)this.menu).containerId, id);
        }
    }

    private void click(float pitch) {
        this.minecraft.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value()), (float)pitch, (float)0.3f));
    }

    private int cellAt(double mx, double my) {
        double x = mx - (double)this.leftPos - 24.0 + 1.0;
        double y = my - (double)this.topPos - 20.0 + 1.0;
        if (x < 0.0 || y < 0.0) {
            return -1;
        }
        int c = (int)(x / 18.0);
        int r = (int)(y / 18.0);
        return c < 8 && r < 5 ? r * 8 + c : -1;
    }

    private boolean drawable(int cell) {
        return cell >= 0 && !((CircuitWorkbenchMenu)this.menu).getSlot(cell).hasItem() && ((CircuitWorkbenchMenu)this.menu).getCarried().isEmpty();
    }

    private void paint(int cell) {
        boolean has = ((CircuitWorkbenchMenu)this.menu).trace(cell);
        if (this.paintMode == 0 && has || this.paintMode == 100 && !has) {
            return;
        }
        this.send(this.paintMode + cell, true);
        this.minecraft.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value()), (float)(this.paintMode == 0 ? 2.0f : 1.4f), (float)0.12f));
    }

    private int listRow(double mx, double my) {
        if (mx < (double)(this.leftPos + 186 - 2) || mx >= (double)(this.leftPos + 186 + 88)) {
            return -1;
        }
        int r = (int)Math.floor((my - (double)this.topPos - 18.0 + 1.0) / 10.0);
        return my >= (double)(this.topPos + 18 - 1) && r >= 0 && r < BoardDesign.values().length ? r : -1;
    }

    private int chipAt(double mx, double my, List<BoardDesign> ds) {
        if (my < (double)(this.topPos + 129) || my >= (double)(this.topPos + 129 + 18)) {
            return -1;
        }
        int k = (int)Math.floor((mx - (double)this.leftPos - 8.0) / 20.0);
        return mx >= (double)(this.leftPos + 8) && k >= 0 && k < ds.size() ? k : -1;
    }

    public boolean mouseClicked(double mx, double my, int btn) {
        int cell = this.cellAt(mx, my);
        if (this.drawable(cell) && (btn == 0 || btn == 1)) {
            this.paintMode = btn == 1 || ((CircuitWorkbenchMenu)this.menu).trace(cell) ? 100 : 0;
            this.painting = true;
            this.lastCell = cell;
            this.paint(cell);
            return true;
        }
        int row = this.listRow(mx, my);
        if (row >= 0 && btn == 0) {
            BoardDesign d = BoardDesign.values()[row];
            this.selected = this.selected == d ? null : d;
            this.click(0.6f);
            return true;
        }
        BoardAnalysis.Result r = ((CircuitWorkbenchMenu)this.menu).check();
        int chip = this.chipAt(mx, my, r.designs());
        if (chip >= 0 && btn == 0) {
            this.send(400 + r.designs().get(chip).ordinal(), true);
            this.click(1.2f);
            return true;
        }
        return super.mouseClicked(mx, my, btn);
    }

    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (this.painting) {
            int cell = this.cellAt(mx, my);
            if (cell != this.lastCell && this.drawable(cell)) {
                this.paint(cell);
            }
            this.lastCell = cell;
            return true;
        }
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    public boolean mouseReleased(double mx, double my, int btn) {
        if (this.painting) {
            this.painting = false;
            this.lastCell = -1;
            return true;
        }
        return super.mouseReleased(mx, my, btn);
    }

    private static void bevel(GuiGraphics g, int x0, int y0, int x1, int y1, int fill, int hi, int lo) {
        g.fill(x0, y0, x1, y1, fill);
        g.fill(x0, y0, x1, y0 + 1, hi);
        g.fill(x0, y0, x0 + 1, y1, hi);
        g.fill(x0, y1 - 1, x1, y1, lo);
        g.fill(x1 - 1, y0, x1, y1, lo);
    }

    private int copper(boolean live, int dist, boolean works, float pt) {
        if (!live) {
            return -5214162;
        }
        if (!works) {
            return -3110336;
        }
        double ph = (double)((float)this.ticks + pt) * 0.5 - (double)dist * 2.2;
        double s = Math.pow(0.5 + 0.5 * Math.sin(ph), 6.0);
        int k = (int)(s * 110.0);
        return 0xFF000000 | Math.min(255, 216 + k / 3) << 16 | Math.min(255, 150 + k) << 8 | Math.min(255, 64 + k);
    }

    private ItemStack preview(BoardDesign d) {
        ItemStack st = new ItemStack((ItemLike)d.item());
        int[] p = ((CircuitWorkbenchMenu)this.menu).parts();
        boolean[] t = ((CircuitWorkbenchMenu)this.menu).traces();
        int[] e = new int[40];
        for (int i = 0; i < e.length; ++i) {
            e[i] = p[i] >= 0 ? p[i] : (t[i] ? -2 : -1);
        }
        st.set((DataComponentType)ModRegistry.BOARD_LAYOUT.get(), (Object)BoardLayout.of(e));
        return st;
    }

    protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
        String name;
        BoardDesign chosen;
        boolean ready;
        BoardAnalysis.Stage hs;
        int col;
        int k;
        int col2;
        int row;
        int x = this.leftPos;
        int y = this.topPos;
        CircuitWorkbenchScreen.bevel(g, x, y, x + this.imageWidth, y + this.imageHeight, -3750202, -1, -11184811);
        BoardAnalysis.Result r = ((CircuitWorkbenchMenu)this.menu).check();
        boolean works = r.works();
        ItemStack[] c = ((CircuitWorkbenchMenu)this.menu).cells();
        boolean[] tr = ((CircuitWorkbenchMenu)this.menu).traces();
        boolean[] live = r.live();
        int[] dist = r.dist();
        int[] ghost = this.selected != null ? this.selected.example() : null;
        int bx = x + 24 - 1;
        int by = y + 20 - 1;
        CircuitWorkbenchScreen.bevel(g, bx - 14, by - 4, bx + 144 + 14, by + 90 + 4, -14718150, -13727154, -15448278);
        for (int i = 0; i < 10; ++i) {
            g.fill(bx - 12 + i * 17, by - 3, bx - 11 + i * 17, by - 2, -13727154);
        }
        g.fill(bx - 12, by + 90 + 1, bx - 9, by + 90 + 3, -15448278);
        g.fill(bx + 144 + 9, by + 90 + 1, bx + 144 + 12, by + 90 + 3, -15448278);
        for (row = 0; row < 5; ++row) {
            int a = row * 8;
            int b = row * 8 + 8 - 1;
            g.fill(bx - 11, by + row * 18 + 6, bx - 3, by + row * 18 + 12, live[a] ? -15254 : -3634628);
            g.fill(bx + 144 + 3, by + row * 18 + 6, bx + 144 + 11, by + row * 18 + 12, works && live[b] ? -15254 : -3634628);
        }
        for (row = 0; row < 5; ++row) {
            for (col2 = 0; col2 < 8; ++col2) {
                boolean cond;
                int sx = bx + col2 * 18;
                int sy = by + row * 18;
                int i = row * 8 + col2;
                boolean bl = cond = !c[i].isEmpty() || tr[i];
                g.fill(sx + 1, sy + 1, sx + 17, sy + 17, c[i].isEmpty() ? -15184083 : (live[i] ? -12965358 : -14016234));
                g.fill(sx + 1, sy + 1, sx + 3, sy + 3, -3634628);
                g.fill(sx + 15, sy + 1, sx + 17, sy + 3, -3634628);
                g.fill(sx + 1, sy + 15, sx + 3, sy + 17, -3634628);
                g.fill(sx + 15, sy + 15, sx + 17, sy + 17, -3634628);
                if (cond || ghost == null || ghost[i] != -2) continue;
                for (k = 2; k < 16; k += 3) {
                    g.fill(sx + k, sy + 8, sx + k + 1, sy + 10, -1593855161);
                }
            }
        }
        for (row = 0; row < 5; ++row) {
            for (col2 = 0; col2 < 8; ++col2) {
                boolean cond;
                int i = row * 8 + col2;
                boolean bl = cond = !c[i].isEmpty() || tr[i];
                if (!cond) continue;
                int cx = bx + col2 * 18 + 8;
                int cy = by + row * 18 + 8;
                int cc = this.copper(live[i], Math.max(0, dist[i]), works, pt);
                if (col2 == 0) {
                    g.fill(bx - 3, cy, cx, cy + 2, cc);
                }
                if (col2 == 7) {
                    g.fill(cx, cy, bx + 144 + 3, cy + 2, cc);
                }
                if (col2 < 7 && (!c[i + 1].isEmpty() || tr[i + 1])) {
                    g.fill(cx, cy, cx + 18, cy + 2, cc);
                }
                if (row < 4 && (!c[i + 8].isEmpty() || tr[i + 8])) {
                    g.fill(cx, cy, cx + 2, cy + 18, cc);
                }
                if (!tr[i] || !c[i].isEmpty()) continue;
                g.fill(cx - 2, cy - 2, cx + 4, cy + 4, live[i] ? -15254 : -3634628);
                g.fill(cx, cy, cx + 2, cy + 2, -14016234);
            }
        }
        int hc = this.cellAt(mx, my);
        if (!this.painting && this.drawable(hc) && !tr[hc]) {
            int sx = bx + hc % 8 * 18;
            int sy = by + hc / 8 * 18;
            g.fill(sx + 1, sy + 1, sx + 17, sy + 17, 822063943);
        }
        for (int row2 = 0; row2 < 5; ++row2) {
            for (col = 0; col < 8; ++col) {
                int sx = bx + col * 18;
                int sy = by + row2 * 18;
                int i = row2 * 8 + col;
                if (works && c[i].is((Item)ModRegistry.LED.get())) {
                    int a = 96 + (int)(64.0 * Math.sin((double)((float)this.ticks + pt) * 0.3 + (double)i));
                    g.fill(sx - 1, sy - 1, sx + 19, sy + 19, a << 24 | 0xFF4030);
                }
                if (works && c[i].is(ModRegistry.electronTube())) {
                    g.fill(sx, sy, sx + 18, sy + 18, 1090494528);
                }
                if (ghost == null || ghost[i] < 0 || !c[i].isEmpty() || tr[i]) continue;
                g.renderItem(new ItemStack((ItemLike)BoardDesign.Part.values()[ghost[i]].item()), sx + 1, sy + 1);
                g.pose().pushPose();
                g.pose().translate(0.0f, 0.0f, 250.0f);
                g.fill(sx + 1, sy + 1, sx + 17, sy + 17, -1609019603);
                g.pose().popPose();
            }
        }
        BoardAnalysis.Stage stage = hs = works ? this.hoveredStage(mx, my) : null;
        if (hs != null) {
            col = hs.color();
            int a = 160 + (int)(80.0 * Math.sin((double)((float)this.ticks + pt) * 0.4));
            for (int i : r.cells(hs)) {
                int sx = bx + i % 8 * 18;
                int sy = by + i / 8 * 18;
                int c2 = a << 24 | col & 0xFFFFFF;
                g.fill(sx - 1, sy - 1, sx + 19, sy, c2);
                g.fill(sx - 1, sy + 18, sx + 19, sy + 19, c2);
                g.fill(sx - 1, sy, sx, sy + 18, c2);
                g.fill(sx + 18, sy, sx + 19, sy + 18, c2);
            }
        }
        boolean bl = ready = (chosen = ((CircuitWorkbenchMenu)this.menu).chosen(r)) != null && ((CircuitWorkbenchMenu)this.menu).hasBlank();
        MutableComponent st = works ? (((CircuitWorkbenchMenu)this.menu).hasBlank() ? Component.translatable((String)"gui.airwaves.workbench.ready", (Object[])new Object[]{Component.translatable((String)("item.airwaves." + chosen.id))}) : Component.translatable((String)"gui.airwaves.workbench.need_blank")) : Component.translatable((String)r.status());
        List sl = this.font.split((FormattedText)st, 172);
        if (!sl.isEmpty()) {
            g.drawString(this.font, (FormattedCharSequence)sl.get(0), x + 8, y + 118, ready ? -13727170 : (works ? -6661616 : -12959928), false);
        }
        if (works) {
            for (k = 0; k < r.designs().size(); ++k) {
                BoardDesign d = r.designs().get(k);
                int cx = x + 8 + k * 20;
                int cy = y + 129;
                CircuitWorkbenchScreen.bevel(g, cx, cy, cx + 18, cy + 18, d == chosen ? -1522584 : -7631989, d == chosen ? -3920 : -11184811, d == chosen ? -6657520 : -1);
                g.renderItem(this.preview(d), cx + 1, cy + 1);
            }
        }
        for (k = 42; k < 78; ++k) {
            Slot s = ((CircuitWorkbenchMenu)this.menu).getSlot(k);
            CircuitWorkbenchScreen.slotBox(g, x + s.x - 1, y + s.y - 1, 18);
        }
        g.drawString(this.font, (Component)Component.translatable((String)"gui.airwaves.workbench.blueprints"), x + 186, y + 6, -12959928, false);
        CircuitWorkbenchScreen.bevel(g, x + 186 - 3, y + 18 - 3, x + 186 + 88 + 1, y + 18 + BoardDesign.values().length * 10 + 1, -4934476, -11184811, -1);
        int hover = this.listRow(mx, my);
        for (BoardDesign d : BoardDesign.values()) {
            int i = d.ordinal();
            int ry = y + 18 + i * 10;
            if (d == this.selected) {
                g.fill(x + 186 - 2, ry - 1, x + 186 + 88, ry + 10 - 1, 0x60FFFFFF);
            } else if (i == hover) {
                g.fill(x + 186 - 2, ry - 1, x + 186 + 88, ry + 10 - 1, 0x25FFFFFF);
            }
            g.fill(x + 186 - 2, ry - 1, x + 186, ry + 10 - 1, d.accent);
            g.pose().pushPose();
            g.pose().translate((float)(x + 186 + 1), (float)(ry - 1), 0.0f);
            g.pose().scale(0.6f, 0.6f, 1.0f);
            g.renderItem(new ItemStack((ItemLike)d.item()), 0, 0);
            g.pose().popPose();
            name = this.font.plainSubstrByWidth(Component.translatable((String)("item.airwaves." + d.id)).getString().replace(" Board", ""), 72);
            g.drawString(this.font, name, x + 186 + 13, ry + 1, d == chosen && works ? -13727170 : -12959928, false);
        }
        CircuitWorkbenchScreen.slotBox(g, x + 196 - 1, y + 125 - 1, 18);
        CircuitWorkbenchScreen.slotBox(g, x + 244 - 5, y + 125 - 5, 26);
        if (((CircuitWorkbenchMenu)this.menu).getSlot(40).getItem().isEmpty()) {
            g.renderItem(new ItemStack((ItemLike)ModRegistry.CIRCUIT_BOARD.get()), x + 196, y + 125);
            g.pose().pushPose();
            g.pose().translate(0.0f, 0.0f, 250.0f);
            g.fill(x + 196, y + 125, x + 196 + 16, y + 125 + 16, -1601467509);
            g.pose().popPose();
        }
        int ax = x + 196 + 20;
        int ay = y + 125 + 7;
        g.fill(ax, ay, ax + 16, ay + 3, -7631989);
        if (works) {
            g.fill(ax, ay, ax + (ready ? 16 : 8), ay + 3, ready ? -13727170 : -6661616);
        }
        for (int k2 = 0; k2 < 5; ++k2) {
            g.fill(ax + 16 + k2, ay - 3 + k2, ax + 17 + k2, ay + 6 - k2, ready ? -13727170 : -7631989);
        }
        if (ready) {
            int sp = (int)((double)((float)this.ticks + pt) * 1.2) % 16;
            g.fill(ax + sp, ay - 1, ax + sp + 2, ay + 4, -4653136);
        }
        CircuitWorkbenchScreen.bevel(g, x + 186 - 3, y + 168 - 3, x + 186 + 88 + 1, y + this.imageHeight - 4, -4934476, -11184811, -1);
        int ly = y + 168;
        int bottom = y + this.imageHeight - 6;
        if (!works) {
            for (FormattedCharSequence l : this.font.split((FormattedText)Component.translatable((String)"gui.airwaves.workbench.howto"), 86)) {
                if (ly + 9 <= bottom) {
                    g.drawString(this.font, l, x + 186, ly, -12959928, false);
                    ly += 9;
                    continue;
                }
                break;
            }
        } else {
            g.drawString(this.font, (Component)Component.translatable((String)"gui.airwaves.workbench.stages").withStyle(ChatFormatting.BOLD), x + 186, ly, -12959928, false);
            ly += 10;
            this.stageRows.clear();
            for (Map.Entry<BoardAnalysis.Stage, Integer> e : r.counts().entrySet()) {
                this.stageRows.add(new int[]{ly - y, e.getKey().ordinal()});
                if (this.hoveredStage(mx, my) == e.getKey()) {
                    g.fill(x + 186 - 2, ly - 1, x + 186 + 88, ly + 8, 0x40FFFFFF);
                }
                g.fill(x + 186 - 1, ly + 2, x + 186 + 2, ly + 5, CircuitWorkbenchScreen.stageColor(e.getKey()));
                name = Component.translatable((String)e.getKey().key()).getString();
                g.drawString(this.font, this.font.plainSubstrByWidth((String)(e.getValue() > 1 ? String.valueOf(e.getValue()) + " " + name : name), 84), x + 186 + 3, ly, -12959928, false);
                ly += 8;
            }
            if (r.counts().isEmpty()) {
                g.drawString(this.font, (Component)Component.translatable((String)"gui.airwaves.workbench.no_stages"), x + 186, ly, -6661616, false);
                ly += 9;
            }
            this.flow(g, BoardAnalysis.program(r), x + 186, ly += 3, bottom);
        }
    }

    private void flow(GuiGraphics g, BoardAnalysis.Program p, int x0, int y0, int bottom) {
        ProgramFlow.draw(g, this.font, p, x0, y0, x0 + 88 - 2, bottom, this.ticks / 6);
    }

    private static int stageColor(BoardAnalysis.Stage s) {
        return s.color();
    }

    private BoardAnalysis.Stage hoveredStage(double mx, double my) {
        if (mx < (double)(this.leftPos + 186 - 2) || mx >= (double)(this.leftPos + 186 + 88)) {
            return null;
        }
        for (int[] row : this.stageRows) {
            if (!(my >= (double)(this.topPos + row[0] - 1)) || !(my < (double)(this.topPos + row[0] + 8))) continue;
            return BoardAnalysis.Stage.values()[row[1]];
        }
        return null;
    }

    private static void slotBox(GuiGraphics g, int x, int y, int s) {
        CircuitWorkbenchScreen.bevel(g, x, y, x + s, y + s, -7631989, -11184811, -1);
    }

    protected void renderLabels(GuiGraphics g, int mx, int my) {
        g.drawString(this.font, this.title, 8, 6, -12959928, false);
        g.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12959928, false);
    }

    public void render(GuiGraphics g, int mx, int my, float pt) {
        BoardDesign.Part part;
        super.render(g, mx, my, pt);
        this.renderTooltip(g, mx, my);
        if (!((CircuitWorkbenchMenu)this.menu).getCarried().isEmpty()) {
            return;
        }
        int row = this.listRow(mx, my);
        if (row >= 0) {
            BoardDesign d = BoardDesign.values()[row];
            ArrayList<MutableComponent> tip = new ArrayList<MutableComponent>();
            tip.add(Component.translatable((String)("item.airwaves." + d.id)));
            tip.add(Component.translatable((String)("tooltip.airwaves.board." + d.id)).withStyle(ChatFormatting.DARK_AQUA));
            tip.add(Component.translatable((String)"gui.airwaves.workbench.blueprint_hint").withStyle(ChatFormatting.GRAY));
            tip.add(Component.translatable((String)(d == this.selected ? "gui.airwaves.workbench.click_hide" : "gui.airwaves.workbench.click_show")).withStyle(ChatFormatting.DARK_GRAY));
            g.renderComponentTooltip(this.font, tip, mx, my);
            return;
        }
        BoardAnalysis.Result r = ((CircuitWorkbenchMenu)this.menu).check();
        int chip = this.chipAt(mx, my, r.works() ? r.designs() : List.of());
        if (chip >= 0) {
            BoardDesign d = r.designs().get(chip);
            g.renderComponentTooltip(this.font, List.of(Component.translatable((String)("item.airwaves." + d.id)), Component.translatable((String)("tooltip.airwaves.board." + d.id)).withStyle(ChatFormatting.DARK_AQUA), Component.translatable((String)"gui.airwaves.workbench.pick").withStyle(ChatFormatting.DARK_GRAY)), mx, my);
            return;
        }
        int cell = this.cellAt(mx, my);
        if (cell >= 0 && !((CircuitWorkbenchMenu)this.menu).cells()[cell].isEmpty() && (part = BoardDesign.Part.of(((CircuitWorkbenchMenu)this.menu).cells()[cell])) != null) {
            ArrayList<Object> tip = new ArrayList<Object>();
            tip.add(((CircuitWorkbenchMenu)this.menu).cells()[cell].getHoverName());
            tip.add(Component.translatable((String)("gui.airwaves.part." + part.name().toLowerCase(Locale.ROOT))).withStyle(ChatFormatting.GRAY));
            for (Map.Entry<BoardAnalysis.Stage, Integer> e : r.counts().entrySet()) {
                if (!r.cells(e.getKey()).contains(cell)) continue;
                tip.add(Component.literal((String)"\u25a0 ").withColor(e.getKey().color() & 0xFFFFFF).append((Component)Component.translatable((String)e.getKey().key()).withStyle(ChatFormatting.WHITE)));
            }
            g.renderComponentTooltip(this.font, tip, mx, my);
            return;
        }
        if (!this.painting && this.drawable(cell)) {
            g.renderTooltip(this.font, this.font.split((FormattedText)Component.translatable((String)(((CircuitWorkbenchMenu)this.menu).trace(cell) ? "gui.airwaves.workbench.erase_hint" : "gui.airwaves.workbench.draw_hint")), 160), mx, my);
        }
        int bx = this.leftPos + 24 - 1;
        int by = this.topPos + 20 - 1;
        if (my >= by && my < by + 90) {
            if (mx >= bx - 12 && mx < bx - 2) {
                g.renderTooltip(this.font, (Component)Component.translatable((String)"gui.airwaves.workbench.in"), mx, my);
            }
            if (mx >= bx + 144 + 2 && mx < bx + 144 + 12) {
                g.renderTooltip(this.font, (Component)Component.translatable((String)"gui.airwaves.workbench.out"), mx, my);
            }
        }
    }
}
