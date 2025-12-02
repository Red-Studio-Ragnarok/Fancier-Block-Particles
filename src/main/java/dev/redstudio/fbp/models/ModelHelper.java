package dev.redstudio.fbp.models;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public final class ModelHelper {

	private static int vertexes = 0;

	private static boolean isAllCorruptedTexture = true;

	public static boolean isModelValid(final IBlockState state) {
		final IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(state);

		if (model.getParticleTexture() == null || model.getParticleTexture().getIconName().equals("missingno"))
			return false;

		vertexes = 0;

		ModelTransformer.transform(model, state, 0, (quad, element, data) -> {
			if (element.getUsage() == VertexFormatElement.EnumUsage.POSITION)
				vertexes++;

			if (!quad.getSprite().getIconName().equals("missingno"))
				isAllCorruptedTexture = false;

			return data;
		});

		return (vertexes >= 3) && !isAllCorruptedTexture;
	}
}
