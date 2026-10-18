/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.airwaves.block;

import com.airwaves.Tier;
import com.airwaves.be.CreativeReceiverBE;
import com.airwaves.block.ReceiverBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class CreativeReceiverBlock
extends ReceiverBlock {
    public CreativeReceiverBlock(BlockBehaviour.Properties p) {
        super(Tier.GIANT, p);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)CRYSTAL, (Comparable)Integer.valueOf(2))).setValue((Property)ACTIVE, (Comparable)Boolean.valueOf(true)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p, BlockState s) {
        return new CreativeReceiverBE(p, s);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState st, Level lv, BlockPos pos, Player pl, BlockHitResult hit) {
        return InteractionResult.PASS;
    }
}
