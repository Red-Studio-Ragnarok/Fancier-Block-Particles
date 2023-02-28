package io.redstudioragnarok.fbp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;

import io.redstudioragnarok.fbp.renderer.CubeBatchRenderer;
import net.minecraft.client.renderer.EntityRenderer;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

	@Inject(method = "renderWorldPass(IFJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;renderParticles(Lnet/minecraft/entity/Entity;F)V", shift = Shift.AFTER))
	private void afterParticlesRendered(int pass, float partialTicks, long finishTimeNano, CallbackInfo info) {
		CubeBatchRenderer.endAllBatches();
	}

}
