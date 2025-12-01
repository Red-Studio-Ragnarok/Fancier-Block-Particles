package dev.redstudio.fbp.keys;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import static dev.redstudio.fbp.ProjectConstants.NAME;

/**
 * A class that holds and initialize all the keybindings for the mod.
 *
 * @author Desoroxxx
 */
public class KeyBindings {

    public static final KeyBinding MENU = new KeyBinding(I18n.format("keyBinding.menu"), Keyboard.KEY_P, NAME);
    public static final KeyBinding TOGGLE = new KeyBinding(I18n.format("keyBinding.toggle"), Keyboard.KEY_NONE, NAME);
    public static final KeyBinding FREEZE = new KeyBinding(I18n.format("keyBinding.freeze"), Keyboard.KEY_NONE, NAME);
    public static final KeyBinding KILL_PARTICLES = new KeyBinding(I18n.format("keyBinding.kill"), Keyboard.KEY_NONE, NAME);
    public static final KeyBinding BLACKLIST_GUI = new KeyBinding(I18n.format("keyBinding.blacklistGUI"), Keyboard.KEY_B, NAME);

    public static void init() {
        ClientRegistry.registerKeyBinding(MENU);
        ClientRegistry.registerKeyBinding(TOGGLE);
        ClientRegistry.registerKeyBinding(FREEZE);
        ClientRegistry.registerKeyBinding(KILL_PARTICLES);
        ClientRegistry.registerKeyBinding(BLACKLIST_GUI);
    }
}
