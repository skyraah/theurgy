// SPDX-FileCopyrightText: 2023 klikli-dev
//
// SPDX-License-Identifier: MIT

package com.klikli_dev.theurgy.content.apparatus.salammoniactank.render;

import com.klikli_dev.theurgy.content.apparatus.incubator.IncubatorMercuryVesselBlockItem;
import com.klikli_dev.theurgy.content.apparatus.salammoniactank.SalAmmoniacTankBlockItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SalAmmoniacTankBEWLR extends GeoItemRenderer<SalAmmoniacTankBlockItem> {

    private static final SalAmmoniacTankBEWLR instance = new SalAmmoniacTankBEWLR();
    private final ItemTransform transform;

    public SalAmmoniacTankBEWLR() {
        super(new SalAmmoniacTankModel());
        this.withScale(0.5f);
        this.transform = new ItemTransform(new Vector3f(30, 255, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
    }

    public static SalAmmoniacTankBEWLR get() {
        return instance;
    }

    @Override
    public void preRender(PoseStack poseStack, SalAmmoniacTankBlockItem animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
//        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if (isReRender)
            return;
        //copied from parent, but fixed to also center the model when scaling
        this.itemRenderTranslations = new Matrix4f(poseStack.last().pose());

        if (this.scaleWidth != 1 && this.scaleHeight != 1) {
            poseStack.scale(this.scaleWidth, this.scaleHeight, this.scaleWidth);

            //this is not as clean as I would like it - but it exactly centers the model for 0.5 scale :D
            poseStack.translate(this.scaleWidth / 0.5 - 0.5, -0.1, this.scaleWidth / 0.5 - 0.5);
        }

        poseStack.translate(0.5f, 0.51f, 0.5f);

        if (this.renderPerspective == ItemDisplayContext.GUI) {
            this.transform.apply(false, poseStack);
        }
    }
}
