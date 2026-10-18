/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 */
package com.airwaves.client;

import com.airwaves.client.BoardItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

static class ClientInit.1
implements IClientItemExtensions {
    private BoardItemRenderer renderer;

    ClientInit.1() {
    }

    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        if (this.renderer == null) {
            this.renderer = new BoardItemRenderer();
        }
        return this.renderer;
    }
}
