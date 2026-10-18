/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 */
package com.airwaves.be;

import com.airwaves.Tuning;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public interface Tuned {
    public Tuning getTuning();

    public void setTuning(Tuning var1);

    public Component getStationName();

    public void sync();

    public static Component chamberLine(int crystal) {
        return Component.literal((String)("    Chamber: " + (crystal == 2 ? "amethyst block" : (crystal == 1 ? "amethyst shard" : "empty")))).withStyle(crystal > 0 ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.DARK_GRAY);
    }
}
