package dev.redstudio.fbp.handlers;

import dev.redstudio.fbp.FBP;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static dev.redstudio.fbp.ProjectConstants.LOGGER;
import static dev.redstudio.fbp.ProjectConstants.RED_LOGGER;

/**
 * This class handle everything related to the config system.
 */
public class ConfigHandler {

    private static FileInputStream fileInputStream;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;

    private static PrintWriter writer;

    private static String line;

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
        ensureConfigFilesExist();

        deleteOldConfigFiles();

        readMainConfig();

        if (FBP.waterPhysics)
            readFloatingMaterials();

        readParticleBlacklist();

        if (FBP.fancyPlaceAnim)
            readAnimBlacklist();
    }

    /**
     * Ensure config files exist
     * <p>
     * This method checks if the files and folders in the config folder exists.
     * If not, it creates them.
     */
    private static void ensureConfigFilesExist() {
        try {
            if (!Paths.get(FBP.mainConfigFile.getParent()).toFile().exists() && (Paths.get(FBP.mainConfigFile.getParent()).toFile().mkdirs()))
                LOGGER.error("Could not create config directory");

            if (!FBP.mainConfigFile.exists()) {
                if (!FBP.mainConfigFile.createNewFile())
                    LOGGER.error("Could not create main config file");

                defaults();
            }

            if (!FBP.floatingMaterialsFile.exists()) {
                if (!FBP.floatingMaterialsFile.createNewFile())
                    LOGGER.error("Could not create floating materials file");

                defaultsFloatingMaterials();
            }

            if (!FBP.particleBlacklistFile.exists() && !FBP.particleBlacklistFile.createNewFile())
                LOGGER.error("Could not create particle blacklist file");

            if (!FBP.animBlacklistFile.exists() && !FBP.animBlacklistFile.createNewFile())
                LOGGER.error("Could not create anim blacklist file");

        } catch (final IOException ioException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot initialize configs", "", ioException.getMessage());
        } catch (final SecurityException securityException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot initialize configs", "", "An antivirus is probably causing this", securityException.getMessage());
        } finally {
            closeStreams();
        }
    }

    /**
     * Check for pre 0.8 configs and hopefully delete them
     */
    private static void deleteOldConfigFiles() {
        if (FBP.oldMainConfigFile.exists() && !FBP.oldMainConfigFile.delete())
            LOGGER.error("Could not delete old main config file");

        if (FBP.oldFloatingMaterialsFile.exists() && !FBP.oldFloatingMaterialsFile.delete())
            LOGGER.error("Could not delete old floating materials file");

        if (FBP.oldParticleBlacklistFile.exists() && !FBP.oldParticleBlacklistFile.delete())
            LOGGER.error("Could not delete old particle blacklist file");

        if (FBP.oldAnimBlacklistFile.exists() && !FBP.oldAnimBlacklistFile.delete())
            LOGGER.error("Could not delete old anim blacklist file");

        if (FBP.oldNewMainConfigFile.exists() && !FBP.oldNewMainConfigFile.delete())
            LOGGER.error("Could not delete old new main config file");
    }

    /**
     * Initializes file streams for a specified file.
     * <p>
     * This is used to read the config files.
     *
     * @param file The file object to create streams for
     */
    private static void initStreams(final File file) {
        try {
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);

        } catch (final FileNotFoundException fileNotFoundException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            if (handleFileNotFound(fileNotFoundException.getMessage(), "streams", file))
                initStreams(file);
            closeStreams();
        } catch (final SecurityException securityException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot initialize streams", "", "An antivirus is probably causing this", securityException.getMessage());
            closeStreams();
        }
    }

    /**
     * Closes file streams.
     */
    private static void closeStreams() {
        try {
            if (bufferedReader != null)
                bufferedReader.close();
            if (inputStreamReader != null)
                inputStreamReader.close();
            if (fileInputStream != null)
                fileInputStream.close();

        } catch (final IOException ioException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot close streams", "", ioException.getMessage());
        }
    }

    /**
     * Initializes writer for a specified file.
     * <p>
     * This is used to write to the config files.
     *
     * @param file The file object to create writer for
     */
    private static void initWriter(final File file) {
        try {
            writer = new PrintWriter(file.getPath(), "UTF-8");

        } catch (FileNotFoundException fileNotFoundException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            if (handleFileNotFound(fileNotFoundException.getMessage(), "writer", file))
                initWriter(file);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot initialize writer", "", "Encoding is not supported", unsupportedEncodingException.getMessage());
        } catch (SecurityException securityException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot initialize writer", "", "An antivirus is probably causing this", securityException.getMessage());
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
     *
     * @return True if the file was created successfully, false otherwise
     */
    private static boolean handleFileNotFound(final String message, final String source, final File file) {
        LOGGER.error("Cannot init " + source + " for " + file + " as the file does not exist, details: " + message);
        LOGGER.warn("Trying to create file " + file);

        try {
            if (file.createNewFile()) {
                LOGGER.info("Successfully created file " + file);
                return true;
            }

        } catch (final IOException ioException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot initialize configs", "", ioException.getMessage());
        } catch (final SecurityException securityException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot initialize configs", "", "An antivirus is probably causing this", securityException.getMessage());
        }

        return false;
    }

    /**
     * Reads the main config file and set the corresponding settings if not found sets default.
     */
    private static void readMainConfig() {
        try {
            initStreams(FBP.mainConfigFile);

            final Map<String, String> configValues = new HashMap<>();

            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty())
                    continue;

                final String[] keyValue = line.split(": ");
                if (keyValue.length == 2)
                    configValues.put(keyValue[0], keyValue[1]);
            }

            FBP.enabled = Boolean.parseBoolean(configValues.getOrDefault("enabled", "true"));
            FBP.minAge = Integer.parseInt(configValues.getOrDefault("minAge", "10"));
            FBP.maxAge = Integer.parseInt(configValues.getOrDefault("maxAge", "64"));
            FBP.showInMillis = Boolean.parseBoolean(configValues.getOrDefault("showInMillis", "false"));
            FBP.infiniteDuration = Boolean.parseBoolean(configValues.getOrDefault("infiniteDuration", "false"));
            FBP.particlesPerAxis = Integer.parseInt(configValues.getOrDefault("particlesPerAxis", "4"));
            FBP.scaleMult = Float.parseFloat(configValues.getOrDefault("scaleMult", "0.75"));
            FBP.gravityMult = Float.parseFloat(configValues.getOrDefault("gravityMult", "1.0"));
            FBP.rotationMult = Float.parseFloat(configValues.getOrDefault("rotationMult", "1.0"));
            FBP.randomRotation = Boolean.parseBoolean(configValues.getOrDefault("randomRotation", "true"));
            FBP.randomizedScale = Boolean.parseBoolean(configValues.getOrDefault("randomizedScale", "true"));
            FBP.randomFadingSpeed = Boolean.parseBoolean(configValues.getOrDefault("randomFadingSpeed", "true"));
            FBP.spawnWhileFrozen = Boolean.parseBoolean(configValues.getOrDefault("spawnWhileFrozen", "true"));
            FBP.entityCollision = Boolean.parseBoolean(configValues.getOrDefault("entityCollision", "true"));
            FBP.bounceOffWalls = Boolean.parseBoolean(configValues.getOrDefault("bounceOffWalls", "true"));
            FBP.lowTraction = Boolean.parseBoolean(configValues.getOrDefault("lowTraction", "false"));
            FBP.smartBreaking = Boolean.parseBoolean(configValues.getOrDefault("smartBreaking", "true"));
            FBP.fancyFlame = Boolean.parseBoolean(configValues.getOrDefault("fancyFlame", "false"));
            FBP.fancySmoke = Boolean.parseBoolean(configValues.getOrDefault("fancySmoke", "false"));
            FBP.waterPhysics = Boolean.parseBoolean(configValues.getOrDefault("waterPhysics", "true"));

            FBP.fancyPlaceAnim = Boolean.parseBoolean(configValues.getOrDefault("fancyPlaceAnim", "true"));
            FBP.spawnPlaceParticles = Boolean.parseBoolean(configValues.getOrDefault("spawnPlaceParticles", "true"));

            FBP.fancyWeather = Boolean.parseBoolean(configValues.getOrDefault("fancyWeather", "false"));
            FBP.dynamicWeather = Boolean.parseBoolean(configValues.getOrDefault("dynamicWeather", "false"));
            FBP.weatherParticleDensity = Float.parseFloat(configValues.getOrDefault("weatherParticleDensity", "1.0"));
            FBP.weatherRenderDistance = Float.parseFloat(configValues.getOrDefault("weatherRenderDistance", "1.0"));

            FBP.experiments = Boolean.parseBoolean(configValues.getOrDefault("experiments", "false"));

            FBP.debug = Boolean.parseBoolean(configValues.getOrDefault("debugMode", "false"));

        } catch (final IOException ioException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot read main config file", "", ioException.getMessage());
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

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("#") || line.isEmpty())
                    continue;

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
                        // Todo: (Red Issue Tracker) This should count to the problem counter
                        LOGGER.warn("Material not recognized: " + line + " in Floating Materials.txt, it will be ignored but should be removed.");
                        break;
                }
            }

        } catch (final IOException ioException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot read floating materials file", "", ioException.getMessage());
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

            while ((line = bufferedReader.readLine()) != null && !(line = line.replace(" ", "")).isEmpty())
                addToBlacklist(line, false);
        } catch (final IOException ioException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot read animation blacklist file", "", ioException.getMessage());
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

            while ((line = bufferedReader.readLine()) != null && !(line = line.replace(" ", "")).isEmpty())
                addToBlacklist(line, true);
        } catch (final IOException ioException) {
            // Todo: (Red Issue Tracker) This should count to the problem counter
            RED_LOGGER.printFramedError("Configuration", "Cannot read particle blacklist file", "", ioException.getMessage());
        } finally {
            closeStreams();
        }
    }

    /**
     * Writes the main config file.
     */
    public static void writeMainConfig() {
        initWriter(FBP.mainConfigFile);

        writer.println("# Main configuration file for Fancier Block Particles");
        writer.println("# I advise to use the in game configuration pages instead of manually editing this file");
        writer.println();
        writer.println("enabled: " + FBP.enabled);
        writer.println();
        writer.println("# Particles Config:");
        writer.println();
        writer.println("minAge: " + FBP.minAge);
        writer.println("maxAge: " + FBP.maxAge);
        writer.println("showInMillis: " + FBP.showInMillis);
        writer.println("infiniteDuration: " + FBP.infiniteDuration);
        writer.println("particlesPerAxis: " + FBP.particlesPerAxis);
        writer.println("scaleMult: " + FBP.scaleMult);
        writer.println("gravityMult: " + FBP.gravityMult);
        writer.println("rotationMult: " + FBP.rotationMult);
        writer.println("randomRotation: " + FBP.randomRotation);
        writer.println("randomizedScale: " + FBP.randomizedScale);
        writer.println("randomFadingSpeed: " + FBP.randomFadingSpeed);
        writer.println("spawnWhileFrozen: " + FBP.spawnWhileFrozen);
        writer.println("entityCollision: " + FBP.entityCollision);
        writer.println("bounceOffWalls: " + FBP.bounceOffWalls);
        writer.println("lowTraction: " + FBP.lowTraction);
        writer.println("smartBreaking: " + FBP.smartBreaking);
        writer.println("fancyFlame: " + FBP.fancyFlame);
        writer.println("fancySmoke: " + FBP.fancySmoke);
        writer.println("waterPhysics: " + FBP.waterPhysics);
        writer.println();
        writer.println("# Fancy Block Placement Config:");
        writer.println();
        writer.println("fancyPlaceAnim: " + FBP.fancyPlaceAnim);
        writer.println("spawnPlaceParticles: " + FBP.spawnPlaceParticles);
        writer.println();
        writer.println("# Weather Config:");
        writer.println();
        writer.println("fancyWeather: " + FBP.fancyWeather);
        writer.println("dynamicWeather: " + FBP.dynamicWeather);
        writer.println("weatherParticleDensity: " + FBP.weatherParticleDensity);
        writer.println("weatherRenderDistance: " + FBP.weatherRenderDistance);
        writer.println();
        writer.println("# Experiments:");
        writer.println();
        writer.println("experiments: " + FBP.experiments);
        writer.println();
        writer.println("# Debug Config:");
        writer.println();
        writer.println("debugMode: " + FBP.debug);

        writer.close();
    }

    /**
     * Writes the floating materials config file.
     * <p>
     * Uses hard-coded values for the material names which are the default.
     */
    private static void writeFloatingMaterials() {
        initWriter(FBP.floatingMaterialsFile);

        writer.println("# Configuration file for floatings materials.");
        writer.println("# Anything added here will float, anything else will sink.");
        writer.println("# List of all possible materials: https://shor.cz/1.12Materials");
        writer.println();
        writer.println("Carpet");
        writer.println("Cloth");
        writer.println("Ice");
        writer.println("Packed Ice");
        writer.println("Plants");
        writer.println("Web");
        writer.println("Wood");

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
     * Adds a material to the floating materials list.
     *
     * @param material The material to add
     */
    private static void addMaterial(final Material material) {
        if (!FBP.floatingMaterials.contains(material))
            FBP.floatingMaterials.add(material);
        else
            // Todo: (Red Issue Tracker) This should count to the problem counter
            LOGGER.warn("Found duplicated material: " + material + " in Floating Materials.txt, it will be ignored but should be removed.");
    }

    /**
     * Adds a block by name to either the particle or animation blacklist.
     *
     * @param name The name of the block to add
     * @param particle Whether the block should be added to the particle or animation blacklist
     */
    public static void addToBlacklist(final String name, final boolean particle) {
        if (StringUtils.isEmpty(name))
            return;

        if (!(particle ? FBP.blockParticleBlacklist : FBP.blockAnimBlacklist).contains(name))
            (particle ? FBP.blockParticleBlacklist : FBP.blockAnimBlacklist).add(name);
    }


    /**
     * Add or remove a block from either the particle or animation blacklist.
     * <p>
     * If the block is not blacklisted, it is added to either the particle or animation blacklist.
     * <p>
     * If the block is blacklisted, it is removed from either the particle or animation blacklist.
     *
     * @param block The block to blacklist
     * @param particle Whether the block should be blacklisted from the particle or animation blacklist
     */
    public static void blacklist(final Block block, final boolean particle) {
        if (block == null || block.getRegistryName() == null)
            return;

        final String name = block.getRegistryName().toString();

        if (isBlacklisted(block, particle))
            (particle ? FBP.blockParticleBlacklist : FBP.blockAnimBlacklist).remove(name);
        else
            (particle ? FBP.blockParticleBlacklist : FBP.blockAnimBlacklist).add(name);

        if (particle)
            writeParticleBlacklist();
        else
            writeAnimBlacklist();
    }

    /**
     * Checks if a block is blacklisted.
     *
     * @param block The block to check
     * @param particle Whether to check if the block is blacklisted in the particle or animation blacklist
     */
    public static boolean isBlacklisted(final Block block, final boolean particle) {
        if (block == null || block.getRegistryName() == null)
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
