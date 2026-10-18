/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.element.PonderElement
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.FadeInOutInstruction
 */
package com.airwaves.client.ponder;

import com.airwaves.client.ponder.BoardPanel;
import net.createmod.ponder.api.element.PonderElement;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.FadeInOutInstruction;

public static class BoardPanel.Show
extends FadeInOutInstruction {
    private final BoardPanel panel;

    public BoardPanel.Show(BoardPanel panel, int ticks) {
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
