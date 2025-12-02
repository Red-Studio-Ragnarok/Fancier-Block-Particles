package dev.redstudio.fbp.block;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.handlers.EventHandler;
import dev.redstudio.fbp.material.DummyMaterial;
import dev.redstudio.fbp.node.BlockNode;
import dev.redstudio.fbp.particles.FBPParticleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static dev.redstudio.fbp.ProjectConstants.ID;

public final class AnimationDummyBlock extends Block {

	public final ConcurrentHashMap<BlockPos, BlockNode> blockNodes = new ConcurrentHashMap<>();

	public AnimationDummyBlock() {
		super(new DummyMaterial());

		setRegistryName(new ResourceLocation(ID, "PlaceholderBlock"));

		translucent = true;
	}

	public void copyState(BlockPos pos, IBlockState state, FBPParticleBlock p) {
		if (blockNodes.containsKey(pos))
			return;

		blockNodes.put(pos, new BlockNode(p, state));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (blockNodes.containsKey(pos)) {
			BlockNode node = blockNodes.get(pos);

			return node.state.getBlock().onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
		}

		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.isNormalCube();
		}

		return super.isNormalCube(state, world, pos);
	}

	public boolean isAir(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getBlock().isAir(state, world, pos);
		}

		return super.isAir(state, world, pos);
	}

	public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getBlock().isBed(state, world, pos, player);
		}

		return super.isBed(state, world, pos, player);
	}

	public boolean isBedFoot(IBlockAccess world, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getBlock().isBedFoot(world, pos);
		}

		return super.isBedFoot(world, pos);
	}

	public boolean isBurning(IBlockAccess world, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getBlock().isBurning(world, pos);
		}

		return super.isBurning(world, pos);
	}

	public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getBlock().isFlammable(world, pos, face);
		}

		return super.isFlammable(world, pos, face);
	}

	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getBlock().isLadder(state, world, pos, entity);
		}

		return super.isLadder(state, world, pos, entity);
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getMaterial().isReplaceable();
		}

		return material.isReplaceable();
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getBlock().isPassable(worldIn, pos);
		}

		return !material.blocksMovement();
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			n.state.getBlock().onEntityCollision(worldIn, pos, state, entityIn);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getCollisionBoundingBox(worldIn, pos);
		}

		return Block.FULL_BLOCK_AABB.offset(pos);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getBoundingBox(worldIn, pos);
		}

		return Block.FULL_BLOCK_AABB.offset(pos);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
	}

	@Override
	public float getBlockHardness(IBlockState blockState, World w, BlockPos pos) {
		if (blockNodes.containsKey(pos)) {
			BlockNode n = blockNodes.get(pos);

			return n.state.getBlockHardness(w, pos);
		}

		return blockState.getBlockHardness(w, pos);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		BlockNode node = FBP.DUMMY_BLOCK.blockNodes.get(pos);

		if (node == null)
			return;

		if (state.getBlock() != node.state.getBlock() && (worldIn.getBlockState(pos).getBlock() instanceof AnimationDummyBlock || state.getBlock() instanceof AnimationDummyBlock))
			Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(pos, node.state);

		if (node.particle != null)
			node.particle.killParticle();

		// cleanup just to make sure it gets removed
		EventHandler.removePosEntry(pos);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean b) {
		try {
			if (blockNodes.containsKey(pos))
				blockNodes.get(pos).state.addCollisionBoxToList(worldIn, pos, entityBox, collidingBoxes, entityIn, b);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public float getExplosionResistance(World w, BlockPos p, Entity e, Explosion ex) {
		if (blockNodes.containsKey(p))
			return blockNodes.get(p).state.getBlock().getExplosionResistance(w, p, e, ex);

		return super.getExplosionResistance(w, p, e, ex);
	}

	@Override
	public float getExplosionResistance(Entity e) {
		if (blockNodes.containsKey(e.getPosition()))
			return blockNodes.get(e.getPosition()).state.getBlock().getExplosionResistance(e);

		return super.getExplosionResistance(e);
	}

	@Override
	public float getEnchantPowerBonus(World w, BlockPos p) {
		if (blockNodes.containsKey(p))
			return blockNodes.get(p).state.getBlock().getEnchantPowerBonus(w, p);

		return super.getEnchantPowerBonus(w, p);
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getBlock().getFlammability(world, pos, face);

		return super.getFlammability(world, pos, face);
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getBlock().getFireSpreadSpeed(world, pos, face);

		return super.getFireSpreadSpeed(world, pos, face);
	}

	@Override
	public boolean getWeakChanges(IBlockAccess world, BlockPos pos) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getBlock().getWeakChanges(world, pos);

		return super.getWeakChanges(world, pos);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		if (blockNodes.containsKey(pos)) {
			BlockNode node = blockNodes.get(pos);

			if (node.state.getBlock() != this)
				return blockNodes.get(pos).state.getBlock().getPickBlock(node.state, target, world, pos, player);
		}

		return new ItemStack(Blocks.AIR);
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getWeakPower(blockAccess, pos, side);

		return 0;
	}

	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getBlock().canPlaceTorchOnTop(state, world, pos);

		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getBlock().canPlaceBlockAt(worldIn, pos);

		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getBlock().canPlaceBlockOnSide(worldIn, pos, side);

		return false;
	}

	@Override
	public IBlockState getExtendedState(IBlockState s, IBlockAccess w, BlockPos p) {
		if (blockNodes.containsKey(p))
			return blockNodes.get(p).state.getBlock().getExtendedState(s, w, p);

		return super.getExtendedState(s, w, p);
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity) {
		if (!blockNodes.containsKey(pos))
			return SoundType.STONE;

		BlockNode n = blockNodes.get(pos);

		return n.state.getBlock().getSoundType(n.state, world, pos, entity);
	}

	@Override
	public boolean canConnectRedstone(IBlockState s, IBlockAccess w, BlockPos pos, EnumFacing side) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getBlock().canConnectRedstone(s, w, pos, side);

		return false;
	}

	@Override
	public void onNeighborChange(IBlockAccess w, BlockPos pos, BlockPos p) {
		if (blockNodes.containsKey(pos))
			blockNodes.get(pos).state.getBlock().onNeighborChange(w, pos, p);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if (blockNodes.containsKey(pos))
			blockNodes.get(pos).state.getBlock().onBlockAdded(worldIn, pos, state);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getBlock().getDrops(world, pos, state, fortune);

		return super.getDrops(world, pos, state, fortune);
	}

	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getBlock().getExpDrop(state, world, pos, fortune);

		return super.getExpDrop(state, world, pos, fortune);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random r, int i) {
		return Items.AIR;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public float getAmbientOcclusionLightValue(IBlockState state) {
		return 1;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		if (blockNodes.containsKey(pos))
			return blockNodes.get(pos).state.getBlock().isSideSolid(base_state, world, pos, side);

		return true;
	}
}
