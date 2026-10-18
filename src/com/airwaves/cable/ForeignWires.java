/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.airwaves.cable;

import com.airwaves.Config;
import com.airwaves.cable.CableType;
import com.airwaves.item.CableItem;
import com.airwaves.power.Terminals;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class ForeignWires {
    private ForeignWires() {
    }

    public static CableType asCable(ItemStack st) {
        boolean cee;
        if (st.isEmpty() || st.getItem() instanceof CableItem) {
            return null;
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey((Object)st.getItem());
        String ns = id.getNamespace();
        String p = id.getPath();
        boolean pg = ns.equals("powergrid") && Config.powerGridLoaded();
        boolean bl = cee = ns.equals("electroenergetics") && Config.ceeLoaded();
        if (!pg && !cee) {
            return null;
        }
        if (!p.contains("wire") && !p.contains("cord")) {
            return null;
        }
        if (p.contains("strand") || p.contains("empty") || p.contains("duplex") || p.contains("string_light")) {
            return null;
        }
        if (p.contains("gold") || p.contains("electrum")) {
            return CableType.GOLD;
        }
        if (p.contains("insulated")) {
            return CableType.SILVER;
        }
        return CableType.COPPER;
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void rightClick(PlayerInteractEvent.RightClickBlock e) {
        CableType type = ForeignWires.asCable(e.getItemStack());
        if (type == null || e.getEntity() == null) {
            return;
        }
        Level lv = e.getLevel();
        if (CableItem.refAt(lv, e.getPos(), e.getHitVec().getLocation()) == null) {
            return;
        }
        BlockState st = lv.getBlockState(e.getPos());
        Vec3 local = e.getHitVec().getLocation().subtract((double)e.getPos().getX(), (double)e.getPos().getY(), (double)e.getPos().getZ());
        if (Terminals.has(st) && Terminals.at(st, local) >= 0) {
            return;
        }
        InteractionResult r = CableItem.use(new UseOnContext(e.getEntity(), e.getHand(), e.getHitVec()), type);
        if (r != InteractionResult.PASS) {
            e.setCanceled(true);
            e.setCancellationResult(r);
        }
    }
}
