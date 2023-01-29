package io.redstudioragnarok.FBP.particle;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.renderer.FBPRenderer;
import io.redstudioragnarok.FBP.vector.Vector2D;
import io.redstudioragnarok.FBP.vector.Vector3D;
import net.jafama.FastMath;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.Color;
import java.util.List;

import static io.redstudioragnarok.FBP.util.ParticleUtil.texturedParticle;

public class FBPParticleSnow extends ParticleDigging {

	Minecraft mc;

	double scaleAlpha, prevParticleScale, prevParticleAlpha;
	double endMult = 1;

	Vector3D rot, prevRot, rotStep, tempRot;

	Color color;

	public FBPParticleSnow(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);

		this.sourcePos = new BlockPos(xCoordIn, yCoordIn, zCoordIn);

		rot = new Vector3D();
		prevRot = new Vector3D();
		tempRot = new Vector3D();

		createRotationMatrix();

		this.motionX = xSpeedIn;
		this.motionY = -ySpeedIn;
		this.motionZ = zSpeedIn;
		this.particleGravity = 1;

		mc = Minecraft.getMinecraft();

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

		rotStep = new Vector3D(rx > 0.5 ? 1 : -1, ry > 0.5 ? 1 : -1, rz > 0.5 ? 1 : -1);

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

			rot.x = (float) FastMath.round(rot.x / 90) * 90;
			rot.z = (float) FastMath.round(rot.z / 90) * 90;

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
		if (!FBPRenderer.render) {
			FBPRenderer.queuedParticles.add(this);
			return;
		}
		if (!FBP.isEnabled() && particleMaxAge != 0)
			particleMaxAge = 0;

		Vector2D[] particle = texturedParticle(particleTexture, particleTextureJitterX, particleTextureJitterY, particleTextureIndexX, particleTextureIndexY);

		float x = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float y = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float z = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

		int brightness = getBrightnessForRender(partialTicks);

		float alpha = (float) (prevParticleAlpha + (particleAlpha - prevParticleAlpha) * partialTicks);

		float scale = (float) (prevParticleScale + (particleScale - prevParticleScale) * partialTicks);

		y += scale / 10;

		Vector3D smoothRot = new Vector3D(0, 0, 0);

		if (FBP.rotationMult > 0) {
			smoothRot.y = rot.y;
			smoothRot.z = rot.z;

			if (!FBP.randomRotation)
				smoothRot.x = rot.x;

			// SMOOTH ROTATION
			if (!FBP.frozen) {
				Vector3D vector = new Vector3D();
				rot.partialVector(prevRot, partialTicks, vector);

				if (FBP.randomRotation) {
					smoothRot.y = vector.y;
					smoothRot.z = vector.z;
				} else {
					smoothRot.x = vector.x;
				}
			}
		}

		color = new Color(particleRed, particleGreen, particleBlue, alpha);

		FBPRenderer.renderParticle(buffer, particle, x, y, z, scale / 10, smoothRot, brightness, color);
	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		int brightnessForRender = super.getBrightnessForRender(partialTick);
		int lighting = 0;

		if (this.world.isBlockLoaded(new BlockPos(posX, posY, posZ))) {
			lighting = this.world.getCombinedLight(new BlockPos(posX, posY, posZ), 0);
		}

		return brightnessForRender == 0 ? lighting : brightnessForRender;
	}
}
