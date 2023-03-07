package io.redstudioragnarok.fbp.keys;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBindings {

	public static KeyBinding menu, freeze, toggle, killParticles, blacklistGUI;

	public static void init() {
		menu = new KeyBinding(I18n.format("keybind.menu"), Keyboard.KEY_P, "Fancier Block Particles");
		freeze = new KeyBinding(I18n.format("keybind.freeze"), Keyboard.KEY_NONE, "Fancier Block Particles");
		toggle = new KeyBinding(I18n.format("keybind.toggle"), Keyboard.KEY_NONE, "Fancier Block Particles");
		killParticles = new KeyBinding(I18n.format("keybind.kill"), Keyboard.KEY_NONE, "Fancier Block Particles");
		blacklistGUI = new KeyBinding(I18n.format("keybind.blacklistGUI"), Keyboard.KEY_B, "Fancier Block Particles");

		ClientRegistry.registerKeyBinding(menu);
		ClientRegistry.registerKeyBinding(freeze);
		ClientRegistry.registerKeyBinding(toggle);
		ClientRegistry.registerKeyBinding(killParticles);
		ClientRegistry.registerKeyBinding(blacklistGUI);
	}
}
