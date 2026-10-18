/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.airwaves.block;

import com.airwaves.block.YagiAntennaBlock;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class MastBlock
extends Block {
    public static final BooleanProperty TOP = BooleanProperty.create((String)"top");
    public static final BooleanProperty NARROW = BooleanProperty.create((String)"narrow");
    public static final IntegerProperty SEG = IntegerProperty.create((String)"seg", (int)0, (int)3);
    public static final IntegerProperty CX2 = IntegerProperty.create((String)"cx", (int)0, (int)1);
    public static final IntegerProperty CZ2 = IntegerProperty.create((String)"cz", (int)0, (int)1);
    public static final IntegerProperty CX4 = IntegerProperty.create((String)"cx", (int)0, (int)3);
    public static final IntegerProperty CZ4 = IntegerProperty.create((String)"cz", (int)0, (int)3);
    public static final IntegerProperty CX8 = IntegerProperty.create((String)"cx", (int)0, (int)7);
    public static final IntegerProperty CZ8 = IntegerProperty.create((String)"cz", (int)0, (int)7);
    private static final Map<Long, VoxelShape> SHAPES = new HashMap<Long, VoxelShape>();

    protected MastBlock(BlockBehaviour.Properties p) {
        super(p);
        BlockState d = (BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)TOP, (Comparable)Boolean.valueOf(true));
        if (d.hasProperty((Property)NARROW)) {
            d = (BlockState)d.setValue((Property)NARROW, (Comparable)Boolean.valueOf(false));
        }
        this.registerDefaultState(d);
    }

    public abstract int size();

    public abstract int tier();

    public abstract int maxHeight();

    public IntegerProperty px() {
        return switch (this.size()) {
            case 2 -> CX2;
            case 4 -> CX4;
            case 8 -> CX8;
            default -> null;
        };
    }

    public IntegerProperty pz() {
        return switch (this.size()) {
            case 2 -> CZ2;
            case 4 -> CZ4;
            case 8 -> CZ8;
            default -> null;
        };
    }

    protected boolean segAlways() {
        return false;
    }

    public int cx(BlockState s) {
        return this.size() > 1 ? (Integer)s.getValue((Property)this.px()) : 0;
    }

    public int cz(BlockState s) {
        return this.size() > 1 ? (Integer)s.getValue((Property)this.pz()) : 0;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(new Property[]{TOP});
        if (this.size() >= 4) {
            b.add(new Property[]{NARROW});
        }
        if (this.px() != null) {
            b.add(new Property[]{this.px(), this.pz()});
        }
        if (this.px() != null || this.segAlways()) {
            b.add(new Property[]{SEG});
        }
    }

    public int mask(BlockState s) {
        int n = this.size();
        if (n == 1) {
            return 15;
        }
        int x = this.cx(s);
        int z = this.cz(s);
        int last = n - 1;
        return (z == 0 ? 1 : 0) | (x == last ? 2 : 0) | (z == last ? 4 : 0) | (x == 0 ? 8 : 0);
    }

    protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        boolean top = (Boolean)s.getValue((Property)TOP) != false && (!s.hasProperty((Property)NARROW) || (Boolean)s.getValue((Property)NARROW) == false);
        int m = this.mask(s);
        long key = (long)this.size() << 8 | (long)m << 1 | (long)(top ? 1 : 0);
        return SHAPES.computeIfAbsent(key, k -> this.build(m, top));
    }

    private VoxelShape build(int m, boolean top) {
        if (this.size() == 1) {
            VoxelShape v = Block.box((double)3.5, (double)0.0, (double)3.5, (double)12.5, (double)(top ? 9.0 : 16.0), (double)12.5);
            return top ? Shapes.or((VoxelShape)v, (VoxelShape)Block.box((double)5.0, (double)9.0, (double)5.0, (double)11.0, (double)15.0, (double)11.0)) : v;
        }
        double h = top ? 9.0 : 16.0;
        VoxelShape v = Shapes.empty();
        if ((m & 1) != 0) {
            v = Shapes.or((VoxelShape)v, (VoxelShape)Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)h, (double)(top ? 5.0 : 1.5)));
        }
        if ((m & 2) != 0) {
            v = Shapes.or((VoxelShape)v, (VoxelShape)Block.box((double)(top ? 11.0 : 14.5), (double)0.0, (double)0.0, (double)16.0, (double)h, (double)16.0));
        }
        if ((m & 4) != 0) {
            v = Shapes.or((VoxelShape)v, (VoxelShape)Block.box((double)0.0, (double)0.0, (double)(top ? 11.0 : 14.5), (double)16.0, (double)h, (double)16.0));
        }
        if ((m & 8) != 0) {
            v = Shapes.or((VoxelShape)v, (VoxelShape)Block.box((double)0.0, (double)0.0, (double)0.0, (double)(top ? 5.0 : 1.5), (double)h, (double)16.0));
        }
        return v;
    }

    public BlockState getStateForPlacement(BlockPlaceContext c) {
        return (BlockState)this.defaultBlockState().setValue((Property)TOP, (Comparable)Boolean.valueOf(!MastBlock.continues(c.getLevel().getBlockState(c.getClickedPos().above()))));
    }

    protected BlockState updateShape(BlockState s, Direction d, BlockState n, LevelAccessor l, BlockPos p, BlockPos np) {
        if (d == Direction.UP) {
            return (BlockState)s.setValue((Property)TOP, (Comparable)Boolean.valueOf(!MastBlock.continues(n)));
        }
        return s;
    }

    public static boolean continues(BlockState above) {
        return above.getBlock() instanceof MastBlock || above.getBlock() instanceof YagiAntennaBlock;
    }

    protected boolean propagatesSkylightDown(BlockState s, BlockGetter l, BlockPos p) {
        return true;
    }

    public int nextSeg(BlockState below) {
        return below.getBlock() instanceof MastBlock && below.hasProperty((Property)SEG) ? ((Integer)below.getValue((Property)SEG) + 1) % 4 : 0;
    }

    public static void setNarrow(Level lv, BlockPos origin, boolean v) {
        MastBlock mb;
        block5: {
            block4: {
                BlockState o = lv.getBlockState(origin);
                Block block = o.getBlock();
                if (!(block instanceof MastBlock)) break block4;
                mb = (MastBlock)block;
                if (o.hasProperty((Property)NARROW)) break block5;
            }
            return;
        }
        for (int dx = 0; dx < mb.size(); ++dx) {
            for (int dz = 0; dz < mb.size(); ++dz) {
                BlockPos q = origin.offset(dx, 0, dz);
                BlockState s = lv.getBlockState(q);
                if (!s.is((Block)mb) || (Boolean)s.getValue((Property)NARROW) == v) continue;
                lv.setBlock(q, (BlockState)s.setValue((Property)NARROW, (Comparable)Boolean.valueOf(v)), 2);
            }
        }
    }

    protected void onRemove(BlockState s, Level lv, BlockPos p, BlockState ns, boolean moved) {
        MastBlock wide;
        BlockState below;
        Block block;
        if (!ns.is((Block)this) && !lv.isClientSide && (block = (below = lv.getBlockState(p.below())).getBlock()) instanceof MastBlock && (wide = (MastBlock)block).size() > this.size()) {
            MastBlock.setNarrow(lv, wide.origin(p.below(), below), false);
        }
        super.onRemove(s, lv, p, ns, moved);
    }

    public BlockPos origin(BlockPos pos, BlockState st) {
        return pos.offset(-this.cx(st), 0, -this.cz(st));
    }

    protected ItemStack layerDrop(Level lv, BlockPos origin, BlockState st) {
        return new ItemStack((ItemLike)this.asItem());
    }

    protected boolean customDrop() {
        return this.size() > 1;
    }

    public BlockState playerWillDestroy(Level lv, BlockPos pos, BlockState st, Player pl) {
        if (!lv.isClientSide && this.customDrop()) {
            BlockPos o;
            int n = this.size();
            ItemStack drop = this.layerDrop(lv, o, lv.getBlockState(o = this.origin(pos, st)).is((Block)this) ? lv.getBlockState(o) : st);
            for (int dx = 0; dx < n; ++dx) {
                for (int dz = 0; dz < n; ++dz) {
                    BlockPos q = o.offset(dx, 0, dz);
                    if (q.equals((Object)pos) || !lv.getBlockState(q).is((Block)this)) continue;
                    lv.setBlock(q, Blocks.AIR.defaultBlockState(), 3);
                }
            }
            if (!pl.isCreative()) {
                Block.popResource((Level)lv, (BlockPos)pos, (ItemStack)drop);
            }
        }
        return super.playerWillDestroy(lv, pos, st, pl);
    }
}
