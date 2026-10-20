/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.airwaves.block;

import com.airwaves.ModSounds;
import com.airwaves.be.RadioBE;
import com.airwaves.block.DeviceBlock;
import com.airwaves.logic.Emitter;
import com.airwaves.logic.RadioNetwork;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class RadioBlock
extends DeviceBlock {
    public static final MapCodec<RadioBlock> CODEC = RadioBlock.simpleCodec(RadioBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RadioBlock(BlockBehaviour.Properties p) {
        super(p, 0.0, 0.0, 3.0, 16.0, 12.0, 10.0);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(true)));
    }

    protected MapCodec<RadioBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        super.createBlockStateDefinition(b);
        b.add(new Property[]{POWERED});
    }

    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        return new RadioBE(p, s);
    }

    protected InteractionResult useWithoutItem(BlockState s, Level lv, BlockPos pos, Player pl, BlockHitResult hit) {
        if (!RadioBlock.handEmpty(pl)) {
            return InteractionResult.PASS;
        }
        if (lv.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = lv.getBlockEntity(pos);
        if (!(blockEntity instanceof RadioBE)) {
            return InteractionResult.CONSUME;
        }
        RadioBE be = (RadioBE)blockEntity;
        if (pl.isShiftKeyDown() && lv instanceof ServerLevel) {
            ServerLevel sl = (ServerLevel)lv;
            Emitter e = RadioNetwork.seek(sl, pos, be.antennaTop(), be.tuning, be.rxGain());
            if (e != null) {
                be.tuning = e.tuning();
                be.sync();
                if (!((Boolean)s.getValue((Property)POWERED)).booleanValue()) {
                    lv.setBlock(pos, (BlockState)s.setValue((Property)POWERED, (Comparable)Boolean.valueOf(true)), 3);
                }
            }
            lv.playSound(null, pos, (SoundEvent)ModSounds.SEEK.get(), SoundSource.BLOCKS, 0.4f, e != null ? 1.6f : 0.7f);
            pl.displayClientMessage((Component)Component.literal((String)(be.tuning.display() + (String)(e != null && !e.name().isEmpty() ? "  " + e.name() : ""))), true);
            return InteractionResult.CONSUME;
        }
        boolean on = (Boolean)s.getValue((Property)POWERED) == false;
        lv.setBlock(pos, (BlockState)s.setValue((Property)POWERED, (Comparable)Boolean.valueOf(on)), 3);
        lv.playSound(null, pos, (SoundEvent)ModSounds.SWITCH.get(), SoundSource.BLOCKS, 0.6f, on ? 1.2f : 0.9f);
        if (on) {
            pl.displayClientMessage((Component)Component.literal((String)be.tuning.display()), true);
        }
        return InteractionResult.CONSUME;
    }
}
