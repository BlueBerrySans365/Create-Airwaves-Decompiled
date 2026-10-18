/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.airwaves.block;

import com.airwaves.ModRegistry;
import com.airwaves.Tier;
import com.airwaves.be.TransmitterBE;
import com.airwaves.block.TransmitterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class CreativeTransmitterBlock
extends TransmitterBlock {
    public CreativeTransmitterBlock(BlockBehaviour.Properties p) {
        super(Tier.GIANT, p);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)CRYSTAL, (Comparable)Integer.valueOf(2)));
    }

    @Override
    public BlockEntityType<? extends TransmitterBE> getBlockEntityType() {
        return ModRegistry.CREATIVE_TX_BE.get();
    }

    @Override
    public boolean hasShaftTowards(LevelReader w, BlockPos p, BlockState s, Direction face) {
        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState st, Level lv, BlockPos pos, Player pl, BlockHitResult hit) {
        return InteractionResult.PASS;
    }
}
