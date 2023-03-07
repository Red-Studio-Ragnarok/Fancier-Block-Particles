package io.redstudioragnarok.fbp.keys;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBindings {

	public static KeyBinding menu, freeze, toggle, killParticles, blacklistGUI;

	public static void init() {
		menu = new KeyBinding("Open Menu", Keyboard.KEY_P, "Fancier Block Particles");
		freeze = new KeyBinding("Toggle Freeze Effect", Keyboard.KEY_NONE, "Fancier Block Particles");
		toggle = new KeyBinding("Enable/Disable", Keyboard.KEY_NONE, "Fancier Block Particles");
		killParticles = new KeyBinding("Kill Particles", Keyboard.KEY_NONE, "Fancier Block Particles");
		blacklistGUI = new KeyBinding("Blacklist Block", Keyboard.KEY_B, "Fancier Block Particles");

		ClientRegistry.registerKeyBinding(menu);
		ClientRegistry.registerKeyBinding(freeze);
		ClientRegistry.registerKeyBinding(toggle);
		ClientRegistry.registerKeyBinding(killParticles);
		ClientRegistry.registerKeyBinding(blacklistGUI);
	}
}
