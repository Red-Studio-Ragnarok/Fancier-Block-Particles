package dev.redstudio.fbp.gui.elements;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.gui.GuiUtils;
import dev.redstudio.fbp.gui.InteractiveElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ButtonGlobalToggle extends InteractiveElement {

	private static final String hoverTextEnable = I18n.format("menu.disable") + " FBP";
	private static final String hoverTextDisable = I18n.format("menu.enable") + " FBP";

	public ButtonGlobalToggle(final int id, final int x, final int y) {
		super(id, x, y, "");
	}

	public void update(final int mouseX, final int mouseY) {
		hovered = GuiUtils.isMouseInsideStadium(mouseX, mouseY, x, y, width, height);
	}

	@Override
	public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
		startDrawing(true);

		int v = FBP.enabled ? 156 : 156 + height * 2;

		if (hovered)
			v += height;

		drawTexturedModalRect(x, y, 186, v, width, height);

		drawHoverText(FBP.enabled ? hoverTextEnable : hoverTextDisable, mouseX, mouseY);
	}

	@Override
	public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
		if (hovered) {
			playPressSound(mc.getSoundHandler());
			return true;
		} else
			return false;
	}
}
