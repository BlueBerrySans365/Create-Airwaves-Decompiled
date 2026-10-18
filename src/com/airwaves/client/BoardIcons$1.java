/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 */
package com.airwaves.client;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

class BoardIcons.1
extends LinkedHashMap<String, ResourceLocation> {
    BoardIcons.1(int arg0, float arg1, boolean arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, ResourceLocation> e) {
        if (this.size() <= 256) {
            return false;
        }
        Minecraft.getInstance().getTextureManager().release(e.getValue());
        return true;
    }
}
