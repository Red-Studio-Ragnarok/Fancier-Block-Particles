package io.redstudioragnarok.FBP.util;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec2f;

public class ParticleUtil {

    private static Vec2f[] particle = new Vec2f[] { new Vec2f(0, 0), new Vec2f(0, 0), new Vec2f(0, 0), new Vec2f(0, 0) };
    private static float textureX1, textureX2, textureY1, textureY2;

    private static Vec2f gasParticle = new Vec2f(0, 0);
    private static float textureX, textureY;

    public static Vec2f[] texturedParticle(TextureAtlasSprite particleTexture, float particleTextureJitterX, float particleTextureJitterY, int particleTextureIndexX, int particleTextureIndexY) {
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

        particle = new Vec2f[]{ new Vec2f(textureX2, textureY2), new Vec2f(textureX2, textureY1), new Vec2f(textureX1, textureY1), new Vec2f(textureX1, textureY2) };

        return particle;
    }

    public static Vec2f gasParticle(TextureAtlasSprite particleTexture) {
        textureX = particleTexture.getInterpolatedU((0.1f + 1) / 4 * 16);
        textureY = particleTexture.getInterpolatedV((0.1f + 1) / 4 * 16);

        gasParticle = new Vec2f(textureX, textureY);

        return gasParticle;
    }
}
