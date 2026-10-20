/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.be;

import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

private static class YagiAntennaBE.TopBox
extends CenteredSideValueBoxTransform {
    static final float TOP = 12.0f;

    YagiAntennaBE.TopBox() {
        super((state, d) -> d == Direction.UP);
    }

    public Vec3 getLocalOffset(LevelAccessor l, BlockPos p, BlockState s) {
        return new Vec3(0.5, 0.753125, 0.5);
    }
}
