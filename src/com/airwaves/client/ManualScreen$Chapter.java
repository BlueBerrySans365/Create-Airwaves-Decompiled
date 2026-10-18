/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ItemStack
 */
package com.airwaves.client;

import com.airwaves.client.ManualScreen;
import java.util.List;
import net.minecraft.world.item.ItemStack;

private record ManualScreen.Chapter(String title, String shortTitle, String group, List<ItemStack> icons, List<String> paragraphs, List<List<ManualScreen.Line>> pages) {
}
