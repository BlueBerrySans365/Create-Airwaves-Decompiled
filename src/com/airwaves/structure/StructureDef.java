/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.airwaves.structure;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class StructureDef {
    private static final Map<String, StructureDef> CACHE = new HashMap<String, StructureDef>();
    public final String name;
    public final int[][] parts;
    public final double[][] connectors;
    private final double[][][] shapes;
    private final double[][][] solid;
    private final VoxelShape[][] outline;
    private final VoxelShape[][] collide;
    private final int[] anchor;

    private StructureDef(String name, JsonObject o) {
        int k;
        this.name = name;
        JsonArray p = o.getAsJsonArray("parts");
        this.parts = new int[p.size()][3];
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (int i = 0; i < p.size(); ++i) {
            JsonArray a = p.get(i).getAsJsonArray();
            for (k = 0; k < 3; ++k) {
                this.parts[i][k] = a.get(k).getAsInt();
            }
            minX = Math.min(minX, this.parts[i][0]);
            maxX = Math.max(maxX, this.parts[i][0]);
            minY = Math.min(minY, this.parts[i][1]);
            minZ = Math.min(minZ, this.parts[i][2]);
            maxZ = Math.max(maxZ, this.parts[i][2]);
        }
        this.anchor = new int[]{Math.floorDiv(minX + maxX, 2), minY, Math.floorDiv(minZ + maxZ, 2)};
        this.shapes = StructureDef.boxes(o.getAsJsonArray("shapes"));
        this.solid = StructureDef.boxes(o.getAsJsonArray("solid"));
        JsonArray c = o.getAsJsonArray("connectors");
        this.connectors = new double[c.size()][3];
        for (int i = 0; i < c.size(); ++i) {
            for (k = 0; k < 3; ++k) {
                this.connectors[i][k] = c.get(i).getAsJsonArray().get(k).getAsDouble();
            }
        }
        this.outline = new VoxelShape[2][this.parts.length];
        this.collide = new VoxelShape[2][this.parts.length];
    }

    private static double[][][] boxes(JsonArray arr) {
        double[][][] out = new double[arr.size()][][];
        for (int i = 0; i < arr.size(); ++i) {
            JsonArray l = arr.get(i).getAsJsonArray();
            out[i] = new double[l.size()][6];
            for (int j = 0; j < l.size(); ++j) {
                for (int k = 0; k < 6; ++k) {
                    out[i][j][k] = l.get(j).getAsJsonArray().get(k).getAsDouble();
                }
            }
        }
        return out;
    }

    public static StructureDef get(String name) {
        return CACHE.computeIfAbsent(name, n -> {
            StructureDef structureDef;
            block9: {
                InputStream in = StructureDef.class.getResourceAsStream("/assets/airwaves/structures/" + n + ".json");
                try {
                    if (in == null) {
                        throw new IllegalStateException("missing structure " + n);
                    }
                    structureDef = new StructureDef((String)n, JsonParser.parseReader((Reader)new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject());
                    if (in == null) break block9;
                }
                catch (Throwable throwable) {
                    try {
                        if (in != null) {
                            try {
                                in.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
                in.close();
            }
            return structureDef;
        });
    }

    public int size() {
        return this.parts.length;
    }

    private static BlockPos rot(int dx, int dy, int dz, Direction.Axis axis) {
        return axis == Direction.Axis.Z ? new BlockPos(-dz, dy, dx) : new BlockPos(dx, dy, dz);
    }

    public BlockPos partPos(BlockPos at, int i, Direction.Axis axis) {
        return at.offset((Vec3i)StructureDef.rot(this.parts[i][0] - this.anchor[0], this.parts[i][1] - this.anchor[1], this.parts[i][2] - this.anchor[2], axis));
    }

    public BlockPos corePos(BlockPos partPos, int i, Direction.Axis axis) {
        return partPos.subtract((Vec3i)StructureDef.rot(this.parts[i][0] - this.parts[0][0], this.parts[i][1] - this.parts[0][1], this.parts[i][2] - this.parts[0][2], axis));
    }

    public BlockPos partFromCore(BlockPos core, int i, Direction.Axis axis) {
        return core.offset((Vec3i)StructureDef.rot(this.parts[i][0] - this.parts[0][0], this.parts[i][1] - this.parts[0][1], this.parts[i][2] - this.parts[0][2], axis));
    }

    public Vec3 connector(BlockPos core, int k, Direction.Axis axis) {
        double z;
        double x;
        double[] c = this.connectors[k];
        int[] c0 = this.parts[0];
        double y = c[1] / 16.0 - (double)c0[1];
        if (axis == Direction.Axis.Z) {
            x = -c[2] / 16.0 - (double)(-c0[2] - 1);
            z = c[0] / 16.0 - (double)c0[0];
        } else {
            x = c[0] / 16.0 - (double)c0[0];
            z = c[2] / 16.0 - (double)c0[2];
        }
        return new Vec3((double)core.getX() + x, (double)core.getY() + y, (double)core.getZ() + z);
    }

    public VoxelShape outline(int part, Direction.Axis axis) {
        return StructureDef.shape(this.outline, this.shapes, part, axis);
    }

    public VoxelShape collision(int part, Direction.Axis axis) {
        return StructureDef.shape(this.collide, this.solid, part, axis);
    }

    private static VoxelShape shape(VoxelShape[][] cache, double[][][] src, int part, Direction.Axis axis) {
        int a;
        int n = a = axis == Direction.Axis.Z ? 1 : 0;
        if (part < 0 || part >= src.length) {
            return Shapes.block();
        }
        VoxelShape v = cache[a][part];
        if (v != null) {
            return v;
        }
        v = Shapes.empty();
        for (double[] b : src[part]) {
            double x0 = b[0];
            double y0 = b[1];
            double z0 = b[2];
            double x1 = b[3];
            double y1 = b[4];
            double z1 = b[5];
            if (a == 1) {
                double nx0 = 16.0 - z1;
                double nx1 = 16.0 - z0;
                double nz0 = x0;
                double nz1 = x1;
                x0 = nx0;
                x1 = nx1;
                z0 = nz0;
                z1 = nz1;
            }
            v = Shapes.or((VoxelShape)v, (VoxelShape)Block.box((double)x0, (double)y0, (double)z0, (double)x1, (double)y1, (double)z1));
        }
        cache[a][part] = v.optimize();
        return cache[a][part];
    }
}
