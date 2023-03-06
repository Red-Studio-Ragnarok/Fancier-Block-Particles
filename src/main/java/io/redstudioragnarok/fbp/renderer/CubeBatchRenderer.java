package io.redstudioragnarok.fbp.renderer;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.renderer.color.IColorProvider;
import io.redstudioragnarok.fbp.renderer.light.ILightCoordProvider;
import io.redstudioragnarok.fbp.renderer.texture.ITexCoordProvider;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CubeBatchRenderer {

	private static final WorldVertexBufferUploader VBO_UPLOADER = new WorldVertexBufferUploader();
	private static final Map<RenderType, BufferBuilder> BUFFER_BUILDERS = Stream.of(RenderType.values())
			.collect(Collectors.toMap(Function.identity(), renderType -> new BufferBuilder(1 << 20), (u, v) -> {
				throw new IllegalStateException(String.format("Duplicate key %s", u));
			}, () -> new EnumMap<>(RenderType.class)));

	public static void renderCube(RenderType renderType, float x, float y, float z, float rotX, float rotY, float rotZ,
			float scaleX, float scaleY, float scaleZ, ITexCoordProvider texCoordProvider, IColorProvider colorProvider,
			ILightCoordProvider lightCoordProvider) {
		FastCubeUploader.putCube(getBuffer(renderType), x, y, z, rotX, rotY, rotZ, scaleX, scaleY, scaleZ,
				texCoordProvider, colorProvider, lightCoordProvider);
	}

	private static BufferBuilder getBuffer(RenderType renderType) {
		BufferBuilder buffer = BUFFER_BUILDERS.get(renderType);
		if (!buffer.isDrawing) {
			buffer.begin(GL11.GL_QUADS, FBP.vertexFormat);
		}
		return buffer;
	}

	public static void endAllBatches() {
		BUFFER_BUILDERS.forEach(CubeBatchRenderer::endBatch);
	}

	public static void endBatch(RenderType renderType) {
		BufferBuilder buffer = BUFFER_BUILDERS.get(renderType);
		if (buffer == null) {
			return;
		}
		endBatch(renderType, buffer);
	}

	private static void endBatch(RenderType renderType, BufferBuilder buffer) {
		if (!buffer.isDrawing) {
			return;
		}
		buffer.finishDrawing();
		if (buffer.vertexCount > 0) {
			renderType.setupRenderState();
			VBO_UPLOADER.draw(buffer);
			renderType.clearRenderState();
		}
		buffer.reset();
	}

}
