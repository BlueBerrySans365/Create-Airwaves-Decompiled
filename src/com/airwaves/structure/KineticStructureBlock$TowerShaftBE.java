/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.airwaves.structure;

import com.airwaves.ModRegistry;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public static class KineticStructureBlock.TowerShaftBE
extends KineticBlockEntity {
    public KineticStructureBlock.TowerShaftBE(BlockPos p, BlockState s) {
        super(ModRegistry.TOWER_SHAFT_BE.get(), p, s);
    }

    public float calculateStressApplied() {
        this.lastStressApplied = 0.0f;
        return 0.0f;
    }
}
