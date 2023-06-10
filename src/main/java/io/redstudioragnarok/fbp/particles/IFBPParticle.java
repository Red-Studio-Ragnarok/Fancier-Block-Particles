package io.redstudioragnarok.fbp.particles;

import io.redstudioragnarok.fbp.utils.MathUtil;
import meldexun.matrixutil.MatrixStack;
import net.minecraft.client.particle.Particle;

public interface IFBPParticle {

	static MatrixStack MATRIX_STACK = new MatrixStack();

	default Particle self() {
		return (Particle) this;
	}

	default void renderParticle(float partialTicks) {
		float x = (float) (MathUtil.lerp(this.self().prevPosX, this.self().posX, partialTicks) - Particle.interpPosX);
		float y = (float) (MathUtil.lerp(this.self().prevPosY, this.self().posY, partialTicks) - Particle.interpPosY);
		float z = (float) (MathUtil.lerp(this.self().prevPosZ, this.self().posZ, partialTicks) - Particle.interpPosZ);

		int brightness = this.self().getBrightnessForRender(partialTicks);

		MATRIX_STACK.push();
		MATRIX_STACK.translate(x, y, z);
		this.renderParticle(MATRIX_STACK, partialTicks, brightness);
		MATRIX_STACK.pop();
	}

	void renderParticle(MatrixStack matrixStack, float partialTicks, int brightness);

}
