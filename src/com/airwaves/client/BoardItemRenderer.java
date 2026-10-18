/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 */
package com.airwaves.client;

import com.airwaves.client.BoardIcons;
import com.airwaves.item.BoardItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BoardItemRenderer
extends BlockEntityWithoutLevelRenderer {
    public BoardItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public void renderByItem(ItemStack st, ItemDisplayContext ctx, PoseStack ps, MultiBufferSource buf, int light, int overlay) {
        ResourceLocation tex = BoardIcons.texture(BoardItem.layout(st));
        VertexConsumer vc = buf.getBuffer(RenderType.entityCutout((ResourceLocation)tex));
        PoseStack.Pose p = ps.last();
        float zf = 0.53125f;
        float zb = 0.46875f;
        BoardItemRenderer.v(vc, p, 0.0f, 0.0f, zf, 0.0f, 1.0f, light, overlay, 0.0f, 0.0f, 1.0f);
        BoardItemRenderer.v(vc, p, 1.0f, 0.0f, zf, 1.0f, 1.0f, light, overlay, 0.0f, 0.0f, 1.0f);
        BoardItemRenderer.v(vc, p, 1.0f, 1.0f, zf, 1.0f, 0.0f, light, overlay, 0.0f, 0.0f, 1.0f);
        BoardItemRenderer.v(vc, p, 0.0f, 1.0f, zf, 0.0f, 0.0f, light, overlay, 0.0f, 0.0f, 1.0f);
        BoardItemRenderer.v(vc, p, 0.0f, 1.0f, zb, 0.0f, 0.0f, light, overlay, 0.0f, 0.0f, -1.0f);
        BoardItemRenderer.v(vc, p, 1.0f, 1.0f, zb, 1.0f, 0.0f, light, overlay, 0.0f, 0.0f, -1.0f);
        BoardItemRenderer.v(vc, p, 1.0f, 0.0f, zb, 1.0f, 1.0f, light, overlay, 0.0f, 0.0f, -1.0f);
        BoardItemRenderer.v(vc, p, 0.0f, 0.0f, zb, 0.0f, 1.0f, light, overlay, 0.0f, 0.0f, -1.0f);
    }

    private static void v(VertexConsumer vc, PoseStack.Pose p, float x, float y, float z, float u, float v, int light, int overlay, float nx, float ny, float nz) {
        vc.addVertex(p, x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(overlay).setLight(light).setNormal(p, nx, ny, nz);
    }
}
