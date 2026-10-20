/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.ModelResourceLocation
 *  net.minecraft.resources.ResourceLocation
 *  net.neoforged.neoforge.client.model.data.ModelData
 */
package com.airwaves.client;

import com.airwaves.be.YagiAntennaBE;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.data.ModelData;

public class YagiRenderer
implements BlockEntityRenderer<YagiAntennaBE> {
    public static final ModelResourceLocation ARMS = ModelResourceLocation.standalone((ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"block/yagi_antenna_arms"));
    public static final float PX = 7.8738f;
    public static final float PZ = 8.475f;

    public YagiRenderer(BlockEntityRendererProvider.Context c) {
    }

    public void render(YagiAntennaBE be, float pt, PoseStack ms, MultiBufferSource buf, int light, int overlay) {
        float diff;
        float target = be.bearing();
        if (Float.isNaN(be.shown)) {
            be.shown = target;
        }
        be.shown = Math.abs(diff = ((target - be.shown) % 360.0f + 540.0f) % 360.0f - 180.0f) < 0.05f ? target : be.shown + diff * 0.08f;
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(ARMS);
        ms.pushPose();
        ms.translate(0.4921125f, 0.0f, 0.5296875f);
        ms.mulPose(Axis.YP.rotationDegrees(-be.shown));
        ms.translate(-0.4921125f, 0.0f, -0.5296875f);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(ms.last(), buf.getBuffer(RenderType.cutout()), be.getBlockState(), model, 1.0f, 1.0f, 1.0f, light, overlay, ModelData.EMPTY, RenderType.cutout());
        ms.popPose();
    }

    public boolean shouldRenderOffScreen(YagiAntennaBE be) {
        return true;
    }
}
