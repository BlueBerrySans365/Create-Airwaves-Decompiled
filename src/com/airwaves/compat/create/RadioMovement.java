/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.behaviour.movement.MovementBehaviour
 *  com.simibubi.create.content.contraptions.behaviour.MovementContext
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.registries.DeferredBlock
 *  org.jetbrains.annotations.Nullable
 */
package com.airwaves.compat.create;

import com.airwaves.ModRegistry;
import com.airwaves.Tuning;
import com.airwaves.block.RadioBlock;
import com.airwaves.block.ReceiverBlock;
import com.airwaves.logic.MobileRadios;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Nullable;

public class RadioMovement
implements MovementBehaviour {
    private final int kind;

    private RadioMovement(int kind) {
        this.kind = kind;
    }

    public static void register() {
        RadioMovement.reg((Block)ModRegistry.RADIO.get(), 1);
        for (DeferredBlock<ReceiverBlock> deferredBlock : ModRegistry.RECEIVERS) {
            RadioMovement.reg((Block)deferredBlock.get(), 2);
        }
        RadioMovement.reg((Block)ModRegistry.CREATIVE_RECEIVER.get(), 2);
        RadioMovement.reg((Block)ModRegistry.RADIO_CONSOLE.get(), 3);
        RadioMovement.reg((Block)ModRegistry.SPEAKER.get(), 4);
        for (DeferredBlock deferredBlock : ModRegistry.TRANSMITTERS) {
            RadioMovement.reg((Block)deferredBlock.get(), 5);
        }
        RadioMovement.reg((Block)ModRegistry.CREATIVE_TRANSMITTER.get(), 5);
        RadioMovement.reg((Block)ModRegistry.MICROPHONE.get(), 6);
    }

    private static void reg(Block b, int kind) {
        MovementBehaviour.REGISTRY.register((Object)b, (Object)new RadioMovement(kind));
    }

    private static UUID id(MovementContext c) {
        if (!c.data.hasUUID("airwaves_id")) {
            c.data.putUUID("airwaves_id", UUID.randomUUID());
        }
        return c.data.getUUID("airwaves_id");
    }

    public boolean mustTickWhileDisabled() {
        return true;
    }

    @Nullable
    public ItemStack canBeDisabledVia(MovementContext context) {
        return null;
    }

    public void tick(MovementContext c) {
        CompoundTag be;
        ServerLevel sl;
        block11: {
            block10: {
                Level level = c.world;
                if (!(level instanceof ServerLevel)) break block10;
                sl = (ServerLevel)level;
                if (c.position != null && c.contraption != null && c.contraption.entity != null) break block11;
            }
            return;
        }
        MobileRadios.Mobile m = MobileRadios.update(RadioMovement.id(c), this.kind, sl, c.position, c.contraption.entity.getId());
        CompoundTag compoundTag = be = c.blockEntityData == null ? new CompoundTag() : c.blockEntityData;
        if (be.contains("freq")) {
            m.tuning = new Tuning(be.getBoolean("fm"), be.getDouble("freq")).clamped();
        }
        BlockState s = c.state;
        switch (this.kind) {
            case 1: {
                m.on = s.hasProperty((Property)RadioBlock.POWERED) && (Boolean)s.getValue((Property)RadioBlock.POWERED) != false;
                m.rxGain = 0.8;
                break;
            }
            case 2: {
                boolean creative = s.is((Block)ModRegistry.CREATIVE_RECEIVER.get());
                m.on = creative || s.hasProperty((Property)ReceiverBlock.CRYSTAL) && (Integer)s.getValue((Property)ReceiverBlock.CRYSTAL) > 0;
                m.rxGain = creative ? 1000000.0 : 1.2;
                break;
            }
            case 3: {
                m.on = true;
                m.rxGain = 1.0;
                break;
            }
            case 5: {
                boolean creative = s.is((Block)ModRegistry.CREATIVE_TRANSMITTER.get());
                m.on = creative || be.getBoolean("active");
                double d = m.range = creative ? 1.0E8 : be.getDouble("range");
                if (!(m.range <= 0.0) || !m.on) break;
                m.range = 300.0;
                break;
            }
            default: {
                m.on = true;
            }
        }
    }

    public void stopMoving(MovementContext c) {
        if (c.data.hasUUID("airwaves_id")) {
            MobileRadios.remove(c.data.getUUID("airwaves_id"));
        }
    }
}
