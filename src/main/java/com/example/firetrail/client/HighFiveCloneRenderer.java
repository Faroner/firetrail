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

        // Render the owner's model AT THE CLONE'S POSITION.
        // The old code used owner - clone coordinates, which moved the model
        // back onto/inside the player instead of rendering it where the clone is.
        var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double renderX = clone.getX() - cameraPos.x;
        double renderY = clone.getY() - cameraPos.y;
        double renderZ = clone.getZ() - cameraPos.z;

        Minecraft.getInstance().getEntityRenderDispatcher().render(
                owner,
                renderX,
                renderY,
                renderZ,
                clone.getYRot(),
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
