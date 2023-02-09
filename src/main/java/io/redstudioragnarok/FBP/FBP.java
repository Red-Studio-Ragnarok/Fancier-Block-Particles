package io.redstudioragnarok.FBP;

import io.redstudioragnarok.FBP.block.AnimationDummyBlock;
import io.redstudioragnarok.FBP.handler.ConfigHandler;
import io.redstudioragnarok.FBP.handler.EventHandler;
import io.redstudioragnarok.FBP.handler.GuiHandler;
import io.redstudioragnarok.FBP.handler.KeyInputHandler;
import io.redstudioragnarok.FBP.keys.KeyBindings;
import io.redstudioragnarok.FBP.particle.FBPParticleManager;
import io.redstudioragnarok.FBP.util.ModReference;
import io.redstudioragnarok.FBP.vector.Vector3D;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;

@Mod(clientSideOnly = true, modid = ModReference.MOD_ID, name = ModReference.MOD_NAME, version = ModReference.VERSION, guiFactory = "io.redstudioragnarok.FBP.config.FBPConfigGuiFactory")
public class FBP {

	@Instance(ModReference.MOD_ID)
	public static FBP INSTANCE;

	public static final Minecraft mc = Minecraft.getMinecraft();

	public static final ResourceLocation LOCATION_PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");
	public static final ResourceLocation FBP_BUG = new ResourceLocation(ModReference.MOD_ID + ":textures/gui/bug.png");
	public static final ResourceLocation FBP_FBP = new ResourceLocation(ModReference.MOD_ID + ":textures/gui/fbp.png");
	public static final ResourceLocation FBP_WIDGETS = new ResourceLocation(ModReference.MOD_ID + ":textures/gui/widgets.png");

	public static File oldMainConfig;
	public static File oldFloatingMaterialsFile;
	public static File oldAnimBlacklistFile;
	public static File oldParticleBlacklistFile;

	public static File mainConfigFile = null;
	public static File floatingMaterialsFile = null;
	public static File animBlacklistFile = null;
	public static File particleBlacklistFile = null;

	public static int minAge, maxAge, particlesPerAxis;

	public static float scaleMult, gravityMult, rotationMult, weatherParticleDensity, weatherRenderDistance;

	public static boolean enabled, showInMillis, infiniteDuration, randomRotation, spawnWhileFrozen, spawnRedstoneBlockParticles, randomizedScale, randomFadingSpeed, entityCollision, bounceOffWalls, lowTraction, smartBreaking, fancyPlaceAnim, spawnPlaceParticles, fancyWeather, dynamicWeather, fancyFlame, fancySmoke, waterPhysics, frozen;

	public static List<Material> floatingMaterials;
	public static List<String> blockParticleBlacklist;
	public static List<String> blockAnimBlacklist;

	public static final SplittableRandom random = new SplittableRandom();

	public static final Vector3D[] CUBE = {
			// TOP
			new Vector3D(1, 1, -1), new Vector3D(1, 1, 1), new Vector3D(-1, 1, 1), new Vector3D(-1, 1, -1),

			// BOTTOM
			new Vector3D(-1, -1, -1), new Vector3D(-1, -1, 1), new Vector3D(1, -1, 1), new Vector3D(1, -1, -1),

			// FRONT
			new Vector3D(-1, -1, 1), new Vector3D(-1, 1, 1), new Vector3D(1, 1, 1), new Vector3D(1, -1, 1),

			// BACK
			new Vector3D(1, -1, -1), new Vector3D(1, 1, -1), new Vector3D(-1, 1, -1), new Vector3D(-1, -1, -1),

			// LEFT
			new Vector3D(-1, -1, -1), new Vector3D(-1, 1, -1), new Vector3D(-1, 1, 1), new Vector3D(-1, -1, 1),

			// RIGHT
			new Vector3D(1, -1, 1), new Vector3D(1, 1, 1), new Vector3D(1, 1, -1), new Vector3D(1, -1, -1)
	};

	public static final Vector3D[] CUBE_NORMALS = {
			new Vector3D(0, 1, 0), new Vector3D(0, -1, 0),

			new Vector3D(0, 0, 1), new Vector3D(0, 0, -1),

			new Vector3D(-1, 0, 0), new Vector3D(1, 0, 0)
	};

	public static final VertexFormat POSITION_TEX_COLOR_LMAP_NORMAL = new VertexFormat();

	public static final AnimationDummyBlock FBPBlock = new AnimationDummyBlock();

	public static IRenderHandler fancyWeatherRenderer, originalWeatherRenderer;
	public static FBPParticleManager fancyEffectRenderer;
	public static ParticleManager originalEffectRenderer;

	public static final EventHandler eventHandler = new EventHandler();
	public static final GuiHandler guiHandler = new GuiHandler();

	public static TextureAtlasSprite snowTexture;

	public FBP() {
		INSTANCE = this;

		POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.POSITION_3F);
		POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.TEX_2F);
		POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.COLOR_4UB);
		POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.TEX_2S);
		POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.NORMAL_3B);

		blockParticleBlacklist = new ArrayList<>();
		blockAnimBlacklist = new ArrayList<>();
		floatingMaterials = new ArrayList<>();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		oldMainConfig = new File(evt.getModConfigurationDirectory() + "/FBP/Particle.properties");
		oldFloatingMaterialsFile = new File(evt.getModConfigurationDirectory() + "/FBP/FloatingMaterials.txt");
		oldAnimBlacklistFile = new File(evt.getModConfigurationDirectory() + "/FBP/AnimBlockBlacklist.txt");
		oldParticleBlacklistFile = new File(evt.getModConfigurationDirectory() + "/FBP/ParticleBlockBlacklist.txt");

		mainConfigFile = new File(evt.getModConfigurationDirectory() + "/FBP/Config.txt");
		floatingMaterialsFile = new File(evt.getModConfigurationDirectory() + "/FBP/Floating Materials.txt");
		animBlacklistFile = new File(evt.getModConfigurationDirectory() + "/FBP/Animation Block Blacklist.txt");
		particleBlacklistFile = new File(evt.getModConfigurationDirectory() + "/FBP/Particle Block Blacklist.txt");

		ConfigHandler.init();
		KeyBindings.init();

		MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(eventHandler);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(guiHandler);
		snowTexture = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.SNOW.getDefaultState());
	}

	public static boolean isEnabled() {
		boolean result = enabled;

		if (!result)
			frozen = false;

		return result;
	}

	public static void setEnabled(boolean enabled) {
		if (FBP.enabled != enabled) {
			if (enabled) {
				FBP.fancyEffectRenderer.carryOver();

				mc.effectRenderer = FBP.fancyEffectRenderer;
				if (fancyWeather) {
					mc.world.provider.setWeatherRenderer(FBP.fancyWeatherRenderer);
				}
			} else {
				mc.effectRenderer = FBP.originalEffectRenderer;
				mc.world.provider.setWeatherRenderer(FBP.originalWeatherRenderer);
			}
		}
		FBP.enabled = enabled;
	}

	public boolean doesMaterialFloat(Material mat) {
		return floatingMaterials.contains(mat);
	}

	public boolean isBlacklisted(Block b, boolean particle) {
		if (b == null)
			return true;

		return (particle ? blockParticleBlacklist : blockAnimBlacklist).contains(Objects.requireNonNull(b.getRegistryName()).toString());
	}

	public static void addToBlacklist(Block b, boolean particle) {
		if (b == null)
			return;

		String name = Objects.requireNonNull(b.getRegistryName()).toString();

		if (!(particle ? blockParticleBlacklist : blockAnimBlacklist).contains(name))
			(particle ? blockParticleBlacklist : blockAnimBlacklist).add(name);
	}

	public static void addToBlacklist(String name, boolean particle) {
		if (StringUtils.isEmpty(name))
			return;

		for (ResourceLocation rl : Block.REGISTRY.getKeys()) {
			String s = rl.toString();

			if (s.equals(name)) {
				Block b = Block.REGISTRY.getObject(rl);

				if (b == Blocks.REDSTONE_BLOCK)
					break;

				addToBlacklist(b, particle);
				break;
			}
		}
	}

	public void removeFromBlacklist(Block b, boolean particle) {
		if (b == null)
			return;

		String name = Objects.requireNonNull(b.getRegistryName()).toString();

		(particle ? blockParticleBlacklist : blockAnimBlacklist).remove(name);
	}

	public static void resetBlacklist(boolean particle) {
		if (particle)
			blockParticleBlacklist.clear();
		else
			blockAnimBlacklist.clear();
	}
}
