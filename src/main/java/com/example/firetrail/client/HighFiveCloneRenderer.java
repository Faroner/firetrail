package com.example.firetrail.client;

import com.example.firetrail.entity.HighFiveCloneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class HighFiveCloneRenderer extends EntityRenderer<HighFiveCloneEntity> {
    private final HighFiveCloneModel model;

    public HighFiveCloneRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.35F;
        ModelPart root = context.bakeLayer(HighFiveCloneModel.LAYER);
        this.model = new HighFiveCloneModel(root);
    }

    @Override
    public void render(HighFiveCloneEntity clone, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Entity owner = clone.getOwnerEntity();
        if (!(owner instanceof Player player)) {
            return;
        }

        poseStack.pushPose();
        // Entity models use the same local coordinate convention as vanilla
        // humanoids: feet at y=0 and head above the body.
        poseStack.translate(0.0D, 1.5D, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        model.setupAnim(clone, 0.0F, 0.0F,
                clone.getAge() + partialTick, 0.0F, 0.0F);

        ResourceLocation skin = Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(player.getGameProfile());
        if (player instanceof AbstractClientPlayer clientPlayer) {
            skin = clientPlayer.getSkinTextureLocation();
        }

        VertexConsumer vertex = buffer.getBuffer(RenderType.entityTranslucent(skin));
        model.renderToBuffer(poseStack, vertex, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(HighFiveCloneEntity entity) {
        return new ResourceLocation("minecraft", "textures/entity/steve.png");
    }
}
