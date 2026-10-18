/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.ModelResourceLocation
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.data.ModelData
 */
package com.airwaves.client;

import com.airwaves.be.InsulatorBE;
import com.airwaves.client.CableRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.data.ModelData;

public class InsulatorRenderer
extends CableRenderer<InsulatorBE> {
    public static final ModelResourceLocation SPOOL = ModelResourceLocation.standalone((ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"block/insulator_spool"));
    public static final ModelResourceLocation SPOOL_WOUND = ModelResourceLocation.standalone((ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"airwaves", (String)"block/insulator_spool_wound"));
    private static final float PIVOT = 0.48125f;

    @Override
    public void render(InsulatorBE be, float pt, PoseStack ps, MultiBufferSource buf, int light, int overlay) {
        super.render(be, pt, ps, buf, light, overlay);
        Minecraft mc = Minecraft.getInstance();
        BakedModel model = mc.getModelManager().getModel(be.links().isEmpty() ? SPOOL : SPOOL_WOUND);
        float t = (float)(System.currentTimeMillis() - be.changedAt) / 1400.0f;
        float angle = t >= 1.0f || be.changedAt == 0L ? 0.0f : (1.0f - (1.0f - t) * (1.0f - t) * (1.0f - t)) * 720.0f;
        Direction f = be.getBlockState().hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING) ? (Direction)be.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
        ps.pushPose();
        ps.translate(0.5, 0.0, 0.5);
        ps.mulPose(Axis.YP.rotationDegrees(-f.toYRot() + 180.0f));
        ps.translate(-0.5, 0.0, -0.5);
        ps.translate(0.5, (double)0.48125f, 0.5);
        ps.mulPose(Axis.XP.rotationDegrees(angle));
        ps.translate(-0.5, (double)-0.48125f, -0.5);
        mc.getBlockRenderer().getModelRenderer().renderModel(ps.last(), buf.getBuffer(RenderType.cutout()), null, model, 1.0f, 1.0f, 1.0f, light, overlay, ModelData.EMPTY, RenderType.cutout());
        ps.popPose();
    }
}
