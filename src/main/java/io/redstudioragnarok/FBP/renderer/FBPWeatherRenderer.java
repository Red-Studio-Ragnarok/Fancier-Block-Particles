package io.redstudioragnarok.FBP.renderer;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.particle.FBPParticleRain;
import io.redstudioragnarok.FBP.particle.FBPParticleSnow;
import net.jafama.FastMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.IRenderHandler;

import static io.redstudioragnarok.FBP.FBP.*;

public class FBPWeatherRenderer extends IRenderHandler {

	int tickCounter, secondaryTickCounter, desiredMultiplier;
	int multiplier = 0;

	static float density;

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
	}

	public void onUpdate() {
			if (mc.world.isRaining()) {

				// Smooth transition
				if (secondaryTickCounter++ >= 20 && dynamicWeather) {
					desiredMultiplier = mc.world.isThundering() ? 10 : 0;
					multiplier = multiplier < desiredMultiplier ? multiplier += 1 : multiplier > desiredMultiplier ? multiplier -= 1 : multiplier;
					secondaryTickCounter = 0;
				}

				density = weatherParticleDensity + weatherRenderDistance * 4;

				if (tickCounter++ >= 12 - multiplier) {
					double mX = mc.player.motionX * 26;
					double mZ = mc.player.motionZ * 26;
					double mT = FastMath.sqrtQuick(mX * mX + mZ * mZ) / 25;

					BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

					for (int i = 0; i < 32 * density; i++) {
						// Get random position within radius of a little over the player's render distance
						double angle = FBP.random.nextDouble() * FastMath.PI * 2;
						double radius = FastMath.sqrtQuick(FBP.random.nextDouble()) * 35 * weatherRenderDistance;
						double X = mc.player.posX + mX + radius * FastMath.cosQuick(angle);
						double Z = mc.player.posZ + mZ + radius * FastMath.sinQuick(angle);

						// Check if position is within a snow biomes
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
								if (i % 2 == 0) {
									mc.effectRenderer.addEffect(new FBPParticleSnow(mc.world, X, Y, Z, FBP.random.nextDouble(-0.5, 0.5), FBP.random.nextDouble(0.25, 1) + mT * 1.5f, FBP.random.nextDouble(-0.5, 0.5), Blocks.SNOW.getDefaultState()));
								}
							} else {
								mc.effectRenderer.addEffect(new FBPParticleRain(mc.world, X, Y, Z, 0.1, FBP.random.nextDouble(0.75, 0.99) + mT / 2, 0.1, Blocks.SNOW.getDefaultState()));
							}
						}
					}
					tickCounter = 0;
				}
				tickCounter++;
			}
	}
}
