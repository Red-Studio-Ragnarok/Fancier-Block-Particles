package io.redstudioragnarok.FBP.handler;

import io.netty.util.internal.ConcurrentSet;
import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.model.ModelHelper;
import io.redstudioragnarok.FBP.node.BlockNode;
import io.redstudioragnarok.FBP.node.BlockPosNode;
import io.redstudioragnarok.FBP.particle.FBPParticleBlock;
import io.redstudioragnarok.FBP.particle.FBPParticleManager;
import io.redstudioragnarok.FBP.renderer.FBPRenderer;
import io.redstudioragnarok.FBP.renderer.FBPWeatherRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Objects;

import static io.redstudioragnarok.FBP.FBP.mc;

public class EventHandler {

	static IWorldEventListener worldEventListener;

	ConcurrentSet<BlockPosNode> list;

	static IRenderHandler currentWeatherRenderer;
	static ParticleManager currentEffectRenderer;

	public EventHandler() {
		list = new ConcurrentSet<>();

		worldEventListener = new IWorldEventListener() {
			@Override
			public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
			}

			@Override
			public void broadcastSound(int soundID, BlockPos pos, int data) {
			}

			@Override
			public void onEntityAdded(Entity entityIn) {
			}

			@Override
			public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
			}

			@Override
			public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
			}

			@Override
			public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
			}

			@Override
			public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
			}

			@Override
			public void playRecord(SoundEvent soundIn, BlockPos pos) {
			}

			@Override
			public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
			}

			@Override
			public void onEntityRemoved(Entity entityIn) {
			}

			@Override
			public void notifyLightSet(BlockPos pos) {
			}

			@Override
			public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
				if (FBP.enabled && FBP.fancyPlaceAnim && (flags == 11 || flags == 3) && !oldState.equals(newState)) {
					BlockPosNode node = getNodeWithPos(pos);

					if (node != null && !node.checked) {
						if (newState.getBlock() == FBP.FBPBlock || newState.getBlock() == Blocks.AIR || oldState.getBlock() == newState.getBlock()) {
							removePosEntry(pos);
							return;
						}

						IBlockState state = newState.getActualState(worldIn, pos);

						if (state.getBlock() instanceof BlockDoublePlant || !ModelHelper.isModelValid(state)) {
							removePosEntry(pos);
							return;
						}

						long seed = MathHelper.getPositionRandom(pos);

						boolean isNotFalling = true;

						if (state.getBlock() instanceof BlockFalling) {
							if (BlockFalling.canFallThrough(worldIn.getBlockState(pos.offset(EnumFacing.DOWN))))
								isNotFalling = false;
						}

						if (!FBP.INSTANCE.isBlacklisted(state.getBlock(), false) && isNotFalling) {
							node.checked = true;

							FBPParticleBlock particleBlock = new FBPParticleBlock(worldIn, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, state, seed);

							mc.effectRenderer.addEffect(particleBlock);

							FBP.FBPBlock.copyState(pos, state, particleBlock);
						}
					}
				}
			}
		};
	}

	@SubscribeEvent
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		List<Particle> particles = FBPRenderer.queuedParticles;

		if (particles.isEmpty()) {
			return;
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		bufferbuilder.begin(GL11.GL_QUADS, FBP.POSITION_TEX_COLOR_LMAP_NORMAL);

		Entity renderViewEntity = mc.getRenderViewEntity();
		float partialTicks = mc.getRenderPartialTicks();
		float rotX = ActiveRenderInfo.getRotationX();
		float rotZ = ActiveRenderInfo.getRotationZ();
		float rotYZ = ActiveRenderInfo.getRotationYZ();
		float rotXY = ActiveRenderInfo.getRotationXY();
		float rotXZ = ActiveRenderInfo.getRotationXZ();

		FBPRenderer.render = true;
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).renderParticle(bufferbuilder, renderViewEntity, partialTicks, rotX, rotZ, rotYZ, rotXY, rotXZ);
		}
		FBPRenderer.render = false;

		mc.entityRenderer.enableLightmap();
		RenderHelper.enableStandardItemLighting();

		tessellator.draw();

		RenderHelper.disableStandardItemLighting();
		mc.entityRenderer.disableLightmap();

		particles.clear();
	}

	@SubscribeEvent
	public void onInteractionEvent(RightClickBlock result) {
		if (result.getHitVec() == null || !(result.getItemStack().getItem() instanceof ItemBlock))
			return;

		boolean secondSlab = false;

		Block inHand = Block.getBlockFromItem(result.getItemStack().getItem());

		BlockPos pos = result.getPos();
		BlockPos posOffset = result.getPos().offset(Objects.requireNonNull(result.getFace()));

		IBlockState atPos = result.getWorld().getBlockState(pos);
		IBlockState atPosOffset = result.getWorld().getBlockState(posOffset);

		float x = (float) (result.getHitVec().x - pos.getX());
		float y = (float) (result.getHitVec().y - pos.getY());
		float z = (float) (result.getHitVec().z - pos.getZ());

		if (atPos.getBlock() == FBP.FBPBlock) {
			BlockNode node = FBP.FBPBlock.blockNodes.get(pos);

			if (node != null) {
				node.state.getBlock();
				boolean activated = node.originalBlock.onBlockActivated(result.getWorld(), pos, node.state, mc.player, result.getHand(), result.getFace(), x, y, z);

				if (activated)
					return;

				atPos = node.state;
			}

			if (atPos.getBlock() instanceof BlockSlab) {
				BlockSlab.EnumBlockHalf half = atPos.getValue(BlockSlab.HALF);

				if (result.getFace() == EnumFacing.UP) {
					if (half == EnumBlockHalf.BOTTOM) {
						secondSlab = true;
					}
				} else if (result.getFace() == EnumFacing.DOWN) {
					if (half == EnumBlockHalf.TOP) {
						secondSlab = true;
					}
				}
			}
		}

		if (atPosOffset.getBlock() == FBP.FBPBlock) {
			BlockNode blockNode = FBP.FBPBlock.blockNodes.get(posOffset);

			if (blockNode != null) {
				blockNode.state.getBlock();
				atPosOffset = blockNode.state;
			}
		}

		boolean addedOffset = false;

		BlockPosNode blockPosNode = new BlockPosNode();


		if (atPosOffset.getMaterial().isReplaceable() && !atPos.getBlock().isReplaceable(result.getWorld(), pos) && inHand.canPlaceBlockAt(result.getWorld(), posOffset)) {
			blockPosNode.add(posOffset);
			addedOffset = true;
		} else
			blockPosNode.add(pos);

		boolean okToAdd = inHand != Blocks.AIR && inHand.canPlaceBlockAt(result.getWorld(), addedOffset ? posOffset : pos) && !secondSlab;

		BlockPosNode lastBlockPosNode = getNodeWithPos(pos);
		BlockPosNode lastBlockPosNodeOffset = getNodeWithPos(posOffset);

		if (okToAdd) {
			boolean replaceable = (addedOffset ? atPosOffset : atPos).getBlock().isReplaceable(result.getWorld(), (addedOffset ? posOffset : pos));

			if (lastBlockPosNode != null && !addedOffset && lastBlockPosNode.checked) // replace
				return;
			if (lastBlockPosNodeOffset != null && addedOffset && (lastBlockPosNodeOffset.checked || replaceable)) // place on side
				return;

			Chunk chunk = mc.world.getChunk((addedOffset ? posOffset : pos));
			chunk.resetRelightChecks();
			chunk.setLightPopulated(true);

			list.add(blockPosNode);
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent tick) {
		if (!mc.isGamePaused() && mc.world != null && mc.world.provider.getWeatherRenderer() == FBP.fancyWeatherRenderer && FBP.enabled) {
			((FBPWeatherRenderer) FBP.fancyWeatherRenderer).onUpdate();
		}
	}

	@SubscribeEvent
	public void onWorldLoadEvent(WorldEvent.Load world) {
		world.getWorld().addEventListener(worldEventListener);
		list.clear();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent e) {
		if (e.getEntity() == mc.player) {
			FBP.fancyEffectRenderer = new FBPParticleManager(e.getWorld(), mc.renderEngine);
			FBP.fancyWeatherRenderer = new FBPWeatherRenderer();

			currentWeatherRenderer = mc.world.provider.getCloudRenderer();
			currentEffectRenderer = mc.effectRenderer;

			if (FBP.originalWeatherRenderer == null || (FBP.originalWeatherRenderer != currentWeatherRenderer && currentWeatherRenderer != FBP.fancyWeatherRenderer))
				FBP.originalWeatherRenderer = currentWeatherRenderer;
			if (FBP.originalEffectRenderer == null || (FBP.originalEffectRenderer != currentEffectRenderer && currentEffectRenderer != FBP.fancyEffectRenderer))
				FBP.originalEffectRenderer = currentEffectRenderer;

			if (FBP.enabled) {
				mc.effectRenderer = FBP.fancyEffectRenderer;

				if (FBP.fancyWeather)
					mc.world.provider.setWeatherRenderer(FBP.fancyWeatherRenderer);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerPlaceBlockEvent(BlockEvent.PlaceEvent placeEvent) {
		Block placedBlock = placeEvent.getPlacedBlock().getBlock();

		if (placedBlock == FBP.FBPBlock)
			placeEvent.setCanceled(true);
	}

	BlockPosNode getNodeWithPos(BlockPos pos) {
		for (BlockPosNode node : list) {
			if (node.hasPos(pos))
				return node;
		}
		return null;
	}

	public void removePosEntry(BlockPos pos) {
		for (int i = 0; i < list.size(); i++) {
			BlockPosNode node = getNodeWithPos(pos);

			if (node != null)
				list.remove(node);
		}
	}
}
