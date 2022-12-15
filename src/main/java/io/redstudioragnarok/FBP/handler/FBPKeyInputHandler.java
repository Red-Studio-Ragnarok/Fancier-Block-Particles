package io.redstudioragnarok.FBP.handler;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.gui.FBPGuiBlacklist;
import io.redstudioragnarok.FBP.gui.FBPGuiMenuPage0;
import io.redstudioragnarok.FBP.keys.FBPKeyBindings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import static io.redstudioragnarok.FBP.FBP.mc;

public class FBPKeyInputHandler {

	public static FBPKeyInputHandler INSTANCE;

	boolean wasOpened = false;


	public FBPKeyInputHandler() {
		INSTANCE = this;
	}

	@SubscribeEvent
	public void onKeyboardInput(InputEvent.KeyInputEvent e) {
		onInput();
	}

	public void onInput() {
		if (FBPKeyBindings.FBPMenu.isPressed())
			mc.displayGuiScreen(new FBPGuiMenuPage0());

		if (FBPKeyBindings.FBPFreeze.isPressed())
			FBP.frozen = !FBP.frozen;

		if (FBPKeyBindings.FBPToggle.isPressed())
			FBP.setEnabled(!FBP.enabled);

		boolean isShiftDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
		boolean isFastAddDown = FBPKeyBindings.FBPBlacklistMenu.isKeyDown();

		if (isFastAddDown) {
			Block block;
			ItemStack stack = null;

			boolean useHeldBlock = isShiftDown && (block = Block.getBlockFromName((stack = mc.player.getHeldItemMainhand()).getItem().getRegistryName().toString())) != null && block != Blocks.AIR;

			if (!wasOpened && (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.BLOCK) || useHeldBlock)) {
				mc.displayGuiScreen(useHeldBlock ? (new FBPGuiBlacklist(stack)) : (new FBPGuiBlacklist(mc.objectMouseOver.getBlockPos())));

				Mouse.setGrabbed(true);

				wasOpened = true;
			}
		} else if (wasOpened)
			wasOpened = false;
	}
}
