/*
 * Decompiled with CFR 0.152.
 */
package com.airwaves.logic;

import com.airwaves.cable.CableType;
import com.airwaves.cable.NodeRef;

public record Cables.Seg(NodeRef from, NodeRef to, CableType type) {
}
