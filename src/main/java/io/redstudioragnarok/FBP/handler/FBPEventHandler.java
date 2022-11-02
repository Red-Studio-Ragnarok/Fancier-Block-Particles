package io.redstudioragnarok.FBP.handler;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.model.FBPModelHelper;
import io.redstudioragnarok.FBP.node.BlockNode;
import io.redstudioragnarok.FBP.node.BlockPosNode;
import io.redstudioragnarok.FBP.particle.FBPParticleBlock;
import io.redstudioragnarok.FBP.particle.FBPParticleManager;
import io.redstudioragnarok.FBP.renderer.FBPRenderer;
import io.redstudioragnarok.FBP.renderer.FBPWeatherRenderer;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.*;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
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

public class FBPEventHandler {

	Minecraft mc;

	static IWorldEventListener listener;

	ConcurrentSet<BlockPosNode> list;

	public FBPEventHandler() {
		mc = Minecraft.getMinecraft();

		list = new ConcurrentSet<>();

		listener = new IWorldEventListener() {
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
			public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z,
					double xSpeed, double ySpeed, double zSpeed, int... parameters) {
			}

			@Override
			public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord,
					double xSpeed, double ySpeed, double zSpeed, int... parameters) {
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

						if (state.getBlock() instanceof BlockDoublePlant || !FBPModelHelper.isModelValid(state)) {
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

							FBPParticleBlock p = new FBPParticleBlock(worldIn, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, state, seed);

							mc.effectRenderer.addEffect(p);

							FBP.FBPBlock.copyState(pos, state, p);
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

		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		tessellator.draw();

		RenderHelper.disableStandardItemLighting();
		mc.entityRenderer.disableLightmap();

		particles.clear();
	}

	@SubscribeEvent
	public void onInteractionEvent(RightClickBlock e) {
		if (e.getHitVec() == null || !e.getWorld().isRemote || !(e.getItemStack().getItem() instanceof ItemBlock))
			return;

		BlockPos pos = e.getPos();
		BlockPos pos_o = e.getPos().offset(Objects.requireNonNull(e.getFace()));

		Block inHand;

		IBlockState atPos = e.getWorld().getBlockState(pos);
		IBlockState offset = e.getWorld().getBlockState(pos_o);

		boolean bool = false;

		float f = (float) (e.getHitVec().x - pos.getX());
		float f1 = (float) (e.getHitVec().y - pos.getY());
		float f2 = (float) (e.getHitVec().z - pos.getZ());

		if (atPos.getBlock() == FBP.FBPBlock) {
			BlockNode n = FBP.FBPBlock.blockNodes.get(pos);

			if (n != null) {
				n.state.getBlock();
				boolean activated = n.originalBlock.onBlockActivated(e.getWorld(), pos, n.state, mc.player, e.getHand(), e.getFace(), f, f1, f2);

				if (activated)
					return;

				atPos = n.state;
			}

			// if placed quicky atop each other
			if (atPos.getBlock() instanceof BlockSlab) {
				BlockSlab.EnumBlockHalf half = atPos.getValue(BlockSlab.HALF);

				if (e.getFace() == EnumFacing.UP) {
					if (half == EnumBlockHalf.BOTTOM) {
						bool = true;
					}
				} else if (e.getFace() == EnumFacing.DOWN) {
					if (half == EnumBlockHalf.TOP) {
						bool = true;
					}
				}
			}
		}

		if (offset.getBlock() == FBP.FBPBlock) {
			BlockNode n = FBP.FBPBlock.blockNodes.get(pos_o);

			if (n != null) {
				n.state.getBlock();
				offset = n.state;
			}
		}

		e.getItemStack();
		e.getItemStack().getItem();
		inHand = Block.getBlockFromItem(e.getItemStack().getItem());

		boolean addedOffset = false;

		BlockPosNode node = new BlockPosNode();


		if (!bool && offset.getMaterial().isReplaceable() && !atPos.getBlock().isReplaceable(e.getWorld(), pos) && inHand.canPlaceBlockAt(e.getWorld(), pos_o)) {
			node.add(pos_o);
			addedOffset = true;
		} else
			node.add(pos);

		boolean okToAdd = inHand != Blocks.AIR && inHand.canPlaceBlockAt(e.getWorld(), addedOffset ? pos_o : pos);

		// do torch check
		if (inHand instanceof BlockTorch) {
			BlockTorch bt = (BlockTorch) inHand;

			if (!bt.canPlaceBlockAt(e.getWorld(), pos_o))
				okToAdd = false;

			if (atPos.getBlock() == Blocks.TORCH) {
				for (EnumFacing fc : EnumFacing.VALUES) {
					BlockPos p = pos_o.offset(fc);
					Block bl = e.getWorld().getBlockState(p).getBlock();

					if (bl != Blocks.TORCH && bl != FBP.FBPBlock && bl.isSideSolid(bl.getDefaultState(), e.getWorld(), p, fc)) {
						okToAdd = true;
						break;
					} else
						okToAdd = false;
				}
			}
		}

		BlockPosNode last = getNodeWithPos(pos);
		BlockPosNode last_o = getNodeWithPos(pos_o);

		// add if all ok
		if (okToAdd) {
			boolean replaceable = (addedOffset ? offset : atPos).getBlock().isReplaceable(e.getWorld(), (addedOffset ? pos_o : pos));

			if (last != null && !addedOffset && last.checked) // replace
				return;
			if (last_o != null && addedOffset && (last_o.checked || replaceable)) // place on side
				return;

			Chunk c = mc.world.getChunk((addedOffset ? pos_o : pos));
			c.resetRelightChecks();
			c.setLightPopulated(true);

			list.add(node);
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent e) {
		if (!mc.isGamePaused() && mc.world != null && mc.world.provider.getWeatherRenderer() == FBP.fancyWeatherRenderer && FBP.enabled) {
			((FBPWeatherRenderer) FBP.fancyWeatherRenderer).onUpdate();
		}
	}

	@SubscribeEvent
	public void onWorldLoadEvent(WorldEvent.Load e) {
		FBPConfigHandler.init();

		e.getWorld().addEventListener(listener);
		list.clear();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent e) {
		if (e.getEntity() == mc.player) {
			FBP.fancyEffectRenderer = new FBPParticleManager(e.getWorld(), mc.renderEngine);
			FBP.fancyWeatherRenderer = new FBPWeatherRenderer();

			IRenderHandler currentWeatherRenderer = mc.world.provider.getCloudRenderer();

			if (FBP.originalWeatherRenderer == null || (FBP.originalWeatherRenderer != currentWeatherRenderer && currentWeatherRenderer != FBP.fancyWeatherRenderer))
				FBP.originalWeatherRenderer = currentWeatherRenderer;
			if (FBP.originalEffectRenderer == null || (FBP.originalEffectRenderer != mc.effectRenderer && FBP.originalEffectRenderer != FBP.fancyEffectRenderer))
				FBP.originalEffectRenderer = mc.effectRenderer;

			if (FBP.enabled) {
				mc.effectRenderer = FBP.fancyEffectRenderer;

				if (FBP.fancyRain || FBP.fancySnow)
					mc.world.provider.setWeatherRenderer(FBP.fancyWeatherRenderer);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerPlaceBlockEvent(BlockEvent.PlaceEvent e) {
		IBlockState bs = e.getPlacedBlock();
		Block placed = bs.getBlock();

		if (placed == FBP.FBPBlock)
			e.setCanceled(true);
	}

	BlockPosNode getNodeWithPos(BlockPos pos) {
		for (BlockPosNode n : list) {
			if (n.hasPos(pos))
				return n;
		}
		return null;
	}

	public void removePosEntry(BlockPos pos) {
		for (int i = 0; i < list.size(); i++) {
			BlockPosNode n = getNodeWithPos(pos);

			if (n != null)
				list.remove(n);
		}
	}
}
