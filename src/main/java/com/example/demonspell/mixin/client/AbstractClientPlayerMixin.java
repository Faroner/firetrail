package com.example.demonspell.mixin.client;

import com.example.demonspell.DemonFormState;
import com.example.demonspell.DemonSpellMod;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
    private static final ResourceLocation DEMON_TEXTURE =
            new ResourceLocation(DemonSpellMod.MODID, "textures/player/demon.png");

    @Inject(method = "getSkinTextureLocation", at = @At("HEAD"), cancellable = true)
    private void demonspell$useDemonSkin(CallbackInfoReturnable<ResourceLocation> cir) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
        if (DemonFormState.isActive(player.getUUID())) {
            cir.setReturnValue(DEMON_TEXTURE);
        }
    }
}
