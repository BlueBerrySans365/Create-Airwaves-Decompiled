/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.airwaves.compat.cee;

import com.airwaves.Tier;
import com.airwaves.block.TransmitterBlock;
import com.airwaves.compat.cee.CEEMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public static class CEEBlocks.Transmitter
extends TransmitterBlock
implements CEEMachine {
    public CEEBlocks.Transmitter(Tier t, BlockBehaviour.Properties p) {
        super(t, p);
    }

    public void onPlace(BlockState s, Level l, BlockPos p, BlockState old, boolean moving) {
        super.onPlace(s, l, p, old, moving);
        CEEMachine.placed(l, p, s, (Block)this);
    }

    protected void tick(BlockState s, ServerLevel l, BlockPos p, RandomSource r) {
        CEEMachine.ticked(this, l, p, s);
        super.tick(s, l, p, r);
    }

    public BlockState playerWillDestroy(Level l, BlockPos p, BlockState s, Player pl) {
        CEEMachine.creativeBreak(l, p, pl);
        return super.playerWillDestroy(l, p, s, pl);
    }

    public InteractionResult onSneakWrenched(BlockState s, UseOnContext c) {
        this.removeWiresByPlayer(c.getPlayer(), c.getLevel(), c.getClickedPos());
        return super.onSneakWrenched(s, c);
    }
}
