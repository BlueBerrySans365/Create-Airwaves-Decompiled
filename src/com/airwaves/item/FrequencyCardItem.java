/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.airwaves.item;

import com.airwaves.ModRegistry;
import com.airwaves.ModSounds;
import com.airwaves.Tuning;
import com.airwaves.be.Tuned;
import com.airwaves.item.Tunable;
import com.airwaves.structure.StructureBlock;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FrequencyCardItem
extends Item
implements Tunable {
    public FrequencyCardItem(Item.Properties p) {
        super(p);
    }

    @Override
    public boolean canTune(ItemStack stack) {
        return stack.has((DataComponentType)ModRegistry.TUNING.get());
    }

    public boolean isFoil(ItemStack stack) {
        return stack.has((DataComponentType)ModRegistry.TUNING.get());
    }

    public InteractionResult useOn(UseOnContext ctx) {
        BlockPos blockPos;
        BlockPos pos;
        Player pl = ctx.getPlayer();
        if (pl == null || !pl.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        Level lv = ctx.getLevel();
        BlockState st = lv.getBlockState(pos = ctx.getClickedPos());
        Block block = st.getBlock();
        if (block instanceof StructureBlock) {
            StructureBlock sb = (StructureBlock)block;
            blockPos = sb.corePos(st, pos);
        } else {
            blockPos = pos;
        }
        BlockEntity be = lv.getBlockEntity(blockPos);
        if (!(be instanceof Tuned)) {
            return InteractionResult.PASS;
        }
        Tuned t = (Tuned)be;
        if (!lv.isClientSide) {
            ItemStack card = ctx.getItemInHand();
            card.set((DataComponentType)ModRegistry.TUNING.get(), (Object)t.getTuning());
            if (t.getStationName() != null) {
                card.set(DataComponents.CUSTOM_NAME, (Object)t.getStationName());
            }
            lv.playSound(null, pos, (SoundEvent)ModSounds.TUNE.get(), SoundSource.PLAYERS, 0.5f, 1.4f);
            pl.displayClientMessage((Component)Component.literal((String)("Copied " + t.getTuning().display())).withStyle(ChatFormatting.AQUA), true);
        }
        return InteractionResult.sidedSuccess((boolean)lv.isClientSide);
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        Tuning t = (Tuning)stack.get((DataComponentType)ModRegistry.TUNING.get());
        if (t != null) {
            tip.add((Component)Component.literal((String)t.display()).withStyle(ChatFormatting.AQUA));
        } else {
            tip.add((Component)Component.literal((String)"Blank").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
