package io.redstudioragnarok.fbp.particles;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.renderer.CubeBatchRenderer;
import io.redstudioragnarok.fbp.renderer.RenderType;
import io.redstudioragnarok.fbp.renderer.color.ColorUtil;
import io.redstudioragnarok.fbp.renderer.light.LightUtil;
import io.redstudioragnarok.fbp.renderer.texture.TextureUtil;
import io.redstudioragnarok.fbp.vectors.Vector3F;
import net.jafama.FastMath;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static io.redstudioragnarok.fbp.FBP.mc;

public class FBPParticleSnow extends ParticleDigging {

	double scaleAlpha, prevParticleScale, prevParticleAlpha;
	double endMult = 1;

	Vector3F rot, prevRot, rotStep, tempRot;

	public FBPParticleSnow(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);

		this.sourcePos = new BlockPos(xCoordIn, yCoordIn, zCoordIn);

		rot = new Vector3F();
		prevRot = new Vector3F();
		tempRot = new Vector3F();

		createRotationMatrix();

		this.motionX = xSpeedIn;
		this.motionY = -ySpeedIn;
		this.motionZ = zSpeedIn;
		this.particleGravity = 1;

		particleScale *= FBP.random.nextDouble(FBP.scaleMult - 0.25, FBP.scaleMult + 0.25);
		particleMaxAge = (int) FBP.random.nextDouble(250, 300);
		this.particleRed = this.particleGreen = this.particleBlue = 1;

		scaleAlpha = particleScale * 0.75;

		this.particleAlpha = 0;
		this.particleScale = 0;

		this.canCollide = true;

		if (FBP.randomFadingSpeed)
			endMult *= FBP.random.nextDouble(0.7, 1);
	}

	private void createRotationMatrix() {
		double rx = FBP.random.nextDouble();
		double ry = FBP.random.nextDouble();
		double rz = FBP.random.nextDouble();

		rotStep = new Vector3F(rx > 0.5 ? 1 : -1, ry > 0.5 ? 1 : -1, rz > 0.5 ? 1 : -1);

		rot.copy(rotStep);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void onUpdate() {
		prevRot.copy(rot);

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		prevParticleAlpha = particleAlpha;
		prevParticleScale = particleScale;

		if (!mc.isGamePaused()) {
			particleAge++;

			if (posY < mc.player.posY - (mc.gameSettings.renderDistanceChunks * 16))
				setExpired();

			tempRot.copy(rotStep);
			tempRot.scale((FBP.rotationMult * 5));
			rot.add(tempRot);

			if (this.particleAge >= this.particleMaxAge) {
				if (FBP.randomFadingSpeed)
					particleScale *= 0.75 * endMult;
				else
					particleScale *= 0.75;

				if (particleAlpha > 0.01 && particleScale <= scaleAlpha) {
					if (FBP.randomFadingSpeed)
						particleAlpha *= 0.65 * endMult;
					else
						particleAlpha *= 0.65;
				}

				if (particleAlpha <= 0.01)
					setExpired();
			} else {
				if (particleScale < 1) {
					if (FBP.randomFadingSpeed)
						particleScale += 0.075 * endMult;
					else
						particleScale += 0.075;

					if (particleScale > 1)
						particleScale = 1;
				}

				if (particleAlpha < 1) {
					if (FBP.randomFadingSpeed)
						particleAlpha += 0.045 * endMult;
					else
						particleAlpha += 0.045;

					if (particleAlpha > 1)
						particleAlpha = 1;
				}
			}

			if (world.getBlockState(new BlockPos(posX, posY, posZ)).getMaterial().isLiquid())
				setExpired();

			motionY -= 0.04 * this.particleGravity;

			move(motionX, motionY, motionZ);

			if (onGround) {
				rot.x = (float) FastMath.round(rot.x / 90) * 90;
				rot.z = (float) FastMath.round(rot.z / 90) * 90;
			}

			motionX *= 0.98;

			if (motionY < -0.2) // minimal motionY
				motionY *= 0.75;

			motionZ *= 0.98;

			if (onGround) {
				motionX *= 0.68;
				motionZ *= 0.68;

				rotStep.scale(0.85F);

				this.particleAge += 2;
			}
		}
	}

	@Override
	public void move(double x, double y, double z) {
		double X = x;
		double Y = y;
		double Z = z;

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
		this.onGround = y != Y && Y < 0;

		if (!FBP.lowTraction && !FBP.bounceOffWalls) {
			if (x != X)
				motionX *= 0.69;
			if (z != Z)
				motionZ *= 0.69;
		}
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
		scale *= 0.1F;

		y += scale;

		Vector3F smoothRot = new Vector3F(0, 0, 0);

		if (FBP.rotationMult > 0) {
			smoothRot.y = rot.y;
			smoothRot.z = rot.z;

			if (!FBP.randomRotation)
				smoothRot.x = rot.x;

			// SMOOTH ROTATION
			if (!FBP.frozen) {
				Vector3F vector = new Vector3F();
				rot.partialVector(prevRot, partialTicks, vector);

				if (FBP.randomRotation) {
					smoothRot.y = vector.y;
					smoothRot.z = vector.z;
				} else {
					smoothRot.x = vector.x;
				}
			}
		}

		CubeBatchRenderer.renderCube(RenderType.BLOCK_TEXTURE_ITEM_LIGHTING, x, y, z, smoothRot.x, smoothRot.y, smoothRot.z, scale, scale, scale,
				TextureUtil.particleTexCoordProvider(particleTexture, particleTextureJitterX, particleTextureJitterY, particleTextureIndexX, particleTextureIndexY),
				ColorUtil.uniformColorProvider(particleRed, particleGreen, particleBlue, alpha),
				LightUtil.uniformLightCoordProvider(brightness));
	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		return LightUtil.getCombinedLight((float) posX, (float) posY, (float) posZ);
	}
}
