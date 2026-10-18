/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ContainerLevelAccess
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.airwaves.block;

import com.airwaves.item.Tunable;
import com.airwaves.menu.TuningTableMenu;
import com.mojang.serialization.MapCodec;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TuningTableBlock
extends HorizontalDirectionalBlock {
    public static final MapCodec<TuningTableBlock> CODEC = TuningTableBlock.simpleCodec(TuningTableBlock::new);
    public static final EnumProperty<Part> PART = EnumProperty.create((String)"part", Part.class);
    private static final VoxelShape SHAPE = Block.box((double)0.0, (double)0.0, (double)0.0, (double)16.0, (double)15.0, (double)16.0);

    public TuningTableBlock(BlockBehaviour.Properties p) {
        super(p);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH)).setValue(PART, (Comparable)((Object)Part.CENTER)));
    }

    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(new Property[]{FACING, PART});
    }

    protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return SHAPE;
    }

    public BlockState getStateForPlacement(BlockPlaceContext c) {
        Direction f = c.getHorizontalDirection().getOpposite();
        BlockPos p = c.getClickedPos();
        Level l = c.getLevel();
        if (!l.getBlockState(p.relative(f.getClockWise())).canBeReplaced(c) || !l.getBlockState(p.relative(f.getCounterClockWise())).canBeReplaced(c)) {
            return null;
        }
        return (BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)f)).setValue(PART, (Comparable)((Object)Part.CENTER));
    }

    public void setPlacedBy(Level l, BlockPos p, BlockState s, LivingEntity who, ItemStack st) {
        if (l.isClientSide) {
            return;
        }
        Direction f = (Direction)s.getValue((Property)FACING);
        l.setBlock(p.relative(f.getClockWise()), (BlockState)s.setValue(PART, (Comparable)((Object)Part.LEFT)), 3);
        l.setBlock(p.relative(f.getCounterClockWise()), (BlockState)s.setValue(PART, (Comparable)((Object)Part.RIGHT)), 3);
    }

    protected BlockState updateShape(BlockState s, Direction d, BlockState n, LevelAccessor l, BlockPos p, BlockPos np) {
        boolean nIsSide;
        Direction f = (Direction)s.getValue((Property)FACING);
        Part part = (Part)((Object)s.getValue(PART));
        boolean nIsCenter = n.is((Block)this) && n.getValue(PART) == Part.CENTER;
        boolean bl = nIsSide = n.is((Block)this) && n.getValue(PART) != Part.CENTER;
        if (part == Part.CENTER) {
            if (!(d != f.getClockWise() && d != f.getCounterClockWise() || nIsSide)) {
                return Blocks.AIR.defaultBlockState();
            }
        } else {
            Direction toCenter;
            Direction direction = toCenter = part == Part.LEFT ? f.getCounterClockWise() : f.getClockWise();
            if (d == toCenter && !nIsCenter) {
                return Blocks.AIR.defaultBlockState();
            }
        }
        return s;
    }

    public BlockState playerWillDestroy(Level l, BlockPos p, BlockState s, Player pl) {
        Part part = (Part)((Object)s.getValue(PART));
        if (!l.isClientSide && !pl.isCreative() && part != Part.CENTER) {
            Direction f = (Direction)s.getValue((Property)FACING);
            l.destroyBlock(part == Part.LEFT ? p.relative(f.getCounterClockWise()) : p.relative(f.getClockWise()), true, (Entity)pl);
        }
        return super.playerWillDestroy(l, p, s, pl);
    }

    protected InteractionResult useWithoutItem(BlockState s, Level l, BlockPos p, Player pl, BlockHitResult h) {
        if (!pl.getMainHandItem().isEmpty() && !(pl.getMainHandItem().getItem() instanceof Tunable)) {
            return InteractionResult.PASS;
        }
        if (!l.isClientSide) {
            SimpleMenuProvider mp = new SimpleMenuProvider((id, inv, pp) -> new TuningTableMenu(id, inv, ContainerLevelAccess.create((Level)l, (BlockPos)p)), (Component)Component.translatable((String)"container.airwaves.tuning_table"));
            pl.openMenu((MenuProvider)mp);
        }
        return InteractionResult.sidedSuccess((boolean)l.isClientSide);
    }

    public static enum Part implements StringRepresentable
    {
        LEFT,
        CENTER,
        RIGHT;


        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
