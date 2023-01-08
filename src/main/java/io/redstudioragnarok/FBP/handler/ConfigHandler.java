package io.redstudioragnarok.FBP.handler;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.util.ObfuscationUtil;
import net.minecraft.block.material.Material;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static io.redstudioragnarok.FBP.util.ModReference.FBP_LOG;

public class ConfigHandler {

	static FileInputStream fileInputStream;
	static InputStreamReader inputStreamReader;
	static BufferedReader bufferedReader;

	public static void init() {
		try {
			if (!Paths.get(FBP.config.getParent()).toFile().exists())
				Paths.get(FBP.config.getParent()).toFile().mkdirs();

			if (!FBP.config.exists()) {
				FBP.config.createNewFile();

				defaults(true);
			}

			if (!FBP.floatingMaterialsFile.exists()) {
				FBP.floatingMaterialsFile.createNewFile();

				defaultsFloatingMaterials(true);
			}

			if (!FBP.animBlacklistFile.exists())
				FBP.animBlacklistFile.createNewFile();

			if (!FBP.particleBlacklistFile.exists())
				FBP.particleBlacklistFile.createNewFile();

			read();
			readFloatingMaterials();

			readAnimExceptions();
			readParticleExceptions();

			write();
			writeFloatingMaterials();

			writeAnimExceptions();
			writeParticleExceptions();

			closeStreams();
		} catch (IOException e) {
			closeStreams();

			write();
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

	static void read() {
		try {
			initStreams(FBP.config);

			String line;

			while ((line = bufferedReader.readLine()) != null) {

				if (line.contains("enabled="))
					FBP.enabled = Boolean.parseBoolean(line.replace("enabled=", ""));
				else if (line.contains("weatherParticleDensity="))
					FBP.weatherParticleDensity = Float.parseFloat(line.replace("weatherParticleDensity=", ""));
				else if (line.contains("weatherRenderDistance="))
					FBP.weatherRenderDistance = Float.parseFloat(line.replace("weatherRenderDistance=", " "));
				else if (line.contains("particlesPerAxis="))
					FBP.particlesPerAxis = Integer.parseInt(line.replace("particlesPerAxis=", ""));
				else if (line.contains("waterPhysics="))
					FBP.waterPhysics = Boolean.parseBoolean(line.replace("waterPhysics=", ""));
				else if (line.contains("fancyFlame="))
					FBP.fancyFlame = Boolean.parseBoolean(line.replace("fancyFlame=", ""));
				else if (line.contains("fancySmoke="))
					FBP.fancySmoke = Boolean.parseBoolean(line.replace("fancySmoke=", ""));
				else if (line.contains("fancyWeather="))
					FBP.fancyWeather = Boolean.parseBoolean(line.replace("fancyWeather=", ""));
				else if (line.contains("dynamicWeather="))
					FBP.dynamicWeather = Boolean.parseBoolean(line.replace("dynamicWeather=", ""));
				else if (line.contains("spawnPlaceParticles="))
					FBP.spawnPlaceParticles = Boolean.parseBoolean(line.replace("spawnPlaceParticles=", ""));
				else if (line.contains("fancyPlaceAnim="))
					FBP.fancyPlaceAnim = Boolean.parseBoolean(line.replace("fancyPlaceAnim=", ""));
				else if (line.contains("smartBreaking="))
					FBP.smartBreaking = Boolean.parseBoolean(line.replace("smartBreaking=", ""));
				else if (line.contains("lowTraction="))
					FBP.lowTraction = Boolean.parseBoolean(line.replace("lowTraction=", ""));
				else if (line.contains("bounceOffWalls="))
					FBP.bounceOffWalls = Boolean.parseBoolean(line.replace("bounceOffWalls=", ""));
				else if (line.contains("showInMillis="))
					FBP.showInMillis = Boolean.parseBoolean(line.replace("showInMillis=", ""));
				else if (line.contains("randomRotation="))
					FBP.randomRotation = Boolean.parseBoolean(line.replace("randomRotation=", ""));
				else if (line.contains("entityCollision="))
					FBP.entityCollision = Boolean.parseBoolean(line.replace("entityCollision=", ""));
				else if (line.contains("randomFadingSpeed="))
					FBP.randomFadingSpeed = Boolean.parseBoolean(line.replace("randomFadingSpeed=", ""));
				else if (line.contains("randomizedScale="))
					FBP.randomizedScale = Boolean.parseBoolean(line.replace("randomizedScale=", ""));
				else if (line.contains("spawnWhileFrozen="))
					FBP.spawnWhileFrozen = Boolean.parseBoolean(line.replace("spawnWhileFrozen=", ""));
				else if (line.contains("spawnRedstoneBlockParticles="))
					FBP.spawnRedstoneBlockParticles = Boolean.parseBoolean(line.replace("spawnRedstoneBlockParticles=", ""));
				else if (line.contains("infiniteDuration="))
					FBP.infiniteDuration = Boolean.parseBoolean(line.replace("infiniteDuration=", ""));
				else if (line.contains("minAge="))
					FBP.minAge = Integer.parseInt(line.replace("minAge=", ""));
				else if (line.contains("maxAge="))
					FBP.maxAge = Integer.parseInt(line.replace("maxAge=", ""));
				else if (line.contains("scaleMult="))
					FBP.scaleMult = Float.parseFloat(line.replace("scaleMult=", ""));
				else if (line.contains("gravityMult="))
					FBP.gravityMult = Float.parseFloat(line.replace("gravityMult=", ""));
				else if (line.contains("rotationMult="))
					FBP.rotationMult = Float.parseFloat(line.replace("rotationMult=", ""));
			}

			closeStreams();
		} catch (Exception e) {
			closeStreams();

			write();
		}
	}

	static void readFloatingMaterials() {
		try {
			initStreams(FBP.floatingMaterialsFile);

			String line;

			FBP.floatingMaterials.clear();

			Field[] materials = Material.class.getDeclaredFields();

			// Read and discard the first four lines
			for (int i = 0; i < 4; i++) {
				bufferedReader.readLine();
			}

			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();

				boolean found = false;

				for (Field field : materials) {
					String fieldName = field.getName();

					if (field.getType() == Material.class) {
						String translated = ObfuscationUtil.translateObfMaterialName(fieldName);

						if (line.equals(translated)) {
							try {
								Material mat = (Material) field.get(null);

								if (!FBP.floatingMaterials.contains(mat))
									FBP.floatingMaterials.add(mat);

								found = true;
								break;
							} catch (Exception ex) {
								// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
							}
						}
					}
				}

				if (!found)
					FBP_LOG.error("[FBP]: Material not recognized: " + line);
			}

			closeStreams();
		} catch (Exception e) {
			closeStreams();

			write();
		}
	}

	static void readAnimExceptions() {
		try {
			initStreams(FBP.animBlacklistFile);

			String line;

			FBP.INSTANCE.resetBlacklist(false);

			while ((line = bufferedReader.readLine()) != null && !(line = line.replaceAll(" ", "")).equals(""))
				FBP.INSTANCE.addToBlacklist(line, false);
		} catch (Exception e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
		}

		closeStreams();
	}

	static void readParticleExceptions() {
		try {
			initStreams(FBP.particleBlacklistFile);

			String line;

			FBP.INSTANCE.resetBlacklist(true);

			while ((line = bufferedReader.readLine()) != null && !(line = line.replaceAll(" ", "")).equals(""))
				FBP.INSTANCE.addToBlacklist(line, true);
		} catch (Exception e) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
		}

		closeStreams();
	}

	public static void write() {
		try {
			PrintWriter writer = new PrintWriter(FBP.config.getPath(), "UTF-8");
			writer.println("enabled=" + FBP.enabled);
			writer.println("weatherParticleDensity=" + FBP.weatherParticleDensity);
			writer.println("weatherRenderDistance=" + FBP.weatherRenderDistance);
			writer.println("particlesPerAxis=" + FBP.particlesPerAxis);
			writer.println("waterPhysics=" + FBP.waterPhysics);
			writer.println("fancyFlame=" + FBP.fancyFlame);
			writer.println("fancySmoke=" + FBP.fancySmoke);
			writer.println("fancyWeather=" + FBP.fancyWeather);
			writer.println("dynamicWeather=" + FBP.dynamicWeather);
			writer.println("spawnPlaceParticles=" + FBP.spawnPlaceParticles);
			writer.println("fancyPlaceAnim=" + FBP.fancyPlaceAnim);
			writer.println("smartBreaking=" + FBP.smartBreaking);
			writer.println("lowTraction=" + FBP.lowTraction);
			writer.println("bounceOffWalls=" + FBP.bounceOffWalls);
			writer.println("showInMillis=" + FBP.showInMillis);
			writer.println("randomRotation=" + FBP.randomRotation);
			writer.println("entityCollision=" + FBP.entityCollision);
			writer.println("randomizedScale=" + FBP.randomizedScale);
			writer.println("randomFadingSpeed=" + FBP.randomFadingSpeed);
			writer.println("spawnRedstoneBlockParticles=" + FBP.spawnRedstoneBlockParticles);
			writer.println("spawnWhileFrozen=" + FBP.spawnWhileFrozen);
			writer.println("infiniteDuration=" + FBP.infiniteDuration);
			writer.println("minAge=" + FBP.minAge);
			writer.println("maxAge=" + FBP.maxAge);
			writer.println("scaleMult=" + FBP.scaleMult);
			writer.println("gravityMult=" + FBP.gravityMult);
			writer.print("rotationMult=" + FBP.rotationMult);
			writer.close();
		} catch (Exception e) {
			closeStreams();

			if (!FBP.config.exists()) {
				if (!Paths.get(FBP.config.getParent()).toFile().exists())
					Paths.get(FBP.config.getParent()).toFile().mkdirs();

				try {
					FBP.config.createNewFile();
				} catch (IOException e1) {
					// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
				}
			}

			write();
		}
	}

	public static void writeAnimExceptions() {
		try {
			PrintWriter writer = new PrintWriter(FBP.animBlacklistFile.getPath(), "UTF-8");

			for (String ex : FBP.blockAnimBlacklist)
				writer.println(ex);

			writer.close();
		} catch (Exception e) {
			closeStreams();

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

	public static void writeParticleExceptions() {
		try {
			PrintWriter writer = new PrintWriter(FBP.particleBlacklistFile.getPath(), "UTF-8");

			for (String ex : FBP.blockParticleBlacklist)
				writer.println(ex);

			writer.close();
		} catch (Exception e) {
			closeStreams();

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

	static void writeFloatingMaterials() {
		try {
			PrintWriter writer = new PrintWriter(FBP.floatingMaterialsFile.getPath(), "UTF-8");

			Field[] materials = Material.class.getDeclaredFields();

			writer.println("Configuration file for floatings materials.");
			writer.println("Anything added here will float, anything else will sink.");
			writer.println("List of all possible materials: https://i-like.cat/Materials");
			writer.println("");

			for (Field field : materials) {
				String fieldName = field.getName();

				if (field.getType() == Material.class) {
					String translated = ObfuscationUtil.translateObfMaterialName(fieldName);
					try {
						Material material = (Material) field.get(null);
						if (material == Material.AIR || !FBP.floatingMaterials.contains(material))
							continue;

						writer.println(translated);

					} catch (Exception ex) {
						// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
					}
				}
			}

			writer.close();
		} catch (Exception e) {
			closeStreams();
		}
	}

	public static void defaults(boolean write) {
		FBP.enabled = true;
		FBP.minAge = 10;
		FBP.maxAge = 55;
		FBP.scaleMult = 0.75F;
		FBP.gravityMult = 1.0F;
		FBP.rotationMult = 1.0F;
		FBP.particlesPerAxis = 4;
		FBP.weatherParticleDensity = 1.0F;
		FBP.weatherRenderDistance = 1.0F;
		FBP.lowTraction = false;
		FBP.bounceOffWalls = true;
		FBP.randomRotation = true;
		FBP.entityCollision = true;
		FBP.randomizedScale = true;
		FBP.randomFadingSpeed = true;
		FBP.spawnRedstoneBlockParticles = true;
		FBP.infiniteDuration = false;
		FBP.spawnWhileFrozen = true;
		FBP.smartBreaking = true;
		FBP.fancyPlaceAnim = true;
		FBP.spawnPlaceParticles = true;
		FBP.fancyWeather = false;
		FBP.dynamicWeather = false;
		FBP.fancySmoke = false;
		FBP.fancyFlame = false;
		FBP.waterPhysics = true;

		if (write)
			write();
	}

	public static void defaultsFloatingMaterials(boolean write) {
		FBP.floatingMaterials.clear();

		FBP.floatingMaterials.add(Material.LEAVES);
		FBP.floatingMaterials.add(Material.PLANTS);
		FBP.floatingMaterials.add(Material.ICE);
		FBP.floatingMaterials.add(Material.PACKED_ICE);
		FBP.floatingMaterials.add(Material.CLOTH);
		FBP.floatingMaterials.add(Material.CARPET);
		FBP.floatingMaterials.add(Material.WOOD);
		FBP.floatingMaterials.add(Material.WEB);

		if (write)
			writeFloatingMaterials();
	}

	public static void skipLines(int numberOfLines) throws IOException {
		for (int i = 0; i < numberOfLines; i++) {
			bufferedReader.readLine();
		}
	}
}
