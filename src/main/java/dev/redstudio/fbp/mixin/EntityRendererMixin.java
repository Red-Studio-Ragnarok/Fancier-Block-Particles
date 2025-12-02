package dev.redstudio.fbp.mixin;

import dev.redstudio.fbp.renderer.CubeBatchRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public final class EntityRendererMixin {

	@Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lnet/minecraft/entity/Entity;F)V", shift = Shift.AFTER))
	private void afterParticlesRendered(final CallbackInfo callbackInfo) {
		CubeBatchRenderer.endAllBatches();
	}
}
