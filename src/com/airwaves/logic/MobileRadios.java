/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.network.PacketDistributor
 */
package com.airwaves.logic;

import com.airwaves.Tuning;
import com.airwaves.logic.Emitter;
import com.airwaves.logic.RadioNetwork;
import com.airwaves.net.HandheldAudioPayload;
import com.airwaves.net.RadioSoundPayload;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public final class MobileRadios {
    public static final int RADIO = 1;
    public static final int RECEIVER = 2;
    public static final int CONSOLE = 3;
    public static final int SPEAKER = 4;
    public static final int TRANSMITTER = 5;
    public static final int MICROPHONE = 6;
    private static final Map<UUID, Mobile> ALL = new HashMap<UUID, Mobile>();
    public static final double HEAR = 20.0;
    public static final double MIC_RANGE = 3.5;
    public static final double DESK_RANGE = 2.5;
    public static final double CONSOLE_FM = 320.0;
    public static final double CONSOLE_AM = 480.0;

    private MobileRadios() {
    }

    public static Mobile update(UUID id, int kind, ServerLevel sl, Vec3 pos, int contraption) {
        Mobile m = ALL.computeIfAbsent(id, k -> new Mobile((UUID)k, kind));
        m.dim = sl.dimension();
        m.pos = pos;
        m.contraption = contraption;
        m.seen = sl.getGameTime();
        return m;
    }

    public static void remove(UUID id) {
        ALL.remove(id);
    }

    public static void clear() {
        ALL.clear();
    }

    private static List<Mobile> in(ServerLevel sl) {
        long now = sl.getGameTime();
        ALL.values().removeIf(m -> m.dim == sl.dimension() && now - m.seen > 60L);
        ArrayList<Mobile> out = new ArrayList<Mobile>();
        for (Mobile m2 : ALL.values()) {
            if (m2.dim != sl.dimension()) continue;
            out.add(m2);
        }
        return out;
    }

    public static void addEmitters(ServerLevel sl, List<Emitter> em) {
        for (Mobile m : MobileRadios.in(sl)) {
            double r;
            if (!m.on || !m.station()) continue;
            double d = m.kind == 3 ? (m.tuning.fm() ? 320.0 : 480.0) : (r = m.range);
            if (r <= 0.0) continue;
            BlockPos at = BlockPos.containing((Position)m.pos);
            em.add(new Emitter(m.root, at, m.pos.y + 2.0, m.tuning, r, r >= 1.0E8 ? 1.0f : 0.95f, m.kind == 3 ? "Radio Console (moving)" : "Moving station"));
        }
    }

    public static void tune(ServerLevel sl, List<Emitter> em, List<BlockPos> jam) {
        for (Mobile m : MobileRadios.in(sl)) {
            if (!m.listens() || !m.on) {
                m.q = 0.0f;
                continue;
            }
            ArrayList<Emitter> others = new ArrayList<Emitter>();
            for (Emitter e : em) {
                if (MobileRadios.ownStation(m, e.root())) continue;
                others.add(e);
            }
            RadioNetwork.Best b = RadioNetwork.best((Level)sl, BlockPos.containing((Position)m.pos), m.pos.y + 1.0, m.tuning, m.rxGain, others, jam);
            m.root = b.root();
            m.q = b.q();
        }
    }

    private static boolean ownStation(Mobile listener, long root) {
        for (Mobile o : ALL.values()) {
            if (!o.station() || o.contraption != listener.contraption || o.dim != listener.dim || o.root != root) continue;
            return true;
        }
        return false;
    }

    public static long voice(ServerLevel sl, Vec3 at) {
        Mobile best = null;
        double bd = Double.MAX_VALUE;
        for (Mobile m : MobileRadios.in(sl)) {
            double d = m.pos.distanceTo(at);
            Mobile station = null;
            if (m.kind == 6 && d <= 3.5) {
                station = MobileRadios.stationOf(m);
            } else if (m.kind == 5 && d <= 2.5) {
                station = m;
            } else if (m.kind == 3 && d <= 3.5) {
                station = m;
            }
            if (station == null || !station.on || !(d < bd)) continue;
            bd = d;
            best = station;
        }
        return best == null ? 0L : best.root;
    }

    private static Mobile stationOf(Mobile mic) {
        Mobile best = null;
        double bd = Double.MAX_VALUE;
        for (Mobile o : ALL.values()) {
            double d;
            if (o.kind != 5 || !o.on || o.contraption != mic.contraption || o.dim != mic.dim || !((d = o.pos.distanceTo(mic.pos)) < bd)) continue;
            bd = d;
            best = o;
        }
        return best;
    }

    public static Mobile byRoot(ServerLevel sl, long root) {
        for (Mobile m : MobileRadios.in(sl)) {
            if (!m.station() || m.root != root) continue;
            return m;
        }
        return null;
    }

    private static void listeners(ServerLevel sl, long root, UUID talker, Out out) {
        HashMap<ServerPlayer, float[]> best = new HashMap<ServerPlayer, float[]>();
        HashMap<ServerPlayer, Boolean> fmOf = new HashMap<ServerPlayer, Boolean>();
        for (Mobile mobile : MobileRadios.in(sl)) {
            if (!mobile.listens() || !mobile.on || mobile.q < 0.04f || mobile.root != root) continue;
            ArrayList<Vec3> mouths = new ArrayList<Vec3>();
            mouths.add(mobile.pos);
            for (Mobile s : ALL.values()) {
                if (s.kind != 4 || s.contraption != mobile.contraption || s.dim != mobile.dim) continue;
                mouths.add(s.pos);
            }
            for (ServerPlayer p : sl.players()) {
                if (talker != null && p.getUUID().equals(talker)) continue;
                double d = Double.MAX_VALUE;
                for (Vec3 v : mouths) {
                    d = Math.min(d, v.distanceTo(p.getEyePosition()));
                }
                if (d > 20.0) continue;
                float near = (float)Math.pow(1.0 - d / 20.0, 1.4);
                float[] cur = (float[])best.get(p);
                if (cur != null && !(near * mobile.q > cur[0] * cur[1])) continue;
                best.put(p, new float[]{near, mobile.q});
                fmOf.put(p, mobile.tuning.fm());
            }
        }
        for (Map.Entry entry : best.entrySet()) {
            out.to((ServerPlayer)entry.getKey(), ((float[])entry.getValue())[1], (Boolean)fmOf.get(entry.getKey()), ((float[])entry.getValue())[0]);
        }
    }

    public static void deliverVoice(ServerLevel sl, long root, UUID talker, byte[] data, float gain) {
        MobileRadios.listeners(sl, root, talker, (p, q, fm, near) -> PacketDistributor.sendToPlayer((ServerPlayer)p, (CustomPacketPayload)new HandheldAudioPayload(talker, data, q, fm, gain * near * 1.1f), (CustomPacketPayload[])new CustomPacketPayload[0]));
    }

    public static void deliverSound(ServerLevel sl, long root, ResourceLocation id, float vol, float pitch, byte kind, long key, int offsetMs) {
        MobileRadios.listeners(sl, root, null, (p, q, fm, near) -> PacketDistributor.sendToPlayer((ServerPlayer)p, (CustomPacketPayload)new RadioSoundPayload(BlockPos.ZERO, true, id, vol * near, pitch, q, fm, kind, key ^ 0x7F4A7C15L, offsetMs), (CustomPacketPayload[])new CustomPacketPayload[0]));
    }

    public static final class Mobile {
        public final UUID id;
        public final int kind;
        public ResourceKey<Level> dim;
        public Vec3 pos = Vec3.ZERO;
        public Tuning tuning = Tuning.DEFAULT;
        public int contraption;
        public boolean on;
        public double range;
        public double rxGain = 1.0;
        public long seen;
        public long root;
        public float q;

        Mobile(UUID id, int kind) {
            this.id = id;
            this.kind = kind;
            this.root = id.getMostSignificantBits() ^ id.getLeastSignificantBits() ^ 0x4D0B11E5L;
        }

        public boolean listens() {
            return this.kind == 1 || this.kind == 2 || this.kind == 3;
        }

        public boolean station() {
            return this.kind == 5 || this.kind == 3;
        }
    }

    private static interface Out {
        public void to(ServerPlayer var1, float var2, boolean var3, float var4);
    }
}
