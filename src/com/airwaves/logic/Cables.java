/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.logic;

import com.airwaves.ModRegistry;
import com.airwaves.ModSounds;
import com.airwaves.be.CableNode;
import com.airwaves.be.MicrophoneBE;
import com.airwaves.be.ReceiverBE;
import com.airwaves.be.SpeakerBE;
import com.airwaves.be.TransmitterBE;
import com.airwaves.cable.CableType;
import com.airwaves.cable.Link;
import com.airwaves.cable.NodeRef;
import com.airwaves.compat.SableCompat;
import com.airwaves.item.CableItem;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public final class Cables {
    public static final int MAX_NODES = 512;

    private Cables() {
    }

    public static CableNode node(Level lv, BlockPos p) {
        CableNode n;
        BlockEntity blockEntity;
        return lv.isLoaded(p) && (blockEntity = lv.getBlockEntity(p)) instanceof CableNode ? (n = (CableNode)blockEntity) : null;
    }

    public static Item item(CableType t) {
        return switch (t) {
            case CableType.GOLD -> (CableItem)((Object)ModRegistry.GOLD_WIRE.get());
            case CableType.SILVER -> (CableItem)((Object)ModRegistry.SILVER_WIRE.get());
            case CableType.FIBER -> (CableItem)((Object)ModRegistry.FIBER_CABLE.get());
            default -> (CableItem)((Object)ModRegistry.COPPER_WIRE.get());
        };
    }

    public static boolean compatible(CableNode a, int sa, CableNode b, int sb) {
        if (a.junction(sa) || b.junction(sb)) {
            return true;
        }
        return a instanceof MicrophoneBE && b instanceof TransmitterBE || b instanceof MicrophoneBE && a instanceof TransmitterBE || a instanceof ReceiverBE && b instanceof SpeakerBE || b instanceof ReceiverBE && a instanceof SpeakerBE;
    }

    public static boolean canLink(Level lv, NodeRef a, NodeRef b, CableType t) {
        if (a.pos().equals((Object)b.pos())) {
            return false;
        }
        CableNode na = Cables.node(lv, a.pos());
        CableNode nb = Cables.node(lv, b.pos());
        if (na == null || nb == null) {
            return false;
        }
        if (a.slot() >= na.connectors() || b.slot() >= nb.connectors()) {
            return false;
        }
        if (SableCompat.distance(lv, na.anchor(a.slot()), nb.anchor(b.slot())) > (double)t.maxLen + 0.5) {
            return false;
        }
        if (!na.accepts(a.slot(), t) || !nb.accepts(b.slot(), t)) {
            return false;
        }
        if (na.links().has(a.slot(), b.pos(), b.slot())) {
            return false;
        }
        if (na.links().count(a.slot()) >= na.maxLinks(a.slot()) || nb.links().count(b.slot()) >= nb.maxLinks(b.slot())) {
            return false;
        }
        return Cables.compatible(na, a.slot(), nb, b.slot());
    }

    public static boolean link(Level lv, NodeRef a, NodeRef b, CableType t) {
        if (!Cables.canLink(lv, a, b, t)) {
            return false;
        }
        CableNode na = Cables.node(lv, a.pos());
        CableNode nb = Cables.node(lv, b.pos());
        na.links().add(new Link(a.slot(), b.pos(), b.slot(), t));
        nb.links().add(new Link(b.slot(), a.pos(), a.slot(), t));
        na.cablesChanged();
        nb.cablesChanged();
        return true;
    }

    public static List<ItemStack> unlink(Level lv, BlockPos p, int slot) {
        CableNode n = Cables.node(lv, p);
        ArrayList<ItemStack> out = new ArrayList<ItemStack>();
        if (n == null) {
            return out;
        }
        for (Link l : new ArrayList<Link>(n.links().list())) {
            if (slot >= 0 && l.slot() != slot) continue;
            CableNode m = Cables.node(lv, l.other());
            if (m != null && m.links().remove(l.otherSlot(), p, l.slot())) {
                m.cablesChanged();
            }
            n.links().remove(l.slot(), l.other(), l.otherSlot());
            out.add(new ItemStack((ItemLike)Cables.item(l.type())));
        }
        n.cablesChanged();
        return out;
    }

    public static void broken(Level lv, BlockPos p, List<Link> links) {
        for (Link l : links) {
            CableNode m = Cables.node(lv, l.other());
            if (m == null || !m.links().remove(l.otherSlot(), p, l.slot())) continue;
            m.cablesChanged();
        }
        for (Link l : links) {
            lv.addFreshEntity((Entity)new ItemEntity(lv, (double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5, new ItemStack((ItemLike)Cables.item(l.type()))));
        }
    }

    public static void checkStretch(Level lv, BlockPos p, CableNode n) {
        for (Link l : new ArrayList<Link>(n.links().list())) {
            double d;
            CableNode m = Cables.node(lv, l.other());
            if (m == null || (d = SableCompat.distance(lv, n.anchor(l.slot()), m.anchor(l.otherSlot()))) <= (double)l.type().maxLen * 1.25) continue;
            if (m.links().remove(l.otherSlot(), p, l.slot())) {
                m.cablesChanged();
            }
            n.links().remove(l.slot(), l.other(), l.otherSlot());
            n.cablesChanged();
            lv.addFreshEntity((Entity)new ItemEntity(lv, (double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5, new ItemStack((ItemLike)Cables.item(l.type()))));
            lv.playSound(null, p, (SoundEvent)ModSounds.CABLE_DETACH.get(), SoundSource.BLOCKS, 1.0f, 0.7f);
        }
    }

    static Vec3 anchorOf(Level lv, NodeRef r) {
        CableNode n = Cables.node(lv, r.pos());
        return n == null ? Vec3.atCenterOf((Vec3i)r.pos()) : n.anchor(r.slot());
    }

    public static <T> List<Hit<T>> reach(Level lv, BlockPos start, Class<T> type) {
        ArrayList<Hit<T>> out = new ArrayList<Hit<T>>();
        CableNode s = Cables.node(lv, start);
        if (s == null) {
            return out;
        }
        HashMap<NodeRef, Seg> parent = new HashMap<NodeRef, Seg>();
        HashSet<NodeRef> seen = new HashSet<NodeRef>();
        ArrayDeque<NodeRef> q = new ArrayDeque<NodeRef>();
        for (Link l : s.links().list()) {
            NodeRef far = l.far();
            if (!seen.add(far)) continue;
            parent.put(far, new Seg(new NodeRef(start, l.slot()), far, l.type()));
            q.add(far);
        }
        HashSet<BlockPos> found = new HashSet<BlockPos>();
        while (!q.isEmpty() && seen.size() < 512) {
            NodeRef r = (NodeRef)q.poll();
            CableNode n = Cables.node(lv, r.pos());
            if (n == null || r.pos().equals((Object)start)) continue;
            if (type.isInstance(n) && found.add(r.pos())) {
                LinkedList<Seg> path = new LinkedList<Seg>();
                NodeRef c = r;
                int guard = 0;
                while (parent.containsKey(c) && guard++ < 512) {
                    Seg sg = (Seg)parent.get(c);
                    path.addFirst(sg);
                    c = sg.from();
                    if (!c.pos().equals((Object)start)) continue;
                    break;
                }
                out.add(new Hit<T>(type.cast(n), path));
            }
            if (!n.junction(r.slot())) continue;
            for (Link l : n.links().list()) {
                NodeRef far;
                if (l.slot() != r.slot() || !seen.add(far = l.far())) continue;
                parent.put(far, new Seg(r, far, l.type()));
                q.add(far);
            }
        }
        return out;
    }

    public static <T> List<T> targets(List<Hit<T>> hits) {
        ArrayList<T> l = new ArrayList<T>();
        for (Hit<T> h : hits) {
            l.add(h.target());
        }
        return l;
    }

    public static boolean isNode(BlockEntity be) {
        return be instanceof CableNode;
    }

    public record Seg(NodeRef from, NodeRef to, CableType type) {
    }

    public record Hit<T>(T target, List<Seg> path) {
        public float lineQuality(Level lv) {
            double loss = 0.0;
            for (Seg s : this.path) {
                loss += s.type().loss * SableCompat.distance(lv, Cables.anchorOf(lv, s.from()), Cables.anchorOf(lv, s.to()));
            }
            return (float)Math.max(0.25, 1.0 - loss);
        }
    }
}
