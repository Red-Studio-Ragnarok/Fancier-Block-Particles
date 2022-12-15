package io.redstudioragnarok.FBP.util;

import io.redstudioragnarok.FBP.vector.Vector2D;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ParticleUtil {

    private static Vector2D[] particle = new Vector2D[] { new Vector2D(), new Vector2D(), new Vector2D(), new Vector2D() };
    private static float textureX1, textureX2, textureY1, textureY2;

    private static Vector2D gasParticle = new Vector2D();
    private static float textureX, textureY;

    public static Vector2D[] texturedParticle(TextureAtlasSprite particleTexture, float particleTextureJitterX, float particleTextureJitterY, int particleTextureIndexX, int particleTextureIndexY) {
        if (particleTexture != null) {
            textureX1 = particleTexture.getInterpolatedU(particleTextureJitterX / 4 * 16);
            textureY1 = particleTexture.getInterpolatedV(particleTextureJitterY / 4 * 16);

            textureX2 = particleTexture.getInterpolatedU((particleTextureJitterX + 1) / 4 * 16);
            textureY2 = particleTexture.getInterpolatedV((particleTextureJitterY + 1) / 4 * 16);
        } else {
            textureX1 = (particleTextureIndexX + particleTextureJitterX / 4) / 16;
            textureY1 = (particleTextureIndexY + particleTextureJitterY / 4) / 16;

            textureX2 = textureX1 + 0.015F;
            textureY2 = textureY1 + 0.015F;
        }

        particle = new Vector2D[]{ new Vector2D(textureX2, textureY2), new Vector2D(textureX2, textureY1), new Vector2D(textureX1, textureY1), new Vector2D(textureX1, textureY2) };

        return particle;
    }

    public static Vector2D gasParticle(TextureAtlasSprite particleTexture) {
        textureX = particleTexture.getInterpolatedU((0.1f + 1) / 4 * 16);
        textureY = particleTexture.getInterpolatedV((0.1f + 1) / 4 * 16);

        gasParticle = new Vector2D(textureX, textureY);

        return gasParticle;
    }
}
