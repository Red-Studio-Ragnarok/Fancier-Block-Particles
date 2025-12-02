package dev.redstudio.fbp.particles;

import dev.redstudio.fbp.renderer.*;
import dev.redstudio.fbp.renderer.color.ColorUtil;
import dev.redstudio.fbp.renderer.light.LightUtil;
import dev.redstudio.fbp.renderer.texture.TextureUtil;
import dev.redstudio.redcore.math.MathUtil;
import meldexun.matrixutil.MatrixStack;
import meldexun.matrixutil.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class FBPParticleSpinningFlame extends Particle implements IFBPParticle {

    public FBPParticleSpinningFlame(World world, double x, double y, double z) {
        super(world, x, y, z);
        particleMaxAge = 200;
    }

    public FBPParticleSpinningFlame(World world, double x, double y, double z, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(world, x, y, z, xSpeedIn, ySpeedIn, zSpeedIn);
        particleMaxAge = 200;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        renderParticle(partialTicks);
    }

    @Override
    public void renderParticle(MatrixStack matrixStack, float partialTicks, int brightness) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        boolean thirdPersionFrontView = mc.gameSettings.thirdPersonView == 2;
        float rx = (thirdPersionFrontView ? 1.0F : -1.0F) * MathUtil.lerp(player.prevRotationPitch, partialTicks, player.rotationPitch);
        float ry = (thirdPersionFrontView ? 0.0F : 180.0F) - MathUtil.lerp(player.prevRotationYaw, partialTicks, player.rotationYaw);
        float rz = 10.0F * (particleAge + partialTicks);

        Quaternion rotation = Quaternion.createRotateY((float) Math.toRadians(ry));
        rotation.rotateX((float) Math.toRadians(rx));
        rotation.rotateZ((float) Math.toRadians(rz));
        matrixStack.rotate(rotation);
        matrixStack.scale(0.125F, 0.125F, 0.125F);

        FastQuadUploader.putQuad(CubeBatchRenderer.getBuffer(RenderType.PARTICLE_TEXTURE), matrixStack,
                TextureUtil.uniformTexCoordProvider(0, 3 * 8.0F / 128.0F, 8.0F / 128.0F, 4 * 8.0F / 128.0F),
                ColorUtil.uniformColorProvider(0xFFFFFFFF),
                LightUtil.uniformLightCoordProvider(brightness));
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        return LightUtil.getCombinedLight((float) posX, (float) posY, (float) posZ);
    }

}
