package io.redstudioragnarok.FBP;

import io.redstudioragnarok.FBP.block.FBPAnimationDummyBlock;
import io.redstudioragnarok.FBP.handler.FBPEventHandler;
import io.redstudioragnarok.FBP.handler.FBPGuiHandler;
import io.redstudioragnarok.FBP.handler.FBPKeyInputHandler;
import io.redstudioragnarok.FBP.keys.FBPKeyBindings;
import io.redstudioragnarok.FBP.particle.FBPParticleManager;
import io.redstudioragnarok.FBP.util.ModReference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
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

@Mod(clientSideOnly = true, modid = ModReference.MOD_ID, name = ModReference.MOD_NAME, version = ModReference.VERSION)
public class FBP {

	@Instance(ModReference.MOD_ID)
	public static FBP INSTANCE;

	public static final ResourceLocation LOCATION_PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");
	public static final ResourceLocation FBP_BUG = new ResourceLocation(ModReference.MOD_ID + ":textures/gui/bug.png");
	public static final ResourceLocation FBP_FBP = new ResourceLocation(ModReference.MOD_ID + ":textures/gui/fbp.png");
	public static final ResourceLocation FBP_WIDGETS = new ResourceLocation(ModReference.MOD_ID + ":textures/gui/widgets.png");

	public static File animBlacklistFile = null;
	public static File particleBlacklistFile = null;
	public static File floatingMaterialsFile = null;
	public static File config = null;

	public static int minAge, maxAge, particlesPerAxis;

	public static double scaleMult, gravityMult, rotationMult, weatherParticleDensity;

	public static boolean enabled = true;
	public static boolean showInMillis = false;
	public static boolean infiniteDuration = false;
	public static boolean randomRotation, spawnWhileFrozen, spawnRedstoneBlockParticles, randomizedScale, randomFadingSpeed, entityCollision, bounceOffWalls, lowTraction, smartBreaking, fancyPlaceAnim, spawnPlaceParticles, fancyRain, fancySnow, fancyFlame, fancySmoke, waterPhysics, frozen;

	public static List<String> blockParticleBlacklist;
	public static List<String> blockAnimBlacklist;
	public static List<Material> floatingMaterials;

	public static final SplittableRandom random = new SplittableRandom();

	public static final Vec3d[] CUBE = {
			// TOP
			new Vec3d(1, 1, -1), new Vec3d(1, 1, 1), new Vec3d(-1, 1, 1), new Vec3d(-1, 1, -1),

			// BOTTOM
			new Vec3d(-1, -1, -1), new Vec3d(-1, -1, 1), new Vec3d(1, -1, 1), new Vec3d(1, -1, -1),

			// FRONT
			new Vec3d(-1, -1, 1), new Vec3d(-1, 1, 1), new Vec3d(1, 1, 1), new Vec3d(1, -1, 1),

			// BACK
			new Vec3d(1, -1, -1), new Vec3d(1, 1, -1), new Vec3d(-1, 1, -1), new Vec3d(-1, -1, -1),

			// LEFT
			new Vec3d(-1, -1, -1), new Vec3d(-1, 1, -1), new Vec3d(-1, 1, 1), new Vec3d(-1, -1, 1),

			// RIGHT
			new Vec3d(1, -1, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, -1), new Vec3d(1, -1, -1)
	};

	public static final Vec3d[] CUBE_NORMALS = {
			new Vec3d(0, 1, 0), new Vec3d(0, -1, 0),

			new Vec3d(0, 0, 1), new Vec3d(0, 0, -1),

			new Vec3d(-1, 0, 0), new Vec3d(1, 0, 0)
	};

	public static final VertexFormat POSITION_TEX_COLOR_LMAP_NORMAL = new VertexFormat();

	public static final FBPAnimationDummyBlock FBPBlock = new FBPAnimationDummyBlock();

	public static IRenderHandler fancyWeatherRenderer, originalWeatherRenderer;
	public static FBPParticleManager fancyEffectRenderer;
	public static ParticleManager originalEffectRenderer;

	public static final FBPEventHandler eventHandler = new FBPEventHandler();
	public static final FBPGuiHandler guiHandler = new FBPGuiHandler();

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

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		config = new File(evt.getModConfigurationDirectory() + "/FBP/Particle.properties");
		animBlacklistFile = new File(evt.getModConfigurationDirectory() + "/FBP/AnimBlockBlacklist.txt");
		particleBlacklistFile = new File(evt.getModConfigurationDirectory() + "/FBP/ParticleBlockBlacklist.txt");
		floatingMaterialsFile = new File(evt.getModConfigurationDirectory() + "/FBP/FloatingMaterials.txt");

		FBPKeyBindings.init();

		MinecraftForge.EVENT_BUS.register(new FBPKeyInputHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(eventHandler);
	}

	@EventHandler
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

				Minecraft.getMinecraft().effectRenderer = FBP.fancyEffectRenderer;
				Minecraft.getMinecraft().world.provider.setWeatherRenderer(FBP.fancyWeatherRenderer);
			} else {
				Minecraft.getMinecraft().effectRenderer = FBP.originalEffectRenderer;
				Minecraft.getMinecraft().world.provider.setWeatherRenderer(FBP.originalWeatherRenderer);
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

	public void addToBlacklist(Block b, boolean particle) {
		if (b == null)
			return;

		String name = Objects.requireNonNull(b.getRegistryName()).toString();

		if (!(particle ? blockParticleBlacklist : blockAnimBlacklist).contains(name))
			(particle ? blockParticleBlacklist : blockAnimBlacklist).add(name);
	}

	public void addToBlacklist(String name, boolean particle) {
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

	public void resetBlacklist(boolean particle) {
		if (particle)
			blockParticleBlacklist.clear();
		else
			blockAnimBlacklist.clear();
	}
}
