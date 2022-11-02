package io.redstudioragnarok.FBP.handler;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.util.FBPObfUtil;
import net.minecraft.block.material.Material;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class FBPConfigHandler {

	static FileInputStream fis;
	static InputStreamReader isr;
	static BufferedReader br;

	public static void init() {
		try {
			defaults(false);

			if (!Paths.get(FBP.config.getParent()).toFile().exists())
				Paths.get(FBP.config.getParent()).toFile().mkdirs();

			if (!FBP.config.exists()) {
				FBP.config.createNewFile();

				write();
			}

			if (!FBP.animBlacklistFile.exists())
				FBP.animBlacklistFile.createNewFile();

			if (!FBP.particleBlacklistFile.exists())
				FBP.particleBlacklistFile.createNewFile();

			if (!FBP.floatingMaterialsFile.exists()) {
				FBP.floatingMaterialsFile.createNewFile();

				FBP.floatingMaterials.clear();

				FBP.floatingMaterials.add(Material.LEAVES);
				FBP.floatingMaterials.add(Material.PLANTS);
				FBP.floatingMaterials.add(Material.ICE);
				FBP.floatingMaterials.add(Material.PACKED_ICE);
				FBP.floatingMaterials.add(Material.CARPET);
				FBP.floatingMaterials.add(Material.WOOD);
				FBP.floatingMaterials.add(Material.WEB);
			} else
				readFloatingMaterials();

			read();
			readAnimExceptions();
			readParticleExceptions();

			write();
			writeAnimExceptions();
			writeParticleExceptions();
			writeFloatingMaterials();

			closeStreams();
		} catch (IOException e) {
			closeStreams();

			write();
		}
	}

	public static void write() {
		try {
			PrintWriter writer = new PrintWriter(FBP.config.getPath(), "UTF-8");
			writer.println("enabled=" + FBP.enabled);
			writer.println("weatherParticleDensity=" + FBP.weatherParticleDensity);
			writer.println("particlesPerAxis=" + FBP.particlesPerAxis);
			writer.println("restOnFloor=" + FBP.restOnFloor);
			writer.println("waterPhysics=" + FBP.waterPhysics);
			writer.println("fancyFlame=" + FBP.fancyFlame);
			writer.println("fancySmoke=" + FBP.fancySmoke);
			writer.println("fancyRain=" + FBP.fancyRain);
			writer.println("fancySnow=" + FBP.fancySnow);
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
					e1.printStackTrace();
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
					e1.printStackTrace();
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
					e1.printStackTrace();
				}
			}
		}
	}

	static void writeFloatingMaterials() {
		try {
			PrintWriter writer = new PrintWriter(FBP.floatingMaterialsFile.getPath(), "UTF-8");

			Field[] materials = Material.class.getDeclaredFields();

			for (Field f : materials) {
				String fieldName = f.getName();

				if (f.getType() == Material.class) {
					String translated = FBPObfUtil.translateObfMaterialName(fieldName).toLowerCase();
					try {
						Material mat = (Material) f.get(null);
						if (mat == Material.AIR)
							continue;

						boolean flag = FBP.INSTANCE.doesMaterialFloat(mat);

						writer.println(translated + "=" + flag);
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				}
			}

			writer.close();
		} catch (Exception e) {
			closeStreams();
		}
	}

	static void read() {
		try {
			fis = new FileInputStream(FBP.config);
			isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			br = new BufferedReader(isr);

			String line;

			while ((line = br.readLine()) != null) {
				line = line.replaceAll(" ", "");

				if (line.contains("enabled="))
					FBP.enabled = Boolean.parseBoolean(line.replace("enabled=", ""));
				else if (line.contains("weatherParticleDensity="))
					FBP.weatherParticleDensity = Double.parseDouble(line.replace("weatherParticleDensity=", ""));
				else if (line.contains("particlesPerAxis="))
					FBP.particlesPerAxis = Integer.parseInt(line.replace("particlesPerAxis=", ""));
				else if (line.contains("restOnFloor="))
					FBP.restOnFloor = Boolean.parseBoolean(line.replace("restOnFloor=", ""));
				else if (line.contains("waterPhysics="))
					FBP.waterPhysics = Boolean.parseBoolean(line.replace("waterPhysics=", ""));
				else if (line.contains("fancyFlame="))
					FBP.fancyFlame = Boolean.parseBoolean(line.replace("fancyFlame=", ""));
				else if (line.contains("fancySmoke="))
					FBP.fancySmoke = Boolean.parseBoolean(line.replace("fancySmoke=", ""));
				else if (line.contains("fancyRain="))
					FBP.fancyRain = Boolean.parseBoolean(line.replace("fancyRain=", ""));
				else if (line.contains("fancySnow="))
					FBP.fancySnow = Boolean.parseBoolean(line.replace("fancySnow=", ""));
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
					FBP.scaleMult = Double.parseDouble(line.replace("scaleMult=", ""));
				else if (line.contains("gravityMult="))
					FBP.gravityMult = Double.parseDouble(line.replace("gravityMult=", ""));
				else if (line.contains("rotationMult="))
					FBP.rotationMult = Double.parseDouble(line.replace("rotationMult=", ""));
			}

			closeStreams();
		} catch (Exception e) {
			closeStreams();

			write();
		}
	}

	static void readAnimExceptions() {
		try {
			fis = new FileInputStream(FBP.animBlacklistFile);
			isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			br = new BufferedReader(isr);

			String line;

			FBP.INSTANCE.resetBlacklist(false);

			while ((line = br.readLine()) != null && !(line = line.replaceAll(" ", "").toLowerCase()).equals(""))
				FBP.INSTANCE.addToBlacklist(line, false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		closeStreams();
	}

	static void readParticleExceptions() {
		try {
			fis = new FileInputStream(FBP.particleBlacklistFile);
			isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			br = new BufferedReader(isr);

			String line;

			FBP.INSTANCE.resetBlacklist(true);

			while ((line = br.readLine()) != null && !(line = line.replaceAll(" ", "").toLowerCase()).equals(""))
				FBP.INSTANCE.addToBlacklist(line, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		closeStreams();
	}

	static void readFloatingMaterials() {
		try {
			fis = new FileInputStream(FBP.floatingMaterialsFile);
			isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			br = new BufferedReader(isr);

			String line;

			FBP.floatingMaterials.clear();

			Field[] materials = Material.class.getDeclaredFields();

			while ((line = br.readLine()) != null) {
				line = line.trim().toLowerCase();

				String[] split = line.split("=");

				if (split.length < 2)
					continue;

				String materialName = split[0].replace("_", "");
				boolean flag = Boolean.parseBoolean(split[1]);

				if (!flag)
					continue;

				boolean found = false;

				for (Field f : materials) {
					String fieldName = f.getName();

					if (f.getType() == Material.class) {
						String translated = FBPObfUtil.translateObfMaterialName(fieldName).toLowerCase().replace("_", "");

						if (materialName.equals(translated)) {
							try {
								Material mat = (Material) f.get(null);

								if (!FBP.floatingMaterials.contains(mat))
									FBP.floatingMaterials.add(mat);

								found = true;
								break;
							} catch (Exception ex) {
								throw new RuntimeException(ex);
							}
						}
					}
				}

				if (!found)
					System.out.println("[FBP]: Material not recognized: " + materialName);
			}

			closeStreams();
		} catch (Exception e) {
			closeStreams();

			write();
		}
	}

	static void closeStreams() {
		try {
			br.close();
			isr.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void defaults(boolean write) {
		FBP.minAge = 10;
		FBP.maxAge = 55;
		FBP.scaleMult = 0.75;
		FBP.gravityMult = 1.0;
		FBP.rotationMult = 1.0;
		FBP.particlesPerAxis = 4;
		FBP.weatherParticleDensity = 1.0;
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
		FBP.fancyRain = false;
		FBP.fancySnow = false;
		FBP.fancySmoke = false;
		FBP.fancyFlame = false;
		FBP.waterPhysics = true;
		FBP.restOnFloor = true;

		if (write)
			write();
	}
}
