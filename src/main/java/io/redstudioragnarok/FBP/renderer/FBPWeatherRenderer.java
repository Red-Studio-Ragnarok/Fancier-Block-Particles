package io.redstudioragnarok.FBP.renderer;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.particle.FBPParticleRain;
import io.redstudioragnarok.FBP.particle.FBPParticleSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.IRenderHandler;

import static io.redstudioragnarok.FBP.FBP.mc;

public class FBPWeatherRenderer extends IRenderHandler {

	int tickCounter;

	public FBPWeatherRenderer() {
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
	}

	public void onUpdate() {
		if (FBP.fancyWeather) {
			float f = mc.world.getRainStrength(mc.getRenderPartialTicks());

			if (f > 0.0F) {
				if (tickCounter++ >= 2) {
					int r = 35;

					double mX = mc.player.motionX * 26;
					double mZ = mc.player.motionZ * 26;
					double mT = MathHelper.sqrt(mX * mX + mZ * mZ) / 25;

					BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

					int j = 0;

					for (int i = 0; i < 8 * FBP.weatherParticleDensity; i++) {
						// get random position within radius of a little over the player's render
						// distance
						double angle = FBP.random.nextDouble() * Math.PI * 2;
						double radius = MathHelper.sqrt(FBP.random.nextDouble()) * r;
						double X = mc.player.posX + mX + radius * Math.cos(angle);
						double Z = mc.player.posZ + mZ + radius * Math.sin(angle);

						if (mc.player.getDistance(X, mc.player.posY, Z) > mc.gameSettings.renderDistanceChunks * 16)
							continue;

						// check if position is within a snow biome
						blockpos$mutableblockpos.setPos(X, mc.player.posY, Z);
						Biome biome = mc.world.getBiome(blockpos$mutableblockpos);

						int surfaceHeight = mc.world.getPrecipitationHeight(blockpos$mutableblockpos).getY();

						int Y = (int) (mc.player.posY + 15 + FBP.random.nextDouble() * 10 + (mc.player.motionY * 6));

						if (Y <= surfaceHeight + 2)
							Y = surfaceHeight + 10;

						if (biome.canRain() || biome.getEnableSnow()) {
							float temp = biome.getTemperature(blockpos$mutableblockpos);
							float finalTemp = mc.world.getBiomeProvider().getTemperatureAtHeight(temp, surfaceHeight);

							if (finalTemp < 0.15F) {
								if (FBP.fancyWeather && i % 2 == 0) {
									mc.effectRenderer.addEffect(new FBPParticleSnow(mc.world, X, Y, Z, FBP.random.nextDouble(-0.5, 0.5), FBP.random.nextDouble(0.25, 1) + mT * 1.5f, FBP.random.nextDouble(-0.5, 0.5), Blocks.SNOW.getDefaultState()));
								}
							} else if (FBP.fancyWeather) {
								mc.effectRenderer.addEffect(new FBPParticleRain(mc.world, X, Y, Z, 0.1,
										FBP.random.nextDouble(0.75, 0.99) + mT / 2, 0.1,
										Blocks.SNOW.getDefaultState()));
							}

							j++;
						}
					}
					tickCounter = 0;
				}

				tickCounter++;
			}
		}
	}
}
