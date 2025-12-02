package dev.redstudio.fbp.particles;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.renderer.CubeBatchRenderer;
import dev.redstudio.fbp.renderer.RenderType;
import dev.redstudio.fbp.renderer.color.ColorUtil;
import dev.redstudio.fbp.renderer.light.LightUtil;
import dev.redstudio.fbp.renderer.texture.TextureUtil;
import dev.redstudio.redcore.math.ClampUtil;
import net.minecraft.block.Block;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class FBPParticleSmoke extends ParticleSmokeNormal {

	double scaleAlpha, prevParticleScale, prevParticleAlpha;
	double endMult = 0.75;

	final float AngleY;

	ParticleSmokeNormal original;

	protected FBPParticleSmoke(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, final double mX, final double mY, final double mZ, float scale, ParticleSmokeNormal original) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, mX, mY, mZ, scale);

		this.original = original;

		motionX = mX;
		motionY = mY;
		motionZ = mZ;

		scaleAlpha = particleScale * 0.85;

		Block block = worldIn.getBlockState(new BlockPos(xCoordIn, yCoordIn, zCoordIn)).getBlock();

		if (block == Blocks.FIRE) {
			particleScale *= 0.65F;
			particleGravity *= 0.25F;

			motionX = FBP.RANDOM.nextDouble(-0.05, 0.05);
			motionY = FBP.RANDOM.nextDouble() * 0.5;
			motionZ = FBP.RANDOM.nextDouble(-0.05, 0.05);

			motionY *= 0.35;

			scaleAlpha = particleScale * 0.5;

			particleMaxAge = FBP.RANDOM.nextInt(7, 18);
		} else if (block == Blocks.TORCH) {
			particleScale *= 0.45F;

			motionX = FBP.RANDOM.nextDouble(-0.05, 0.05);
			motionY = FBP.RANDOM.nextDouble() * 0.5;
			motionZ = FBP.RANDOM.nextDouble(-0.05, 0.05);

			motionX *= 0.925;
			motionY = 0.005;
			motionZ *= 0.925;

			particleRed = 0.275f;
			particleGreen = 0.275f;
			particleBlue = 0.275f;

			scaleAlpha = particleScale * 0.75;

			particleMaxAge = FBP.RANDOM.nextInt(5, 10);
		} else {
			particleScale = scale;
			motionY *= 0.935;
		}

		particleScale *= FBP.scaleMult;

		AngleY = rand.nextFloat() * 80;

		particleAlpha = 0.9f;

		if (FBP.randomFadingSpeed)
			endMult = ClampUtil.clampMaxFirst((float) FBP.RANDOM.nextDouble(0.425, 1.15), 0.5432F, 1);

		multipleParticleScaleBy(1);
	}

	@Override
	public Particle multipleParticleScaleBy(float scale) {
		Particle particle = super.multipleParticleScaleBy(scale);

		float newScale = particleScale / 20;

		setBoundingBox(new AxisAlignedBB(posX - newScale, posY - newScale, posZ - newScale, posX + newScale, posY + newScale, posZ + newScale));

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
			isExpired = true;

		if (++particleAge >= particleMaxAge) {
			if (FBP.randomFadingSpeed)
				particleScale *= (float) (0.88 * endMult);
			else
				particleScale *= 0.88F;

			if (particleAlpha > 0.01 && particleScale <= scaleAlpha) {
				if (FBP.randomFadingSpeed)
					particleAlpha *= (float) (0.76 * endMult);
				else
					particleAlpha *= 0.76F;
			}

			if (particleAlpha <= 0.01)
				setExpired();
		}

		motionY += 0.004;
		move(motionX, motionY, motionZ);

		if (posY == prevPosY) {
			motionX *= 1.1;
			motionZ *= 1.1;
		}

		motionX *= 0.95;
		motionY *= 0.95;
		motionZ *= 0.95;

		if (onGround) {
			motionX *= 0.89;
			motionZ *= 0.89;
		}
	}

	@Override
	public void move(double x, double y, double z) {
		double Y = y;

		List<AxisAlignedBB> list = world.getCollisionBoxes(null, getBoundingBox().expand(x, y, z));

		for (AxisAlignedBB axisalignedbb : list) {
			y = axisalignedbb.calculateYOffset(getBoundingBox(), y);
		}

		setBoundingBox(getBoundingBox().offset(0, y, 0));

		for (AxisAlignedBB axisalignedbb : list) {
			x = axisalignedbb.calculateXOffset(getBoundingBox(), x);
		}

		setBoundingBox(getBoundingBox().offset(x, 0, 0));

		for (AxisAlignedBB axisalignedbb : list) {
			z = axisalignedbb.calculateZOffset(getBoundingBox(), z);
		}

		setBoundingBox(getBoundingBox().offset(0, 0, z));

		// RESET
		resetPositionToBB();
		onGround = y != Y;
	}

	@Override
	protected void resetPositionToBB() {
		AxisAlignedBB axisalignedbb = getBoundingBox();
		posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2;
		posY = (axisalignedbb.minY + axisalignedbb.maxY) / 2;
		posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if (!FBP.enabled && particleMaxAge != 0)
			particleMaxAge = 0;

		float x = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float y = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float z = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

		int brightness = getBrightnessForRender(partialTicks);

		float alpha = (float) (prevParticleAlpha + (particleAlpha - prevParticleAlpha) * partialTicks);

		float scale = (float) (prevParticleScale + (particleScale - prevParticleScale) * partialTicks);
		scale *= 0.05F;

		CubeBatchRenderer.renderCube(RenderType.BLOCK_TEXTURE, x, y, z, 0.0F, AngleY, 0.0F, scale, scale, scale,
				TextureUtil.pointTexCoordProvider(0.82109374F, 0.28984374F),
				ColorUtil.multiplyingColorProvider(particleRed, particleGreen, particleBlue, alpha, 0.875F),
				LightUtil.uniformLightCoordProvider(brightness));
	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		return LightUtil.getCombinedLight((float) posX, (float) posY, (float) posZ);
	}
}
