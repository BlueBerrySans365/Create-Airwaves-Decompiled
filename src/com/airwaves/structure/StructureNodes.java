/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.structure;

import com.airwaves.structure.StructureBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

final class StructureNodes {
    private StructureNodes() {
    }

    static Vec3 anchor(BlockEntity be, int slot) {
        StructureBlock b;
        block3: {
            block2: {
                Block block = be.getBlockState().getBlock();
                if (!(block instanceof StructureBlock)) break block2;
                b = (StructureBlock)block;
                if (b.def.connectors.length != 0) break block3;
            }
            return Vec3.atCenterOf((Vec3i)be.getBlockPos());
        }
        return b.def.connector(be.getBlockPos(), Math.min(slot, b.def.connectors.length - 1), (Direction.Axis)be.getBlockState().getValue(StructureBlock.AXIS));
    }
}
