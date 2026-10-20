/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.placement.PoleHelper
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.airwaves.block;

import com.airwaves.block.AntennaBlock;
import com.airwaves.block.MastBlock;
import com.simibubi.create.foundation.placement.PoleHelper;
import java.util.function.Predicate;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

private static class AntennaBlock.ExtendHelper
extends PoleHelper<Boolean> {
    AntennaBlock.ExtendHelper() {
        super(s -> s.getBlock() instanceof AntennaBlock, s -> Direction.Axis.Y, (Property)MastBlock.TOP);
    }

    public Predicate<ItemStack> getItemPredicate() {
        return i -> {
            BlockItem b;
            Item patt0$temp = i.getItem();
            return patt0$temp instanceof BlockItem && (b = (BlockItem)patt0$temp).getBlock() instanceof AntennaBlock;
        };
    }

    public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray) {
        Block block = state.getBlock();
        if (!(block instanceof AntennaBlock)) {
            return PlacementOffset.fail();
        }
        AntennaBlock a = (AntennaBlock)block;
        int above = this.attachedPoles(world, pos, Direction.UP);
        int below = this.attachedPoles(world, pos, Direction.DOWN);
        if (above + below + 1 >= a.maxHeight()) {
            return PlacementOffset.fail();
        }
        BlockPos np = pos.above(above + 1);
        if (!world.isInWorldBounds(np) || !world.getBlockState(np).canBeReplaced()) {
            return PlacementOffset.fail();
        }
        return PlacementOffset.success((Vec3i)np, s -> (BlockState)s.setValue((Property)MastBlock.TOP, (Comparable)Boolean.valueOf(!MastBlock.continues(world.getBlockState(np.above())))));
    }
}
