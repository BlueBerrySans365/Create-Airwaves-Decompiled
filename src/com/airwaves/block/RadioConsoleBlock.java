/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.airwaves.block;

import com.airwaves.ModSounds;
import com.airwaves.be.RadioConsoleBE;
import com.airwaves.block.DeviceBlock;
import com.airwaves.logic.Emitter;
import com.airwaves.logic.RadioNetwork;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class RadioConsoleBlock
extends DeviceBlock {
    public static final MapCodec<RadioConsoleBlock> CODEC = RadioConsoleBlock.simpleCodec(RadioConsoleBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.create((String)"active");

    public RadioConsoleBlock(BlockBehaviour.Properties p) {
        super(p, 0.0, 0.0, 1.0, 16.0, 16.0, 15.0);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)ACTIVE, (Comparable)Boolean.valueOf(false)));
    }

    protected MapCodec<RadioConsoleBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        super.createBlockStateDefinition(b);
        b.add(new Property[]{ACTIVE});
    }

    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        return new RadioConsoleBE(p, s);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState st, Level lv, BlockPos pos, Player pl, InteractionHand h, BlockHitResult hit) {
        BlockEntity blockEntity;
        ItemInteractionResult r = super.useItemOn(stack, st, lv, pos, pl, h, hit);
        if (r.consumesAction() && !lv.isClientSide && (blockEntity = lv.getBlockEntity(pos)) instanceof RadioConsoleBE) {
            RadioConsoleBE be = (RadioConsoleBE)blockEntity;
            be.broadcastTuning();
        }
        return r;
    }

    protected InteractionResult useWithoutItem(BlockState s, Level lv, BlockPos pos, Player pl, BlockHitResult hit) {
        RadioConsoleBE be;
        block8: {
            block7: {
                if (!RadioConsoleBlock.handEmpty(pl)) {
                    return InteractionResult.PASS;
                }
                if (lv.isClientSide) {
                    return InteractionResult.SUCCESS;
                }
                BlockEntity blockEntity = lv.getBlockEntity(pos);
                if (!(blockEntity instanceof RadioConsoleBE)) break block7;
                be = (RadioConsoleBE)blockEntity;
                if (lv instanceof ServerLevel) break block8;
            }
            return InteractionResult.CONSUME;
        }
        ServerLevel sl = (ServerLevel)lv;
        if (pl.isShiftKeyDown()) {
            be.refresh();
            pl.displayClientMessage((Component)Component.literal((String)(be.tuning.display() + "  |  TX " + be.txOn + "/" + be.tx + "  RX " + be.rxOn + "/" + be.rx + (String)(be.relays > 0 ? "  relays " + be.relays : "") + (String)(be.radios > 0 ? "  radios " + be.radios : ""))).withStyle(ChatFormatting.AQUA), true);
            return InteractionResult.CONSUME;
        }
        Emitter e = RadioNetwork.seek(sl, pos, be.antennaTop(), be.tuning, be.rxGain());
        if (e != null) {
            be.tuning = e.tuning();
            be.sync();
            be.broadcastTuning();
        }
        lv.playSound(null, pos, (SoundEvent)ModSounds.SEEK.get(), SoundSource.BLOCKS, 0.4f, e != null ? 1.6f : 0.7f);
        pl.displayClientMessage((Component)Component.literal((String)(be.tuning.display() + (String)(e != null && !e.name().isEmpty() ? "  " + e.name() : (e == null ? "  (no other station)" : "")))), true);
        return InteractionResult.CONSUME;
    }

    protected boolean hasAnalogOutputSignal(BlockState s) {
        return true;
    }

    protected int getAnalogOutputSignal(BlockState s, Level lv, BlockPos pos) {
        int n;
        BlockEntity blockEntity = lv.getBlockEntity(pos);
        if (blockEntity instanceof RadioConsoleBE) {
            RadioConsoleBE be = (RadioConsoleBE)blockEntity;
            n = be.comparator();
        } else {
            n = 0;
        }
        return n;
    }
}
