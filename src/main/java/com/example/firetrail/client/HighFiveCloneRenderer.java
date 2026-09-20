package com.example.firetrail.client;

import com.example.firetrail.entity.HighFiveCloneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;

public class HighFiveCloneRenderer extends EntityRenderer<HighFiveCloneEntity> {
    public HighFiveCloneRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.35F;
    }

    @Override
    public void render(HighFiveCloneEntity clone, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Entity owner = clone.getOwnerEntity();
        if (owner == null) return;

        poseStack.pushPose();
        // The clone is a visual copy of the caster. The changing distance creates
        // the approach/contact/retreat motion of the high-five.
        Minecraft.getInstance().getEntityRenderDispatcher().render(
                owner,
                owner.getX() - clone.getX(),
                owner.getY() - clone.getY(),
                owner.getZ() - clone.getZ(),
                owner.getYRot(),
                partialTick,
                poseStack,
                buffer,
                packedLight);
        poseStack.popPose();
    }

    @Override
    public net.minecraft.resources.ResourceLocation getTextureLocation(HighFiveCloneEntity entity) {
        return new net.minecraft.resources.ResourceLocation("minecraft", "textures/misc/white.png");
    }
}
