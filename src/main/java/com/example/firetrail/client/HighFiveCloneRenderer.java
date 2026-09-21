package com.example.firetrail.client;

import com.example.firetrail.entity.HighFiveCloneEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HighFiveCloneRenderer extends GeoEntityRenderer<HighFiveCloneEntity> {
    public HighFiveCloneRenderer(EntityRendererProvider.Context context) {
        super(context, new HighFiveCloneModel());
        this.shadowRadius = 0.35F;
    }
}
