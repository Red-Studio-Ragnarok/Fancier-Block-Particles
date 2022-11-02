package io.redstudioragnarok.FBP.particle;

import io.redstudioragnarok.FBP.FBP;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class FBPParticleManager extends ParticleManager {

	public static int ParticleSpawned;

	private static TextureAtlasSprite white;

	Minecraft mc;

	public FBPParticleManager(World worldIn, TextureManager rendererIn) {
		super(worldIn, rendererIn);

		mc = Minecraft.getMinecraft();

		white = mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture((Blocks.SNOW.getDefaultState()));
	}

	public void carryOver() {
		if (Minecraft.getMinecraft().effectRenderer == this)
			return;

		this.fxLayers = (mc.effectRenderer).fxLayers;
		this.particleEmitters = (mc.effectRenderer).particleEmitters;
		this.queue = (mc.effectRenderer).queue;

	}

	@Override
	public void addEffect(Particle effect) {
		Particle toAdd = effect;

		if (!(toAdd instanceof FBPParticleSnow) && !(toAdd instanceof FBPParticleRain)) {
			IBlockState blockState;
			if (FBP.fancyRain && toAdd instanceof ParticleRain) {
				effect.setAlphaF(0);
			} else if (toAdd instanceof FBPParticleDigging) {
				blockState = ((ParticleDigging)effect).sourceState;

				if (blockState != null && !(FBP.frozen && !FBP.spawnWhileFrozen) && (FBP.spawnRedstoneBlockParticles || blockState.getBlock() != Blocks.REDSTONE_BLOCK)) {
					if (blockState.getBlock() instanceof BlockLiquid || FBP.INSTANCE.isBlacklisted(blockState.getBlock(), true)) {
						effect.setExpired();
						return;
					}
				}
			} else if (FBP.fancyFlame && toAdd instanceof ParticleFlame && !(toAdd instanceof FBPParticleFlame) && Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
				toAdd = new FBPParticleFlame(world, effect.posX, effect.posY, effect.posZ, FBP.random.nextDouble() * 0.25, true);
				effect.setExpired();
			} else if (FBP.fancySmoke && toAdd instanceof ParticleSmokeNormal && !(toAdd instanceof FBPParticleSmokeNormal) && Minecraft.getMinecraft().gameSettings.particleSetting < 2) {
				ParticleSmokeNormal p = (ParticleSmokeNormal) effect;

				toAdd = new FBPParticleSmokeNormal(world, effect.posX, effect.posY, effect.posZ, effect.motionX, effect.motionY, effect.motionZ, effect.particleScale, white, p);

				toAdd.setRBGColorF(MathHelper.clamp(effect.getRedColorF() + 0.1f, 0.1f, 1), MathHelper.clamp(effect.getGreenColorF() + 0.1f, 0.1f, 1), MathHelper.clamp(effect.getBlueColorF() + 0.1f, 0.1f, 1));

				toAdd.setMaxAge(effect.particleMaxAge);
			} else if (toAdd instanceof ParticleDigging) {
				blockState = ((ParticleDigging)effect).sourceState;


				if (blockState != null && !(FBP.frozen && !FBP.spawnWhileFrozen) && (FBP.spawnRedstoneBlockParticles || blockState.getBlock() != Blocks.REDSTONE_BLOCK)) {
					effect.setExpired();

					if (!(blockState.getBlock() instanceof BlockLiquid) && !FBP.INSTANCE.isBlacklisted(blockState.getBlock(), true)) {
						toAdd = new FBPParticleDigging(world, effect.posX, effect.posY - 0.1D, effect.posZ, 0, 0, 0, effect.particleScale, toAdd.getRedColorF(), toAdd.getGreenColorF(), toAdd.getBlueColorF(), blockState, null, effect.particleTexture);
					} else
						return;
				}
			}
		}

		if (toAdd != effect)
			effect.setExpired();

		super.addEffect(toAdd);
		ParticleSpawned ++;
	}

	@Override
	public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
		Block b = state.getBlock();

		if (!b.isAir(state, world, pos) && !b.addDestroyEffects(world, pos, this) && b != FBP.FBPBlock) {
			state = state.getActualState(world, pos);
			b = state.getBlock();

			TextureAtlasSprite texture = mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);

			for (int j = 0; j < FBP.particlesPerAxis; ++j) {
				for (int k = 0; k < FBP.particlesPerAxis; ++k) {
					for (int l = 0; l < FBP.particlesPerAxis; ++l) {
						double d0 = pos.getX() + ((j + 0.5D) / FBP.particlesPerAxis);
						double d1 = pos.getY() + ((k + 0.5D) / FBP.particlesPerAxis);
						double d2 = pos.getZ() + ((l + 0.5D) / FBP.particlesPerAxis);

						if ((!(b instanceof BlockLiquid) && !(FBP.frozen && !FBP.spawnWhileFrozen)) && (FBP.spawnRedstoneBlockParticles || b != Blocks.REDSTONE_BLOCK) && !FBP.INSTANCE.isBlacklisted(b, true)) {
							float scale = (float) FBP.random.nextDouble(0.75, 1);

							FBPParticleDigging toSpawn = new FBPParticleDigging(world, d0, d1, d2, d0 - pos.getX() - 0.5D, -0.001, d2 - pos.getZ() - 0.5D, scale, 1, 1, 1, state, null, texture).setBlockPos(pos);

							addEffect(toSpawn);
						}
					}
				}
			}
		}
	}

	@Override
	public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = world.getBlockState(pos);

		if (iblockstate.getBlock() == FBP.FBPBlock)
			return;

		if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
			int i = pos.getX();
			int j = pos.getY();
			int k = pos.getZ();
			AxisAlignedBB axisalignedbb = iblockstate.getBoundingBox(world, pos);

			double d0, d1, d2;

			RayTraceResult obj = Minecraft.getMinecraft().objectMouseOver;

			if (obj == null || obj.hitVec == null)
				obj = new RayTraceResult(null, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));

			if (FBP.smartBreaking && (!(iblockstate.getBlock() instanceof BlockLiquid) && !(FBP.frozen && !FBP.spawnWhileFrozen)) && (FBP.spawnRedstoneBlockParticles || iblockstate.getBlock() != Blocks.REDSTONE_BLOCK)) {
				d0 = obj.hitVec.x + FBP.random.nextDouble(-0.21, 0.21) * Math.abs(axisalignedbb.maxX - axisalignedbb.minX);
				d1 = obj.hitVec.y + FBP.random.nextDouble(-0.21, 0.21) * Math.abs(axisalignedbb.maxY - axisalignedbb.minY);
				d2 = obj.hitVec.z + FBP.random.nextDouble(-0.21, 0.21) * Math.abs(axisalignedbb.maxZ - axisalignedbb.minZ);
			} else {
				d0 = i + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2D) + 0.1D + axisalignedbb.minX;
				d1 = j + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2D) + 0.1D + axisalignedbb.minY;
				d2 = k + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2D) + 0.1D + axisalignedbb.minZ;
			}

			switch (side) {
			case DOWN:
				d1 = j + axisalignedbb.minY - 0.1D;
				break;
			case EAST:
				d0 = i + axisalignedbb.maxX + 0.1D;
				break;
			case NORTH:
				d2 = k + axisalignedbb.minZ - 0.1D;
				break;
			case SOUTH:
				d2 = k + axisalignedbb.maxZ + 0.1D;
				break;
			case UP:
				d1 = j + axisalignedbb.maxY + 0.1D;
				break;
			case WEST:
				d0 = i + axisalignedbb.minX - 0.1D;
				break;
			default:
				break;
			}

			if ((!(iblockstate.getBlock() instanceof BlockLiquid) && !(FBP.frozen && !FBP.spawnWhileFrozen)) && (FBP.spawnRedstoneBlockParticles || iblockstate.getBlock() != Blocks.REDSTONE_BLOCK)) {

				int damage = 0;

				try {
					DestroyBlockProgress progress;
					Map<Integer, DestroyBlockProgress> mp = (mc.renderGlobal).damagedBlocks;

					if (!mp.isEmpty()) {

						for (DestroyBlockProgress o : mp.values()) {
							progress = o;

							if (progress.getPosition().equals(pos)) {
								damage = progress.getPartialBlockDamage();
								break;
							}
						}
					}
				} catch (Throwable e) {
					// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
				}

				Particle toSpawn;

				if (!FBP.INSTANCE.isBlacklisted(iblockstate.getBlock(), true)) {
					toSpawn = new FBPParticleDigging(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, -2, 1, 1, 1, iblockstate, side, null).setBlockPos(pos);

					if (FBP.smartBreaking) {
						toSpawn = ((FBPParticleDigging) toSpawn).MultiplyVelocity(side == EnumFacing.UP ? 0.7F : 0.15F);
						toSpawn = toSpawn.multipleParticleScaleBy(0.325F + (damage / 10f) * 0.5F);
					} else {
						toSpawn = ((FBPParticleDigging) toSpawn).MultiplyVelocity(0.2F);
						toSpawn = toSpawn.multipleParticleScaleBy(0.6F);
					}

					addEffect(toSpawn);
				}
			}
		}
	}
}
