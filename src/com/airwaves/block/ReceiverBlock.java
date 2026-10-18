/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.airwaves.block;

import com.airwaves.Tier;
import com.airwaves.be.ReceiverBE;
import com.airwaves.block.DeviceBlock;
import com.airwaves.block.TransmitterBlock;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class ReceiverBlock
extends DeviceBlock {
    public static final MapCodec<ReceiverBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group((App)Codec.INT.fieldOf("tier").forGetter(b -> b.tier.level), (App)ReceiverBlock.propertiesCodec()).apply((Applicative)i, (t, p) -> new ReceiverBlock(Tier.of(t), (BlockBehaviour.Properties)p)));
    public static final BooleanProperty ACTIVE = BooleanProperty.create((String)"active");
    public static final IntegerProperty CRYSTAL = TransmitterBlock.CRYSTAL;
    private static final double[][] BOX = new double[][]{{3.0, 0.0, 4.0, 13.0, 16.0, 13.0}, {1.0, 0.0, 3.0, 15.0, 16.0, 13.0}, {0.0, 0.0, 1.0, 16.0, 16.0, 15.0}, {0.0, 0.0, 0.0, 16.0, 16.0, 16.0}};
    public final Tier tier;

    public ReceiverBlock(Tier tier, BlockBehaviour.Properties p) {
        super(p, BOX[tier.level - 1]);
        this.tier = tier;
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)ACTIVE, (Comparable)Boolean.valueOf(false))).setValue((Property)CRYSTAL, (Comparable)Integer.valueOf(0)));
    }

    protected MapCodec<ReceiverBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        super.createBlockStateDefinition(b);
        b.add(new Property[]{ACTIVE, CRYSTAL});
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState st, Level lv, BlockPos pos, Player pl, InteractionHand h, BlockHitResult hit) {
        ItemInteractionResult r = this.crystalInsert(stack, st, lv, pos, pl, CRYSTAL);
        return r != null ? r : super.useItemOn(stack, st, lv, pos, pl, h, hit);
    }

    protected InteractionResult useWithoutItem(BlockState st, Level lv, BlockPos pos, Player pl, BlockHitResult hit) {
        if (!ReceiverBlock.handEmpty(pl) || !pl.isShiftKeyDown() || (Integer)st.getValue((Property)CRYSTAL) == 0) {
            return InteractionResult.PASS;
        }
        if (!lv.isClientSide) {
            this.crystalRemove((BlockState)st.setValue((Property)ACTIVE, (Comparable)Boolean.valueOf(false)), lv, pos, CRYSTAL);
        }
        return InteractionResult.sidedSuccess((boolean)lv.isClientSide);
    }

    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        return new ReceiverBE(p, s);
    }

    protected boolean isSignalSource(BlockState s) {
        return true;
    }

    protected int getSignal(BlockState s, BlockGetter l, BlockPos p, Direction d) {
        int n;
        BlockEntity blockEntity = l.getBlockEntity(p);
        if (blockEntity instanceof ReceiverBE) {
            ReceiverBE r = (ReceiverBE)blockEntity;
            n = r.signal();
        } else {
            n = 0;
        }
        return n;
    }
}
