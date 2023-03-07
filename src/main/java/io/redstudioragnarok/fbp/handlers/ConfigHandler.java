package io.redstudioragnarok.fbp.handlers;

import io.redstudioragnarok.fbp.FBP;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static io.redstudioragnarok.fbp.utils.ModReference.log;

/**
 * This class handle everything related to the config system.
 */
public class ConfigHandler {

	private static FileInputStream fileInputStream;
	private static InputStreamReader inputStreamReader;
	private static BufferedReader bufferedReader;

	private static PrintWriter writer;

	private static String line;
	public static String name;

	/**
	 * Initializes the configuration system.
	 * <p>
	 * Check if the config files and folder exists, if not, create them.
	 * It also checks if the old config files exists, if yes, delete them.
	 * Then read the config files.
	 * <p>
	 * Only reads Floating Materials config and Animation Blacklist if theses respective features are enabled.
	 */
	public static void init() {
		try {
			if (!Paths.get(FBP.mainConfigFile.getParent()).toFile().exists())
				if (Paths.get(FBP.mainConfigFile.getParent()).toFile().mkdirs())
					log.error("Could not create config directory");

			if (!FBP.mainConfigFile.exists()) {
				if (!FBP.mainConfigFile.createNewFile())
					log.error("Could not create main config file");

				defaults();
			}

			if (!FBP.floatingMaterialsFile.exists()) {
				if (!FBP.floatingMaterialsFile.createNewFile())
                    log.error("Could not create floating materials file");

                defaultsFloatingMaterials();
			}

			if (!FBP.particleBlacklistFile.exists())
				if (!FBP.particleBlacklistFile.createNewFile())
					log.error("Could not create particle blacklist file");

			if (!FBP.animBlacklistFile.exists())
				if (!FBP.animBlacklistFile.createNewFile())
                    log.error("Could not create anim blacklist file");

			// Check for pre 0.8 configs and hopefully delete them

			if (FBP.oldMainConfig.exists())
				if (!FBP.oldMainConfig.delete())
					log.error("Could not delete old main config file");

			if (FBP.oldFloatingMaterialsFile.exists())
				if (!FBP.oldFloatingMaterialsFile.delete())
					log.error("Could not delete old floating materials file");

			if (FBP.oldParticleBlacklistFile.exists())
				if (!FBP.oldParticleBlacklistFile.delete())
                    log.error("Could not delete old particle blacklist file");

            if (FBP.oldAnimBlacklistFile.exists())
				if (!FBP.oldAnimBlacklistFile.delete())
					log.error("Could not delete old anim blacklist file");

			readMainConfig();
			if (FBP.waterPhysics)
				readFloatingMaterials();

			readParticleBlacklist();
			if (FBP.fancyPlaceAnim)
				readAnimBlacklist();

		} catch (IOException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			log.error("Cannot init configs, an IOException occurred: " + e.getMessage());
		} catch (SecurityException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			log.error("Cannot init configs, an antivirus is probably causing this");
		} finally {
			closeStreams();
		}
	}

	/**
	 * Initializes file streams for a specified file.
	 * <p>
	 * This is used to read the config files.
	 *
	 * @param file The file object to create streams for
	 */
	private static void initStreams(File file) {
		try {
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
			bufferedReader = new BufferedReader(inputStreamReader);

		} catch (FileNotFoundException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			if (handleFileNotFound(e.getMessage(), "streams", file))
				initStreams(file);

			closeStreams();
		} catch (SecurityException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			log.error("Cannot init streams for " + file + " an antivirus is probably causing this");

			closeStreams();
		}
	}

	/**
     * Closes file streams.
     */
	private static void closeStreams() {
		try {
			bufferedReader.close();
			inputStreamReader.close();
			fileInputStream.close();

		} catch (IOException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			log.error("Cannot close streams, an IOException occurred: " + e.getMessage());
		}
	}

	/**
     * Initializes writer for a specified file.
	 * <p>
	 * This is used to write to the config files.
     *
     * @param file The file object to create writer for
     */
	private static void initWriter(File file) {
		try {
			writer = new PrintWriter(file.getPath(), "UTF-8");

		} catch (FileNotFoundException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			if (handleFileNotFound(e.getMessage(), "writer", file))
				initWriter(file);
		} catch (UnsupportedEncodingException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			log.error("Cannot init writer for " + file + " encoding is not supported, details: " + e.getMessage());
		} catch (SecurityException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			log.error("Cannot init streams for " + file + " an antivirus is probably causing this");
		}
	}

	/**
	 * Handle a file not found exception.
	 * <p>
	 * It will output an error message to the console.
	 * And then attempt to create a new file, if it fails, it will output an error message to the console.
	 *
	 * @param message The error message associated with the exception
	 * @param source A string indicating the source of the error
	 * @param file The file object that could not be found
	 * @return True if the file was created successfully, false otherwise
	 */
	private static boolean handleFileNotFound(String message, String source, File file) {
		log.error("Cannot init " + source + " for " + file + " as the file does not exist, details: " + message);
		log.warn("Trying to create file " + file);

		try {
			if (file.createNewFile()) {
				log.info("Successfully created file " + file);
				return true;
			}
		} catch (Exception ex) {
			log.error("Could not create file " + file);
		}

		return false;
	}

	/**
     * Reads the main config file and set the corresponding settings.
	 */
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
			log.error("Cannot read main config, an IOException occurred: " + e.getMessage());
		} finally {
			closeStreams();
		}
	}

	/**
     * Reads the floating materials config file and add the found material to the floating material list.
     */
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
						log.error("Material not recognized: " + line);
						break;
				}
			}

		} catch (IOException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			log.error("Cannot read floating materials config, an IOException occurred: " + e.getMessage());
		} finally {
			 closeStreams();
		}
	}

	/**
     * Reads the animation blacklist config file and add the found blocks to the animation blacklist list.
     */
	private static void readAnimBlacklist() {
		try {
			initStreams(FBP.animBlacklistFile);

			FBP.blockAnimBlacklist.clear();

			while ((line = bufferedReader.readLine()) != null && !(line = line.replaceAll(" ", "")).equals(""))
				addToBlacklist(line, false);

		} catch (IOException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			log.error("Cannot read animation blacklist, an IOException occurred: " + e.getMessage());
		} finally {
			closeStreams();
		}
	}


	/**
     * Reads the particle blacklist config file and add the found blocks to the particle blacklist list.
     */
	private static void readParticleBlacklist() {
		try {
			initStreams(FBP.particleBlacklistFile);

			FBP.blockParticleBlacklist.clear();

			while ((line = bufferedReader.readLine()) != null && !(line = line.replaceAll(" ", "")).equals(""))
				addToBlacklist(line, true);

		} catch (IOException e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			log.error("Cannot read particle blacklist, an IOException occurred: " + e.getMessage());
		} finally {
			closeStreams();
		}
	}

	/**
     * Writes the main config file.
     */
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

	/**
     * Writes the floating materials config file.
	 * <p>
	 * Uses hard-coded values for the material names which are the default.
     */
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

	/**
     * Writes the animation blacklist config file.
     */
	public static void writeAnimBlacklist() {
		initWriter(FBP.animBlacklistFile);

		for (String block : FBP.blockAnimBlacklist)
			writer.println(block);

		writer.close();
	}

	/**
     * Writes the particle blacklist config file.
     */
	public static void writeParticleBlacklist() {
		initWriter(FBP.particleBlacklistFile);

		for (String block : FBP.blockParticleBlacklist)
			writer.println(block);

		writer.close();
	}

	/**
	 * Set all the values in the main config file to the default.
	 */
	public static void defaults() {
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

		writeMainConfig();
	}

	/**
	 * Set all the values in the floating materials config file to the default.
	 */
	public static void defaultsFloatingMaterials() {
		FBP.floatingMaterials.clear();

		writeFloatingMaterials();

		readFloatingMaterials();
	}

	/**
	 * Skips a specified number of lines in a file being read by a BufferedReader.
	 *
	 * @param numberOfLines The number of lines to skip in the file
	 * @throws IOException If an I/O error occurs while reading the file
	 */
	private static void skipLines(int numberOfLines) throws IOException {
		for (int i = 0; i < numberOfLines; i++)
			bufferedReader.readLine();
	}

	/**
     * Adds a material to the floating materials list.
     *
     * @param material The material to add
     */
	private static void addMaterial(Material material) {
		if (!FBP.floatingMaterials.contains(material))
			FBP.floatingMaterials.add(material);
		else
			log.warn("Found duplicated material " + material + " in Floating Materials.txt");
	}

	/**
	 * Adds a block to either the particle or animation blacklist.
	 *
	 * @param block The block to add
	 * @param particle Whether the block should be added to the particle or animation blacklist
	 */
	public static void addToBlacklist(Block block, boolean particle) {
		if (block == null)
			return;

		name = block.getRegistryName().toString();

		if (!(particle ? FBP.blockParticleBlacklist : FBP.blockAnimBlacklist).contains(name))
			(particle ? FBP.blockParticleBlacklist : FBP.blockAnimBlacklist).add(name);
	}

	/**
     * Adds a block by name to either the particle or animation blacklist.
     *
     * @param name The name of the block to add
     * @param particle Whether the block should be added to the particle or animation blacklist
	 */
	public static void addToBlacklist(String name, boolean particle) {
		if (StringUtils.isEmpty(name))
			return;

		for (ResourceLocation rl : Block.REGISTRY.getKeys()) {
			String resourceLocation = rl.toString();

			if (resourceLocation.equals(name)) {
				Block block = Block.REGISTRY.getObject(rl);

				addToBlacklist(block, particle);
				break;
			}
		}
	}

	/**
     * Removes a block from either the particle or animation blacklist.
     *
     * @param block The block to remove
	 * @param particle Whether the block should be removed from the particle or animation blacklist
     */
	public static void removeFromBlacklist(Block block, boolean particle) {
		if (block == null)
			return;

		name = block.getRegistryName().toString();

		(particle ? FBP.blockParticleBlacklist : FBP.blockAnimBlacklist).remove(name);
	}

	/**
     * Checks if a block is blacklisted.
     *
     * @param block The block to check
     * @param particle Whether to check if the block is blacklisted in the particle or animation blacklist
	 */
	public static boolean isBlacklisted(Block block, boolean particle) {
		if (block == null)
			return true;

		return (particle ? FBP.blockParticleBlacklist : FBP.blockAnimBlacklist).contains(block.getRegistryName().toString());
	}

	/**
	 * Reloads the floating materials list.
	 * If the list is empty, it is filled by calling readFloatingMaterials().
	 * Otherwise, the list is cleared using the clear() method of the ArrayList class.
	 */
	public static void reloadMaterials() {
		if (FBP.floatingMaterials.isEmpty())
			readFloatingMaterials();
		else
			FBP.floatingMaterials.clear();
	}

	/**
     * Reloads the animation blacklist.
     * If the list is empty, it is filled by calling readAnimBlacklist().
     * Otherwise, the list is cleared using the clear() method of the ArrayList class.
     */
	public static void reloadAnimBlacklist() {
		if (FBP.blockAnimBlacklist.isEmpty())
			readAnimBlacklist();
		else
			FBP.blockAnimBlacklist.clear();
	}
}
