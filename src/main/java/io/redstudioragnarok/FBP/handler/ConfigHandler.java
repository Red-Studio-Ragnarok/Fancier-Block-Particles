package io.redstudioragnarok.FBP.handler;

import io.redstudioragnarok.FBP.FBP;
import net.minecraft.block.material.Material;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static io.redstudioragnarok.FBP.util.ModReference.FBP_LOG;

public class ConfigHandler {

	private static FileInputStream fileInputStream;
	private static InputStreamReader inputStreamReader;
	private static BufferedReader bufferedReader;

	private static PrintWriter writer;

	private static String line;

	public static void init() {
		try {
			if (!Paths.get(FBP.mainConfigFile.getParent()).toFile().exists())
				Paths.get(FBP.mainConfigFile.getParent()).toFile().mkdirs();

			if (!FBP.mainConfigFile.exists()) {
				FBP.mainConfigFile.createNewFile();

				defaults(true);
			}

			if (!FBP.floatingMaterialsFile.exists()) {
				FBP.floatingMaterialsFile.createNewFile();

				defaultsFloatingMaterials();
			}

			if (!FBP.animBlacklistFile.exists())
				FBP.animBlacklistFile.createNewFile();

			if (!FBP.particleBlacklistFile.exists())
				FBP.particleBlacklistFile.createNewFile();

			// Check for pre 0.8 configs and hopefully delete them

			if (FBP.oldConfig.exists())
				FBP.oldConfig.delete();

			if (FBP.oldFloatingMaterialsFile.exists())
				FBP.oldFloatingMaterialsFile.delete();

			if (FBP.oldAnimBlacklistFile.exists())
				FBP.oldAnimBlacklistFile.delete();

			if (FBP.oldParticleBlacklistFile.exists())
				FBP.oldParticleBlacklistFile.delete();

			readMainConfig();
			if (FBP.waterPhysics)
				readFloatingMaterials();

			readAnimBlacklist();
			readParticleBlacklist();

			closeStreams();
		} catch (IOException e) {
			closeStreams();

			writeMainConfig();
		}
	}

	public static void initStreams(File file) {
		try {
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
			bufferedReader = new BufferedReader(inputStreamReader);
		} catch (Exception e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
		}
	}

	static void closeStreams() {
		try {
			bufferedReader.close();
			inputStreamReader.close();
			fileInputStream.close();
		} catch (Exception e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
		}
	}

	static void readMainConfig() {
		try {
			initStreams(FBP.mainConfigFile);

			skipLines(3);

			line = bufferedReader.readLine();
			FBP.enabled = Boolean.parseBoolean(line.replace("enabled=", ""));

			skipLines(3);

			line = bufferedReader.readLine();
			FBP.minAge = Integer.parseInt(line.replace("minAge=", ""));
			line = bufferedReader.readLine();
			FBP.maxAge = Integer.parseInt(line.replace("maxAge=", ""));
			line = bufferedReader.readLine();
			FBP.showInMillis = Boolean.parseBoolean(line.replace("showInMillis=", ""));
			line = bufferedReader.readLine();
			FBP.infiniteDuration = Boolean.parseBoolean(line.replace("infiniteDuration=", ""));
			line = bufferedReader.readLine();
			FBP.particlesPerAxis = Integer.parseInt(line.replace("particlesPerAxis=", ""));
			line = bufferedReader.readLine();
			FBP.scaleMult = Float.parseFloat(line.replace("scaleMult=", ""));
			line = bufferedReader.readLine();
			FBP.gravityMult = Float.parseFloat(line.replace("gravityMult=", ""));
			line = bufferedReader.readLine();
			FBP.rotationMult = Float.parseFloat(line.replace("rotationMult=", ""));
			line = bufferedReader.readLine();
			FBP.randomRotation = Boolean.parseBoolean(line.replace("randomRotation=", ""));
			line = bufferedReader.readLine();
			FBP.randomizedScale = Boolean.parseBoolean(line.replace("randomizedScale=", ""));
			line = bufferedReader.readLine();
			FBP.randomFadingSpeed = Boolean.parseBoolean(line.replace("randomFadingSpeed=", ""));
			line = bufferedReader.readLine();
			FBP.spawnRedstoneBlockParticles = Boolean.parseBoolean(line.replace("spawnRedstoneBlockParticles=", ""));
			line = bufferedReader.readLine();
			FBP.spawnWhileFrozen = Boolean.parseBoolean(line.replace("spawnWhileFrozen=", ""));
			line = bufferedReader.readLine();
			FBP.entityCollision = Boolean.parseBoolean(line.replace("entityCollision=", ""));
			line = bufferedReader.readLine();
			FBP.bounceOffWalls = Boolean.parseBoolean(line.replace("bounceOffWalls=", ""));
			line = bufferedReader.readLine();
			FBP.lowTraction = Boolean.parseBoolean(line.replace("lowTraction=", ""));
			line = bufferedReader.readLine();
			FBP.smartBreaking = Boolean.parseBoolean(line.replace("smartBreaking=", ""));
			line = bufferedReader.readLine();
			FBP.fancyFlame = Boolean.parseBoolean(line.replace("fancyFlame=", ""));
			line = bufferedReader.readLine();
			FBP.fancySmoke = Boolean.parseBoolean(line.replace("fancySmoke=", ""));
			line = bufferedReader.readLine();
			FBP.waterPhysics = Boolean.parseBoolean(line.replace("waterPhysics=", ""));

			skipLines(3);

			line = bufferedReader.readLine();
			FBP.fancyPlaceAnim = Boolean.parseBoolean(line.replace("fancyPlaceAnim=", ""));
			line = bufferedReader.readLine();
			FBP.spawnPlaceParticles = Boolean.parseBoolean(line.replace("spawnPlaceParticles=", ""));

			skipLines(3);

			line = bufferedReader.readLine();
			FBP.fancyWeather = Boolean.parseBoolean(line.replace("fancyWeather=", ""));
			line = bufferedReader.readLine();
			FBP.dynamicWeather = Boolean.parseBoolean(line.replace("dynamicWeather=", ""));
			line = bufferedReader.readLine();
			FBP.weatherParticleDensity = Float.parseFloat(line.replace("weatherParticleDensity=", ""));
			line = bufferedReader.readLine();
			FBP.weatherRenderDistance = Float.parseFloat(line.replace("weatherRenderDistance=", ""));

			closeStreams();
		} catch (Exception e) {
			closeStreams();

			writeMainConfig();
		}
	}

	static void readFloatingMaterials() {
		try {
			initStreams(FBP.floatingMaterialsFile);

			FBP.floatingMaterials.clear();

			skipLines(4);

			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();

				switch (line) {
					case "Anvil":
						addMaterial(Material.ANVIL);
						break;
					case "Barrier":
						addMaterial(Material.BARRIER);
						break;
					case "Cactus":
						addMaterial(Material.CACTUS);
						break;
					case "Cake":
						addMaterial(Material.CAKE);
						break;
					case "Carpet":
						addMaterial(Material.CARPET);
						break;
					case "Circuits":
						addMaterial(Material.CIRCUITS);
						break;
					case "Clay":
						addMaterial(Material.CLAY);
						break;
					case "Cloth":
						addMaterial(Material.CLOTH);
						break;
					case "Coral":
						addMaterial(Material.CORAL);
						break;
					case "Crafted Snow":
						addMaterial(Material.CRAFTED_SNOW);
						break;
					case "Dragon Egg":
						addMaterial(Material.DRAGON_EGG);
						break;
					case "Fire":
						addMaterial(Material.FIRE);
						break;
					case "Glass":
						addMaterial(Material.GLASS);
						break;
					case "Gourd":
						addMaterial(Material.GOURD);
						break;
					case "Grass":
						addMaterial(Material.GRASS);
						break;
					case "Ground":
						addMaterial(Material.GROUND);
						break;
					case "Ice":
						addMaterial(Material.ICE);
						break;
					case "Iron":
						addMaterial(Material.IRON);
						break;
					case "Leaves":
						addMaterial(Material.LEAVES);
						break;
					case "Packed Ice":
						addMaterial(Material.PACKED_ICE);
						break;
					case "Piston":
						addMaterial(Material.PISTON);
						break;
					case "Plants":
						addMaterial(Material.PLANTS);
						break;
					case "Portal":
						addMaterial(Material.PORTAL);
						break;
					case "Redstone Light":
						addMaterial(Material.REDSTONE_LIGHT);
						break;
					case "Rock":
						addMaterial(Material.ROCK);
						break;
					case "Sand":
						addMaterial(Material.SAND);
						break;
					case "Snow":
						addMaterial(Material.SNOW);
						break;
					case "Sponge":
						addMaterial(Material.SPONGE);
						break;
					case "Structure Void":
						addMaterial(Material.STRUCTURE_VOID);
						break;
					case "Tnt":
						addMaterial(Material.TNT);
						break;
					case "Vine":
						addMaterial(Material.VINE);
						break;
					case "Web":
						addMaterial(Material.WEB);
						break;
					case "Wood":
						addMaterial(Material.WOOD);
						break;
					default:
						// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
						FBP_LOG.error("Material not recognized: " + line);
						break;
				}
			}

			closeStreams();
		} catch (Exception e) {
			closeStreams();

			writeMainConfig();
		}
	}

	static void readAnimBlacklist() {
		try {
			initStreams(FBP.animBlacklistFile);

			FBP.INSTANCE.resetBlacklist(false);

			while ((line = bufferedReader.readLine()) != null && !(line = line.replaceAll(" ", "")).equals(""))
				FBP.INSTANCE.addToBlacklist(line, false);
		} catch (Exception e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
		}

		closeStreams();
	}

	static void readParticleBlacklist() {
		try {
			initStreams(FBP.particleBlacklistFile);

			FBP.INSTANCE.resetBlacklist(true);

			while ((line = bufferedReader.readLine()) != null && !(line = line.replaceAll(" ", "")).equals(""))
				FBP.INSTANCE.addToBlacklist(line, true);
		} catch (Exception e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
		}

		closeStreams();
	}

	public static void writeMainConfig() {
		try {
			writer = new PrintWriter(FBP.mainConfigFile.getPath(), "UTF-8");

			writer.println("Main configuration file for Fancier Block Particles");
			writer.println("I advice to use the in game configuration menu instead of manually editing this file");
			writer.println();
			writer.println("enabled=" + FBP.enabled);
			writer.println();
			writer.println("Particles Config:");
			writer.println();
			writer.println("minAge=" + FBP.minAge);
			writer.println("maxAge=" + FBP.maxAge);
			writer.println("showInMillis=" + FBP.showInMillis);
			writer.println("infiniteDuration=" + FBP.infiniteDuration);
			writer.println("particlesPerAxis=" + FBP.particlesPerAxis);
			writer.println("scaleMult=" + FBP.scaleMult);
			writer.println("gravityMult=" + FBP.gravityMult);
			writer.println("rotationMult=" + FBP.rotationMult);
			writer.println("randomRotation=" + FBP.randomRotation);
			writer.println("randomizedScale=" + FBP.randomizedScale);
			writer.println("randomFadingSpeed=" + FBP.randomFadingSpeed);
			writer.println("spawnRedstoneBlockParticles=" + FBP.spawnRedstoneBlockParticles);
			writer.println("spawnWhileFrozen=" + FBP.spawnWhileFrozen);
			writer.println("entityCollision=" + FBP.entityCollision);
			writer.println("bounceOffWalls=" + FBP.bounceOffWalls);
			writer.println("lowTraction=" + FBP.lowTraction);
			writer.println("smartBreaking=" + FBP.smartBreaking);
			writer.println("fancyFlame=" + FBP.fancyFlame);
			writer.println("fancySmoke=" + FBP.fancySmoke);
			writer.println("waterPhysics=" + FBP.waterPhysics);
			writer.println();
			writer.println("Fancy Block Placement Config:");
			writer.println();
			writer.println("fancyPlaceAnim=" + FBP.fancyPlaceAnim);
			writer.println("spawnPlaceParticles=" + FBP.spawnPlaceParticles);
			writer.println();
			writer.println("Weather Config:");
			writer.println();
			writer.println("fancyWeather=" + FBP.fancyWeather);
			writer.println("dynamicWeather=" + FBP.dynamicWeather);
			writer.println("weatherParticleDensity=" + FBP.weatherParticleDensity);
			writer.print("weatherRenderDistance=" + FBP.weatherRenderDistance);

			writer.close();
		} catch (Exception e) {
			writer.close();

			if (!FBP.mainConfigFile.exists()) {
				if (!Paths.get(FBP.mainConfigFile.getParent()).toFile().exists())
					Paths.get(FBP.mainConfigFile.getParent()).toFile().mkdirs();

				try {
					FBP.mainConfigFile.createNewFile();
				} catch (IOException e1) {
					// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
				}
			}

			writeMainConfig();
		}
	}

	static void writeFloatingMaterials() {
		try {
			writer = new PrintWriter(FBP.floatingMaterialsFile.getPath(), "UTF-8");

			writer.println("Configuration file for floatings materials.");
			writer.println("Anything added here will float, anything else will sink.");
			writer.println("List of all possible materials: https://shor.cz/Materials");
			writer.println();
			writer.println("Carpet");
			writer.println("Cloth");
			writer.println("Ice");
			writer.println("Packed Ice");
			writer.println("Plants");
			writer.println("Web");
			writer.print("Wood");

			writer.close();
		} catch (Exception e) {
			writer.close();

			if (!FBP.floatingMaterialsFile.exists()) {
				if (!Paths.get(FBP.floatingMaterialsFile.getParent()).toFile().exists())
					Paths.get(FBP.floatingMaterialsFile.getParent()).toFile().mkdirs();

				try {
					FBP.floatingMaterialsFile.createNewFile();
				} catch (IOException e1) {
					// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
				}
			}

			writeFloatingMaterials();
		}
	}

	public static void writeAnimBlacklist() {
		try {
			writer = new PrintWriter(FBP.animBlacklistFile.getPath(), "UTF-8");

			for (String ex : FBP.blockAnimBlacklist)
				writer.println(ex);

			writer.close();
		} catch (Exception e) {
			writer.close();

			if (!FBP.animBlacklistFile.exists()) {
				if (!Paths.get(FBP.animBlacklistFile.getParent()).toFile().exists())
					Paths.get(FBP.animBlacklistFile.getParent()).toFile().mkdirs();

				try {
					FBP.animBlacklistFile.createNewFile();
				} catch (IOException e1) {
					// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
				}
			}
		}
	}

	public static void writeParticleBlacklist() {
		try {
			writer = new PrintWriter(FBP.particleBlacklistFile.getPath(), "UTF-8");

			for (String ex : FBP.blockParticleBlacklist)
				writer.println(ex);

			writer.close();
		} catch (Exception e) {
			writer.close();

			if (!FBP.particleBlacklistFile.exists()) {
				if (!Paths.get(FBP.particleBlacklistFile.getParent()).toFile().exists())
					Paths.get(FBP.particleBlacklistFile.getParent()).toFile().mkdirs();

				try {
					FBP.particleBlacklistFile.createNewFile();
				} catch (IOException e1) {
					// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
				}
			}
		}
	}

	public static void defaults(boolean write) {
		FBP.enabled = true;
		FBP.bounceOffWalls = true;
		FBP.randomRotation = true;
		FBP.entityCollision = true;
		FBP.randomizedScale = true;
		FBP.randomFadingSpeed = true;
		FBP.spawnRedstoneBlockParticles = true;
		FBP.spawnWhileFrozen = true;
		FBP.smartBreaking = true;
		FBP.fancyPlaceAnim = true;
		FBP.spawnPlaceParticles = true;
		FBP.waterPhysics = true;

		FBP.minAge = 10;
		FBP.maxAge = 64;
		FBP.particlesPerAxis = 4;

		FBP.scaleMult = 0.75F;
		FBP.gravityMult = 1.0F;
		FBP.rotationMult = 1.0F;
		FBP.weatherParticleDensity = 1.0F;
		FBP.weatherRenderDistance = 1.0F;

		if (write)
			writeMainConfig();
	}

	public static void defaultsFloatingMaterials() {
		FBP.floatingMaterials.clear();

		writeFloatingMaterials();

		readFloatingMaterials();
	}

	public static void skipLines(int numberOfLines) throws IOException {
		for (int i = 0; i < numberOfLines; i++) {
			bufferedReader.readLine();
		}
	}

	public static void addMaterial(Material material) {
		if (!FBP.floatingMaterials.contains(material)) {
			FBP.floatingMaterials.add(material);
		} else {
			FBP_LOG.warn("Found duplicated material " + material + " in Floating Materials.txt");
		}
	}

	public static void reloadMaterials() {
		if (FBP.floatingMaterials.isEmpty()) {
			readFloatingMaterials();
		} else {
			FBP.floatingMaterials.clear();
		}
	}
}