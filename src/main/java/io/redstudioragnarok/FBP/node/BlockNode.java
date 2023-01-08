package io.redstudioragnarok.FBP.node;

import io.redstudioragnarok.FBP.particle.FBPParticleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockNode {

	public FBPParticleBlock particle;
	public IBlockState state;
	public Block originalBlock;

	public BlockNode(IBlockState inputState, FBPParticleBlock inputParticle) {
		particle = inputParticle;
		state = inputState;
		originalBlock = inputState.getBlock();
	}
}
