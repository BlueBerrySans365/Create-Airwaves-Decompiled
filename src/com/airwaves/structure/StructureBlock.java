/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.airwaves.structure;

import com.airwaves.ModRegistry;
import com.airwaves.ModSounds;
import com.airwaves.Tuning;
import com.airwaves.be.CableNode;
import com.airwaves.be.Tuned;
import com.airwaves.cable.Link;
import com.airwaves.compat.ElectricCompat;
import com.airwaves.item.CableItem;
import com.airwaves.item.Tunable;
import com.airwaves.logic.Cables;
import com.airwaves.power.TowerTerminals;
import com.airwaves.structure.KineticStructureBlock;
import com.airwaves.structure.StructureDef;
import com.airwaves.structure.TowerCoreBE;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntityTicker;
import java.util.ArrayList;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StructureBlock
extends BaseEntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    private static final ThreadLocal<Object[]> PENDING = new ThreadLocal();
    public final StructureDef def;
    public final IntegerProperty part;
    private final Property<?>[] extra;
    private final BiFunction<BlockPos, BlockState, BlockEntity> beFactory;
    private ItemStack dropOverride;
    private MapCodec<StructureBlock> codec;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static StructureBlock create(String name, BlockBehaviour.Properties p, BiFunction<BlockPos, BlockState, BlockEntity> be, Property<?> ... extra) {
        StructureDef d = StructureDef.get(name);
        PENDING.set(new Object[]{IntegerProperty.create((String)"part", (int)0, (int)(d.size() - 1)), extra});
        try {
            StructureBlock structureBlock = new StructureBlock(d, p, be);
            return structureBlock;
        }
        finally {
            PENDING.remove();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static StructureBlock createKinetic(String name, BlockBehaviour.Properties p, BiFunction<BlockPos, BlockState, BlockEntity> be, Property<?> ... extra) {
        StructureDef d = StructureDef.get(name);
        PENDING.set(new Object[]{IntegerProperty.create((String)"part", (int)0, (int)(d.size() - 1)), extra});
        try {
            KineticStructureBlock kineticStructureBlock = ElectricCompat.tower(d, p, be);
            return kineticStructureBlock;
        }
        finally {
            PENDING.remove();
        }
    }

    protected StructureBlock(StructureDef def, BlockBehaviour.Properties p, BiFunction<BlockPos, BlockState, BlockEntity> be) {
        super(p);
        Object[] pend = PENDING.get();
        this.def = def;
        this.part = (IntegerProperty)pend[0];
        this.extra = (Property[])pend[1];
        this.beFactory = be;
        BlockState d = (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)this.part, (Comparable)Integer.valueOf(0))).setValue(AXIS, (Comparable)Direction.Axis.X);
        for (Property<?> p2 : this.extra) {
            if (p2 instanceof BooleanProperty) {
                BooleanProperty bp = (BooleanProperty)p2;
                d = (BlockState)d.setValue((Property)bp, (Comparable)Boolean.valueOf(false));
                continue;
            }
            if (!(p2 instanceof IntegerProperty)) continue;
            IntegerProperty ip = (IntegerProperty)p2;
            d = (BlockState)d.setValue((Property)ip, (Comparable)((Integer)ip.getPossibleValues().iterator().next()));
        }
        this.registerDefaultState(d);
    }

    protected MapCodec<? extends BaseEntityBlock> codec() {
        if (this.codec == null) {
            this.codec = MapCodec.unit((Object)((Object)this));
        }
        return this.codec;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        Object[] pend = PENDING.get();
        b.add(new Property[]{(IntegerProperty)pend[0], AXIS});
        for (Property p : (Property[])pend[1]) {
            b.add(new Property[]{p});
        }
    }

    public int partOf(BlockState s) {
        return (Integer)s.getValue((Property)this.part);
    }

    public BlockPos corePos(BlockState s, BlockPos pos) {
        return this.def.corePos(pos, (Integer)s.getValue((Property)this.part), (Direction.Axis)s.getValue(AXIS));
    }

    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        BiFunction<BlockPos, BlockState, BlockEntity> f;
        if ((Integer)s.getValue((Property)this.part) == 0) {
            return this.beFactory != null ? this.beFactory.apply(p, s) : null;
        }
        if (TowerTerminals.isTerminal(this, s) && (f = ElectricCompat.towerTerminalBE()) != null) {
            return f.apply(p, s);
        }
        return null;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lv, BlockState s, BlockEntityType<T> t) {
        if ((Integer)s.getValue((Property)this.part) != 0 && TowerTerminals.isTerminal(this, s) && ElectricCompat.towerTerminalBE() != null) {
            return new SmartBlockEntityTicker();
        }
        if (lv.isClientSide || (Integer)s.getValue((Property)this.part) != 0) {
            return null;
        }
        return (l, p, st, be) -> {
            if (be instanceof Ticking) {
                Ticking tk = (Ticking)be;
                tk.serverTick();
            }
        };
    }

    protected RenderShape getRenderShape(BlockState s) {
        return RenderShape.MODEL;
    }

    protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return this.def.outline((Integer)s.getValue((Property)this.part), (Direction.Axis)s.getValue(AXIS));
    }

    protected VoxelShape getCollisionShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return this.def.collision((Integer)s.getValue((Property)this.part), (Direction.Axis)s.getValue(AXIS));
    }

    protected boolean propagatesSkylightDown(BlockState s, BlockGetter l, BlockPos p) {
        return true;
    }

    protected float getShadeBrightness(BlockState s, BlockGetter l, BlockPos p) {
        return 1.0f;
    }

    protected ItemStack drop(Level lv, BlockPos core) {
        ItemStack st = new ItemStack((ItemLike)this.asItem());
        BlockEntity blockEntity = lv.getBlockEntity(core);
        if (blockEntity instanceof Tuned) {
            Tuned t = (Tuned)blockEntity;
            st.set((DataComponentType)ModRegistry.TUNING.get(), (Object)t.getTuning());
            if (t.getStationName() != null) {
                st.set(DataComponents.CUSTOM_NAME, (Object)t.getStationName());
            }
        }
        return st;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState st, Level lv, BlockPos pos, Player pl, InteractionHand h, BlockHitResult hit) {
        BlockState cs;
        Tunable tn;
        if (stack.getItem() instanceof CableItem) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        BlockPos core = this.corePos(st, pos);
        Item item = stack.getItem();
        if (item instanceof Tunable && (tn = (Tunable)item).canTune(stack) && (item = lv.getBlockEntity(core)) instanceof Tuned) {
            Tuning t;
            Tuned be = (Tuned)item;
            if (!pl.isSecondaryUseActive() && !(t = (Tuning)stack.getOrDefault((DataComponentType)ModRegistry.TUNING.get(), (Object)Tuning.DEFAULT)).matches(be.getTuning())) {
                if (!lv.isClientSide) {
                    be.setTuning(t);
                    be.sync();
                    lv.playSound(null, pos, (SoundEvent)ModSounds.TUNE.get(), SoundSource.BLOCKS, 0.4f, 1.8f);
                }
                return ItemInteractionResult.sidedSuccess((boolean)lv.isClientSide);
            }
        }
        if ((cs = lv.getBlockState(core)).hasProperty((Property)TowerCoreBE.CRYSTAL) && pos.distManhattan((Vec3i)core) <= 2) {
            int v;
            int n = stack.is(Items.AMETHYST_SHARD) ? 1 : (v = stack.is(Items.AMETHYST_BLOCK) ? 2 : 0);
            if (v > 0) {
                if ((Integer)cs.getValue((Property)TowerCoreBE.CRYSTAL) != 0) {
                    return ItemInteractionResult.CONSUME;
                }
                if (!lv.isClientSide) {
                    lv.setBlock(core, (BlockState)cs.setValue((Property)TowerCoreBE.CRYSTAL, (Comparable)Integer.valueOf(v)), 3);
                    if (!pl.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    lv.playSound(null, core, (SoundEvent)ModSounds.CRYSTAL_IN.get(), SoundSource.BLOCKS, 1.0f, v == 2 ? 0.7f : 1.0f);
                }
                return ItemInteractionResult.sidedSuccess((boolean)lv.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected InteractionResult useWithoutItem(BlockState st, Level lv, BlockPos pos, Player pl, BlockHitResult hit) {
        BlockPos core = this.corePos(st, pos);
        BlockState cs = lv.getBlockState(core);
        if (pl.isSecondaryUseActive() && cs.hasProperty((Property)TowerCoreBE.CRYSTAL) && (Integer)cs.getValue((Property)TowerCoreBE.CRYSTAL) > 0 && pos.distManhattan((Vec3i)core) <= 2) {
            if (!lv.isClientSide) {
                int v = (Integer)cs.getValue((Property)TowerCoreBE.CRYSTAL);
                lv.setBlock(core, (BlockState)cs.setValue((Property)TowerCoreBE.CRYSTAL, (Comparable)Integer.valueOf(0)), 3);
                Block.popResource((Level)lv, (BlockPos)core.above(), (ItemStack)new ItemStack((ItemLike)(v == 2 ? Items.AMETHYST_BLOCK : Items.AMETHYST_SHARD)));
                lv.playSound(null, core, (SoundEvent)ModSounds.CRYSTAL_OUT.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return InteractionResult.sidedSuccess((boolean)lv.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public ItemStack getCloneItemStack(LevelReader l, BlockPos p, BlockState s) {
        return new ItemStack((ItemLike)this.asItem());
    }

    public BlockState playerWillDestroy(Level lv, BlockPos pos, BlockState st, Player pl) {
        if (!lv.isClientSide) {
            BlockPos core = this.corePos(st, pos);
            if (!pl.getAbilities().instabuild) {
                Block.popResource((Level)lv, (BlockPos)pos, (ItemStack)this.drop(lv, core));
            }
        }
        return super.playerWillDestroy(lv, pos, st, pl);
    }

    protected void onRemove(BlockState s, Level lv, BlockPos pos, BlockState ns, boolean moved) {
        if (!ns.is((Block)this) && !lv.isClientSide) {
            BlockEntity blockEntity;
            BlockPos core = this.corePos(s, pos);
            Direction.Axis ax = (Direction.Axis)s.getValue(AXIS);
            if ((Integer)s.getValue((Property)this.part) == 0 && (blockEntity = lv.getBlockEntity(pos)) instanceof CableNode) {
                CableNode n = (CableNode)blockEntity;
                Cables.broken(lv, pos, new ArrayList<Link>(n.links().list()));
            }
            for (int i = 0; i < this.def.size(); ++i) {
                BlockState qs;
                BlockPos q = this.def.partFromCore(core, i, ax);
                if (q.equals((Object)pos) || !(qs = lv.getBlockState(q)).is((Block)this) || (Integer)qs.getValue((Property)this.part) != i) continue;
                lv.setBlock(q, Blocks.AIR.defaultBlockState(), 35);
            }
        }
        super.onRemove(s, lv, pos, ns, moved);
    }

    public <V extends Comparable<V>> void setAll(Level lv, BlockPos core, Property<V> prop, V v) {
        BlockState cs = lv.getBlockState(core);
        if (!cs.is((Block)this) || cs.getValue(prop) == v) {
            return;
        }
        Direction.Axis ax = (Direction.Axis)cs.getValue(AXIS);
        for (int i = 0; i < this.def.size(); ++i) {
            BlockPos q = this.def.partFromCore(core, i, ax);
            BlockState qs = lv.getBlockState(q);
            if (!qs.is((Block)this)) continue;
            lv.setBlock(q, (BlockState)qs.setValue(prop, v), 2);
        }
    }

    public static <V extends Comparable<V>> void setCore(Level lv, BlockPos core, Property<V> prop, V v) {
        BlockState s = lv.getBlockState(core);
        if (s.hasProperty(prop) && s.getValue(prop) != v) {
            lv.setBlock(core, (BlockState)s.setValue(prop, v), 3);
        }
    }

    public static interface Ticking {
        public void serverTick();
    }
}
