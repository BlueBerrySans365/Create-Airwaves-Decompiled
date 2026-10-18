/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.HorizontalKineticBlock
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.power;

import com.airwaves.kinetic.KineticDeviceBlock;
import com.airwaves.power.TowerTerminals;
import com.airwaves.structure.StructureBlock;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class Terminals {
    public static final int POSITIVE = 0;
    public static final int NEGATIVE = 1;
    public static final double[][] DEFAULT = new double[][]{{11.0, 1.0, 0.0, 14.0, 4.0, 1.0}, {2.0, 1.0, 0.0, 5.0, 4.0, 1.0}};

    private Terminals() {
    }

    public static double[][] spec(BlockState st) {
        double[][] dArray;
        Block block = st.getBlock();
        if (block instanceof KineticDeviceBlock) {
            KineticDeviceBlock k = (KineticDeviceBlock)block;
            dArray = k.terminals();
        } else {
            dArray = DEFAULT;
        }
        return dArray;
    }

    public static Direction facing(BlockState st) {
        return st.hasProperty(HorizontalKineticBlock.HORIZONTAL_FACING) ? (Direction)st.getValue(HorizontalKineticBlock.HORIZONTAL_FACING) : Direction.NORTH;
    }

    public static boolean has(BlockState st) {
        return TowerTerminals.isTerminal(st) || st.getBlock() instanceof KineticDeviceBlock && st.hasProperty(HorizontalKineticBlock.HORIZONTAL_FACING);
    }

    public static AABB box(BlockState st, int i) {
        if (st.getBlock() instanceof StructureBlock) {
            return TowerTerminals.box(st, i);
        }
        double[] b = Terminals.spec(st)[i];
        Direction f = Terminals.facing(st);
        return new AABB(Terminals.rotate(new Vec3(b[0] / 16.0, b[1] / 16.0, b[2] / 16.0), f), Terminals.rotate(new Vec3(b[3] / 16.0, b[4] / 16.0, b[5] / 16.0), f));
    }

    public static Vec3 point(BlockState st, int i) {
        if (st.getBlock() instanceof StructureBlock) {
            return TowerTerminals.point(st, i);
        }
        double[] b = Terminals.spec(st)[i];
        return Terminals.rotate(new Vec3((b[0] + b[3]) / 32.0, (b[1] + b[4]) / 32.0, (b[2] - 1.0) / 16.0), Terminals.facing(st));
    }

    public static int at(BlockState st, Vec3 local) {
        for (int i = 0; i < 2; ++i) {
            if (!Terminals.box(st, i).inflate(0.03).contains(local)) continue;
            return i;
        }
        return -1;
    }

    private static Vec3 rotate(Vec3 v, Direction f) {
        return switch (f) {
            case Direction.EAST -> new Vec3(1.0 - v.z, v.y, v.x);
            case Direction.SOUTH -> new Vec3(1.0 - v.x, v.y, 1.0 - v.z);
            case Direction.WEST -> new Vec3(v.z, v.y, 1.0 - v.x);
            default -> v;
        };
    }
}
