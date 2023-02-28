package io.redstudioragnarok.FBP.particle;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.renderer.CubeBatchRenderer;
import io.redstudioragnarok.FBP.renderer.RenderType;
import io.redstudioragnarok.FBP.renderer.color.ColorUtil;
import io.redstudioragnarok.FBP.renderer.light.LightUtil;
import io.redstudioragnarok.FBP.renderer.texture.TextureUtil;
import io.redstudioragnarok.FBP.utils.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;

import static io.redstudioragnarok.FBP.FBP.snowTexture;

public class FBPParticleSmoke extends ParticleSmokeNormal {

	double scaleAlpha, prevParticleScale, prevParticleAlpha;
	double endMult = 0.75;

	final float AngleY;

	ParticleSmokeNormal original;

	protected FBPParticleSmoke(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, final double mX, final double mY, final double mZ, float scale, ParticleSmokeNormal original) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, mX, mY, mZ, scale);

		this.original = original;

		this.motionX = mX;
		this.motionY = mY;
		this.motionZ = mZ;

		this.particleTexture = snowTexture;

		scaleAlpha = particleScale * 0.85;

		Block block = worldIn.getBlockState(new BlockPos(xCoordIn, yCoordIn, zCoordIn)).getBlock();

		if (block == Blocks.FIRE) {
			this.particleScale *= 0.65;
			this.particleGravity *= 0.25;

			this.motionX = FBP.random.nextDouble(-0.05, 0.05);
			this.motionY = FBP.random.nextDouble() * 0.5;
			this.motionZ = FBP.random.nextDouble(-0.05, 0.05);

			this.motionY *= 0.35;

			scaleAlpha = particleScale * 0.5;

			particleMaxAge = FBP.random.nextInt(7, 18);
		} else if (block == Blocks.TORCH) {
			particleScale *= 0.45;

			this.motionX = FBP.random.nextDouble(-0.05, 0.05);
			this.motionY = FBP.random.nextDouble() * 0.5;
			this.motionZ = FBP.random.nextDouble(-0.05, 0.05);

			this.motionX *= 0.925;
			this.motionY = 0.005;
			this.motionZ *= 0.925;

			this.particleRed = 0.275f;
			this.particleGreen = 0.275f;
			this.particleBlue = 0.275f;

			scaleAlpha = particleScale * 0.75;

			particleMaxAge = FBP.random.nextInt(5, 10);
		} else {
			particleScale = scale;
			motionY *= 0.935;
		}

		particleScale *= FBP.scaleMult;

		AngleY = rand.nextFloat() * 80;

		particleAlpha = 0.9f;

		if (FBP.randomFadingSpeed)
			endMult = MathUtil.clampMaxFirst((float) FBP.random.nextDouble(0.425, 1.15), 0.5432F, 1);

		multipleParticleScaleBy(1);
	}

	@Override
	public Particle multipleParticleScaleBy(float scale) {
		Particle particle = super.multipleParticleScaleBy(scale);

		float newScale = particleScale / 20;

		this.setBoundingBox(new AxisAlignedBB(posX - newScale, posY - newScale, posZ - newScale, posX + newScale, posY + newScale, posZ + newScale));

		return particle;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		prevParticleAlpha = particleAlpha;
		prevParticleScale = particleScale;

		if (!FBP.fancySmoke)
			this.isExpired = true;

		if (++this.particleAge >= this.particleMaxAge) {
			if (FBP.randomFadingSpeed)
				particleScale *= 0.88 * endMult;
			else
				particleScale *= 0.88;

			if (particleAlpha > 0.01 && particleScale <= scaleAlpha) {
				if (FBP.randomFadingSpeed)
					particleAlpha *= 0.76 * endMult;
				else
					particleAlpha *= 0.76;
			}

			if (particleAlpha <= 0.01)
				setExpired();
		}

		this.motionY += 0.004;
		this.move(this.motionX, this.motionY, this.motionZ);

		if (this.posY == this.prevPosY) {
			this.motionX *= 1.1;
			this.motionZ *= 1.1;
		}

		this.motionX *= 0.95;
		this.motionY *= 0.95;
		this.motionZ *= 0.95;

		if (this.onGround) {
			this.motionX *= 0.89;
			this.motionZ *= 0.89;
		}
	}

	@Override
	public void move(double x, double y, double z) {
		double Y = y;

		List<AxisAlignedBB> list = this.world.getCollisionBoxes(null, this.getBoundingBox().expand(x, y, z));

		for (AxisAlignedBB axisalignedbb : list) {
			y = axisalignedbb.calculateYOffset(this.getBoundingBox(), y);
		}

		this.setBoundingBox(this.getBoundingBox().offset(0, y, 0));

		for (AxisAlignedBB axisalignedbb : list) {
			x = axisalignedbb.calculateXOffset(this.getBoundingBox(), x);
		}

		this.setBoundingBox(this.getBoundingBox().offset(x, 0, 0));

		for (AxisAlignedBB axisalignedbb : list) {
			z = axisalignedbb.calculateZOffset(this.getBoundingBox(), z);
		}

		this.setBoundingBox(this.getBoundingBox().offset(0, 0, z));

		// RESET
		resetPositionToBB();
		this.onGround = y != Y;
	}

	@Override
	protected void resetPositionToBB() {
		AxisAlignedBB axisalignedbb = this.getBoundingBox();
		this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2;
		this.posY = (axisalignedbb.minY + axisalignedbb.maxY) / 2;
		this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if (!FBP.isEnabled() && particleMaxAge != 0)
			particleMaxAge = 0;

		float x = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float y = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float z = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

		int brightness = getBrightnessForRender(partialTicks);

		float alpha = (float) (prevParticleAlpha + (particleAlpha - prevParticleAlpha) * partialTicks);

		float scale = (float) (prevParticleScale + (particleScale - prevParticleScale) * partialTicks);
		scale *= 0.05F;

		CubeBatchRenderer.renderCube(RenderType.BLOCK_TEXTURE, x, y, z, 0.0F, AngleY, 0.0F, scale, scale, scale,
				TextureUtil.pointTexCoordProvider(particleTexture.getInterpolatedU(4.4F), particleTexture.getInterpolatedV(4.4F)),
				ColorUtil.multiplyingColorProvider(particleRed, particleGreen, particleBlue, alpha, 0.875F),
				LightUtil.uniformLightCoordProvider(brightness));
	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		return LightUtil.getCombinedLight(world, posX, posY, posZ);
	}
}
