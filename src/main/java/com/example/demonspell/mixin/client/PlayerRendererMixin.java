package com.example.demonspell.mixin.client;

import com.example.demonspell.DemonFormState;
import com.example.demonspell.DemonSpellMod;
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
    private static final ResourceLocation DEMON_TEXTURE =
            new ResourceLocation(DemonSpellMod.MODID, "textures/player/demon.png");

    @Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true)
    private void demonspell$useDemonTexture(AbstractClientPlayer player,
                                              CallbackInfoReturnable<ResourceLocation> cir) {
        if (DemonFormState.isActive(player.getUUID())) {
            cir.setReturnValue(DEMON_TEXTURE);
        }
    }
}
