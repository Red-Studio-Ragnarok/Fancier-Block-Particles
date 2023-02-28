package io.redstudioragnarok.fbp;

import io.redstudioragnarok.fbp.block.AnimationDummyBlock;
import io.redstudioragnarok.fbp.handlers.*;
import io.redstudioragnarok.fbp.keys.KeyBindings;
import io.redstudioragnarok.fbp.particle.FBPParticleManager;
import io.redstudioragnarok.fbp.utils.ModReference;
import meldexun.matrixutil.MathUtil;
import net.jafama.FastMath;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

@Mod(clientSideOnly = true, modid = ModReference.MOD_ID, name = ModReference.MOD_NAME, version = ModReference.VERSION, guiFactory = "io.redstudioragnarok.fbp.config.FBPConfigGuiFactory")
public class FBP {

	@Instance(ModReference.MOD_ID)
	public static FBP INSTANCE;

	public static final Minecraft mc = Minecraft.getMinecraft();

	public static final ResourceLocation particlesTexture = new ResourceLocation("textures/particle/particles.png");
	public static final ResourceLocation menuTexture = new ResourceLocation(ModReference.MOD_ID + ":textures/gui/widgets.png");
	public static final ResourceLocation bugIcon = new ResourceLocation(ModReference.MOD_ID + ":textures/gui/bug.png");
	public static final ResourceLocation fbpIcon = new ResourceLocation(ModReference.MOD_ID + ":textures/gui/fbp.png");

	public static File mainConfigFile;
	public static File floatingMaterialsFile;
	public static File animBlacklistFile;
	public static File particleBlacklistFile;

	public static File oldMainConfig;
	public static File oldFloatingMaterialsFile;
	public static File oldAnimBlacklistFile;
	public static File oldParticleBlacklistFile;

	public static boolean enabled, showInMillis, infiniteDuration, randomRotation, spawnWhileFrozen, spawnRedstoneBlockParticles, randomizedScale, randomFadingSpeed, entityCollision, bounceOffWalls, lowTraction, smartBreaking, fancyPlaceAnim, spawnPlaceParticles, fancyWeather, dynamicWeather, fancyFlame, fancySmoke, waterPhysics, frozen;

	public static int minAge, maxAge, particlesPerAxis;

	public static float scaleMult, gravityMult, rotationMult, weatherParticleDensity, weatherRenderDistance;

	public static List<Material> floatingMaterials = new ArrayList<>();
	public static List<String> blockParticleBlacklist = new ArrayList<>();
	public static List<String> blockAnimBlacklist = new ArrayList<>();

	public static final SplittableRandom random = new SplittableRandom();

	public static final VertexFormat POSITION_TEX_COLOR_MAP_NORMAL = new VertexFormat();

	public static final AnimationDummyBlock FBPBlock = new AnimationDummyBlock();

	public static IRenderHandler fancyWeatherRenderer, originalWeatherRenderer;
	public static FBPParticleManager fancyEffectRenderer;
	public static ParticleManager originalEffectRenderer;

	public static TextureAtlasSprite snowTexture;

	public FBP() {
		INSTANCE = this;

		POSITION_TEX_COLOR_MAP_NORMAL.addElement(DefaultVertexFormats.POSITION_3F);
		POSITION_TEX_COLOR_MAP_NORMAL.addElement(DefaultVertexFormats.TEX_2F);
		POSITION_TEX_COLOR_MAP_NORMAL.addElement(DefaultVertexFormats.COLOR_4UB);
		POSITION_TEX_COLOR_MAP_NORMAL.addElement(DefaultVertexFormats.TEX_2S);
		POSITION_TEX_COLOR_MAP_NORMAL.addElement(DefaultVertexFormats.NORMAL_3B);
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent preInitializationEvent) {
		oldMainConfig = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/Particle.properties");
		oldFloatingMaterialsFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/FloatingMaterials.txt");
		oldAnimBlacklistFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/AnimBlockBlacklist.txt");
		oldParticleBlacklistFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/ParticleBlockBlacklist.txt");

		mainConfigFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/Config.txt");
		floatingMaterialsFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/Floating Materials.txt");
		animBlacklistFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/Animation Block Blacklist.txt");
		particleBlacklistFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/Particle Block Blacklist.txt");

		ConfigHandler.init();
		KeyBindings.init();

		MinecraftForge.EVENT_BUS.register(KeyInputHandler.class);

		MathUtil.setSinFunc(FastMath::sin);
		MathUtil.setCosFunc(FastMath::cos);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent initializationEvent) {
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent postInitializationEvent) {
		MinecraftForge.EVENT_BUS.register(new GuiHandler());
		MinecraftForge.EVENT_BUS.register(DebugHandler.class);

		snowTexture = mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.SNOW.getDefaultState());
	}

	public static void setEnabled(boolean newState) {
		if (enabled != newState) {
			if (newState) {
				fancyEffectRenderer.carryOver();

				mc.effectRenderer = fancyEffectRenderer;
				if (fancyWeather) {
					mc.world.provider.setWeatherRenderer(fancyWeatherRenderer);
				}
			} else {
				mc.effectRenderer = originalEffectRenderer;
				mc.world.provider.setWeatherRenderer(originalWeatherRenderer);

				frozen = false;
			}
		}
		enabled = newState;
	}
}
