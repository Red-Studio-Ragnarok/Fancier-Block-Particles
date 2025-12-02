package dev.redstudio.fbp.models;

import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public final class DummyBakedModel implements IBakedModel {

	private final List<BakedQuad>[] quads = new List[7];
	private final IBakedModel parent;
	@Setter private TextureAtlasSprite particle;

	public DummyBakedModel(final IBakedModel parent) {
		this.parent = parent;

		for (int i = 0; i < quads.length; i++)
			quads[i] = new ArrayList<>();
	}

	public void addQuad(final EnumFacing side, final BakedQuad quad) {
		quads[side == null ? 6 : side.ordinal()].add(quad);
	}

	@Override
	public List<BakedQuad> getQuads(final IBlockState state, final EnumFacing side, final long rand) {
		return quads[side == null ? 6 : side.ordinal()];
	}

	@Override
	public boolean isAmbientOcclusion() {
		return parent == null || parent.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return parent == null || parent.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return particle != null ? particle : parent != null ? parent.getParticleTexture() : null;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(final ItemCameraTransforms.TransformType cameraTransformType) {
		final Pair<? extends IBakedModel, Matrix4f> pair = parent.handlePerspective(cameraTransformType);

		return pair.getLeft() != parent ? pair : ImmutablePair.of(this, pair.getRight());
	}
}
