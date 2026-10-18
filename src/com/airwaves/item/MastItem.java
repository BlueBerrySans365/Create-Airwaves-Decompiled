/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.airwaves.item;

import com.airwaves.block.AntennaBlock;
import com.airwaves.block.MastBlock;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class MastItem
extends BlockItem {
    protected final MastBlock mb;

    public MastItem(MastBlock b, Item.Properties p) {
        super((Block)b, p);
        this.mb = b;
    }

    /*
     * Unable to fully structure code
     */
    public InteractionResult place(BlockPlaceContext ctx) {
        lv = ctx.getLevel();
        pos = ctx.getClickedPos();
        pl = ctx.getPlayer();
        target = this.mb;
        below = pos.below();
        belowSt = lv.getBlockState(below);
        seg = 0;
        narrowing = false;
        var13_10 = belowSt.getBlock();
        if (!(var13_10 instanceof AntennaBlock)) ** GOTO lbl-1000
        under = (AntennaBlock)var13_10;
        if (this.mb instanceof AntennaBlock) {
            b = AntennaBlock.base((BlockGetter)lv, below);
            if (b == null || b[0] != this.mb) {
                return InteractionResult.FAIL;
            }
            w = ((AntennaBlock)this.mb).widthAt((Integer)b[1]);
            if (w == 0) {
                return InteractionResult.FAIL;
            }
            target = AntennaBlock.forWidth(w);
            col = under.column(below, belowSt);
            origin = col.offset(-(w / 2), 1, -(w / 2));
            seg = target.nextSeg(lv.getBlockState(col));
            narrowing = w < under.size();
        } else if ((b = belowSt.getBlock()) instanceof MastBlock && (other = (MastBlock)b) == this.mb) {
            origin = this.mb.origin(below, belowSt).above();
            seg = this.mb.nextSeg(belowSt);
        } else {
            n = this.mb.size();
            origin = pos.offset(-((n - 1) / 2), 0, -((n - 1) / 2));
        }
        n = target.size();
        for (dx = 0; dx < n; ++dx) {
            for (dz = 0; dz < n; ++dz) {
                q = origin.offset(dx, 0, dz);
                if (lv.isInWorldBounds(q) && lv.getBlockState(q).canBeReplaced()) continue;
                return InteractionResult.FAIL;
            }
        }
        if (!lv.isClientSide) {
            for (dx = 0; dx < n; ++dx) {
                for (dz = 0; dz < n; ++dz) {
                    q = origin.offset(dx, 0, dz);
                    s = (BlockState)target.defaultBlockState().setValue((Property)MastBlock.TOP, (Comparable)Boolean.valueOf(lv.getBlockState(q.above()).getBlock() instanceof MastBlock == false));
                    if (n > 1) {
                        s = (BlockState)((BlockState)s.setValue((Property)target.px(), (Comparable)Integer.valueOf(dx))).setValue((Property)target.pz(), (Comparable)Integer.valueOf(dz));
                    }
                    if (s.hasProperty((Property)MastBlock.SEG)) {
                        s = (BlockState)s.setValue((Property)MastBlock.SEG, (Comparable)Integer.valueOf(seg));
                    }
                    lv.setBlock(q, s, 3);
                }
            }
            if (narrowing) {
                var13_14 = belowSt.getBlock();
                if (var13_14 instanceof MastBlock) {
                    wb = (MastBlock)var13_14;
                    v0 = wb.origin(below, belowSt);
                } else {
                    v0 = below;
                }
                MastBlock.setNarrow(lv, v0, true);
            }
            lv.playSound(null, pos, target.defaultBlockState().getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0f, 0.8f);
            if (pl == null || !pl.getAbilities().instabuild) {
                ctx.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess((boolean)lv.isClientSide);
    }
}
