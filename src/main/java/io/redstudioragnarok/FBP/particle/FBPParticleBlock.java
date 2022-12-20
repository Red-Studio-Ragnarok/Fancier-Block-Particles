package io.redstudioragnarok.FBP.particle;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.vector.Vector2D;
import io.redstudioragnarok.FBP.vector.Vector3D;
import net.jafama.FastMath;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

public class FBPParticleBlock extends Particle {

	public BlockPos blockPos;

	Block block;
	IBlockState blockState;

	BlockModelRenderer blockModelRenderer;

	IBakedModel bakedModel;

	Minecraft mc;

	EnumFacing facing;

	Vector3D prevRotation;
	Vector3D smoothRot = new Vector3D();
	Vector3D rot;

	long textureSeed;

	float startingAngle;
	float step = 0.00275f;

	float height;
	float prevHeight;

	float smoothStep;

	boolean spawned = false;
	long tick = -1;

	boolean blockSet = false;

	TileEntity tileEntity;

	public FBPParticleBlock(World worldIn, double posXIn, double posYIn, double posZIn, IBlockState state, long inputSeed) {
		super(worldIn, posXIn, posYIn, posZIn);

		blockPos = new BlockPos(posXIn, posYIn, posZIn);

		mc = Minecraft.getMinecraft();

		facing = mc.player.getHorizontalFacing();

		startingAngle = (float) FBP.random.nextDouble(0.03125, 0.0635);

		prevRotation = new Vector3D();
		rot = new Vector3D();

		switch (facing) {
		case EAST:
			rot.z = -startingAngle;
			rot.x = -startingAngle;
			break;
		case NORTH:
			rot.x = -startingAngle;
			rot.z = startingAngle;
			break;
		case SOUTH:
			rot.x = startingAngle;
			rot.z = -startingAngle;
			break;
		case WEST:
			rot.z = startingAngle;
			rot.x = startingAngle;
			break;
		}

		textureSeed = inputSeed;

		block = (blockState = state).getBlock();

		blockModelRenderer = mc.getBlockRendererDispatcher().getBlockModelRenderer();

		this.canCollide = false;

		bakedModel = mc.getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);

		tileEntity = worldIn.getTileEntity(blockPos);
	}

	@Override
	public void onUpdate() {

		if (!canCollide) {
			IBlockState state = mc.world.getBlockState(blockPos);

			if (state.getBlock() != FBP.FBPBlock || state.getBlock() == block) {
				if (blockSet && state.getBlock() == Blocks.AIR) {
					// the block was destroyed during the animation
					killParticle();

					FBP.FBPBlock.onBlockHarvested(mc.world, blockPos, state, null);
					mc.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
					return;
				}

				FBP.FBPBlock.copyState(blockPos, blockState, this);
				mc.world.setBlockState(blockPos, FBP.FBPBlock.getDefaultState(), 2);

				Chunk chunk = mc.world.getChunk(blockPos);
				chunk.resetRelightChecks();
				chunk.setLightPopulated(true);


				blockSet = true;
			}

			spawned = true;
		}

		if (this.isExpired || mc.isGamePaused())
			return;

		prevHeight = height;

		prevRotation.copy(rot);

		switch (facing) {
		case EAST:
			rot.z += step;
			rot.x += step;
			break;
		case NORTH:
			rot.x += step;
			rot.z -= step;
			break;
		case SOUTH:
			rot.x -= step;
			rot.z += step;
			break;
		case WEST:
			rot.z -= step;
			rot.x -= step;
			break;
		}

		height -= step * 5;

		step *= 1.5;
	}

	@Override
	public void renderParticle(BufferBuilder buff, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if (this.isExpired)
			return;

		if (canCollide) {
			Block b = mc.world.getBlockState(blockPos).getBlock();
			if (block != b && b != Blocks.AIR && mc.world.getBlockState(blockPos).getBlock() != blockState.getBlock()) {
				mc.world.setBlockState(blockPos, blockState, 2);

				if (tileEntity != null)
					mc.world.setTileEntity(blockPos, tileEntity);

				mc.world.sendPacketToServer(new CPacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, blockPos, facing));

				// cleanup just to make sure it gets removed
				FBP.eventHandler.removePosEntry(blockPos);
			}
			if (tick >= 1) {
				killParticle();
				return;
			}

			tick++;
		}
		if (!spawned)
			return;

		float x = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX) - 0.5f;
		float y = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY) - 0.5f;
		float z = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ) - 0.5f;

		smoothStep = ((float) (prevHeight + (height - prevHeight) * (double) partialTicks));

		rot.partialVector(prevRotation, partialTicks, smoothRot);

		if (smoothStep <= 0)
			smoothStep = 0;

		Vector3D t = new Vector3D(0, smoothStep, 0);
		Vector3D tRot = new Vector3D(0, smoothStep, 0);

		switch (facing) {
		case EAST:
			if (smoothRot.z > 0) {
				this.canCollide = true;
				smoothRot.z = 0;
				smoothRot.x = 0;
			}

			t.x = -smoothStep;
			t.z = smoothStep;

			tRot.x = 1;
			break;
		case NORTH:
			if (smoothRot.z < 0) {
				this.canCollide = true;
				smoothRot.x = 0;
				smoothRot.z = 0;
			}

			t.x = smoothStep;
			t.z = smoothStep;
			break;
		case SOUTH:
			if (smoothRot.x < 0) {
				this.canCollide = true;
				smoothRot.x = 0;
				smoothRot.z = 0;
			}

			t.x = -smoothStep;
			t.z = -smoothStep;

			tRot.x = 1;
			tRot.z = 1;
			break;
		case WEST:
			if (smoothRot.z < 0) {
				this.canCollide = true;
				smoothRot.z = 0;
				smoothRot.x = 0;
			}

			t.x = smoothStep;
			t.z = -smoothStep;

			tRot.z = 1;
			break;
		}

		if (tick == 0) {
			if ((!(FBP.frozen && !FBP.spawnWhileFrozen) && (FBP.spawnRedstoneBlockParticles || block != Blocks.REDSTONE_BLOCK)) && mc.gameSettings.particleSetting < 2) {
				spawnParticles();
			}
		}
		buff.setTranslation(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());

		Tessellator.getInstance().draw();
		mc.getRenderManager().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		buff.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		GlStateManager.pushMatrix();

		GlStateManager.enableCull();
		GlStateManager.translate(x, y, z);

		GlStateManager.translate(tRot.x, tRot.y, tRot.z);

		GlStateManager.rotate((float) FastMath.toDegrees(smoothRot.x), 1, 0, 0);
		GlStateManager.rotate((float) FastMath.toDegrees(smoothRot.z), 0, 0, 1);

		GlStateManager.translate(-tRot.x, -tRot.y, -tRot.z);
		GlStateManager.translate(t.x, t.y, t.z);

		blockModelRenderer.renderModelSmooth(mc.world, bakedModel, blockState, blockPos, buff, false, textureSeed);

		buff.setTranslation(0, 0, 0);

		Tessellator.getInstance().draw();
		GlStateManager.popMatrix();

		mc.getTextureManager().bindTexture(FBP.LOCATION_PARTICLE_TEXTURE);
		buff.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
	}

	private void spawnParticles() {
		if (mc.world.getBlockState(blockPos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockAir)
			return;

		AxisAlignedBB aabb = block.getSelectedBoundingBox(blockState, mc.world, blockPos);

		Vector2D[] corners = new Vector2D[] { new Vector2D((float) aabb.minX, (float) aabb.minZ), new Vector2D((float) aabb.maxX, (float) aabb.maxZ), new Vector2D((float) aabb.minX, (float) aabb.maxZ), new Vector2D((float) aabb.maxX, (float) aabb.minZ) };

		Vector2D middle = new Vector2D((float) (blockPos.getX() + 0.5), (float) (blockPos.getZ() + 0.5));

		for (Vector2D corner : corners) {
			double mX = middle.x - corner.x;
			double mZ = middle.y - corner.y;

			mX /= -0.5;
			mZ /= -0.5;

			mc.effectRenderer.addEffect(new FBPParticleDigging(mc.world, corner.x, blockPos.getY() + 0.1, corner.y, mX, 0, mZ, 0.6f, 1, 1, 1, block.getActualState(blockState, mc.world, blockPos), null, this.particleTexture).multipleParticleScaleBy(0.5f).multiplyVelocity(0.5f));
		}
	}

	public void killParticle() {
		this.isExpired = true;

		FBP.FBPBlock.blockNodes.remove(blockPos);
		FBP.eventHandler.removePosEntry(blockPos);
	}

	@Override
	public void setExpired() {
		FBP.eventHandler.removePosEntry(blockPos);
	}
}
