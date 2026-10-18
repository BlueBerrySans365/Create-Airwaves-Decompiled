/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package com.airwaves.client;

import com.airwaves.be.CableNode;
import com.airwaves.cable.Link;
import com.airwaves.client.CablePulses;
import com.airwaves.compat.SableCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CableRenderer<T extends BlockEntity>
implements BlockEntityRenderer<T> {
    public static final ResourceLocation WHITE = ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"textures/misc/cable.png");
    public static final ResourceLocation TEX_PULSE = ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"textures/misc/cable_pulse.png");
    private static final Map<BlockEntity, Bounds> BOUNDS = new WeakHashMap<BlockEntity, Bounds>();

    public static Vec3 anchor(Level lv, BlockPos pos, int slot) {
        BlockEntity blockEntity;
        if (lv.isLoaded(pos) && (blockEntity = lv.getBlockEntity(pos)) instanceof CableNode) {
            CableNode n = (CableNode)blockEntity;
            return n.anchor(Math.min(slot, n.connectors() - 1));
        }
        return Vec3.atCenterOf((Vec3i)pos);
    }

    public void render(T be, float pt, PoseStack ps, MultiBufferSource buf, int light, int overlay) {
        CableRenderer.renderCables(be, ps, buf, pt);
    }

    public static void renderCables(BlockEntity be, PoseStack ps, MultiBufferSource buf, float pt) {
        Level lv = be.getLevel();
        if (lv == null || !(be instanceof CableNode)) {
            return;
        }
        CableNode node = (CableNode)be;
        BlockPos o = be.getBlockPos();
        for (Link l : node.links().list()) {
            CableNode n2;
            BlockEntity blockEntity;
            boolean otherDraws;
            boolean bl = otherDraws = l.other().asLong() < o.asLong() && lv.isLoaded(l.other()) && (blockEntity = lv.getBlockEntity(l.other())) instanceof CableNode && (n2 = (CableNode)blockEntity).links().has(l.otherSlot(), o, l.slot()) && SableCompat.sameSpace(o, l.other());
            if (otherDraws) continue;
            Vec3 aw = node.anchor(l.slot());
            Vec3 bw = CableRenderer.anchor(lv, l.other(), l.otherSlot());
            if (!SableCompat.sameSpace(o, l.other())) {
                bw = SableCompat.intoRenderSpaceOf(o, SableCompat.rendered(bw, pt), pt);
            }
            Vec3 a = aw.subtract((double)o.getX(), (double)o.getY(), (double)o.getZ());
            Vec3 b = bw.subtract((double)o.getX(), (double)o.getY(), (double)o.getZ());
            int la = LevelRenderer.getLightColor((BlockAndTintGetter)lv, (BlockPos)BlockPos.containing((Position)aw));
            int lb = LevelRenderer.getLightColor((BlockAndTintGetter)lv, (BlockPos)BlockPos.containing((Position)bw));
            CableRenderer.line(ps, buf, a, b, la, lb, l.type().color, l.type().width);
            int dir = CablePulses.dir(o.asLong(), l.slot(), l.other().asLong(), l.otherSlot());
            if (dir == 0) continue;
            CableRenderer.pulse(ps, buf, dir > 0 ? a : b, dir > 0 ? b : a, l.type().width * 1.6f);
        }
    }

    public static void renderCables(BlockEntity be, PoseStack ps, MultiBufferSource buf) {
        CableRenderer.renderCables(be, ps, buf, 0.0f);
    }

    static Vec3 at(Vec3 a, Vec3 d, double sag, double u) {
        return new Vec3(a.x + d.x * u, a.y + d.y * u - sag * 4.0 * u * (1.0 - u), a.z + d.z * u);
    }

    static double sag(double len) {
        return Math.min(2.4, 0.045 * len + 0.06);
    }

    public static void line(PoseStack ps, MultiBufferSource buf, Vec3 a, Vec3 b, int lightA, int lightB, int argb, float w) {
        Vec3 d = b.subtract(a);
        double len = d.length();
        if (len < 0.001) {
            return;
        }
        int n = Math.max(4, Math.min(24, (int)(len * 1.25)));
        double sag = CableRenderer.sag(len);
        double hx = -d.z;
        double hz = d.x;
        double hl = Math.sqrt(hx * hx + hz * hz);
        Vec3 side = hl < 1.0E-4 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(hx / hl, 0.0, hz / hl);
        float r = (float)(argb >> 16 & 0xFF) / 255.0f;
        float g = (float)(argb >> 8 & 0xFF) / 255.0f;
        float bl = (float)(argb & 0xFF) / 255.0f;
        VertexConsumer vc = buf.getBuffer(RenderType.entitySolid((ResourceLocation)WHITE));
        PoseStack.Pose pose = ps.last();
        Vec3 prev = a;
        for (int i = 1; i <= n; ++i) {
            double u = (double)i / (double)n;
            Vec3 p = CableRenderer.at(a, d, sag, u);
            Vec3 seg = p.subtract(prev);
            double sl = seg.length();
            Vec3 up = hl < 1.0E-4 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
            Vec3 wv = up.subtract(seg.scale(up.dot(seg) / Math.max(1.0E-9, sl * sl))).normalize().scale((double)(w / 2.0f));
            Vec3 wh = side.scale((double)(w / 2.0f));
            int la = CableRenderer.mix(lightA, lightB, (double)(i - 1) / (double)n);
            int lb = CableRenderer.mix(lightA, lightB, u);
            CableRenderer.quad(vc, pose, prev.subtract(wv), prev.add(wv), p.add(wv), p.subtract(wv), r * 0.86f, g * 0.86f, bl * 0.86f, la, lb, (float)side.x, 0.0f, (float)side.z);
            CableRenderer.quad(vc, pose, prev.subtract(wh), prev.add(wh), p.add(wh), p.subtract(wh), r, g, bl, la, lb, 0.0f, 1.0f, 0.0f);
            prev = p;
        }
    }

    private static void quad(VertexConsumer vc, PoseStack.Pose pose, Vec3 a, Vec3 b, Vec3 c, Vec3 d, float r, float g, float bl, int la, int lb, float nx, float ny, float nz) {
        CableRenderer.v(vc, pose, a, r, g, bl, 1.0f, 0.0f, 1.0f, la, nx, ny, nz);
        CableRenderer.v(vc, pose, b, r, g, bl, 1.0f, 0.0f, 0.0f, la, nx, ny, nz);
        CableRenderer.v(vc, pose, c, r, g, bl, 1.0f, 1.0f, 0.0f, lb, nx, ny, nz);
        CableRenderer.v(vc, pose, d, r, g, bl, 1.0f, 1.0f, 1.0f, lb, nx, ny, nz);
        CableRenderer.v(vc, pose, d, r, g, bl, 1.0f, 1.0f, 1.0f, lb, -nx, -ny, -nz);
        CableRenderer.v(vc, pose, c, r, g, bl, 1.0f, 1.0f, 0.0f, lb, -nx, -ny, -nz);
        CableRenderer.v(vc, pose, b, r, g, bl, 1.0f, 0.0f, 0.0f, la, -nx, -ny, -nz);
        CableRenderer.v(vc, pose, a, r, g, bl, 1.0f, 0.0f, 1.0f, la, -nx, -ny, -nz);
    }

    static void pulse(PoseStack ps, MultiBufferSource buf, Vec3 from, Vec3 to, float w) {
        Vec3 d = to.subtract(from);
        double len = d.length();
        if (len < 0.001) {
            return;
        }
        double sag = CableRenderer.sag(len);
        double t = (double)(System.currentTimeMillis() % 100000L) / 1000.0;
        double speed = 9.0;
        double spacing = 3.0;
        double dash = 0.7;
        VertexConsumer vc = buf.getBuffer(RenderType.entityTranslucentEmissive((ResourceLocation)TEX_PULSE));
        PoseStack.Pose pose = ps.last();
        double hx = -d.z;
        double hz = d.x;
        double hl = Math.sqrt(hx * hx + hz * hz);
        float nx = hl < 1.0E-4 ? 1.0f : (float)(hx / hl);
        float nz = hl < 1.0E-4 ? 0.0f : (float)(hz / hl);
        double off = t * speed % spacing;
        for (double s0 = off - spacing; s0 < len; s0 += spacing) {
            double a0 = Math.max(0.0, s0);
            double a1 = Math.min(len, s0 + dash);
            if (a1 <= a0) continue;
            Vec3 p0 = CableRenderer.at(from, d, sag, a0 / len);
            Vec3 p1 = CableRenderer.at(from, d, sag, a1 / len);
            Vec3 seg = p1.subtract(p0);
            double sl = seg.length();
            if (sl < 1.0E-4) continue;
            Vec3 up = hl < 1.0E-4 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
            Vec3 wv = up.subtract(seg.scale(up.dot(seg) / (sl * sl))).normalize().scale((double)(w / 2.0f));
            int light = 0xF000F0;
            CableRenderer.v(vc, pose, p0.subtract(wv), 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, light, nx, 0.0f, nz);
            CableRenderer.v(vc, pose, p0.add(wv), 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, light, nx, 0.0f, nz);
            CableRenderer.v(vc, pose, p1.add(wv), 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, light, nx, 0.0f, nz);
            CableRenderer.v(vc, pose, p1.subtract(wv), 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, light, nx, 0.0f, nz);
        }
    }

    private static void v(VertexConsumer vc, PoseStack.Pose pose, Vec3 p, float r, float g, float b, float a, float u, float vv, int light, float nx, float ny, float nz) {
        vc.addVertex(pose, (float)p.x, (float)p.y, (float)p.z).setColor(r, g, b, a).setUv(u, vv).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, nx, ny, nz);
    }

    private static int mix(int la, int lb, double t) {
        int bl = (int)((double)LightTexture.block((int)la) + (double)(LightTexture.block((int)lb) - LightTexture.block((int)la)) * t);
        int sk = (int)((double)LightTexture.sky((int)la) + (double)(LightTexture.sky((int)lb) - LightTexture.sky((int)la)) * t);
        return LightTexture.pack((int)bl, (int)sk);
    }

    public boolean shouldRenderOffScreen(T be) {
        return false;
    }

    public int getViewDistance() {
        return 96;
    }

    public AABB getRenderBoundingBox(T be) {
        return CableRenderer.bounds(be);
    }

    public static AABB bounds(BlockEntity be) {
        long now = System.currentTimeMillis();
        Bounds c = BOUNDS.get(be);
        if (c != null && now - c.at() < 1000L) {
            return c.box();
        }
        AABB box = CableRenderer.computeBounds(be);
        BOUNDS.put(be, new Bounds(box, now));
        return box;
    }

    private static AABB computeBounds(BlockEntity be) {
        AABB b = new AABB(be.getBlockPos());
        Level lv = be.getLevel();
        if (be instanceof CableNode) {
            CableNode c = (CableNode)be;
            for (int i = 0; i < c.connectors(); ++i) {
                Vec3 a = c.anchor(i);
                b = b.minmax(new AABB(a, a));
            }
            for (Link l : c.links().list()) {
                if (!SableCompat.sameSpace(be.getBlockPos(), l.other())) continue;
                Vec3 a = lv == null ? Vec3.atCenterOf((Vec3i)l.other()) : CableRenderer.anchor(lv, l.other(), l.otherSlot());
                b = b.minmax(new AABB(a, a));
            }
        }
        return b.inflate(3.0);
    }

    private record Bounds(AABB box, long at) {
    }
}
