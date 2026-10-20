/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.jetbrains.annotations.Nullable
 */
package com.airwaves.block;

import com.airwaves.ModRegistry;
import com.airwaves.be.YagiAntennaBE;
import com.airwaves.block.AntennaBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class YagiAntennaBlock
extends Block
implements IBE<YagiAntennaBE> {
    public static final TagKey<Block> MOUNTS = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"yagi_mounts"));
    private static final VoxelShape SHAPE = Shapes.or((VoxelShape)YagiAntennaBlock.box((double)7.0, (double)0.0, (double)7.5, (double)9.0, (double)11.0, (double)9.5), (VoxelShape)YagiAntennaBlock.box((double)0.0, (double)7.0, (double)0.0, (double)16.0, (double)12.0, (double)16.0));

    public YagiAntennaBlock(BlockBehaviour.Properties p) {
        super(p);
    }

    public static boolean isMount(BlockState s) {
        return s.is(MOUNTS);
    }

    protected boolean canSurvive(BlockState s, LevelReader lv, BlockPos pos) {
        return YagiAntennaBlock.isMount(lv.getBlockState(pos.below())) && lv.getBlockState(pos.below(2)).getBlock() instanceof AntennaBlock;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext c) {
        BlockState s = this.defaultBlockState();
        return this.canSurvive(s, (LevelReader)c.getLevel(), c.getClickedPos()) ? s : null;
    }

    protected BlockState updateShape(BlockState s, Direction d, BlockState n, LevelAccessor l, BlockPos p, BlockPos np) {
        return d == Direction.DOWN && !this.canSurvive(s, (LevelReader)l, p) ? Blocks.AIR.defaultBlockState() : super.updateShape(s, d, n, l, p, np);
    }

    public void setPlacedBy(Level lv, BlockPos pos, BlockState s, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(lv, pos, s, placer, stack);
        if (placer != null) {
            this.withBlockEntityDo((BlockGetter)lv, pos, be -> be.setBearing(Math.round(placer.getYRot() + 180.0f) % 360));
        }
    }

    protected VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return SHAPE;
    }

    public Class<YagiAntennaBE> getBlockEntityClass() {
        return YagiAntennaBE.class;
    }

    public BlockEntityType<? extends YagiAntennaBE> getBlockEntityType() {
        return ModRegistry.YAGI_BE.get();
    }
}
