package io.redstudioragnarok.fbp.handlers;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.GuiBlacklist;
import io.redstudioragnarok.fbp.gui.pages.Page0;
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

	private static boolean blacklistGUIOpen = false;

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

		if (KeyBindings.blacklistGUI.isKeyDown()) {
			Block block;
			ItemStack stack = null;

			boolean useHeldBlock = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && (block = Block.getBlockFromName((stack = mc.player.getHeldItemMainhand()).getItem().getRegistryName().toString())) != null && block != Blocks.AIR;

			if (!blacklistGUIOpen && (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.BLOCK) || useHeldBlock) && mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() != FBP.dummyBlock) {
				mc.displayGuiScreen(useHeldBlock ? (new GuiBlacklist(stack)) : (new GuiBlacklist(mc.objectMouseOver.getBlockPos())));

				Mouse.setGrabbed(true);

				blacklistGUIOpen = true;
			}
		} else if (blacklistGUIOpen)
			blacklistGUIOpen = false;
	}
}
