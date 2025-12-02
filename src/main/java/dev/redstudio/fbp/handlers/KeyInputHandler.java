package dev.redstudio.fbp.handlers;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.gui.GuiBlacklist;
import dev.redstudio.fbp.gui.pages.Page0;
import dev.redstudio.fbp.keys.KeyBindings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import static dev.redstudio.fbp.FBP.MC;

public final class KeyInputHandler {

	private static boolean blacklistGUIOpen = false;

	@SubscribeEvent
	public static void onKeyboardInput(final InputEvent.KeyInputEvent keyInputEvent) {
		onInput();
	}

	public static void onInput() {
		if (KeyBindings.MENU.isPressed())
			MC.displayGuiScreen(new Page0());

		if (KeyBindings.FREEZE.isPressed() && FBP.enabled)
			FBP.frozen = !FBP.frozen;

		if (KeyBindings.TOGGLE.isPressed())
			FBP.setEnabled(!FBP.enabled);

		if (KeyBindings.BLACKLIST_GUI.isKeyDown()) {
			Block block;
			ItemStack stack = null;

			boolean useHeldBlock = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && (block = Block.getBlockFromName((stack = MC.player.getHeldItemMainhand()).getItem().getRegistryName().toString())) != null && block != Blocks.AIR;

			if (!blacklistGUIOpen && (MC.objectMouseOver != null && MC.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.BLOCK) || useHeldBlock) && MC.world.getBlockState(MC.objectMouseOver.getBlockPos()).getBlock() != FBP.DUMMY_BLOCK) {
				MC.displayGuiScreen(useHeldBlock ? (new GuiBlacklist(stack)) : (new GuiBlacklist(MC.objectMouseOver.getBlockPos())));

				Mouse.setGrabbed(true);

				blacklistGUIOpen = true;
			}
		} else if (blacklistGUIOpen) {
			blacklistGUIOpen = false;
		}
	}
}
