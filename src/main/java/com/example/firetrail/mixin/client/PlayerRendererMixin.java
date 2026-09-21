package com.example.firetrail.mixin.client;

import com.example.firetrail.DarkFormState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    private static final ResourceLocation DARK_TEXTURE =
            new ResourceLocation("firetrail", "textures/player/dark_spawn.png");

    @Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true)
    private void firetrail$darkTexture(AbstractClientPlayer player,
                                       CallbackInfoReturnable<ResourceLocation> cir) {
        if (DarkFormState.isActive(player.getUUID())) {
            cir.setReturnValue(DARK_TEXTURE);
        }
    }
}
