package io.redstudioragnarok.fbp.renderer.light;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class LightUtil {

	private static int lightCoord;
	private static final ILightCoordProvider UNIFORM_LIGHT_COORD_PROVIDER = facing -> lightCoord;

	public static int pack(int skyLight, int blockLight) {
		return skyLight << 20 | blockLight << 4;
	}

	public static int block(int combinedLight) {
		return (combinedLight >> 4) & 15;
	}

	public static int sky(int combinedLight) {
		return combinedLight >> 20;
	}

	public static ILightCoordProvider uniformLightCoordProvider(int skyLight, int blockLight) {
		return uniformLightCoordProvider(pack(skyLight, blockLight));
	}

	public static ILightCoordProvider uniformLightCoordProvider(int combinedLight) {
		LightUtil.lightCoord = combinedLight;
		return UNIFORM_LIGHT_COORD_PROVIDER;
	}

	public static int getCombinedLight(World world, double x, double y, double z) {
		return getCombinedLight(world, MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
	}

	public static int getCombinedLight(World world, int x, int y, int z) {
		if (y < 0 || y >= 256)
			return 0;
		Chunk chunk = world.getChunk(x >> 4, z >> 4);
		ExtendedBlockStorage section = chunk.getBlockStorageArray()[y >> 4];
		if (section != null) {
			if (section.get(x & 15, y & 15, z & 15).useNeighborBrightness()) {
				int light = 0;
				if (y > 0)
					light = getNeighborLight(world, chunk, section, x, y - 1, z, light);
				if (y < 255)
					light = getNeighborLight(world, chunk, section, x, y + 1, z, light);
				light = getNeighborLight(world, chunk, section, x, y, z - 1, light);
				light = getNeighborLight(world, chunk, section, x, y, z + 1, light);
				light = getNeighborLight(world, chunk, section, x - 1, y, z, light);
				return getNeighborLight(world, chunk, section, x + 1, y, z, light);
			} else {
				return pack(world.provider.hasSkyLight() ? section.getSkyLight(x & 15, y & 15, z & 15) : 0,
						section.getBlockLight(x & 15, y & 15, z & 15));
			}
		} else {
			return pack(world.provider.hasSkyLight() && y >= chunk.getHeightMap()[(z & 15) << 4 | (x & 15)] ? 15 : 0,
					0);
		}
	}

	private static int getNeighborLight(World world, Chunk chunk, ExtendedBlockStorage section, int x, int y, int z,
			int skyBlock) {
		if (x >> 4 != chunk.x || z >> 4 != chunk.z) {
			chunk = world.getChunk(x >> 4, z >> 4);
			section = chunk.getBlockStorageArray()[y >> 4];
		} else if (y >> 4 != section.getYLocation() >> 4) {
			section = chunk.getBlockStorageArray()[y >> 4];
		}
		return getLight(world, chunk, section, x, y, z, sky(skyBlock), block(skyBlock));
	}

	private static int getLight(World world, Chunk chunk, ExtendedBlockStorage section, int x, int y, int z, int sky,
			int block) {
		if (section != null) {
			if (world.provider.hasSkyLight() && sky < 15) {
				sky = Math.max(sky, section.getSkyLight(x & 15, y & 15, z & 15));
			}
			if (block < 15) {
				block = Math.max(block, section.getBlockLight(x & 15, y & 15, z & 15));
			}
		} else if (world.provider.hasSkyLight() && sky < 15 && y >= chunk.getHeightMap()[(z & 15) << 4 | (x & 15)]) {
			sky = 15;
		}
		return pack(sky, block);
	}

}
