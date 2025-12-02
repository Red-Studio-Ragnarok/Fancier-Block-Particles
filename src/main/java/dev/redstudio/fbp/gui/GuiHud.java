package dev.redstudio.fbp.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import static dev.redstudio.fbp.FBP.MC;

public class GuiHud extends Gui {

	public GuiHud(final String text, final String color) {
		MC.fontRenderer.drawStringWithShadow(text, ((float) new ScaledResolution(MC).getScaledWidth() / 2 - (float) MC.fontRenderer.getStringWidth(text) / 2), 5, GuiUtils.hexToDecimalColor(color));
	}
}
