/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Holder
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.item.JukeboxSong
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.JukeboxBlockEntity
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.neoforge.event.PlayLevelSoundEvent
 *  net.neoforged.neoforge.event.PlayLevelSoundEvent$AtEntity
 *  net.neoforged.neoforge.event.PlayLevelSoundEvent$AtPosition
 */
package com.airwaves.logic;

import com.airwaves.be.MicrophoneBE;
import com.airwaves.be.TapePlayerBE;
import com.airwaves.logic.RadioNetwork;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

public final class SoundCapture {
    public static boolean suppress;
    private static final Map<ResourceKey<Level>, Map<Long, Map<Long, Heard>>> HEARD;
    public static final int JUKEBOX_RANGE = 6;

    private SoundCapture() {
    }

    @SubscribeEvent
    public static void atPosition(PlayLevelSoundEvent.AtPosition e) {
        SoundCapture.handle(e.getLevel(), e.getPosition(), (PlayLevelSoundEvent)e);
    }

    @SubscribeEvent
    public static void atEntity(PlayLevelSoundEvent.AtEntity e) {
        SoundCapture.handle(e.getEntity().level(), e.getEntity().position(), (PlayLevelSoundEvent)e);
    }

    private static void handle(Level lv, Vec3 at, PlayLevelSoundEvent e) {
        if (suppress || e.isCanceled() || !(lv instanceof ServerLevel)) {
            return;
        }
        ServerLevel sl = (ServerLevel)lv;
        Holder h = e.getSound();
        if (h == null) {
            return;
        }
        SoundSource src = e.getSource();
        if (src == SoundSource.MUSIC || src == SoundSource.AMBIENT || src == SoundSource.VOICE) {
            return;
        }
        RadioNetwork.onWorldSound(sl, at, ((SoundEvent)h.value()).getLocation(), e.getNewVolume(), e.getNewPitch());
    }

    public static void pollJukeboxes(ServerLevel sl, Collection<MicrophoneBE> mics) {
        Map dim = HEARD.computeIfAbsent((ResourceKey<Level>)sl.dimension(), k -> new HashMap());
        HashSet<Long> alive = new HashSet<Long>();
        for (MicrophoneBE m : mics) {
            TapePlayerBE tp;
            if (m.isRemoved()) continue;
            long mk = m.getBlockPos().asLong();
            alive.add(mk);
            TapePlayerBE tape = m instanceof TapePlayerBE && (tp = (TapePlayerBE)m).isRecording() ? tp : null;
            boolean live = !(m instanceof TapePlayerBE) && m.feeds != null && m.feeds.active && !m.feeds.isRemoved();
            long root = live ? m.feeds.getBlockPos().asLong() : 0L;
            HashMap<Long, Heard> now = new HashMap<Long, Heard>();
            if (tape != null || live) {
                BlockPos c = m.getBlockPos();
                for (BlockPos p : BlockPos.betweenClosed((BlockPos)c.offset(-6, -6, -6), (BlockPos)c.offset(6, 6, 6))) {
                    JukeboxSong song;
                    JukeboxBlockEntity jb;
                    BlockEntity blockEntity;
                    if (!sl.isLoaded(p) || !((blockEntity = sl.getBlockEntity(p)) instanceof JukeboxBlockEntity) || !(jb = (JukeboxBlockEntity)blockEntity).getSongPlayer().isPlaying() || (song = jb.getSongPlayer().getSong()) == null) continue;
                    ResourceLocation sid = ((SoundEvent)song.soundEvent().value()).getLocation();
                    Heard was = (Heard)dim.getOrDefault(mk, Map.of()).get(p.asLong());
                    now.put(p.asLong(), new Heard(sid, root, was != null && was.sound().equals((Object)sid) && was.root() == root ? was.since() : sl.getGameTime()));
                }
            }
            Map before = dim.computeIfAbsent(mk, k -> new HashMap());
            for (Map.Entry e : before.entrySet()) {
                Heard n = (Heard)now.get(e.getKey());
                if (n != null && n.sound().equals((Object)((Heard)e.getValue()).sound())) continue;
                SoundCapture.send(sl, m, tape, (Heard)e.getValue(), (Long)e.getKey(), (byte)2);
            }
            for (Map.Entry e : now.entrySet()) {
                boolean fresh;
                Heard b = (Heard)before.get(e.getKey());
                boolean bl = fresh = b == null || !b.sound().equals((Object)((Heard)e.getValue()).sound());
                if (tape != null) {
                    if (!fresh) continue;
                    tape.capture(((Heard)e.getValue()).sound(), 1.0f, 1.0f, (byte)1);
                    continue;
                }
                if (((Heard)e.getValue()).root() == 0L) continue;
                RadioNetwork.broadcastSound(sl, ((Heard)e.getValue()).root(), ((Heard)e.getValue()).sound(), 1.0f, 1.0f, (byte)1, (Long)e.getKey(), (int)Math.min(Integer.MAX_VALUE, (sl.getGameTime() - ((Heard)e.getValue()).since()) * 50L));
            }
            before.clear();
            before.putAll(now);
        }
        Iterator it = dim.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = it.next();
            if (alive.contains(e.getKey())) continue;
            for (Map.Entry h : ((Map)e.getValue()).entrySet()) {
                if (((Heard)h.getValue()).root() == 0L) continue;
                RadioNetwork.broadcastSound(sl, ((Heard)h.getValue()).root(), ((Heard)h.getValue()).sound(), 1.0f, 1.0f, (byte)2, (Long)h.getKey());
            }
            it.remove();
        }
    }

    private static void send(ServerLevel sl, MicrophoneBE m, TapePlayerBE tape, Heard h, long jukebox, byte kind) {
        if (tape != null) {
            tape.capture(h.sound(), 1.0f, 1.0f, kind);
            return;
        }
        if (h.root() != 0L) {
            RadioNetwork.broadcastSound(sl, h.root(), h.sound(), 1.0f, 1.0f, kind, jukebox);
        }
    }

    public static void clear() {
        HEARD.clear();
    }

    static {
        HEARD = new HashMap<ResourceKey<Level>, Map<Long, Map<Long, Heard>>>();
    }

    private record Heard(ResourceLocation sound, long root, long since) {
    }
}
