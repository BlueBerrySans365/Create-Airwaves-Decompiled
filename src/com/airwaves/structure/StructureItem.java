/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.airwaves.structure;

import com.airwaves.ModRegistry;
import com.airwaves.ModSounds;
import com.airwaves.Tuning;
import com.airwaves.item.Tunable;
import com.airwaves.structure.StructureBlock;
import com.airwaves.structure.StructureDef;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class StructureItem
extends BlockItem {
    protected final StructureBlock sb;

    public StructureItem(StructureBlock b, Item.Properties p) {
        super((Block)b, p);
        this.sb = b;
    }

    public static Direction.Axis axisFor(Player pl) {
        return pl == null ? Direction.Axis.X : pl.getDirection().getClockWise().getAxis();
    }

    public InteractionResult place(BlockPlaceContext ctx) {
        Level lv = ctx.getLevel();
        BlockPos at = ctx.getClickedPos();
        Player pl = ctx.getPlayer();
        Direction.Axis ax = StructureItem.axisFor(pl);
        StructureDef d = this.sb.def;
        for (int i = 0; i < d.size(); ++i) {
            BlockPos q = d.partPos(at, i, ax);
            if (lv.isInWorldBounds(q) && lv.getBlockState(q).canBeReplaced()) continue;
            if (lv.isClientSide && pl != null) {
                pl.displayClientMessage((Component)Component.literal((String)"\u2716").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }
        if (!lv.isClientSide) {
            int i;
            BlockState base = (BlockState)this.sb.defaultBlockState().setValue(StructureBlock.AXIS, (Comparable)ax);
            for (i = 0; i < d.size(); ++i) {
                lv.setBlock(d.partPos(at, i, ax), (BlockState)base.setValue((Property)this.sb.part, (Comparable)Integer.valueOf(i)), 2);
            }
            for (i = 0; i < d.size(); ++i) {
                BlockPos q = d.partPos(at, i, ax);
                lv.blockUpdated(q, (Block)this.sb);
            }
            BlockPos core = d.partPos(at, 0, ax);
            BlockEntity be = lv.getBlockEntity(core);
            ItemStack st = ctx.getItemInHand();
            if (be != null) {
                be.applyComponentsFromItemStack(st);
                be.setChanged();
            }
            this.onPlaced(lv, core, st);
            lv.playSound(null, at, (SoundEvent)ModSounds.STRUCTURE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            if (pl == null || !pl.getAbilities().instabuild) {
                st.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess((boolean)lv.isClientSide);
    }

    protected void onPlaced(Level lv, BlockPos core, ItemStack stack) {
    }

    public static class Tuned
    extends StructureItem
    implements Tunable {
        public Tuned(StructureBlock b, Item.Properties p) {
            super(b, p);
        }

        public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
            tip.add((Component)Component.literal((String)((Tuning)stack.getOrDefault((DataComponentType)ModRegistry.TUNING.get(), (Object)Tuning.DEFAULT)).display()).withStyle(ChatFormatting.AQUA));
        }
    }
}
