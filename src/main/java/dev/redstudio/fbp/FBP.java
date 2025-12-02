package dev.redstudio.fbp;

import dev.redstudio.fbp.block.AnimationDummyBlock;
import dev.redstudio.fbp.handlers.*;
import dev.redstudio.fbp.keys.KeyBindings;
import dev.redstudio.fbp.particles.FBPParticleManager;
import meldexun.matrixutil.MathUtil;
import net.jafama.FastMath;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.*;

import static dev.redstudio.fbp.ProjectConstants.*;

//   /$$$$$$$$                           /$$                           /$$$$$$$  /$$                     /$$             /$$$$$$$                       /$$     /$$           /$$
//  | $$_____/                          |__/                          | $$__  $$| $$                    | $$            | $$__  $$                     | $$    |__/          | $$
//  | $$    /$$$$$$  /$$$$$$$   /$$$$$$$ /$$  /$$$$$$   /$$$$$$       | $$  \ $$| $$  /$$$$$$   /$$$$$$$| $$   /$$      | $$  \ $$ /$$$$$$   /$$$$$$  /$$$$$$   /$$  /$$$$$$$| $$  /$$$$$$   /$$$$$$$
//  | $$$$$|____  $$| $$__  $$ /$$_____/| $$ /$$__  $$ /$$__  $$      | $$$$$$$ | $$ /$$__  $$ /$$_____/| $$  /$$/      | $$$$$$$/|____  $$ /$$__  $$|_  $$_/  | $$ /$$_____/| $$ /$$__  $$ /$$_____/
//  | $$__/ /$$$$$$$| $$  \ $$| $$      | $$| $$$$$$$$| $$  \__/      | $$__  $$| $$| $$  \ $$| $$      | $$$$$$/       | $$____/  /$$$$$$$| $$  \__/  | $$    | $$| $$      | $$| $$$$$$$$|  $$$$$$
//  | $$   /$$__  $$| $$  | $$| $$      | $$| $$_____/| $$            | $$  \ $$| $$| $$  | $$| $$      | $$_  $$       | $$      /$$__  $$| $$        | $$ /$$| $$| $$      | $$| $$_____/ \____  $$
//  | $$  |  $$$$$$$| $$  | $$|  $$$$$$$| $$|  $$$$$$$| $$            | $$$$$$$/| $$|  $$$$$$/|  $$$$$$$| $$ \  $$      | $$     |  $$$$$$$| $$        |  $$$$/| $$|  $$$$$$$| $$|  $$$$$$$ /$$$$$$$/
//  |__/   \_______/|__/  |__/ \_______/|__/ \_______/|__/            |_______/ |__/ \______/  \_______/|__/  \__/      |__/      \_______/|__/         \___/  |__/ \_______/|__/ \_______/|_______/
@Mod(clientSideOnly = true, modid = ID, name = NAME, version = VERSION, dependencies = "required-after:redcore@[0.4-Dev-3,)", guiFactory = "dev.redstudio.fbp.config.FBPConfigGuiFactory", updateJSON = "https://forge.curseupdate.com/666575/fbp")
public class FBP {

    public static final Minecraft MC = Minecraft.getMinecraft();

    public static final ResourceLocation PARTICLES_TEXTURE = new ResourceLocation("textures/particle/particles.png");

    public static File mainConfigFile, floatingMaterialsFile, animBlacklistFile, particleBlacklistFile;

    public static File oldMainConfigFile, oldFloatingMaterialsFile, oldParticleBlacklistFile, oldAnimBlacklistFile;

    public static File oldNewMainConfigFile;

    public static boolean enabled, frozen, showInMillis, infiniteDuration, randomRotation, spawnWhileFrozen, randomizedScale, randomFadingSpeed, entityCollision, bounceOffWalls, lowTraction, smartBreaking, fancyFlame, fancySmoke, waterPhysics;
    public static boolean fancyPlaceAnim, spawnPlaceParticles;
    public static boolean fancyWeather, dynamicWeather;

    public static boolean experiments;

    public static boolean debug;

    public static int minAge, maxAge, particlesPerAxis;

    public static float scaleMult, gravityMult, rotationMult;
    public static float weatherParticleDensity, weatherRenderDistance;

    public static List<Material> floatingMaterials = new ArrayList<>();
    public static List<String> blockParticleBlacklist = new ArrayList<>();
    public static List<String> blockAnimBlacklist = new ArrayList<>();

    public static final SplittableRandom RANDOM = new SplittableRandom();

    public static IRenderHandler fancyWeatherRenderer, originalWeatherRenderer;
    public static FBPParticleManager fancyEffectRenderer;
    public static ParticleManager originalEffectRenderer;

    public static final AnimationDummyBlock DUMMY_BLOCK = new AnimationDummyBlock();

    public static final VertexFormat VERTEX_FORMAT = new VertexFormat();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent preInitializationEvent) {
        oldMainConfigFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/Particle.properties");
        oldFloatingMaterialsFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/FloatingMaterials.txt");
        oldAnimBlacklistFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/AnimBlockBlacklist.txt");
        oldParticleBlacklistFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/ParticleBlockBlacklist.txt");

        oldNewMainConfigFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/Config.txt");

        mainConfigFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/Config.yaml");
        floatingMaterialsFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/Floating Materials.txt");
        animBlacklistFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/Animation Block Blacklist.txt");
        particleBlacklistFile = new File(preInitializationEvent.getModConfigurationDirectory() + "/fbp/Particle Block Blacklist.txt");

        ConfigHandler.init();

        MathUtil.setSinFunc(FastMath::sin);
        MathUtil.setCosFunc(FastMath::cos);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent initializationEvent) {
        KeyBindings.init();

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new GuiHandler());

        MinecraftForge.EVENT_BUS.register(KeyInputHandler.class);

        updateDebugHandler();

        VERTEX_FORMAT.addElement(DefaultVertexFormats.POSITION_3F);
        VERTEX_FORMAT.addElement(DefaultVertexFormats.TEX_2F);
        VERTEX_FORMAT.addElement(DefaultVertexFormats.COLOR_4UB);
        VERTEX_FORMAT.addElement(DefaultVertexFormats.TEX_2S);
        VERTEX_FORMAT.addElement(DefaultVertexFormats.NORMAL_3B);
    }

    /**
     * Enable or disable FBP
     * <p>
     * Currently, not really documented as it probably won't exist at least not like this when the separation from MC particle system is done
     */
    public static void setEnabled(boolean newState) {
        if (enabled != newState) {
            if (newState) {
                if (fancyEffectRenderer != null)
                    fancyEffectRenderer.carryOver();

                MC.effectRenderer = fancyEffectRenderer;

                if (MC.world != null && fancyWeather)
                    MC.world.provider.setWeatherRenderer(fancyWeatherRenderer);
            } else {
                MC.effectRenderer = originalEffectRenderer;

                if (MC.world != null)
                    MC.world.provider.setWeatherRenderer(originalWeatherRenderer);

                frozen = false;
            }
        }

        enabled = newState;
    }

    public static void updateDebugHandler() {
        if (debug)
            MinecraftForge.EVENT_BUS.register(DebugHandler.class);
        else
            MinecraftForge.EVENT_BUS.unregister(DebugHandler.class);
    }
}
