package com.example.firetrail.client;

import com.example.firetrail.entity.HighFiveCloneEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HighFiveCloneModel extends GeoModel<HighFiveCloneEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation("firetrail", "geo/high_five_clone.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation("firetrail", "animations/high_five_clone.animation.json");
    private static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("minecraft", "textures/entity/steve.png");

    @Override
    public ResourceLocation getModelResource(HighFiveCloneEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(HighFiveCloneEntity animatable) {
        if (animatable.getOwnerEntity() instanceof AbstractClientPlayer player) {
            return player.getSkinTextureLocation();
        }
        return FALLBACK_TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(HighFiveCloneEntity animatable) {
        return ANIMATION;
    }
}
