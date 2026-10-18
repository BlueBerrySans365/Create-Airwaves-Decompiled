/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.EntitySelector
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.common.NeoForge
 */
package com.airwaves.be;

import com.airwaves.ModRegistry;
import com.airwaves.api.event.CircuitBoxOutputEvent;
import com.airwaves.be.TunableBE;
import com.airwaves.block.CircuitBoxBlock;
import com.airwaves.item.BoardItem;
import com.airwaves.logic.BoardAnalysis;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

public class CircuitBoxBE
extends TunableBE {
    private ItemStack board = ItemStack.EMPTY;
    private BoardAnalysis.Program program;
    private int output;
    private long[] until = new long[0];
    private int[] held = new int[0];
    private int[] mem = new int[0];
    private int[] prev = new int[0];
    public static final double MOTION_RADIUS = 8.0;

    public CircuitBoxBE(BlockPos p, BlockState s) {
        super(ModRegistry.CIRCUIT_BOX_BE.get(), p, s);
    }

    public ItemStack board() {
        return this.board;
    }

    public int output() {
        return this.output;
    }

    @Override
    public double rxGain() {
        return 0.6;
    }

    public void setBoard(ItemStack b) {
        BlockState st;
        this.board = b;
        this.program = null;
        if (this.level != null && !this.level.isClientSide && (Boolean)(st = this.getBlockState()).getValue((Property)CircuitBoxBlock.BOARD) != !b.isEmpty()) {
            this.level.setBlock(this.worldPosition, (BlockState)st.setValue((Property)CircuitBoxBlock.BOARD, (Comparable)Boolean.valueOf(!b.isEmpty())), 3);
        }
        this.sync();
    }

    private BoardAnalysis.Program program() {
        if (this.program == null) {
            int n;
            this.program = this.board.isEmpty() ? null : BoardAnalysis.program(BoardItem.layout(this.board).analyse());
            int n2 = n = this.program == null ? 0 : this.program.steps().size();
            if (this.until.length != n) {
                this.until = new long[n];
                this.held = new int[n];
                this.prev = new int[n];
                if (this.mem.length != n) {
                    this.mem = new int[n];
                }
            }
        }
        return this.program;
    }

    public void serverTick() {
        BlockState st;
        boolean lit;
        Level k2;
        if (this.level == null) {
            return;
        }
        BoardAnalysis.Program p = this.board.isEmpty() ? null : this.program();
        long now = this.level.getGameTime();
        int s = 0;
        if (p != null) {
            Direction back = (Direction)this.getBlockState().getValue((Property)CircuitBoxBlock.FACING);
            s = switch (p.source()) {
                default -> throw new MatchException(null, null);
                case BoardAnalysis.Source.RADIO -> {
                    if (this.carrier) {
                        yield Math.max(1, Math.round(this.quality * 15.0f));
                    }
                    yield 0;
                }
                case BoardAnalysis.Source.CLOCK -> {
                    if (now % (long)p.clockTicks() < (long)(p.clockTicks() / 2)) {
                        yield 15;
                    }
                    yield 0;
                }
                case BoardAnalysis.Source.REDSTONE -> this.level.getSignal(this.worldPosition.relative(back), back);
                case BoardAnalysis.Source.DAYLIGHT -> this.daylight();
                case BoardAnalysis.Source.RAIN -> this.rain();
                case BoardAnalysis.Source.MOTION -> this.motion();
            };
            for (int k2 = 0; k2 < p.steps().size() && k2 < this.until.length; ++k2) {
                boolean rising = s > 0 && this.prev[k2] == 0;
                int in = s;
                switch (p.steps().get(k2)) {
                    case INVERT: {
                        s = s > 0 ? 0 : 15;
                        break;
                    }
                    case AMPLIFY: {
                        s = s > 0 ? 15 : 0;
                        break;
                    }
                    case BLINK: {
                        s = now / 5L % 2L == 0L ? s : 0;
                        break;
                    }
                    case ONLY_ON_AIR: {
                        s = this.carrier ? s : 0;
                        break;
                    }
                    case HOLD: {
                        if (s > 0) {
                            this.held[k2] = s;
                            this.until[k2] = now + 20L;
                            break;
                        }
                        if (now >= this.until[k2]) break;
                        s = this.held[k2];
                        break;
                    }
                    case TOGGLE: {
                        if (rising) {
                            int n = k2;
                            this.mem[n] = this.mem[n] ^ 1;
                        }
                        s = this.mem[k2] == 1 ? 15 : 0;
                        break;
                    }
                    case COUNT: {
                        if (rising) {
                            this.mem[k2] = (this.mem[k2] + 1) % 16;
                        }
                        s = this.mem[k2];
                        break;
                    }
                    case PULSE: {
                        if (rising) {
                            this.until[k2] = now + 4L;
                        }
                        s = now < this.until[k2] ? 15 : 0;
                        break;
                    }
                    case ONLY_DAY: {
                        s = this.daylight() >= 8 ? s : 0;
                        break;
                    }
                    case ONLY_RAIN: {
                        s = this.rain() > 0 ? s : 0;
                        break;
                    }
                    case ONLY_PLAYER: {
                        s = this.motion() > 0 ? s : 0;
                    }
                }
                this.prev[k2] = in;
            }
        }
        if (!this.board.isEmpty() && (k2 = this.level) instanceof ServerLevel) {
            ServerLevel sl = (ServerLevel)k2;
            s = ((CircuitBoxOutputEvent)NeoForge.EVENT_BUS.post((Event)new CircuitBoxOutputEvent(sl, this.worldPosition, this.board, p, s))).getOutput();
        }
        boolean bl = lit = p != null && p.lamp() && s > 0;
        if (s != this.output) {
            this.output = s;
            this.setChanged();
            CircuitBoxBlock.updateFront(this.level, this.worldPosition, this.getBlockState());
        }
        if ((Boolean)(st = this.getBlockState()).getValue((Property)CircuitBoxBlock.LIT) != lit) {
            this.level.setBlock(this.worldPosition, (BlockState)st.setValue((Property)CircuitBoxBlock.LIT, (Comparable)Boolean.valueOf(lit)), 3);
        }
    }

    private int daylight() {
        int v = this.level.getBrightness(LightLayer.SKY, this.worldPosition.above()) - this.level.getSkyDarken();
        return Math.max(0, Math.min(15, v));
    }

    private int rain() {
        if (!this.level.isRainingAt(this.worldPosition.above())) {
            return 0;
        }
        return this.level.isThundering() ? 15 : 8;
    }

    private int motion() {
        Player pl = this.level.getNearestPlayer((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5, 8.0, EntitySelector.NO_SPECTATORS);
        if (pl == null) {
            return 0;
        }
        return Math.max(1, 15 - (int)Math.floor(pl.distanceToSqr(this.worldPosition.getCenter()) < 1.0 ? 0.0 : Math.sqrt(pl.distanceToSqr(this.worldPosition.getCenter()))));
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tip, boolean sneaking) {
        tip.add((Component)Component.literal((String)"    Circuit Box").withStyle(ChatFormatting.WHITE));
        if (this.board.isEmpty()) {
            tip.add((Component)Component.literal((String)"    no board: right-click one in").withStyle(ChatFormatting.GOLD));
            return true;
        }
        BoardAnalysis.Program p = this.program();
        tip.add((Component)Component.literal((String)"    ").append(this.board.getHoverName()).withStyle(ChatFormatting.GRAY));
        tip.add((Component)Component.literal((String)("    " + BoardAnalysis.describe(p))).withStyle(ChatFormatting.DARK_AQUA));
        if (p.source() == BoardAnalysis.Source.RADIO || p.steps().contains((Object)BoardAnalysis.Step.ONLY_ON_AIR)) {
            tip.add((Component)Component.literal((String)("    " + this.tuning.display())).withStyle(ChatFormatting.AQUA));
            tip.add((Component)Component.literal((String)(this.carrier ? "    station heard: " + Math.round(this.quality * 100.0f) + "%" : "    no station")).withStyle(this.carrier ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
        }
        tip.add((Component)Component.literal((String)("    output " + this.output)).withStyle(this.output > 0 ? ChatFormatting.RED : ChatFormatting.DARK_GRAY));
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider r) {
        super.saveAdditional(tag, r);
        if (!this.board.isEmpty()) {
            tag.put("board", this.board.save(r));
        }
        tag.putInt("out", this.output);
        tag.putIntArray("mem", this.mem);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider r) {
        ItemStack b;
        super.loadAdditional(tag, r);
        ItemStack itemStack = b = tag.contains("board") ? ItemStack.parseOptional((HolderLookup.Provider)r, (CompoundTag)tag.getCompound("board")) : ItemStack.EMPTY;
        if (!ItemStack.matches((ItemStack)b, (ItemStack)this.board)) {
            this.board = b;
            this.program = null;
        }
        this.output = tag.getInt("out");
        this.mem = tag.getIntArray("mem");
    }
}
