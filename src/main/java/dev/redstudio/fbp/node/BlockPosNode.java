package dev.redstudio.fbp.node;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.util.math.BlockPos;

public final class BlockPosNode {

	final ConcurrentSet<BlockPos> possible = new ConcurrentSet<>();

	public boolean checked = false;

	public void add(final BlockPos blockPos) {
		possible.add(blockPos);
	}

	public boolean hasPos(final BlockPos blockPos) {
		return possible.contains(blockPos);
	}
}
