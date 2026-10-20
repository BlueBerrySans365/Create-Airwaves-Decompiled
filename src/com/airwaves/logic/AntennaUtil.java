/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.airwaves.logic;

import com.airwaves.be.CreativeAntennaBE;
import com.airwaves.be.YagiAntennaBE;
import com.airwaves.block.AntennaBlock;
import com.airwaves.block.CreativeAntennaBlock;
import com.airwaves.block.YagiAntennaBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class AntennaUtil {
    public static final int MIN_TX_HEIGHT = 4;
    private static final int[] CAPS = new int[]{20, 40, 64, 96};
    private static final double[] RANGE_MULT = new double[]{1.0, 1.3, 1.7, 2.3};
    private static final float[] QUALITY_BONUS = new float[]{0.0f, 0.08f, 0.15f, 0.22f};
    public static final String[] TX_NAMES = new String[]{"Low-Power", "Mid-Power", "High-Power", "Max-Power"};
    public static final String[] RX_NAMES = new String[]{"Basic", "Sensitive", "High-Gain", "Max-Gain"};
    public static final String[] TIER_NAMES = TX_NAMES;
    public static final double FORWARD = 1.9;
    public static final double SIDE = 0.0;
    public static final double BACK = 0.0;
    public static final double BEAM_EDGE = 0.5;

    private AntennaUtil() {
    }

    public static int cap(int tier) {
        return CAPS[Math.max(1, Math.min(4, tier)) - 1];
    }

    public static Info scan(Level l, BlockPos base, int wantTier) {
        if (l.isLoaded(base) && l.getBlockState(base).getBlock() instanceof CreativeAntennaBlock) {
            double d;
            int n = AntennaUtil.cap(wantTier);
            int n2 = Math.max(1, Math.min(4, wantTier));
            BlockEntity blockEntity = l.getBlockEntity(base);
            if (blockEntity instanceof CreativeAntennaBE) {
                CreativeAntennaBE ca = (CreativeAntennaBE)blockEntity;
                d = ca.range();
            } else {
                d = 1000.0;
            }
            return new Info(n, n2, d);
        }
        Info i = AntennaUtil.scan(l, base);
        if (i.height() <= 0) {
            return Info.NONE;
        }
        int t = Math.max(1, Math.min(4, wantTier));
        return new Info(Math.min(i.height(), AntennaUtil.cap(t)), t, -1.0, i.beam());
    }

    public static Info scan(Level l, BlockPos base) {
        int h;
        Block block;
        if (!l.isLoaded(base) || !((block = l.getBlockState(base).getBlock()) instanceof AntennaBlock)) {
            return Info.NONE;
        }
        AntennaBlock ab = (AntennaBlock)block;
        int cap = ab.maxHeight();
        BlockPos.MutableBlockPos p = ab.column(base, l.getBlockState(base)).mutable();
        for (h = 0; h < cap && l.isLoaded((BlockPos)p) && l.getBlockState((BlockPos)p).getBlock() instanceof AntennaBlock; ++h) {
            p.move(Direction.UP);
        }
        return new Info(h, ab.tier(), -1.0, AntennaUtil.yagiAbove(l, (BlockPos)p, ab.size()));
    }

    public static Float yagiAbove(Level l, BlockPos at, int size) {
        if (!l.isLoaded(at) || !YagiAntennaBlock.isMount(l.getBlockState(at))) {
            return null;
        }
        return AntennaUtil.yagi(l, at.above());
    }

    public static double range(int h, int tier, boolean fm) {
        if (h <= 0 || tier <= 0) {
            return 0.0;
        }
        double r = h <= 20 ? 600.0 * Math.pow((double)h / 4.0, 1.13) : 600.0 * Math.pow(5.0, 1.13) * Math.pow((double)h / 20.0, 0.75);
        return fm ? r : (r *= RANGE_MULT[tier - 1]) * 1.3;
    }

    public static double range(Info a, boolean fm) {
        return AntennaUtil.range(a.height(), a.tier(), fm);
    }

    public static double range(int h, boolean fm) {
        return AntennaUtil.range(h, 1, fm);
    }

    public static float quality(Info a) {
        if (a.height() <= 0) {
            return 0.0f;
        }
        return Math.min(1.0f, 0.5f + 0.5f * Math.min(1.0f, (float)a.height() / 20.0f) + QUALITY_BONUS[a.tier() - 1]);
    }

    public static float quality(int h) {
        return AntennaUtil.quality(new Info(h, 1));
    }

    public static double rxGain(Info a) {
        if (a.height() <= 0) {
            return 1.0;
        }
        return (1.0 + 0.12 * (double)Math.min(a.height(), 30)) * (1.0 + 0.2 * (double)(a.tier() - 1));
    }

    public static Float yagi(Level l, BlockPos at) {
        Float f;
        if (!l.isLoaded(at)) {
            return null;
        }
        BlockEntity blockEntity = l.getBlockEntity(at);
        if (blockEntity instanceof YagiAntennaBE) {
            YagiAntennaBE y = (YagiAntennaBE)blockEntity;
            f = Float.valueOf(y.bearing());
        } else {
            f = null;
        }
        return f;
    }

    public static double bx(float deg) {
        return Math.sin(Math.toRadians(deg));
    }

    public static double bz(float deg) {
        return -Math.cos(Math.toRadians(deg));
    }

    public static String compass(float deg) {
        String[] n = new String[]{"north", "north-east", "east", "south-east", "south", "south-west", "west", "north-west"};
        return n[Math.floorMod(Math.round(deg / 45.0f), 8)];
    }

    public static double pattern(Float beam, double dx, double dz) {
        double d = Math.sqrt(dx * dx + dz * dz);
        if (d < 1.0E-6) {
            return 1.0;
        }
        double cos = (dx * AntennaUtil.bx(beam.floatValue()) + dz * AntennaUtil.bz(beam.floatValue())) / d;
        if (cos <= 0.5) {
            return 0.0;
        }
        double k = (cos - 0.5) / 0.5;
        return 1.9 * k * Math.sqrt(k);
    }

    public static String tierName(int tier) {
        return tier <= 0 ? "none" : TIER_NAMES[tier - 1];
    }

    public record Info(int height, int tier, double fixedRange, Float beam) {
        public static final Info NONE = new Info(0, 0);

        public Info(int height, int tier) {
            this(height, tier, -1.0, null);
        }

        public Info(int height, int tier, double fixedRange) {
            this(height, tier, fixedRange, null);
        }

        public boolean creative() {
            return this.fixedRange >= 0.0;
        }
    }
}
