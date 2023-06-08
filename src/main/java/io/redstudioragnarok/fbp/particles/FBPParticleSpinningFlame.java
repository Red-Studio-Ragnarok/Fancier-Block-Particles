package io.redstudioragnarok.fbp.particles;

import io.redstudioragnarok.fbp.renderer.CubeBatchRenderer;
import io.redstudioragnarok.fbp.renderer.FastQuadUploader;
import io.redstudioragnarok.fbp.renderer.RenderType;
import io.redstudioragnarok.fbp.renderer.color.ColorUtil;
import io.redstudioragnarok.fbp.renderer.light.LightUtil;
import io.redstudioragnarok.fbp.renderer.texture.TextureUtil;
import io.redstudioragnarok.fbp.utils.MathUtil;
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
		this.particleMaxAge = 200;
	}

	public FBPParticleSpinningFlame(World world, double x, double y, double z, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
		super(world, x, y, z, xSpeedIn, ySpeedIn, zSpeedIn);
		this.particleMaxAge = 200;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		this.renderParticle(partialTicks);
	}

	@Override
	public void renderParticle(MatrixStack matrixStack, float partialTicks, int brightness) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		boolean thirdPersionFrontView = mc.gameSettings.thirdPersonView == 2;
		float rx = (thirdPersionFrontView ? 1.0F : -1.0F) * MathUtil.lerp(player.prevRotationPitch, player.rotationPitch, partialTicks);
		float ry = (thirdPersionFrontView ? 0.0F : 180.0F) - MathUtil.lerp(player.prevRotationYaw, player.rotationYaw, partialTicks);
		float rz = 10.0F * (this.particleAge + partialTicks);

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
		return LightUtil.getCombinedLight((float) this.posX, (float) this.posY, (float) this.posZ);
	}

}
