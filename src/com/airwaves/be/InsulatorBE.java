/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.be;

import com.airwaves.ModRegistry;
import com.airwaves.be.CableNode;
import com.airwaves.be.NodeBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class InsulatorBE
extends NodeBE {
    public InsulatorBE(BlockPos p, BlockState s) {
        super(ModRegistry.INSULATOR_BE.get(), p, s);
    }

    @Override
    public int maxLinks(int slot) {
        return 4;
    }

    @Override
    public boolean junction(int slot) {
        return true;
    }

    @Override
    public Vec3 anchor(int slot) {
        return CableNode.facing(this, 8.0, 9.6, 8.0);
    }
}
