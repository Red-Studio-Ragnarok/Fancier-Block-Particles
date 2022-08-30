package com.TominoCZ.FBP.node;

import com.TominoCZ.FBP.particle.FBPParticleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class BlockNode {
	public IBlockState state;
	public Block originalBlock;

	public FBPParticleBlock particle;

	public BlockNode(IBlockState s, FBPParticleBlock p) {
		particle = p;
		state = s;
		originalBlock = s.getBlock();
	}
}
