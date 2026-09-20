package com.example.firetrail.client;

import com.example.firetrail.entity.HighFiveCloneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class HighFiveCloneRenderer extends EntityRenderer<HighFiveCloneEntity> {
    private RemotePlayer fakePlayer;
    private GameProfile fakeProfile;

    public HighFiveCloneRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.35F;
    }

    @Override
    public void render(HighFiveCloneEntity clone, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Entity owner = clone.getOwnerEntity();
        if (!(owner instanceof Player player) || !(clone.level() instanceof ClientLevel clientLevel)) {
            return;
        }

        GameProfile profile = player.getGameProfile();
        if (fakePlayer == null || fakeProfile == null || !fakeProfile.getId().equals(profile.getId())) {
            fakeProfile = profile;
            fakePlayer = new RemotePlayer(clientLevel, profile);
        }

        // The fake player is only a visual model. It is NOT the real player,
        // so it cannot copy the player's current spell-casting animation.
        fakePlayer.setPos(clone.getX(), clone.getY(), clone.getZ());
        fakePlayer.setYRot(clone.getYRot());
        fakePlayer.setYHeadRot(clone.getYRot());
        fakePlayer.yBodyRot = clone.getYRot();
        fakePlayer.yBodyRotO = clone.getYRot();
        fakePlayer.setXRot(0.0F);
        // Animate only the clone's arm during the high-five.
        // It does not inherit the real player's spell/cast animation.
        float highFive = clone.getHighFiveAnimation(partialTick);
        fakePlayer.attackAnim = highFive;
        fakePlayer.oAttackAnim = highFive;
        fakePlayer.swinging = highFive > 0.0F;
        fakePlayer.setSprinting(false);
        fakePlayer.setShiftKeyDown(false);

        // Freeze normal player movement animation. The clone will only move
        // because HighFiveCloneEntity changes its position.
        fakePlayer.walkDist = 0.0F;
        fakePlayer.walkDistO = 0.0F;
        fakePlayer.xxa = 0.0F;
        fakePlayer.zza = 0.0F;

        poseStack.pushPose();
        // The custom entity renderer is already positioned at the clone's
        // location, so render the visual player at the local origin.
        Minecraft.getInstance().getEntityRenderDispatcher().render(
                fakePlayer,
                0.0D, 0.0D, 0.0D,
                clone.getYRot(), partialTick, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Override
    public net.minecraft.resources.ResourceLocation getTextureLocation(HighFiveCloneEntity entity) {
        return new net.minecraft.resources.ResourceLocation("minecraft", "textures/misc/white.png");
    }
}
