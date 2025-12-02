package dev.redstudio.fbp.node;

import dev.redstudio.fbp.particles.FBPParticleBlock;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.state.IBlockState;

@RequiredArgsConstructor
public final class BlockNode {

	public final FBPParticleBlock particle;
	public final IBlockState state;
}
