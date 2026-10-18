/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ContainerLevelAccess
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  org.jetbrains.annotations.Nullable
 */
package com.airwaves.block;

import com.airwaves.ModRegistry;
import com.airwaves.be.TapePlayerBE;
import com.airwaves.block.DeviceBlock;
import com.airwaves.item.TapeItem;
import com.airwaves.menu.TapePlayerMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TapePlayerBlock
extends DeviceBlock {
    public static final MapCodec<TapePlayerBlock> CODEC = TapePlayerBlock.simpleCodec(TapePlayerBlock::new);

    public TapePlayerBlock(BlockBehaviour.Properties p) {
        super(p, 1.0, 0.0, 3.0, 15.0, 16.0, 15.0);
    }

    protected MapCodec<TapePlayerBlock> codec() {
        return CODEC;
    }

    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        return new TapePlayerBE(p, s);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lv, BlockState s, BlockEntityType<T> t) {
        return lv.isClientSide ? null : TapePlayerBlock.createTickerHelper(t, ModRegistry.TAPE_PLAYER_BE.get(), (l, p, st, be) -> be.serverTick());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState st, Level lv, BlockPos pos, Player pl, InteractionHand h, BlockHitResult hit) {
        TapePlayerBE be;
        BlockEntity blockEntity;
        if (stack.getItem() instanceof TapeItem && (blockEntity = lv.getBlockEntity(pos)) instanceof TapePlayerBE && (be = (TapePlayerBE)blockEntity).stack().isEmpty()) {
            if (!lv.isClientSide) {
                be.inv.setItem(0, stack.split(1));
                lv.playSound(null, pos, (SoundEvent)SoundEvents.ARMOR_EQUIP_GENERIC.value(), SoundSource.BLOCKS, 0.6f, 1.6f);
            }
            return ItemInteractionResult.sidedSuccess((boolean)lv.isClientSide);
        }
        return super.useItemOn(stack, st, lv, pos, pl, h, hit);
    }

    protected InteractionResult useWithoutItem(BlockState st, Level lv, BlockPos pos, Player pl, BlockHitResult hit) {
        if (!lv.isClientSide) {
            pl.openMenu(st.getMenuProvider(lv, pos));
        }
        return InteractionResult.sidedSuccess((boolean)lv.isClientSide);
    }

    protected MenuProvider getMenuProvider(BlockState st, Level lv, BlockPos pos) {
        BlockEntity blockEntity = lv.getBlockEntity(pos);
        if (!(blockEntity instanceof TapePlayerBE)) {
            return null;
        }
        TapePlayerBE be = (TapePlayerBE)blockEntity;
        return new SimpleMenuProvider((id, inv, p) -> new TapePlayerMenu(id, inv, (Container)be.inv, be.data, ContainerLevelAccess.create((Level)lv, (BlockPos)pos), be), (Component)Component.translatable((String)"container.airwaves.tape_player"));
    }

    protected void neighborChanged(BlockState s, Level lv, BlockPos p, Block b, BlockPos from, boolean moved) {
        BlockEntity blockEntity;
        super.neighborChanged(s, lv, p, b, from, moved);
        if (!lv.isClientSide && (blockEntity = lv.getBlockEntity(p)) instanceof TapePlayerBE) {
            TapePlayerBE be = (TapePlayerBE)blockEntity;
            be.onRedstone(lv.hasNeighborSignal(p));
        }
    }

    @Override
    protected void onRemove(BlockState s, Level lv, BlockPos p, BlockState ns, boolean moved) {
        BlockEntity blockEntity;
        if (!s.is(ns.getBlock()) && (blockEntity = lv.getBlockEntity(p)) instanceof TapePlayerBE) {
            TapePlayerBE be = (TapePlayerBE)blockEntity;
            Containers.dropContents((Level)lv, (BlockPos)p, (Container)be.inv);
        }
        super.onRemove(s, lv, p, ns, moved);
    }

    protected boolean hasAnalogOutputSignal(BlockState s) {
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected int getAnalogOutputSignal(BlockState s, Level lv, BlockPos p) {
        BlockEntity blockEntity = lv.getBlockEntity(p);
        if (!(blockEntity instanceof TapePlayerBE)) return 0;
        TapePlayerBE be = (TapePlayerBE)blockEntity;
        if (be.mode != 1) return 0;
        return 15;
    }
}
