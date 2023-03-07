package io.redstudioragnarok.fbp.handlers;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.GuiBlacklist;
import io.redstudioragnarok.fbp.gui.menu.Page0;
import io.redstudioragnarok.fbp.keys.KeyBindings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import static io.redstudioragnarok.fbp.FBP.mc;

public class KeyInputHandler {

	private static boolean wasOpened = false;

	@SubscribeEvent
	public static void onKeyboardInput(InputEvent.KeyInputEvent e) {
		onInput();
	}

	public static void onInput() {
		if (KeyBindings.menu.isPressed())
			mc.displayGuiScreen(new Page0());

		if (KeyBindings.freeze.isPressed() && FBP.enabled)
			FBP.frozen = !FBP.frozen;

		if (KeyBindings.toggle.isPressed())
			FBP.setEnabled(!FBP.enabled);

		boolean isShiftDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		boolean isFastAddDown = KeyBindings.blacklistGUI.isKeyDown();

		if (isFastAddDown) {
			Block block;
			ItemStack stack = null;

			boolean useHeldBlock = isShiftDown && (block = Block.getBlockFromName((stack = mc.player.getHeldItemMainhand()).getItem().getRegistryName().toString())) != null && block != Blocks.AIR;

			if (!wasOpened && (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.BLOCK) || useHeldBlock)) {
				mc.displayGuiScreen(useHeldBlock ? (new GuiBlacklist(stack)) : (new GuiBlacklist(mc.objectMouseOver.getBlockPos())));

				Mouse.setGrabbed(true);

				wasOpened = true;
			}
		} else if (wasOpened)
			wasOpened = false;
	}
}
