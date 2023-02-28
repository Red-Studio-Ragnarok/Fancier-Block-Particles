package io.redstudioragnarok.fbp.renderer;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.particles.FBPParticleRain;
import io.redstudioragnarok.fbp.particles.FBPParticleSnow;
import net.jafama.FastMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.IRenderHandler;

import static io.redstudioragnarok.fbp.FBP.*;

/**
 * This class extends IRenderHandler and is responsible for spawning weather particles.
 */
public class FBPWeatherRenderer extends IRenderHandler {

	private static boolean snowBiome = false;

	private static int tickCounter, secondaryTickCounter, desiredThunderMultiplier, surfaceHeight, Y;
	private static int thunderMultiplier = 0;

	private static float density;

	private static double angle, radius, X, Z, mX, mZ, mT;

	private static BlockPos.MutableBlockPos blockpos$mutableblockpos;
	private static Biome biome;

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
	}

	/**
	 * Updates density and spawn new weather particles.
	 */
	public void onUpdate() {
		if (mc.world.isRaining()) {

			// Smooth transition when switching from rain to thunder
			if (secondaryTickCounter++ >= 20 && dynamicWeather) {
				if (!snowBiome)
					desiredThunderMultiplier = mc.world.isThundering() ? 5 : 0;
				else
					desiredThunderMultiplier = mc.world.isThundering() ? 3 : 0;

				thunderMultiplier = thunderMultiplier < desiredThunderMultiplier ? thunderMultiplier + 1 : thunderMultiplier > desiredThunderMultiplier ? thunderMultiplier - 1 : thunderMultiplier;

				secondaryTickCounter = 0;
			}

			density = weatherParticleDensity + weatherRenderDistance * 4;

			if (tickCounter++ >= 12 - thunderMultiplier) {
				mX = mc.player.motionX * 26;
				mZ = mc.player.motionZ * 26;
				mT = FastMath.sqrtQuick(mX * mX + mZ * mZ) / 25;

				blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

				for (int i = 0; i < 32 * density; i++) {
					// Get random position within radius of a little over the player's render distance
					angle = FBP.random.nextDouble() * FastMath.PI * 2;
					radius = FastMath.sqrtQuick(FBP.random.nextDouble()) * 35 * weatherRenderDistance;
					X = mc.player.posX + mX + radius * FastMath.cosQuick(angle);
					Z = mc.player.posZ + mZ + radius * FastMath.sinQuick(angle);

					Y = (int) (mc.player.posY + 15 + FBP.random.nextDouble() * 10 + (mc.player.motionY * 6));

					blockpos$mutableblockpos.setPos(X, mc.player.posY, Z);
					biome = mc.world.getBiome(blockpos$mutableblockpos);

					snowBiome = biome.getEnableSnow();

					surfaceHeight = mc.world.getPrecipitationHeight(blockpos$mutableblockpos).getY();

					if (Y <= surfaceHeight + 2)
						Y = surfaceHeight + 10;

					if (biome.canRain()) {
						mc.effectRenderer.addEffect(new FBPParticleRain(mc.world, X, Y, Z, 0.1, FBP.random.nextDouble(0.75, 0.99) + mT / 2, 0.1, Blocks.SNOW.getDefaultState()));
					} else if (snowBiome) {
						if ((i & 1) == 0)
							mc.effectRenderer.addEffect(new FBPParticleSnow(mc.world, X, Y, Z, FBP.random.nextDouble(-0.5, 0.5), FBP.random.nextDouble(0.25, 1) + mT * 1.5, FBP.random.nextDouble(-0.5, 0.5), Blocks.SNOW.getDefaultState()));
					}
				}
				tickCounter = 0;
			}
			tickCounter++;
		}
	}
}
