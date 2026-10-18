/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.ClientSubLevelAccess
 *  dev.ryanhcode.sable.companion.SableCompanion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 */
package com.airwaves.compat;

import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public final class SableCompat {
    private static final SableCompanion S = SableCompanion.INSTANCE;

    private SableCompat() {
    }

    public static boolean onShip(Level lv, BlockPos p) {
        return lv != null && S.getContaining(lv, (Vec3i)p) != null;
    }

    public static Vec3 global(Level lv, Vec3 p) {
        if (lv == null) {
            return p;
        }
        Vector3d v = S.projectOutOfSubLevel(lv, new Vector3d(p.x, p.y, p.z));
        return new Vec3(v.x, v.y, v.z);
    }

    public static Vec3 global(Level lv, BlockPos p) {
        return SableCompat.global(lv, Vec3.atCenterOf((Vec3i)p));
    }

    public static BlockPos globalPos(Level lv, BlockPos p) {
        return SableCompat.onShip(lv, p) ? BlockPos.containing((Position)SableCompat.global(lv, p)) : p;
    }

    public static double distance(Level lv, Vec3 a, Vec3 b) {
        if (lv == null) {
            return a.distanceTo(b);
        }
        return Math.sqrt(S.distanceSquaredWithSubLevels(lv, (Position)a, (Position)b));
    }

    public static double altitude(Level lv, BlockPos p) {
        int z;
        if (!SableCompat.onShip(lv, p)) {
            return 0.0;
        }
        Vec3 g = SableCompat.global(lv, p);
        int x = (int)Math.floor(g.x);
        if (!lv.isLoaded(new BlockPos(x, 64, z = (int)Math.floor(g.z)))) {
            return 0.0;
        }
        return Math.max(0.0, g.y - (double)lv.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z));
    }

    public static Vec3 intoRenderSpaceOf(BlockPos local, Vec3 globalPoint, float partialTick) {
        ClientSubLevelAccess s = S.getContainingClient((Vec3i)local);
        return s == null ? globalPoint : s.renderPose(partialTick).transformPositionInverse(globalPoint);
    }

    public static Vec3 rendered(Vec3 plotPoint, float partialTick) {
        ClientSubLevelAccess s = S.getContainingClient((Position)plotPoint);
        return s == null ? plotPoint : s.renderPose(partialTick).transformPosition(plotPoint);
    }

    public static boolean sameSpace(BlockPos a, BlockPos b) {
        ClientSubLevelAccess y;
        ClientSubLevelAccess x = S.getContainingClient((Vec3i)a);
        return x == (y = S.getContainingClient((Vec3i)b)) || x != null && y != null && x.getUniqueId().equals(y.getUniqueId());
    }
}
