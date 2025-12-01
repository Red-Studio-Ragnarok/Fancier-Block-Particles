package dev.redstudio.fbp.particles;

import io.redstudioragnarok.redcore.utils.MathUtil;
import meldexun.matrixutil.MatrixStack;
import net.minecraft.client.particle.Particle;

public interface IFBPParticle {

    MatrixStack MATRIX_STACK = new MatrixStack();

    default Particle self() {
        return (Particle) this;
    }

    default void renderParticle(float partialTicks) {
        float x = (float) (MathUtil.lerp(self().prevPosX, partialTicks, self().posX) - Particle.interpPosX);
        float y = (float) (MathUtil.lerp(self().prevPosY, partialTicks, self().posY) - Particle.interpPosY);
        float z = (float) (MathUtil.lerp(self().prevPosZ, partialTicks, self().posZ) - Particle.interpPosZ);

        int brightness = self().getBrightnessForRender(partialTicks);

        MATRIX_STACK.push();
        MATRIX_STACK.translate(x, y, z);
        renderParticle(MATRIX_STACK, partialTicks, brightness);
        MATRIX_STACK.pop();
    }

    void renderParticle(MatrixStack matrixStack, float partialTicks, int brightness);

}
