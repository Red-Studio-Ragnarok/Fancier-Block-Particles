package dev.redstudio.fbp.models;

import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.*;

public final class ModelTransformer {

	public static void transform(final IBakedModel model, final IBlockState state, final long rand, final IVertexTransformer transformer) {
		final DummyBakedModel out = new DummyBakedModel(model);

		for (int i = 0; i <= 6; i++) {
			final EnumFacing side = (i == 6 ? null : EnumFacing.VALUES[i]);

			for (final BakedQuad quad : model.getQuads(state, side, rand))
				out.addQuad(side, transform(quad, transformer));
		}
	}

	private static BakedQuad transform(final BakedQuad quad, final IVertexTransformer transformer) {
		final VertexFormat format = quad.getFormat();
		final UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(format);

		LightUtil.putBakedQuad(new VertexTransformerWrapper(builder, quad, transformer), quad);

		return builder.build();
	}

	private static final class VertexTransformerWrapper implements IVertexConsumer {

		private final IVertexConsumer parent;
		private final BakedQuad parentQuad;
		private final IVertexTransformer transformer;
		@Getter private final VertexFormat vertexFormat;

		public VertexTransformerWrapper(final IVertexConsumer parent, final BakedQuad parentQuad, final IVertexTransformer transformer) {
			this.parent = parent;
			this.parentQuad = parentQuad;
			this.transformer = transformer;

			vertexFormat = parent.getVertexFormat();
		}

		// region Setters

		@Override
		public void setQuadTint(final int tint) {
			parent.setQuadTint(tint);
		}

		@Override
		public void setQuadOrientation(final EnumFacing orientation) {
			parent.setQuadOrientation(orientation);
		}

		@Override
		public void setApplyDiffuseLighting(final boolean diffuse) {
			parent.setApplyDiffuseLighting(diffuse);
		}

		@Override
		public void setTexture(final TextureAtlasSprite texture) {
			parent.setTexture(texture);
		}

		// endregion

		@Override
		public void put(final int elementId, final float... data) {
			final VertexFormatElement element = vertexFormat.getElement(elementId);

			parent.put(elementId, transformer.transform(parentQuad, element, data));
		}
	}

	public interface IVertexTransformer {
		float[] transform(final BakedQuad quad, final VertexFormatElement element, final float... data);
	}
}
