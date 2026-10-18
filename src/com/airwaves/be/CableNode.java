/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.be;

import com.airwaves.be.LinkSet;
import com.airwaves.cable.CableType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public interface CableNode {
    public LinkSet links();

    public BlockPos nodePos();

    default public int connectors() {
        return 1;
    }

    public int maxLinks(int var1);

    default public boolean accepts(int slot, CableType t) {
        return t.light;
    }

    default public boolean junction(int slot) {
        return false;
    }

    public Vec3 anchor(int var1);

    public void cablesChanged();

    default public int slotAt(Vec3 hit) {
        int best = 0;
        double bd = Double.MAX_VALUE;
        for (int i = 0; i < this.connectors(); ++i) {
            double d = this.anchor(i).distanceToSqr(hit);
            if (!(d < bd)) continue;
            bd = d;
            best = i;
        }
        return best;
    }

    public static Vec3 facing(BlockEntity be, double px, double py, double pz) {
        double x = px / 16.0;
        double y = py / 16.0;
        double z = pz / 16.0;
        BlockState st = be.getBlockState();
        if (st.hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING)) {
            double ox = x;
            double oz = z;
            Direction f = (Direction)st.getValue((Property)BlockStateProperties.HORIZONTAL_FACING);
            switch (f) {
                case SOUTH: {
                    x = 1.0 - ox;
                    z = 1.0 - oz;
                    break;
                }
                case EAST: {
                    x = 1.0 - oz;
                    z = ox;
                    break;
                }
                case WEST: {
                    x = oz;
                    z = 1.0 - ox;
                    break;
                }
            }
        }
        BlockPos p = be.getBlockPos();
        return new Vec3((double)p.getX() + x, (double)p.getY() + y, (double)p.getZ() + z);
    }
}
