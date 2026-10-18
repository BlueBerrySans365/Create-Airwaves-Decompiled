/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.network.PacketDistributor
 */
package com.airwaves.logic;

import com.airwaves.ModRegistry;
import com.airwaves.Tuning;
import com.airwaves.api.RadioStation;
import com.airwaves.api.event.CollectStationsEvent;
import com.airwaves.api.event.RadioNetworkTickEvent;
import com.airwaves.api.event.VoiceBroadcastEvent;
import com.airwaves.be.CableNode;
import com.airwaves.be.CircuitBoxBE;
import com.airwaves.be.DimensionalRelayBE;
import com.airwaves.be.JammerBE;
import com.airwaves.be.MicrophoneBE;
import com.airwaves.be.RadioBE;
import com.airwaves.be.RadioConsoleBE;
import com.airwaves.be.ReceiverBE;
import com.airwaves.be.RelayBE;
import com.airwaves.be.SpeakerBE;
import com.airwaves.be.TapePlayerBE;
import com.airwaves.be.TransmitterBE;
import com.airwaves.be.TunableBE;
import com.airwaves.block.TransmitterBlock;
import com.airwaves.compat.SableCompat;
import com.airwaves.item.PortableRadioItem;
import com.airwaves.logic.AntennaUtil;
import com.airwaves.logic.Cables;
import com.airwaves.logic.Emitter;
import com.airwaves.logic.SoundCapture;
import com.airwaves.logic.WeatherUtil;
import com.airwaves.net.CablePulsePayload;
import com.airwaves.net.HandheldAudioPayload;
import com.airwaves.net.HandheldStatePayload;
import com.airwaves.net.RadioAudioPayload;
import com.airwaves.net.RadioSoundPayload;
import com.airwaves.net.StaticBurstPayload;
import com.airwaves.net.StationInfo;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

public final class RadioNetwork {
    private static final Map<ResourceKey<Level>, Lvl> LEVELS = new HashMap<ResourceKey<Level>, Lvl>();
    private static final Map<UUID, HH> HANDHELD = new HashMap<UUID, HH>();
    public static final double MIC_RANGE = 3.5;
    public static final double DESK_RANGE = 2.5;
    public static final double JAM_RADIUS = 48.0;
    private static final Map<Long, long[]> TERRAIN = new ConcurrentHashMap<Long, long[]>();
    public static final double TAPE_VOICE_RANGE = 6.0;
    public static final double SOUND_RANGE = 8.0;
    private static final Map<Long, long[]> SOUND_RATE = new HashMap<Long, long[]>();
    public static final double WALKIE_FM = 320.0;
    public static final double WALKIE_AM = 480.0;
    private static final Map<UUID, Long> WALKIE_SHOWN = new HashMap<UUID, Long>();
    private static final Map<Long, Talk> TALKING = new HashMap<Long, Talk>();
    public static final long TALK_WINDOW_MS = 150L;

    private RadioNetwork() {
    }

    private static Lvl lvl(Level l) {
        return LEVELS.computeIfAbsent((ResourceKey<Level>)l.dimension(), k -> new Lvl());
    }

    public static long crossRoot(long root, ResourceKey<Level> from) {
        return root ^ (long)from.location().hashCode() * -7046029254386353131L ^ 0x5DEECE66DL;
    }

    public static String dimName(ResourceKey<Level> k) {
        if (k == Level.OVERWORLD) {
            return "the Overworld";
        }
        if (k == Level.NETHER) {
            return "the Nether";
        }
        if (k == Level.END) {
            return "the End";
        }
        String p = k.location().getPath().replace('_', ' ');
        return p.isEmpty() ? k.location().toString() : Character.toUpperCase(p.charAt(0)) + p.substring(1);
    }

    public static void clear() {
        LEVELS.clear();
        HANDHELD.clear();
        TALKING.clear();
    }

    public static void register(BlockEntity be) {
        Lvl l = RadioNetwork.lvl(be.getLevel());
        if (be instanceof TransmitterBE) {
            TransmitterBE t = (TransmitterBE)be;
            l.tx.add(t);
        } else if (be instanceof RelayBE) {
            RelayBE r = (RelayBE)be;
            l.relays.add(r);
        } else if (be instanceof RadioBE || be instanceof ReceiverBE || be instanceof RadioConsoleBE || be instanceof CircuitBoxBE) {
            l.sinks.add((TunableBE)be);
        } else if (be instanceof MicrophoneBE) {
            MicrophoneBE m = (MicrophoneBE)be;
            l.mics.add(m);
        } else if (be instanceof JammerBE) {
            JammerBE j = (JammerBE)be;
            l.jammers.add(j);
        } else if (be instanceof SpeakerBE) {
            SpeakerBE sp = (SpeakerBE)be;
            l.speakers.add(sp);
        } else if (be instanceof DimensionalRelayBE) {
            DimensionalRelayBE d = (DimensionalRelayBE)be;
            l.links.add(d);
        }
    }

    public static void unregister(BlockEntity be) {
        if (be.getLevel() == null) {
            return;
        }
        Lvl l = RadioNetwork.lvl(be.getLevel());
        if (be instanceof TransmitterBE) {
            TransmitterBE t = (TransmitterBE)be;
            l.tx.remove(t);
        } else if (be instanceof RelayBE) {
            RelayBE r = (RelayBE)be;
            l.relays.remove(r);
        } else if (be instanceof TunableBE) {
            TunableBE t = (TunableBE)be;
            l.sinks.remove(t);
        } else if (be instanceof MicrophoneBE) {
            MicrophoneBE m = (MicrophoneBE)be;
            l.mics.remove(m);
        } else if (be instanceof JammerBE) {
            JammerBE j = (JammerBE)be;
            l.jammers.remove(j);
        } else if (be instanceof SpeakerBE) {
            SpeakerBE sp = (SpeakerBE)be;
            l.speakers.remove(sp);
        } else if (be instanceof DimensionalRelayBE) {
            DimensionalRelayBE d = (DimensionalRelayBE)be;
            l.links.remove((Object)d);
        }
    }

    public static float quality(double dist, double range, boolean fm, float baseQ, double rxGain) {
        double eff = range * rxGain;
        if (eff <= 0.0) {
            return 0.0f;
        }
        double r = dist / eff;
        if (r >= 1.0) {
            return 0.0f;
        }
        double noise = fm ? Math.pow(r, 3.2) : Math.pow(r, 1.35);
        double q = (1.0 - noise) * (double)baseQ * (fm ? 1.0 : 0.88);
        return (float)Mth.clamp((double)q, (double)0.0, (double)1.0);
    }

    public static float terrain(Level lv, BlockPos a, double ay, BlockPos b, double by, boolean fm) {
        double dz;
        double dx = b.getX() - a.getX();
        double len = Math.sqrt(dx * dx + (dz = (double)(b.getZ() - a.getZ())) * dz);
        if (len < 48.0) {
            return 1.0f;
        }
        long key = a.asLong() * 1000003L ^ b.asLong() ^ (fm ? 25214903917L : 0L) ^ (long)(ay * 4.0) << 42 ^ (long)(by * 4.0) << 21 ^ (long)lv.dimension().location().hashCode();
        long now = lv.getGameTime();
        long[] c = TERRAIN.get(key);
        if (c != null && now - c[1] < 200L && now >= c[1]) {
            return Float.intBitsToFloat((int)c[0]);
        }
        float v = RadioNetwork.terrainUncached(lv, a, ay, b, by, fm, dx, dz, len);
        if (TERRAIN.size() > 20000) {
            TERRAIN.clear();
        }
        TERRAIN.put(key, new long[]{Float.floatToIntBits(v), now});
        return v;
    }

    private static float terrainUncached(Level lv, BlockPos a, double ay, BlockPos b, double by, boolean fm, double dx, double dz, double len) {
        int n = (int)Math.min(24.0, Math.max(6.0, len / 64.0));
        double worst = 0.0;
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        for (int i = 1; i < n; ++i) {
            double u = (double)i / (double)n;
            int x = Mth.floor((double)((double)a.getX() + dx * u));
            int z = Mth.floor((double)((double)a.getZ() + dz * u));
            m.set(x, 64, z);
            if (!lv.isLoaded((BlockPos)m)) continue;
            int h = lv.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            worst = Math.max(worst, (double)h - (ay + (by - ay) * u));
        }
        if (worst <= 0.0) {
            return 1.0f;
        }
        return (float)(fm ? Math.max(0.3, 1.0 - worst / 45.0) : Math.max(0.75, 1.0 - worst / 140.0));
    }

    public static Best best(Level lv, BlockPos pos, double rxTop, Tuning t, double gain, List<Emitter> em, List<BlockPos> jam) {
        return RadioNetwork.best(lv, pos, rxTop, t, gain, em, jam, null);
    }

    public static Best best(Level lv, BlockPos pos, double rxTop, Tuning t, double gain, List<Emitter> em, List<BlockPos> jam, Direction rxBeam) {
        float best = 0.0f;
        long root = 0L;
        double night = t.fm() ? 1.0 : WeatherUtil.amNightBoost(lv);
        for (Emitter e : em) {
            double g;
            if (!e.tuning().matches(t)) continue;
            if (e.creative()) {
                if (!(e.baseQ() > best)) continue;
                best = e.baseQ();
                root = e.root();
                continue;
            }
            double d = g = rxBeam == null ? gain : gain * AntennaUtil.pattern(rxBeam, e.pos().getX() - pos.getX(), e.pos().getZ() - pos.getZ());
            float q = RadioNetwork.quality(Math.sqrt(pos.distSqr((Vec3i)e.pos())), e.rangeTo(pos) * night, t.fm(), e.baseQ(), g);
            if (q <= best || (q *= RadioNetwork.jamming(e.pos(), pos, jam)) <= best || !((q *= RadioNetwork.terrain(lv, e.pos(), e.topY(), pos, rxTop, t.fm())) > best)) continue;
            best = q;
            root = e.root();
        }
        if (best > 0.0f) {
            best *= WeatherUtil.factor(lv, pos, t.fm());
        }
        return new Best(best, root);
    }

    public static float jamming(BlockPos from, BlockPos to, List<BlockPos> jam) {
        if (jam.isEmpty()) {
            return 1.0f;
        }
        float f = 1.0f;
        for (BlockPos j : jam) {
            double d = RadioNetwork.segmentDistance(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(), j.getX(), j.getY(), j.getZ());
            if (!(d < 48.0)) continue;
            f *= (float)(0.08 + 0.92 * Math.pow(d / 48.0, 1.5));
        }
        return f;
    }

    static double segmentDistance(double ax, double ay, double az, double bx, double by, double bz, double px, double py, double pz) {
        double dx = bx - ax;
        double dy = by - ay;
        double dz = bz - az;
        double l2 = dx * dx + dy * dy + dz * dz;
        double u = l2 < 1.0E-9 ? 0.0 : Math.max(0.0, Math.min(1.0, ((px - ax) * dx + (py - ay) * dy + (pz - az) * dz) / l2));
        double cx = ax + dx * u - px;
        double cy = ay + dy * u - py;
        double cz = az + dz * u - pz;
        return Math.sqrt(cx * cx + cy * cy + cz * cz);
    }

    public static Emitter seek(ServerLevel lv, BlockPos pos, double rxTop, Tuning cur, double gain) {
        Lvl l = LEVELS.get(lv.dimension());
        if (l == null) {
            return null;
        }
        Emitter first = null;
        Emitter next = null;
        for (Emitter e : l.emitters) {
            if (e.tuning().fm() != cur.fm() || RadioNetwork.best((Level)lv, pos, rxTop, e.tuning(), gain, List.of(e), l.jam).q() < 0.05f) continue;
            double f = e.tuning().freq();
            if (first == null || f < first.tuning().freq()) {
                first = e;
            }
            if (!(f > cur.freq() + 0.005) || next != null && !(f < next.tuning().freq())) continue;
            next = e;
        }
        return next != null ? next : first;
    }

    /*
     * WARNING - void declaration
     */
    public static void tick(ServerLevel level) {
        void var12_56;
        int n;
        Lvl l = RadioNetwork.lvl((Level)level);
        ArrayList<TransmitterBE> nodes = new ArrayList<TransmitterBE>(l.tx);
        nodes.addAll(l.relays);
        nodes.addAll(l.sinks);
        nodes.addAll(l.mics);
        nodes.addAll(l.speakers);
        nodes.addAll(l.jammers);
        for (BlockEntity blockEntity : nodes) {
            CableNode cableNode;
            if (!(blockEntity instanceof CableNode) || (cableNode = (CableNode)blockEntity).links().isEmpty() || !SableCompat.onShip((Level)level, blockEntity.getBlockPos())) continue;
            Cables.checkStretch((Level)level, blockEntity.getBlockPos(), cableNode);
        }
        for (TransmitterBE transmitterBE : l.tx) {
            transmitterBE.networkRefresh();
        }
        for (RelayBE relayBE : l.relays) {
            relayBE.networkRefresh();
        }
        for (TunableBE tunableBE : l.sinks) {
            tunableBE.refresh();
        }
        HashSet<TransmitterBE> wired = new HashSet<TransmitterBE>();
        for (MicrophoneBE microphoneBE : l.mics) {
            List<Cables.Hit<TransmitterBE>> list = Cables.reach((Level)level, microphoneBE.getBlockPos(), TransmitterBE.class);
            microphoneBE.feeds = list.isEmpty() ? null : list.get(0).target();
            microphoneBE.path = list.isEmpty() ? List.of() : list.get(0).path();
            float f = microphoneBE.line = list.isEmpty() ? 1.0f : list.get(0).lineQuality((Level)level);
            if (microphoneBE.feeds == null) continue;
            wired.add(microphoneBE.feeds);
        }
        for (TransmitterBE transmitterBE : l.tx) {
            transmitterBE.wired = wired.contains(transmitterBE);
        }
        SoundCapture.pollJukeboxes(level, l.mics);
        HashSet<SpeakerBE> hashSet = new HashSet<SpeakerBE>();
        for (TunableBE tunableBE : l.sinks) {
            if (!(tunableBE instanceof ReceiverBE)) continue;
            ReceiverBE receiverBE = (ReceiverBE)tunableBE;
            List list = Cables.reach((Level)level, receiverBE.getBlockPos(), SpeakerBE.class);
            receiverBE.speakers = Cables.targets(list);
            receiverBE.lineQ.clear();
            for (Cables.Hit hit : list) {
                receiverBE.lineQ.put((SpeakerBE)hit.target(), Float.valueOf(hit.lineQuality((Level)level)));
            }
            ArrayList<Cables.Seg> segs = new ArrayList<Cables.Seg>();
            for (Object x3 : list) {
                for (Cables.Seg seg : ((Cables.Hit)x3).path()) {
                    if (segs.contains(seg)) continue;
                    segs.add(seg);
                }
            }
            receiverBE.paths = segs;
            hashSet.addAll(receiverBE.speakers);
        }
        for (SpeakerBE speakerBE : l.speakers) {
            if (hashSet.contains(speakerBE)) continue;
            speakerBE.setState(false, false, 0.0f, false);
        }
        ArrayList<Emitter> arrayList = new ArrayList<Emitter>();
        for (TransmitterBE transmitterBE : l.tx) {
            if (!transmitterBE.active) continue;
            arrayList.add(new Emitter(transmitterBE.getBlockPos().asLong(), transmitterBE.radioPos(), transmitterBE.antennaTop(), transmitterBE.tuning, transmitterBE.range, transmitterBE.baseQ, transmitterBE.stationLabel(), transmitterBE.beam));
        }
        for (RadioStation radioStation : ((CollectStationsEvent)NeoForge.EVENT_BUS.post((Event)new CollectStationsEvent(level))).stations()) {
            arrayList.add(radioStation.toEmitter());
        }
        ArrayList<BlockPos> arrayList2 = new ArrayList<BlockPos>();
        for (JammerBE jammerBE : l.jammers) {
            if (!jammerBE.refresh()) continue;
            arrayList2.add(SableCompat.globalPos((Level)level, jammerBE.getBlockPos()));
        }
        l.jam = arrayList2;
        for (DimensionalRelayBE dimensionalRelayBE : l.links) {
            dimensionalRelayBE.refresh();
        }
        ArrayList<DimensionalRelayBE> arrayList3 = new ArrayList<DimensionalRelayBE>();
        for (DimensionalRelayBE d : l.links) {
            if (!d.active) continue;
            arrayList3.add(d);
        }
        l.linkActive = !arrayList3.isEmpty();
        HashSet<Long> hashSet2 = new HashSet<Long>();
        HashMap partners = new HashMap();
        boolean bl = false;
        if (!arrayList3.isEmpty()) {
            for (Map.Entry entry : LEVELS.entrySet()) {
                if (entry.getKey() == level.dimension() || !((Lvl)entry.getValue()).linkActive) continue;
                for (DimensionalRelayBE d : arrayList3) {
                    partners.computeIfAbsent(d, k -> new ArrayList()).add(RadioNetwork.dimName((ResourceKey<Level>)((ResourceKey)entry.getKey())));
                }
                for (Export x : ((Lvl)entry.getValue()).exports) {
                    long r = RadioNetwork.crossRoot(x.root(), (ResourceKey<Level>)((ResourceKey)entry.getKey()));
                    hashSet2.add(r);
                    ++n;
                    for (DimensionalRelayBE dimensionalRelayBE : arrayList3) {
                        arrayList.add(new Emitter(r, dimensionalRelayBE.radioPos(), dimensionalRelayBE.antennaTop(), x.tuning(), dimensionalRelayBE.range(x.tuning().fm()), Math.min(1.0f, (0.5f + 0.5f * x.q()) * 0.95f), x.name()));
                    }
                }
            }
        }
        HashSet<RelayBE> done = new HashSet<RelayBE>();
        boolean bl2 = false;
        while (var12_56 < 4) {
            ArrayList<Emitter> arrayList4 = new ArrayList<Emitter>();
            for (RelayBE r : l.relays) {
                if (!r.active || done.contains(r)) continue;
                float best = 0.0f;
                Object src = null;
                for (Emitter e : arrayList) {
                    BlockPos rp;
                    float q;
                    if (!e.tuning().matches(r.tuning) || !((q = RadioNetwork.quality(Math.sqrt((rp = r.radioPos()).distSqr((Vec3i)e.pos())), e.rangeTo(rp), r.tuning.fm(), e.baseQ(), r.rxGain()) * RadioNetwork.terrain((Level)level, e.pos(), e.topY(), rp, r.antennaTop(), r.tuning.fm()) * RadioNetwork.jamming(e.pos(), rp, arrayList2)) > best)) continue;
                    best = q;
                    src = e;
                }
                if (src == null || !(best >= 0.2f)) continue;
                done.add(r);
                r.relaying = true;
                arrayList4.add(new Emitter(((Emitter)src).root(), r.radioPos(), r.antennaTop(), r.tuning, r.range, Math.min(1.0f, (0.5f + 0.5f * best) * r.antQ), ((Emitter)src).name()));
            }
            if (arrayList4.isEmpty()) break;
            arrayList.addAll(arrayList4);
            ++var12_56;
        }
        for (RelayBE relayBE : l.relays) {
            if (done.contains(relayBE)) continue;
            relayBE.relaying = false;
        }
        HashMap<Long, Export> hashMap = new HashMap<Long, Export>();
        HashMap<DimensionalRelayBE, Integer> hashMap2 = new HashMap<DimensionalRelayBE, Integer>();
        for (DimensionalRelayBE d : arrayList3) {
            BlockPos dp = d.radioPos();
            for (Emitter emitter : arrayList) {
                float q;
                if (hashSet2.contains(emitter.root())) continue;
                float f = q = emitter.creative() ? emitter.baseQ() : RadioNetwork.quality(Math.sqrt(dp.distSqr((Vec3i)emitter.pos())), emitter.rangeTo(dp), emitter.tuning().fm(), emitter.baseQ(), d.rxGain()) * RadioNetwork.terrain((Level)level, emitter.pos(), emitter.topY(), dp, d.antennaTop(), emitter.tuning().fm()) * RadioNetwork.jamming(emitter.pos(), dp, arrayList2);
                if (q < 0.2f) continue;
                hashMap2.merge(d, 1, Integer::sum);
                Export old = (Export)hashMap.get(emitter.root());
                if (old != null && !(old.q() < q)) continue;
                hashMap.put(emitter.root(), new Export(emitter.root(), emitter.tuning(), q, emitter.name()));
            }
        }
        l.exports = new ArrayList(hashMap.values());
        for (DimensionalRelayBE d : l.links) {
            int n2;
            List p = d.active ? partners.getOrDefault((Object)d, List.of()) : List.of();
            int s1 = d.active ? hashMap2.getOrDefault((Object)d, 0) : 0;
            int n3 = n2 = d.active ? n : 0;
            if (p.equals(d.linked) && s1 == d.sending && n2 == d.bringing) continue;
            d.linked = new ArrayList<String>(p);
            d.sending = s1;
            d.bringing = n2;
            d.sendData();
        }
        l.emitters = new ArrayList<Emitter>(arrayList);
        for (TunableBE s : l.sinks) {
            s.updateSignal(arrayList, arrayList2);
        }
        for (ServerPlayer p : level.players()) {
            RadioNetwork.updateHandheld(p, level, arrayList, arrayList2, false);
        }
        NeoForge.EVENT_BUS.post((Event)new RadioNetworkTickEvent(level, l.emitters));
    }

    public static void updateHandheld(ServerPlayer p, ServerLevel level, List<Emitter> em, List<BlockPos> jam, boolean force) {
        ItemStack st = RadioNetwork.findRadio(p);
        HH old = HANDHELD.get(p.getUUID());
        if (st == null) {
            if (old != null || force) {
                PacketDistributor.sendToPlayer((ServerPlayer)p, (CustomPacketPayload)new HandheldStatePayload(false, false, 0.0f, false, ""), (CustomPacketPayload[])new CustomPacketPayload[0]);
            }
            HANDHELD.remove(p.getUUID());
            return;
        }
        Tuning t = (Tuning)st.getOrDefault((DataComponentType)ModRegistry.TUNING.get(), (Object)Tuning.DEFAULT);
        Best b = RadioNetwork.best((Level)level, p.blockPosition(), p.getEyeY(), t, 0.9, em, jam);
        HH nw = new HH(b.root(), b.q(), t.fm(), b.q() > 0.04f);
        if (force || old == null || old.carrier() != nw.carrier() || old.fm() != nw.fm() || Math.round(old.q() * 50.0f) != Math.round(nw.q() * 50.0f)) {
            PacketDistributor.sendToPlayer((ServerPlayer)p, (CustomPacketPayload)new HandheldStatePayload(true, nw.carrier(), nw.q(), nw.fm(), t.display()), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
        HANDHELD.put(p.getUUID(), nw);
    }

    public static float refreshHandheld(ServerPlayer p) {
        Level level = p.level();
        if (!(level instanceof ServerLevel)) {
            return 0.0f;
        }
        ServerLevel sl = (ServerLevel)level;
        Lvl l = LEVELS.get(sl.dimension());
        RadioNetwork.updateHandheld(p, sl, l == null ? List.of() : l.emitters, l == null ? List.of() : l.jam, true);
        HH h = HANDHELD.get(p.getUUID());
        return h == null ? 0.0f : h.q();
    }

    private static ItemStack findRadio(ServerPlayer p) {
        ItemStack off = p.getOffhandItem();
        if (off.getItem() instanceof PortableRadioItem && PortableRadioItem.isOn(off)) {
            return off;
        }
        for (int i = 0; i < 9; ++i) {
            ItemStack s = p.getInventory().getItem(i);
            if (!(s.getItem() instanceof PortableRadioItem) || !PortableRadioItem.isOn(s)) continue;
            return s;
        }
        return null;
    }

    public static void onVoice(ServerPlayer p, byte[] data) {
        Level level = p.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel sl = (ServerLevel)level;
        Lvl l = RadioNetwork.lvl((Level)sl);
        for (MicrophoneBE m : l.mics) {
            TapePlayerBE tp;
            if (!(m instanceof TapePlayerBE) || !(tp = (TapePlayerBE)m).isRecording() || tp.isRemoved() || !(SableCompat.distance((Level)sl, m.getBlockPos().getCenter(), p.getEyePosition()) <= 6.0)) continue;
            tp.captureVoice(data);
        }
        if (RadioNetwork.walkieTalkie(p, sl, l, data)) {
            return;
        }
        RadioNetwork.voiceFromPoint(sl, p.getEyePosition(), p.getUUID(), data);
    }

    public static void voiceFromPoint(ServerLevel sl, Vec3 at, UUID talker, byte[] data) {
        Lvl l = RadioNetwork.lvl((Level)sl);
        TransmitterBE src = null;
        float gain = 0.0f;
        double bd = 3.5;
        for (MicrophoneBE m : l.mics) {
            double d;
            if (m instanceof TapePlayerBE || m.feeds == null || !m.feeds.active || m.feeds.isRemoved() || !((d = SableCompat.distance((Level)sl, m.getBlockPos().getCenter(), at)) <= bd)) continue;
            bd = d;
            src = m.feeds;
            gain = (float)(1.0 - 0.7 * (d / 3.5)) * m.line;
        }
        if (src != null) {
            for (MicrophoneBE m : l.mics) {
                if (m.feeds != src || m instanceof TapePlayerBE || !(SableCompat.distance((Level)sl, m.getBlockPos().getCenter(), at) <= 3.5)) continue;
                long now = sl.getGameTime();
                if (now - m.lastPulse < 6L) break;
                m.lastPulse = now;
                RadioNetwork.pulse(sl, m.path);
                break;
            }
        }
        if (src == null) {
            double bb = 2.5;
            for (TransmitterBE t : l.tx) {
                double d;
                if (!t.active || t.wired || !((d = SableCompat.distance((Level)sl, t.getBlockPos().getCenter(), at)) <= bb)) continue;
                bb = d;
                src = t;
                gain = (float)(0.6 * (1.0 - 0.5 * d / 2.5));
            }
        }
        if (src == null) {
            return;
        }
        src.onAir(sl);
        RadioNetwork.broadcast(sl, src.getBlockPos().asLong(), talker, data, gain);
    }

    public static boolean broadcast(ServerLevel sl, long root, UUID talker, byte[] data, float gain) {
        Lvl l = LEVELS.get(sl.dimension());
        if (l == null) {
            return false;
        }
        if (((VoiceBroadcastEvent)NeoForge.EVENT_BUS.post((Event)new VoiceBroadcastEvent(sl, root, talker, data))).isCanceled()) {
            return false;
        }
        RadioNetwork.deliverAll(sl, l, root, talker, data, gain);
        if (l.linkActive) {
            long cross = RadioNetwork.crossRoot(root, (ResourceKey<Level>)sl.dimension());
            for (Map.Entry<ResourceKey<Level>, Lvl> o : LEVELS.entrySet()) {
                ServerLevel ol;
                if (o.getKey() == sl.dimension() || !o.getValue().linkActive || (ol = sl.getServer().getLevel(o.getKey())) == null) continue;
                RadioNetwork.deliverAll(ol, o.getValue(), cross, talker, data, gain);
            }
        }
        for (Map.Entry<UUID, HH> e : HANDHELD.entrySet()) {
            ServerPlayer lp;
            HH h = e.getValue();
            if (!h.carrier() || e.getKey().equals(talker) || h.root() != root && (!l.linkActive || h.root() != RadioNetwork.crossRoot(root, (ResourceKey<Level>)sl.dimension())) || (lp = sl.getServer().getPlayerList().getPlayer(e.getKey())) == null) continue;
            PacketDistributor.sendToPlayer((ServerPlayer)lp, (CustomPacketPayload)new HandheldAudioPayload(talker, data, h.q(), h.fm(), gain), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
        return true;
    }

    public static void onWorldSound(ServerLevel sl, Vec3 at, ResourceLocation id, float vol, float pitch) {
        Lvl l = LEVELS.get(sl.dimension());
        if (l == null || l.mics.isEmpty()) {
            return;
        }
        HashMap<TransmitterBE, Double> best = new HashMap<TransmitterBE, Double>();
        for (MicrophoneBE m : l.mics) {
            Double o;
            double d;
            if (m.isRemoved() || (d = SableCompat.distance((Level)sl, m.getBlockPos().getCenter(), at)) > 8.0) continue;
            if (m instanceof TapePlayerBE) {
                TapePlayerBE tp = (TapePlayerBE)m;
                tp.capture(id, vol, pitch, (byte)0);
                continue;
            }
            if (m.feeds == null || !m.feeds.active || m.feeds.isRemoved() || (o = (Double)best.get(m.feeds)) != null && !(d < o)) continue;
            best.put(m.feeds, d);
        }
        long now = sl.getGameTime();
        for (Map.Entry e : best.entrySet()) {
            TransmitterBE t = (TransmitterBE)e.getKey();
            long[] rate = SOUND_RATE.computeIfAbsent(t.getBlockPos().asLong(), k -> new long[2]);
            if (rate[0] != now / 20L) {
                rate[0] = now / 20L;
                rate[1] = 0L;
            }
            if ((rate[1] = rate[1] + 1L) > 24L) continue;
            float gain = (float)(1.0 - 0.6 * ((Double)e.getValue() / 8.0));
            t.onAir(sl);
            RadioNetwork.broadcastSound(sl, t.getBlockPos().asLong(), id, vol * gain, pitch, (byte)0, 0L);
        }
    }

    public static void broadcastSound(ServerLevel sl, long root, ResourceLocation id, float vol, float pitch, byte kind, long key) {
        Lvl l = LEVELS.get(sl.dimension());
        if (l == null) {
            return;
        }
        RadioNetwork.deliverSound(sl, l, root, id, vol, pitch, kind, key);
        if (l.linkActive) {
            long cross = RadioNetwork.crossRoot(root, (ResourceKey<Level>)sl.dimension());
            for (Map.Entry<ResourceKey<Level>, Lvl> o : LEVELS.entrySet()) {
                ServerLevel ol;
                if (o.getKey() == sl.dimension() || !o.getValue().linkActive || (ol = sl.getServer().getLevel(o.getKey())) == null) continue;
                RadioNetwork.deliverSound(ol, o.getValue(), cross, id, vol, pitch, kind, key);
            }
        }
        for (Map.Entry<UUID, HH> e : HANDHELD.entrySet()) {
            ServerPlayer lp;
            HH h = e.getValue();
            if (!h.carrier() || h.root() != root && (!l.linkActive || h.root() != RadioNetwork.crossRoot(root, (ResourceKey<Level>)sl.dimension())) || (lp = sl.getServer().getPlayerList().getPlayer(e.getKey())) == null) continue;
            PacketDistributor.sendToPlayer((ServerPlayer)lp, (CustomPacketPayload)new RadioSoundPayload(BlockPos.ZERO, true, id, vol * 0.8f, pitch, h.q(), h.fm(), kind, key), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
    }

    private static void deliverSound(ServerLevel sl, Lvl l, long root, ResourceLocation id, float vol, float pitch, byte kind, long key) {
        for (TunableBE s : l.sinks) {
            ReceiverBE rx;
            RadioBE r;
            if (!s.carrier || s.bestRoot != root) continue;
            if (s instanceof RadioBE && (r = (RadioBE)s).sinkOn()) {
                RadioNetwork.sendSound(sl, r.getBlockPos(), id, vol, pitch, r.quality, r.tuning.fm(), kind, key);
                continue;
            }
            if (!(s instanceof ReceiverBE) || !(rx = (ReceiverBE)s).sinkOn()) continue;
            if (rx.speakers.isEmpty()) {
                RadioNetwork.sendSound(sl, rx.getBlockPos(), id, vol * 0.6f, pitch, rx.quality * 0.92f, rx.tuning.fm(), kind, key);
                continue;
            }
            for (SpeakerBE sp : rx.speakers) {
                RadioNetwork.sendSound(sl, sp.getBlockPos(), id, vol, pitch, rx.quality * rx.lineQ.getOrDefault(sp, Float.valueOf(1.0f)).floatValue(), rx.tuning.fm(), kind, key);
            }
        }
    }

    public static void sendSound(ServerLevel lv, BlockPos pos, ResourceLocation id, float vol, float pitch, float q, boolean fm, byte kind, long key) {
        Vec3 g = SableCompat.global((Level)lv, pos);
        PacketDistributor.sendToPlayersNear((ServerLevel)lv, null, (double)g.x, (double)g.y, (double)g.z, (double)(kind == 0 ? 32.0 : 72.0), (CustomPacketPayload)new RadioSoundPayload(pos, false, id, vol, pitch, q, fm, kind, key), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public static List<Emitter> emitters(Level lv) {
        Lvl l = LEVELS.get(lv.dimension());
        return l == null ? List.of() : Collections.unmodifiableList(l.emitters);
    }

    public static List<BlockEntity> tunedNear(Level lv, BlockPos pos, int radius) {
        Lvl l = LEVELS.get(lv.dimension());
        ArrayList<BlockEntity> out = new ArrayList<BlockEntity>();
        if (l == null) {
            return out;
        }
        double r2 = (double)radius * (double)radius;
        for (BlockEntity blockEntity : l.tx) {
            if (blockEntity.isRemoved() || !(blockEntity.getBlockPos().distSqr((Vec3i)pos) <= r2)) continue;
            out.add(blockEntity);
        }
        for (BlockEntity blockEntity : l.relays) {
            if (blockEntity.isRemoved() || !(blockEntity.getBlockPos().distSqr((Vec3i)pos) <= r2)) continue;
            out.add(blockEntity);
        }
        for (BlockEntity blockEntity : l.sinks) {
            if (blockEntity.isRemoved() || !(blockEntity.getBlockPos().distSqr((Vec3i)pos) <= r2)) continue;
            out.add(blockEntity);
        }
        return out;
    }

    public static boolean onAirNear(Level lv, BlockPos pos, int radius) {
        Lvl l = LEVELS.get(lv.dimension());
        if (l == null) {
            return false;
        }
        double r2 = (double)radius * (double)radius;
        for (TransmitterBE t : l.tx) {
            BlockState st;
            if (t.isRemoved() || t.getBlockPos().distSqr((Vec3i)pos) > r2 || !(st = t.getBlockState()).hasProperty((Property)TransmitterBlock.ON_AIR) || !((Boolean)st.getValue((Property)TransmitterBlock.ON_AIR)).booleanValue()) continue;
            return true;
        }
        return false;
    }

    public static List<BlockPos> jammers(Level lv) {
        Lvl l = LEVELS.get(lv.dimension());
        return l == null ? List.of() : Collections.unmodifiableList(l.jam);
    }

    public static ItemStack heldRadio(Player p) {
        for (InteractionHand h : InteractionHand.values()) {
            ItemStack s = p.getItemInHand(h);
            if (!(s.getItem() instanceof PortableRadioItem) || !PortableRadioItem.isOn(s)) continue;
            return s;
        }
        return null;
    }

    private static boolean walkieTalkie(ServerPlayer p, ServerLevel sl, Lvl l, byte[] data) {
        float q;
        ItemStack held = RadioNetwork.heldRadio((Player)p);
        if (held == null) {
            return false;
        }
        Tuning t = (Tuning)held.getOrDefault((DataComponentType)ModRegistry.TUNING.get(), (Object)Tuning.DEFAULT);
        double reach = t.fm() ? 320.0 : 480.0;
        Vec3 from = p.getEyePosition();
        float gain = 0.75f;
        for (ServerPlayer o : sl.players()) {
            ItemStack r;
            if (o == p || (r = RadioNetwork.findRadio(o)) == null || !((Tuning)r.getOrDefault((DataComponentType)ModRegistry.TUNING.get(), (Object)Tuning.DEFAULT)).matches(t) || !((q = RadioNetwork.walkieQuality(sl, l, from, o.getEyePosition(), reach, t.fm(), 0.9)) > 0.04f)) continue;
            PacketDistributor.sendToPlayer((ServerPlayer)o, (CustomPacketPayload)new HandheldAudioPayload(p.getUUID(), data, q, t.fm(), gain), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
        for (TunableBE s : l.sinks) {
            ReceiverBE rx;
            RadioBE r;
            Vec3 at;
            if (!s.tuning.matches(t) || (q = RadioNetwork.walkieQuality(sl, l, from, at = SableCompat.global((Level)sl, s.getBlockPos()), reach, t.fm(), s.rxGain())) <= 0.04f) continue;
            if (s instanceof RadioBE && (r = (RadioBE)s).sinkOn()) {
                RadioNetwork.send(sl, r.getBlockPos(), p.getUUID(), data, q, t.fm(), gain);
                continue;
            }
            if (!(s instanceof ReceiverBE) || !(rx = (ReceiverBE)s).sinkOn()) continue;
            if (rx.speakers.isEmpty()) {
                RadioNetwork.send(sl, rx.getBlockPos(), p.getUUID(), data, q * 0.92f, t.fm(), gain * 0.6f);
                continue;
            }
            for (SpeakerBE sp : rx.speakers) {
                RadioNetwork.send(sl, sp.getBlockPos(), p.getUUID(), data, q * rx.lineQ.getOrDefault(sp, Float.valueOf(1.0f)).floatValue(), t.fm(), gain);
            }
        }
        long now = sl.getGameTime();
        Long last = WALKIE_SHOWN.get(p.getUUID());
        if (last == null || now - last > 40L) {
            WALKIE_SHOWN.put(p.getUUID(), now);
            p.displayClientMessage((Component)Component.literal((String)("\u25cf Talking on " + t.display())).withStyle(ChatFormatting.RED), true);
        }
        return true;
    }

    private static float walkieQuality(ServerLevel sl, Lvl l, Vec3 a, Vec3 b, double reach, boolean fm, double gain) {
        float q = RadioNetwork.quality(a.distanceTo(b), reach, fm, 0.92f, gain);
        for (BlockPos j : l.jam) {
            double d = RadioNetwork.segmentDistance(a.x, a.y, a.z, b.x, b.y, b.z, (double)j.getX() + 0.5, (double)j.getY() + 0.5, (double)j.getZ() + 0.5);
            if (!(d < 48.0)) continue;
            q *= (float)(0.08 + 0.92 * Math.pow(d / 48.0, 1.5));
        }
        return q;
    }

    private static void deliverAll(ServerLevel sl, Lvl l, long root, UUID talker, byte[] data, float gain) {
        long now = System.currentTimeMillis();
        TALKING.put(root, new Talk(talker, now));
        for (TunableBE s : l.sinks) {
            ReceiverBE rx;
            RadioBE r;
            if (!s.carrier || s.bestRoot != root) continue;
            float clash = RadioNetwork.clash(sl, l, s, root, talker, now);
            if (s instanceof RadioBE && (r = (RadioBE)s).sinkOn()) {
                RadioNetwork.send(sl, r.getBlockPos(), talker, data, r.quality * clash, r.tuning.fm(), gain);
                continue;
            }
            if (!(s instanceof ReceiverBE) || !(rx = (ReceiverBE)s).sinkOn()) continue;
            if (rx.speakers.isEmpty()) {
                RadioNetwork.send(sl, rx.getBlockPos(), talker, data, rx.quality * clash * 0.92f, rx.tuning.fm(), gain * 0.6f);
            } else {
                for (SpeakerBE sp : rx.speakers) {
                    RadioNetwork.send(sl, sp.getBlockPos(), talker, data, rx.quality * clash * rx.lineQ.getOrDefault(sp, Float.valueOf(1.0f)).floatValue(), rx.tuning.fm(), gain);
                }
            }
            if (sl.getGameTime() - rx.lastPulse < 6L || rx.paths.isEmpty()) continue;
            rx.lastPulse = sl.getGameTime();
            RadioNetwork.pulse(sl, rx.paths);
        }
    }

    private static float clash(ServerLevel sl, Lvl l, TunableBE s, long root, UUID talker, long now) {
        float own = s.quality;
        if (own <= 0.0f) {
            return 1.0f;
        }
        float worst = 0.0f;
        BlockPos at = SableCompat.globalPos((Level)sl, s.getBlockPos());
        for (Emitter e : l.emitters) {
            Talk t;
            if (e.root() == root || !e.tuning().matches(s.tuning) || (t = TALKING.get(e.root())) == null || now - t.at() > 150L || t.talker().equals(talker)) continue;
            float q = e.creative() ? e.baseQ() : RadioNetwork.quality(Math.sqrt(at.distSqr((Vec3i)e.pos())), e.rangeTo(at), s.tuning.fm(), e.baseQ(), s.rxGain());
            worst = Math.max(worst, q);
        }
        if (worst <= 0.02f) {
            return 1.0f;
        }
        float ratio = worst / own;
        return ratio >= 1.0f ? 0.08f : Math.max(0.08f, 1.0f - 0.9f * ratio);
    }

    public static void pulse(ServerLevel lv, List<Cables.Seg> segs) {
        if (segs.isEmpty()) {
            return;
        }
        int n = Math.min(512, segs.size());
        long[] f = new long[n];
        long[] t = new long[n];
        int[] fs = new int[n];
        int[] ts = new int[n];
        for (int i = 0; i < n; ++i) {
            Cables.Seg sg = segs.get(i);
            f[i] = sg.from().pos().asLong();
            fs[i] = sg.from().slot();
            t[i] = sg.to().pos().asLong();
            ts[i] = sg.to().slot();
        }
        Vec3 at = SableCompat.global((Level)lv, segs.get(0).from().pos());
        PacketDistributor.sendToPlayersNear((ServerLevel)lv, null, (double)at.x, (double)at.y, (double)at.z, (double)160.0, (CustomPacketPayload)new CablePulsePayload(f, fs, t, ts), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public static void send(ServerLevel lv, BlockPos pos, UUID talker, byte[] data, float q, boolean fm, float gain) {
        Vec3 g = SableCompat.global((Level)lv, pos);
        PacketDistributor.sendToPlayersNear((ServerLevel)lv, null, (double)g.x, (double)g.y, (double)g.z, (double)26.0, (CustomPacketPayload)new RadioAudioPayload(pos, talker, data, q, fm, gain), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public static List<StationInfo> spectrum(ServerPlayer p) {
        Lvl l = LEVELS.get(p.level().dimension());
        if (l == null) {
            return List.of();
        }
        HashMap<CallSite, StationInfo> m = new HashMap<CallSite, StationInfo>();
        for (Emitter e : l.emitters) {
            String key;
            StationInfo old;
            float q = RadioNetwork.best(p.level(), p.blockPosition(), p.getEyeY() + 2.0, e.tuning(), 1.0, List.of(e), l.jam).q();
            if (q < 0.02f || (old = (StationInfo)m.get(key = e.tuning().fm() + ":" + Math.round(e.tuning().freq() * 100.0))) != null && !(old.quality() < q)) continue;
            m.put((CallSite)((Object)key), new StationInfo(e.tuning().fm(), e.tuning().freq(), q, e.name()));
        }
        ArrayList<StationInfo> out = new ArrayList<StationInfo>(m.values());
        out.sort(Comparator.comparingDouble(StationInfo::freq));
        return out.size() > 60 ? out.subList(0, 60) : out;
    }

    public static void lightning(ServerLevel sl, Vec3 at) {
        for (ServerPlayer p : sl.players()) {
            double d = p.position().distanceTo(at);
            float strength = (float)Mth.clamp((double)(1.0 - d / 1200.0), (double)0.12, (double)1.0);
            PacketDistributor.sendToPlayer((ServerPlayer)p, (CustomPacketPayload)new StaticBurstPayload(strength), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
    }

    private static final class Lvl {
        final Set<TransmitterBE> tx = new LinkedHashSet<TransmitterBE>();
        final Set<RelayBE> relays = new LinkedHashSet<RelayBE>();
        final Set<TunableBE> sinks = new LinkedHashSet<TunableBE>();
        final Set<MicrophoneBE> mics = new LinkedHashSet<MicrophoneBE>();
        final Set<JammerBE> jammers = new LinkedHashSet<JammerBE>();
        final Set<SpeakerBE> speakers = new LinkedHashSet<SpeakerBE>();
        final Set<DimensionalRelayBE> links = new LinkedHashSet<DimensionalRelayBE>();
        List<Emitter> emitters = new ArrayList<Emitter>();
        List<BlockPos> jam = new ArrayList<BlockPos>();
        List<Export> exports = new ArrayList<Export>();
        boolean linkActive;

        private Lvl() {
        }
    }

    public record Best(float q, long root) {
    }

    private record Export(long root, Tuning tuning, float q, String name) {
    }

    private record HH(long root, float q, boolean fm, boolean carrier) {
    }

    private record Talk(UUID talker, long at) {
    }
}
