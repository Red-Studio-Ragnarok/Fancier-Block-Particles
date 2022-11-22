package io.redstudioragnarok.FBP.particle;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.renderer.FBPRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class FBPParticleFlame extends ParticleFlame {

	Minecraft mc;

	double startScale, scaleAlpha, prevParticleScale, prevParticleAlpha;
	double endMult = 1;

	boolean spawnAnother;

	Vec3d startPos;

	Vec3d[] cube;

	Vec2f par;

	protected FBPParticleFlame(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double mY, boolean spawnAnother) {
		super(worldIn, xCoordIn, yCoordIn - 0.06, zCoordIn, 0, mY, 0);
		IBlockState blockState = worldIn.getBlockState(new BlockPos(posX, posY, posZ));

		this.spawnAnother = spawnAnother;

		if (blockState == Blocks.TORCH.getDefaultState())
			prevPosY = posY = posY + 0.04;

		startPos = new Vec3d(posX, posY, posZ);

		mc = Minecraft.getMinecraft();

		this.motionY = -0.00085;
		this.particleGravity = -0.05f;

		this.particleTexture = mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.SNOW.getDefaultState());

		particleScale *= FBP.scaleMult * 2.5;
		particleMaxAge = FBP.random.nextInt(3, 5);

		this.particleRed = 1;
		this.particleGreen = 1;
		this.particleBlue = 0;

		float angleY = rand.nextFloat() * 80;

		cube = new Vec3d[FBP.CUBE.length];

		for (int i = 0; i < FBP.CUBE.length; i++) {
			Vec3d vec = FBP.CUBE[i];
			cube[i] = FBPRenderer.rotatef_d(vec, 0, angleY, 0);
		}

		particleAlpha = 1;

		if (FBP.randomFadingSpeed)
			endMult *= FBP.random.nextDouble(0.9875, 1);

		multipleParticleScaleBy(1);
	}

	@Override
	public Particle multipleParticleScaleBy(float scale) {
		Particle particle = super.multipleParticleScaleBy(scale);

		startScale = particleScale;
		scaleAlpha = particleScale * 0.35;

		float newScale = particleScale / 80;

		this.setBoundingBox(new AxisAlignedBB(posX - newScale, posY - newScale, posZ - newScale, posX + newScale, posY + newScale, posZ + newScale));

		return particle;
	}

	@Override
	public int getFXLayer() {
		return 0;
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		prevParticleAlpha = particleAlpha;
		prevParticleScale = particleScale;

		if (!FBP.fancyFlame)
			this.isExpired = true;

		if (++this.particleAge >= this.particleMaxAge) {
			if (FBP.randomFadingSpeed)
				particleScale *= 0.95 * endMult;
			else
				particleScale *= 0.95;

			if (particleAlpha > 0.01 && particleScale <= scaleAlpha) {
				if (FBP.randomFadingSpeed)
					particleAlpha *= 0.9 * endMult;
				else
					particleAlpha *= 0.9;
			}

			if (particleAlpha <= 0.01)
				setExpired();
			else if (particleAlpha <= 0.325 && spawnAnother && world.getBlockState(new BlockPos(posX, posY, posZ)).getBlock() == Blocks.TORCH) {
				spawnAnother = false;

				mc.effectRenderer.addEffect(new FBPParticleFlame(world, startPos.x, startPos.y, startPos.z, 0, false));
			}
		}

		motionY -= 0.02 * this.particleGravity;
		move(0, motionY, 0);
		motionY *= 0.95;

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
	public void renderParticle(BufferBuilder buf, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if (!FBP.isEnabled() && particleMaxAge != 0)
			particleMaxAge = 0;

		float x = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float y = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float z = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

		float texture1 = particleTexture.getInterpolatedU((0.1 + 1) / 4 * 16);
		float texture2 = particleTexture.getInterpolatedV((0.1 + 1) / 4 * 16);

		par = new Vec2f(texture1, texture2);

		// Smooth scale transition
		float scale = (float) (prevParticleScale + (particleScale - prevParticleScale) * partialTicks);

		if (this.particleAge >= this.particleMaxAge)
			this.particleGreen = (float) (scale / startScale);

		int brightness = getBrightnessForRender(partialTicks);

		float alpha = (float) (prevParticleAlpha + (particleAlpha - prevParticleAlpha) * partialTicks);

		FBPRenderer.renderCube_F(buf, par, x, y, z, scale, brightness, particleRed, particleGreen, particleBlue, alpha, cube);
	}

	@Override
	public int getBrightnessForRender(float p_189214_1_) {
		int brightnessForRender = super.getBrightnessForRender(p_189214_1_);
		int ligthing = 0;

		if (this.world.isBlockLoaded(new BlockPos(posX, posY, posZ))) {
			ligthing = this.world.getCombinedLight(new BlockPos(posX, posY, posZ), 0);
		}

		return brightnessForRender == 0 ? ligthing : brightnessForRender;
	}
}
