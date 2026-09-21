package com.example.firetrail.client;

import com.example.firetrail.FireTrailMod;
import com.example.firetrail.registry.ModEntities;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = FireTrailMod.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class ClientModEvents {
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HighFiveCloneModel.LAYER, HighFiveCloneModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.FIRE_TRAIL.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.HIGH_FIVE_CLONE.get(), HighFiveCloneRenderer::new);
    }

    private ClientModEvents() {}
}
