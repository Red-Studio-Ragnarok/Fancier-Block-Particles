package io.redstudioragnarok.FBP.particle;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.keys.KeyBindings;
import io.redstudioragnarok.FBP.model.ModelHelper;
import io.redstudioragnarok.FBP.renderer.FBPRenderer;
import io.redstudioragnarok.FBP.util.MathUtil;
import io.redstudioragnarok.FBP.vector.Vector2D;
import io.redstudioragnarok.FBP.vector.Vector3D;
import net.jafama.FastMath;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

import static io.redstudioragnarok.FBP.util.ParticleUtil.texturedParticle;

public class FBPParticleDigging extends ParticleDigging {

	private final IBlockState blockState;

	static boolean killToggle;

	Minecraft mc;

	float prevGravity;

	double startY, scaleAlpha, prevParticleScale, prevParticleAlpha, prevMotionX, prevMotionZ;
	double endMult = 0.75;

	boolean modeDebounce, wasFrozen, destroyed;

	EnumFacing facing;

	Vector3D rot, prevRot, rotStep;

	Color color;

	static Entity dummyEntity = new Entity(null) {
		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			// TODO Auto-generated method stub?
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			// TODO Auto-generated method stub?
		}

		@Override
		protected void entityInit() {
			// TODO Auto-generated method stub?
		}
	};

	protected FBPParticleDigging(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float scale, float R, float G, float B, IBlockState state, @Nullable EnumFacing facing, @Nullable TextureAtlasSprite texture) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);

		this.particleRed = R;
		this.particleGreen = G;
		this.particleBlue = B;

		mc = Minecraft.getMinecraft();

		rot = new Vector3D();
		prevRot = new Vector3D();

		this.facing = facing;

		createRotationMatrix();

		this.sourcePos = new BlockPos(xCoordIn, yCoordIn, zCoordIn);

		if (scale > -1)
			particleScale = scale;

		if (scale < -1) {
			if (facing != null) {
				if (facing == EnumFacing.UP && FBP.smartBreaking) {
					motionX *= 1.5;
					motionY *= 0.1;
					motionZ *= 1.5;

					double particleSpeed = FastMath.sqrtQuick(motionX * motionX + motionZ * motionZ);

					float x = MathUtil.addOrSubtractBasedOnSign((float) cameraViewDir.x, 0.01F);
					float z = MathUtil.addOrSubtractBasedOnSign((float) cameraViewDir.z, 0.01F);

					motionX = x * particleSpeed;
					motionZ = z * particleSpeed;
				}
			}
		}

		if (modeDebounce == !FBP.randomRotation) {
			rot.zero();
			calculateYAngle();
		}

		this.blockState = state;

		Block block = state.getBlock();

		particleGravity = block.blockParticleGravity * FBP.gravityMult;

		particleScale = FBP.scaleMult * (FBP.randomizedScale ? particleScale : 1);
		particleMaxAge = (int) FBP.random.nextDouble(FBP.minAge, FBP.maxAge + 0.5);

		scaleAlpha = particleScale * 0.82;

		destroyed = facing == null;

		if (texture == null && ModelHelper.isModelValid(state)) {
			BlockModelShapes blockModelShapes = mc.getBlockRendererDispatcher().getBlockModelShapes();

			// GET THE TEXTURE OF THE BLOCK FACE
			if (!destroyed) {
				try {
					List<BakedQuad> quads = blockModelShapes.getModelForState(state).getQuads(state, facing, 0);

					if (!quads.isEmpty())
						this.particleTexture = quads.get(0).getSprite();
				} catch (Exception e) {
					// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
				}
			}

			if (particleTexture == null || particleTexture.getIconName().equals("missingno"))
				this.setParticleTexture(blockModelShapes.getTexture(state));
		} else
			this.particleTexture = texture;

		if (FBP.randomFadingSpeed)
			endMult = MathUtil.clampMaxFirst((float) FBP.random.nextDouble(0.5, 0.9), 0.55F, 0.8F);

		prevGravity = particleGravity;

		startY = posY;

		multipleParticleScaleBy(1);
	}

	@Override
	public Particle multipleParticleScaleBy(float scale) {
		Particle particle = super.multipleParticleScaleBy(scale);

		float newScale = particleScale / 10;

		if (destroyed)
			posY = prevPosY = startY - newScale;

		this.setBoundingBox(new AxisAlignedBB(posX - newScale, posY, posZ - newScale, posX + newScale, posY + 2 * newScale, posZ + newScale));

		return particle;
	}

	public Particle MultiplyVelocity(float multiplier) {
		this.motionX *= multiplier;
		this.motionY = (this.motionY - 0.1) * (multiplier / 2) + 0.1;
		this.motionZ *= multiplier;
		return this;
	}

	@Override
	protected void multiplyColor(@Nullable BlockPos p_187154_1_) {
		if (blockState.getBlock() == Blocks.GRASS && facing != EnumFacing.UP)
			return;

		int i = mc.getBlockColors().colorMultiplier(this.blockState, this.world, p_187154_1_, 0);
		this.particleRed *= (i >> 16 & 255) / 255.0;
		this.particleGreen *= (i >> 8 & 255) / 255.0;
		this.particleBlue *= (i & 255) / 255.0;
	}

	@Override
	public FBPParticleDigging init() {
		multiplyColor(new BlockPos(this.posX, this.posY, this.posZ));
		return this;
	}

	@Override
	public FBPParticleDigging setBlockPos(BlockPos pos) {
		this.multiplyColor(pos);
		return this;
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void onUpdate() {
		boolean allowedToMove = MathUtil.absolute((float) motionX) > 0.0001 || MathUtil.absolute((float) motionZ) > 0.0001;

		if (!FBP.frozen && FBP.bounceOffWalls && !mc.isGamePaused() && particleAge > 0) {
			if (!wasFrozen && allowedToMove) {
				boolean xCollided = prevPosX == posX;
				boolean zCollided = prevPosZ == posZ;

				if (xCollided)
					motionX = -prevMotionX * 0.625;
				if (zCollided)
					motionZ = -prevMotionZ * 0.625;

				if (!FBP.randomRotation && (xCollided || zCollided))
					calculateYAngle();
			} else
				wasFrozen = false;
		}
		if (FBP.frozen && FBP.bounceOffWalls && !wasFrozen)
			wasFrozen = true;

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		prevRot.copy(rot);

		prevParticleAlpha = particleAlpha;
		prevParticleScale = particleScale;

		if (!mc.isGamePaused() && (!FBP.frozen || killToggle)) {
			if (!killToggle) {
				if (!FBP.randomRotation) {
					if (!modeDebounce) {
						modeDebounce = true;

						rot.z = 0;

						calculateYAngle();
					}

					if (allowedToMove) {
						float x = MathUtil.absolute(rotStep.x * getMult());

						if (motionX > 0) {
							if (motionZ > 0)
								rot.x -= x;
							else if (motionZ < 0)
								rot.x += x;
						} else if (motionX < 0) {
							if (motionZ < 0)
								rot.x += x;
							else if (motionZ > 0)
							{
								rot.x -= x;
							}
						}
					}
				} else {
					if (modeDebounce)
					{
						modeDebounce = false;

						rot.z = (float) FBP.random.nextDouble(30, 400);
					}

					if (allowedToMove) {
						Vector3D newVector = new Vector3D(rotStep);
						newVector.scale(getMult());
						rot.add(newVector);
					}
				}
			}

			if (!FBP.infiniteDuration)
				particleAge++;

			if (this.particleAge >= this.particleMaxAge || killToggle) {
				particleScale *= 0.88 * endMult;

				if (particleAlpha > 0.01 && particleScale <= scaleAlpha)
					particleAlpha *= 0.68 * endMult;

				if (particleAlpha <= 0.01)
					setExpired();
			}

			if (!killToggle) {
				if (!onGround)
					motionY -= 0.04 * particleGravity;

				move(motionX, motionY, motionZ);

				rot.x = (float) FastMath.round(rot.x / 90) * 90;
				rot.z = (float) FastMath.round(rot.z / 90) * 90;

				if (MathUtil.absolute((float) motionX) > 0.00001)
					prevMotionX = motionX;
				if (MathUtil.absolute((float) motionZ) > 0.00001)
					prevMotionZ = motionZ;

				if (allowedToMove) {
					motionX *= 0.98;
					motionZ *= 0.98;
				}

				motionY *= 0.98;

				// PHYSICS
				if (FBP.entityCollision) {
					List<Entity> list = world.getEntitiesWithinAABB(Entity.class, this.getBoundingBox());

					for (Entity entityIn : list) {
						if (!entityIn.noClip) {
							float posX = (float) (this.posX - entityIn.posX);
							float posZ = (float) (this.posZ - entityIn.posZ);
							float posMax = MathUtil.absoluteMax(posX, posZ);

							if (posMax >= 0.0099) {
								posMax = (float) FastMath.sqrtQuick(posMax);
								posX /= posMax;
								posZ /= posMax;

								float f3 = 1 / posMax;

								if (f3 > 1)
									f3 = 1;

								this.motionX += posX * f3 / 20;
								this.motionZ += posZ * f3 / 20;

								if (!FBP.randomRotation)
									calculateYAngle();
								if (!FBP.frozen)
									this.onGround = false;
							}
						}
					}
				}

				if (FBP.waterPhysics) {
					if (isInWater()) {
						handleWaterMovement();

						if (FBP.INSTANCE.doesMaterialFloat(this.blockState.getMaterial())) {
							motionY = 0.11 + (particleScale / 1.25) * 0.02;
						} else {
							motionX *= 0.93;
							motionZ *= 0.93;
							particleGravity = 0.35F;

							motionY *= 0.85;
						}

						if (!FBP.randomRotation)
							calculateYAngle();

						if (onGround)
							onGround = false;
					} else {
						particleGravity = prevGravity;
					}
				}

				if (onGround) {
					if (FBP.lowTraction) {
						motionX *= 0.93;
						motionZ *= 0.93;
					} else {
						motionX *= 0.66;
						motionZ *= 0.66;
					}
				}
			}
		}
	}

	public boolean isInWater() {
		double scale = particleScale / 20;

		int minX = MathUtil.floor(posX - scale);
		int maxX = MathUtil.ceiling(posX + scale);

		int minY = MathUtil.floor(posY - scale);
		int maxY = MathUtil.ceiling(posY + scale);

		int minZ = MathUtil.floor(posZ - scale);
		int maxZ = MathUtil.ceiling(posZ + scale);

		if (world.isAreaLoaded(new StructureBoundingBox(minX, minY, minZ, maxX, maxY, maxZ), true)) {
			for (int x = minX; x < maxX; ++x) {
				for (int y = minY; y < maxY; ++y) {
					for (int z = minZ; z < maxZ; ++z) {
						IBlockState block = world.getBlockState(new BlockPos(x, y, z));

						if (block.getMaterial() == Material.WATER) {
							double floatLine = (float) (y + 1) - BlockLiquid.getLiquidHeightPercent(block.getValue(BlockLiquid.LEVEL));

							if (posY <= floatLine)
								return true;
						}
					}
				}
			}
		}

		return false;
	}

	private void handleWaterMovement() {
		dummyEntity.motionX = motionX;
		dummyEntity.motionY = motionY;
		dummyEntity.motionZ = motionZ;

		if (this.world.handleMaterialAcceleration(getBoundingBox().expand(0, -0.4, 0).contract(0.001, 0.001, 0.001), Material.WATER, dummyEntity)) {

			motionX = dummyEntity.motionX;
			motionY = dummyEntity.motionY;
			motionZ = dummyEntity.motionZ;
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
		if (KeyBindings.FBPKillParticles.isKeyDown() && !killToggle)
			killToggle = true;

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

	private void createRotationMatrix() {
		double rx0 = FBP.random.nextDouble();
		double ry0 = FBP.random.nextDouble();
		double rz0 = FBP.random.nextDouble();

		rotStep = new Vector3D(rx0 > 0.5 ? 1 : -1, ry0 > 0.5 ? 1 : -1, rz0 > 0.5 ? 1 : -1);

		rot.copy(rotStep);
	}

	@Override
	public int getBrightnessForRender(float partialTicks) {
		AxisAlignedBB boundingBox = getBoundingBox();

		if (this.world.isBlockLoaded(new BlockPos(posX, 0, posZ))) {
			double boundingBoxHeight = (boundingBox.maxY - boundingBox.minY) * 0.66;
			double posY = this.posY + boundingBoxHeight + 0.01 - particleScale / 10;
			return this.world.getCombinedLight(new BlockPos(posX, posY, posZ), 0);
		} else {
			return 0;
		}
	}

	private void calculateYAngle() {
		float angleSin = (float) FastMath.toDegrees(FastMath.asin(motionX / FastMath.sqrtQuick(motionX * motionX + motionZ * motionZ)));

		if (motionZ > 0)
			rot.y = -angleSin;
		else
			rot.y = angleSin;
	}

	float getMult() {
		return (float) (FastMath.sqrtQuick(motionX * motionX + motionZ * motionZ) * (FBP.randomRotation ? 200 : 500) * FBP.rotationMult);
	}
}
