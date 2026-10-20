/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.foundation.placement.PoleHelper
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.item.FallingBlockEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Fallable
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.airwaves.block;

import com.airwaves.ModRegistry;
import com.airwaves.block.MastBlock;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.placement.PoleHelper;
import java.util.ArrayList;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AntennaBlock
extends MastBlock
implements Fallable {
    public static final int PLACEMENT_HELPER = PlacementHelpers.register((IPlacementHelper)new ExtendHelper());
    private static final int FALL_DELAY = 2;

    protected AntennaBlock(BlockBehaviour.Properties p) {
        super(p);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Item item;
        if (player.isShiftKeyDown() || !player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        IPlacementHelper helper = PlacementHelpers.get((int)PLACEMENT_HELPER);
        if (helper.matchesItem(stack) && (item = stack.getItem()) instanceof BlockItem) {
            BlockItem bi = (BlockItem)item;
            return helper.getOffset(player, level, state, pos, hit).placeInWorld(level, bi, player, hand, hit);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static boolean supported(LevelAccessor lv, BlockPos pos) {
        BlockState below = lv.getBlockState(pos.below());
        return below.getBlock() instanceof AntennaBlock || !below.canBeReplaced();
    }

    @Override
    protected BlockState updateShape(BlockState s, Direction d, BlockState n, LevelAccessor l, BlockPos p, BlockPos np) {
        BlockState out = super.updateShape(s, d, n, l, p, np);
        if (d == Direction.DOWN && !AntennaBlock.supported(l, p)) {
            l.scheduleTick(p, (Block)this, 2);
        }
        return out;
    }

    protected void tick(BlockState s, ServerLevel lv, BlockPos pos, RandomSource r) {
        if (AntennaBlock.supported((LevelAccessor)lv, pos)) {
            return;
        }
        ArrayList<BlockPos> column = new ArrayList<BlockPos>();
        BlockPos.MutableBlockPos m = pos.mutable();
        while (column.size() < 512 && lv.getBlockState((BlockPos)m).getBlock() instanceof AntennaBlock) {
            column.add(m.immutable());
            m.move(Direction.UP);
        }
        for (BlockPos p : column) {
            BlockState st = lv.getBlockState(p);
            if (!(st.getBlock() instanceof AntennaBlock)) continue;
            FallingBlockEntity f = FallingBlockEntity.fall((Level)lv, (BlockPos)p, (BlockState)st);
            f.setHurtsEntities(1.0f, 6);
        }
    }

    public void onLand(Level lv, BlockPos pos, BlockState state, BlockState replaced, FallingBlockEntity e) {
        lv.destroyBlock(pos, true);
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
        return (AntennaBlock)((Object)ModRegistry.ANTENNA.get());
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

    private static class ExtendHelper
    extends PoleHelper<Boolean> {
        ExtendHelper() {
            super(s -> s.getBlock() instanceof AntennaBlock, s -> Direction.Axis.Y, (Property)MastBlock.TOP);
        }

        public Predicate<ItemStack> getItemPredicate() {
            return i -> {
                BlockItem b;
                Item patt0$temp = i.getItem();
                return patt0$temp instanceof BlockItem && (b = (BlockItem)patt0$temp).getBlock() instanceof AntennaBlock;
            };
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
            Block block = state.getBlock();
            if (!(block instanceof AntennaBlock)) {
                return PlacementOffset.fail();
            }
            AntennaBlock a = (AntennaBlock)block;
            int above = this.attachedPoles(world, pos, Direction.UP);
            int below = this.attachedPoles(world, pos, Direction.DOWN);
            if (above + below + 1 >= a.maxHeight()) {
                return PlacementOffset.fail();
            }
            BlockPos np = pos.above(above + 1);
            if (!world.isInWorldBounds(np) || !world.getBlockState(np).canBeReplaced()) {
                return PlacementOffset.fail();
            }
            return PlacementOffset.success((Vec3i)np, s -> (BlockState)s.setValue((Property)MastBlock.TOP, (Comparable)Boolean.valueOf(!MastBlock.continues(world.getBlockState(np.above())))));
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
            return new int[][]{{1, 96}};
        }
    }
}
