/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.airwaves.logic;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ShapeUtil {
    private ShapeUtil() {
    }

    public static Map<Direction, VoxelShape> make(double x1, double y1, double z1, double x2, double y2, double z2) {
        EnumMap<Direction, VoxelShape> m = new EnumMap<Direction, VoxelShape>(Direction.class);
        m.put(Direction.NORTH, Block.box((double)x1, (double)y1, (double)z1, (double)x2, (double)y2, (double)z2));
        m.put(Direction.SOUTH, Block.box((double)(16.0 - x2), (double)y1, (double)(16.0 - z2), (double)(16.0 - x1), (double)y2, (double)(16.0 - z1)));
        m.put(Direction.EAST, Block.box((double)(16.0 - z2), (double)y1, (double)x1, (double)(16.0 - z1), (double)y2, (double)x2));
        m.put(Direction.WEST, Block.box((double)z1, (double)y1, (double)(16.0 - x2), (double)z2, (double)y2, (double)(16.0 - x1)));
        return m;
    }
}
