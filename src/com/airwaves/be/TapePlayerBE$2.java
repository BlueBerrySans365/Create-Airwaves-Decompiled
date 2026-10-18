/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.ContainerData
 */
package com.airwaves.be;

import net.minecraft.world.inventory.ContainerData;

class TapePlayerBE.2
implements ContainerData {
    TapePlayerBE.2() {
    }

    public int get(int i) {
        return switch (i) {
            case 0 -> TapePlayerBE.this.mode;
            case 1 -> TapePlayerBE.this.pos;
            case 2 -> {
                if (TapePlayerBE.this.mode == 2) {
                    yield 6000;
                }
                yield TapePlayerBE.this.tape().length();
            }
            default -> TapePlayerBE.this.loop ? 1 : 0;
        };
    }

    public void set(int i, int v) {
    }

    public int getCount() {
        return 4;
    }
}
