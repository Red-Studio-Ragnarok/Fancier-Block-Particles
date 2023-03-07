package io.redstudioragnarok.fbp.keys;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBindings {

	public static KeyBinding menu, toggle, freeze, killParticles, blacklistGUI;

	public static void init() {
		menu = new KeyBinding(I18n.format("keybind.menu"), Keyboard.KEY_P, I18n.format("name"));
		toggle = new KeyBinding(I18n.format("keybind.toggle"), Keyboard.KEY_NONE, I18n.format("name"));
		freeze = new KeyBinding(I18n.format("keybind.freeze"), Keyboard.KEY_NONE, I18n.format("name"));
		killParticles = new KeyBinding(I18n.format("keybind.kill"), Keyboard.KEY_NONE, I18n.format("name"));
		blacklistGUI = new KeyBinding(I18n.format("keybind.blacklistGUI"), Keyboard.KEY_B, I18n.format("name"));

		ClientRegistry.registerKeyBinding(menu);
		ClientRegistry.registerKeyBinding(toggle);
		ClientRegistry.registerKeyBinding(freeze);
		ClientRegistry.registerKeyBinding(killParticles);
		ClientRegistry.registerKeyBinding(blacklistGUI);
	}
}
