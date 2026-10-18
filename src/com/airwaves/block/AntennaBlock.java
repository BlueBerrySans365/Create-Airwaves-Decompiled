/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.airwaves.block;

import com.airwaves.ModRegistry;
import com.airwaves.block.MastBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AntennaBlock
extends MastBlock {
    protected AntennaBlock(BlockBehaviour.Properties p) {
        super(p);
    }

    public abstract int[][] profile();

    @Override
    public int maxHeight() {
        int h = 0;
        for (int[] s : this.profile()) {
            h += s[1];
        }
        return h;
    }

    public int widthAt(int h) {
        for (int[] s : this.profile()) {
            if (h < s[1]) {
                return s[0];
            }
            h -= s[1];
        }
        return 0;
    }

    public static AntennaBlock forWidth(int w) {
        return switch (w) {
            case 2 -> (Medium)((Object)ModRegistry.ANTENNA_MEDIUM.get());
            case 4 -> (Big)((Object)ModRegistry.ANTENNA_BIG.get());
            case 8 -> (Giant)((Object)ModRegistry.ANTENNA_GIANT.get());
            default -> (Small)((Object)ModRegistry.ANTENNA.get());
        };
    }

    public BlockPos column(BlockPos pos, BlockState st) {
        return this.origin(pos, st).offset(this.size() / 2, 0, this.size() / 2);
    }

    public static Object[] base(BlockGetter lv, BlockPos pos) {
        Block block;
        int n;
        BlockState st = lv.getBlockState(pos);
        Block block2 = st.getBlock();
        if (!(block2 instanceof AntennaBlock)) {
            return null;
        }
        AntennaBlock a = (AntennaBlock)block2;
        BlockPos.MutableBlockPos m = a.column(pos, st).mutable();
        AntennaBlock base = a;
        for (n = 0; n < 512 && (block = lv.getBlockState((BlockPos)m).getBlock()) instanceof AntennaBlock; ++n) {
            AntennaBlock b;
            base = b = (AntennaBlock)block;
            m.move(Direction.DOWN);
        }
        return new Object[]{base, n};
    }

    @Override
    protected ItemStack layerDrop(Level lv, BlockPos origin, BlockState st) {
        Object[] b = AntennaBlock.base((BlockGetter)lv, origin.offset(this.size() / 2, 0, this.size() / 2));
        return new ItemStack((ItemLike)(b == null ? this.asItem() : ((AntennaBlock)((Object)b[0])).asItem()));
    }

    public static class Medium
    extends AntennaBlock {
        public static final MapCodec<Medium> CODEC = Medium.simpleCodec(Medium::new);

        public Medium(BlockBehaviour.Properties p) {
            super(p);
        }

        protected MapCodec<Medium> codec() {
            return CODEC;
        }

        @Override
        public int size() {
            return 2;
        }

        @Override
        public int tier() {
            return 2;
        }

        @Override
        public int[][] profile() {
            return new int[][]{{2, 40}};
        }
    }

    public static class Big
    extends AntennaBlock {
        public static final MapCodec<Big> CODEC = Big.simpleCodec(Big::new);

        public Big(BlockBehaviour.Properties p) {
            super(p);
        }

        protected MapCodec<Big> codec() {
            return CODEC;
        }

        @Override
        public int size() {
            return 4;
        }

        @Override
        public int tier() {
            return 3;
        }

        @Override
        public int[][] profile() {
            return new int[][]{{4, 32}, {2, 32}};
        }
    }

    public static class Giant
    extends AntennaBlock {
        public static final MapCodec<Giant> CODEC = Giant.simpleCodec(Giant::new);

        public Giant(BlockBehaviour.Properties p) {
            super(p);
        }

        protected MapCodec<Giant> codec() {
            return CODEC;
        }

        @Override
        public int size() {
            return 8;
        }

        @Override
        public int tier() {
            return 4;
        }

        @Override
        public int[][] profile() {
            return new int[][]{{8, 32}, {4, 32}, {2, 32}};
        }
    }

    public static class Small
    extends AntennaBlock {
        public static final MapCodec<Small> CODEC = Small.simpleCodec(Small::new);

        public Small(BlockBehaviour.Properties p) {
            super(p);
        }

        protected MapCodec<Small> codec() {
            return CODEC;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public int tier() {
            return 1;
        }

        @Override
        public int[][] profile() {
            return new int[][]{{1, 20}};
        }
    }
}
