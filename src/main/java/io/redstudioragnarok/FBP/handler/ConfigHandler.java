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
				if (Paths.get(FBP.mainConfigFile.getParent()).toFile().mkdirs())
					FBP_LOG.error("Could not create config directory");

			if (!FBP.mainConfigFile.exists()) {
				if (!FBP.mainConfigFile.createNewFile())
					FBP_LOG.error("Could not create main config file");

				defaults(true);
			}

			if (!FBP.floatingMaterialsFile.exists()) {
				if (!FBP.floatingMaterialsFile.createNewFile())
                    FBP_LOG.error("Could not create floating materials file");

                defaultsFloatingMaterials();
			}

			if (!FBP.particleBlacklistFile.exists())
				if (!FBP.particleBlacklistFile.createNewFile())
					FBP_LOG.error("Could not create particle blacklist file");

			if (!FBP.animBlacklistFile.exists())
				if (!FBP.animBlacklistFile.createNewFile())
                    FBP_LOG.error("Could not create anim blacklist file");

			// Check for pre 0.8 configs and hopefully delete them

			if (FBP.oldMainConfig.exists())
				if (!FBP.oldMainConfig.delete())
					FBP_LOG.error("Could not delete old main config file");

			if (FBP.oldFloatingMaterialsFile.exists())
				if (!FBP.oldFloatingMaterialsFile.delete())
					FBP_LOG.error("Could not delete old floating materials file");

			if (FBP.oldParticleBlacklistFile.exists())
				if (!FBP.oldParticleBlacklistFile.delete())
                    FBP_LOG.error("Could not delete old particle blacklist file");

            if (FBP.oldAnimBlacklistFile.exists())
				if (!FBP.oldAnimBlacklistFile.delete())
					FBP_LOG.error("Could not delete old anim blacklist file");

			readMainConfig();
			if (FBP.waterPhysics)
				readFloatingMaterials();

			readParticleBlacklist();
			if (FBP.fancyPlaceAnim)
				readAnimBlacklist();

		} catch (IOException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			FBP_LOG.error("Cannot init configs, an IOException occurred: " + e.getMessage());
		} catch (SecurityException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			FBP_LOG.error("Cannot init configs, an antivirus is probably causing this");
		} finally {
			closeStreams();
		}
	}

	private static void initStreams(File file) {
		try {
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
			bufferedReader = new BufferedReader(inputStreamReader);

		} catch (FileNotFoundException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			if (handleFileNotFound(e.getMessage(), "streams", file))
				initStreams(file);
		} catch (SecurityException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			FBP_LOG.error("Cannot init streams for " + file + " an antivirus is probably causing this");
		}
	}

	private static void closeStreams() {
		try {
			bufferedReader.close();
			inputStreamReader.close();
			fileInputStream.close();

		} catch (IOException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			FBP_LOG.error("Cannot close streams, an IOException occurred: " + e.getMessage());
		}
	}

	private static void initWriter(File file) {
		try {
			writer = new PrintWriter(file.getPath(), "UTF-8");

		} catch (FileNotFoundException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			if (handleFileNotFound(e.getMessage(), "writer", file))
				initWriter(file);
		} catch (UnsupportedEncodingException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			FBP_LOG.error("Cannot init writer for " + file + " encoding is not supported, details: " + e.getMessage());
		} catch (SecurityException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			FBP_LOG.error("Cannot init streams for " + file + " an antivirus is probably causing this");
		}
	}

	private static boolean handleFileNotFound(String message, String source, File file) {
		FBP_LOG.error("Cannot init " + source + " for " + file + " as the file does not exist, details: " + message);
		FBP_LOG.warn("Trying to create file " + file);

		try {
			if (file.createNewFile()) {
				FBP_LOG.info("Successfully created file " + file);
				return true;
			}
		} catch (Exception ex) {
			FBP_LOG.error("Could not create file " + file);
		}

		return false;
	}

	private static void readMainConfig() {
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

		} catch (IOException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			FBP_LOG.error("Cannot read main config, an IOException occurred: " + e.getMessage());
		} finally {
			closeStreams();
		}
	}

	private static void readFloatingMaterials() {
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

		} catch (IOException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			FBP_LOG.error("Cannot read floating materials config, an IOException occurred: " + e.getMessage());
		} finally {
			 closeStreams();
		}
	}

	private static void readAnimBlacklist() {
		try {
			initStreams(FBP.animBlacklistFile);

			FBP.resetBlacklist(false);

			while ((line = bufferedReader.readLine()) != null && !(line = line.replaceAll(" ", "")).equals(""))
				FBP.addToBlacklist(line, false);

		} catch (IOException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			FBP_LOG.error("Cannot read animation blacklist, an IOException occurred: " + e.getMessage());
		} finally {
			closeStreams();
		}
	}

	private static void readParticleBlacklist() {
		try {
			initStreams(FBP.particleBlacklistFile);

			FBP.resetBlacklist(true);

			while ((line = bufferedReader.readLine()) != null && !(line = line.replaceAll(" ", "")).equals(""))
				FBP.addToBlacklist(line, true);

		} catch (IOException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			FBP_LOG.error("Cannot read particle blacklist, an IOException occurred: " + e.getMessage());
		} finally {
			closeStreams();
		}
	}

	public static void writeMainConfig() {
		initWriter(FBP.mainConfigFile);

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
	}

	private static void writeFloatingMaterials() {
		initWriter(FBP.floatingMaterialsFile);

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
	}

	public static void writeAnimBlacklist() {
		initWriter(FBP.animBlacklistFile);

		for (String ex : FBP.blockAnimBlacklist)
			writer.println(ex);

		writer.close();
	}

	public static void writeParticleBlacklist() {
		initWriter(FBP.particleBlacklistFile);

		for (String ex : FBP.blockParticleBlacklist)
			writer.println(ex);

		writer.close();
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

	public static void reloadMaterials() {
		if (FBP.floatingMaterials.isEmpty())
			readFloatingMaterials();
		else
			FBP.floatingMaterials.clear();
	}

	public static void reloadAnimBlacklist() {
		if (FBP.blockAnimBlacklist.isEmpty())
			readAnimBlacklist();
		else
			FBP.blockAnimBlacklist.clear();
	}

	private static void addMaterial(Material material) {
		if (!FBP.floatingMaterials.contains(material))
			FBP.floatingMaterials.add(material);
		else
			FBP_LOG.warn("Found duplicated material " + material + " in Floating Materials.txt");
	}

	private static void skipLines(int numberOfLines) throws IOException {
		for (int i = 0; i < numberOfLines; i++)
			bufferedReader.readLine();
	}
}
