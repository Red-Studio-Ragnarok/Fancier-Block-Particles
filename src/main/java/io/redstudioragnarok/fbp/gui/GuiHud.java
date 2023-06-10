package io.redstudioragnarok.fbp.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import static io.redstudioragnarok.fbp.FBP.mc;

public class GuiHud extends Gui {

	public GuiHud(final String text, final String color) {
		mc.fontRenderer.drawStringWithShadow(text, ((float) new ScaledResolution(mc).getScaledWidth() / 2 - (float) mc.fontRenderer.getStringWidth(text) / 2), 5, GuiUtils.hexToDecimalColor(color));
	}
}
