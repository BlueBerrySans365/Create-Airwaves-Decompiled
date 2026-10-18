/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.FormattedCharSequence
 */
package com.airwaves.client;

import net.minecraft.util.FormattedCharSequence;

private record ManualScreen.Line(FormattedCharSequence text, int color, int indent, boolean heading, boolean tip, String bullet, String fig, int figLines) {
}
