package io.redstudioragnarok.FBP.renderer.texture;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class TextureUtil {

	private static float u0;
	private static float v0;
	private static float u1;
	private static float v1;
	private static final ITexCoordProvider UNIFORM_TEX_COORD_PROVIDER = new ITexCoordProvider() {

		@Override
		public float u0(EnumFacing facing) {
			return u0;
		}

		@Override
		public float v0(EnumFacing facing) {
			return v0;
		}

		@Override
		public float u1(EnumFacing facing) {
			return u1;
		}

		@Override
		public float v1(EnumFacing facing) {
			return v1;
		}

	};

	public static ITexCoordProvider particleTexCoordProvider(TextureAtlasSprite texture, float textureJitterX,
			float textureJitterY, int textureIndexX, int textureIndexY) {
		if (texture != null) {
			return uniformTexCoordProvider(texture.getInterpolatedU(textureJitterX * 4.0F),
					texture.getInterpolatedV(textureJitterY * 4.0F),
					texture.getInterpolatedU((textureJitterX + 1.0F) * 4.0F),
					texture.getInterpolatedV((textureJitterY + 1.0F) * 4.0F));
		} else {
			return uniformTexCoordProvider((textureIndexX + textureJitterX * 0.25F) * 0.0625F,
					(textureIndexY + textureJitterY * 0.25F) * 0.0625F,
					(textureIndexX + textureJitterX * 0.25F) * 0.0625F + 0.015609375F,
					(textureIndexY + textureJitterY * 0.25F) * 0.0625F + 0.015609375F);
		}
	}

	public static ITexCoordProvider uniformTexCoordProvider(float u0, float v0, float u1, float v1) {
		TextureUtil.u0 = u0;
		TextureUtil.v0 = v0;
		TextureUtil.u1 = u1;
		TextureUtil.v1 = v1;
		return UNIFORM_TEX_COORD_PROVIDER;
	}

	public static ITexCoordProvider pointTexCoordProvider(float u, float v) {
		return uniformTexCoordProvider(u, v, u, v);
	}

}
