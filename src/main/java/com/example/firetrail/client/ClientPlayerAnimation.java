package com.example.firetrail.client;

import com.example.firetrail.entity.HighFiveCloneEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.example.firetrail.FireTrailMod;

@Mod.EventBusSubscriber(modid = FireTrailMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientPlayerAnimation {
    @SubscribeEvent
    public static void animateHighFivePlayer(RenderPlayerEvent.Pre event) {
        AbstractClientPlayer player = event.getEntity();
        HighFiveCloneEntity clone = findActiveClone(player);
        if (clone == null) return;

        float age = clone.getAge() + event.getPartialTick();
        if (age < 6.0F || age > 18.0F) return;

        float t;
        if (age < 11.5F) {
            t = smooth((age - 6.0F) / 5.5F);
        } else if (age < 13.0F) {
            t = 1.0F;
        } else {
            t = 1.0F - smooth((age - 13.0F) / 5.0F);
        }

        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
        float lift = -1.35F * t;
        model.rightArm.xRot = lift;
        model.rightArm.yRot = -0.10F * t;
        model.rightArm.zRot = -0.18F * t;
    }

    private static HighFiveCloneEntity findActiveClone(AbstractClientPlayer player) {
        if (player.level() == null) return null;
        for (Entity entity : player.level().getEntities(player,
                player.getBoundingBox().inflate(3.0D), e -> e instanceof HighFiveCloneEntity)) {
            HighFiveCloneEntity clone = (HighFiveCloneEntity) entity;
            if (player.getUUID().equals(clone.getOwnerUUID())) return clone;
        }
        return null;
    }

    private static float smooth(float x) {
        x = Math.max(0.0F, Math.min(1.0F, x));
        return x * x * (3.0F - 2.0F * x);
    }

    private ClientPlayerAnimation() {}
}
