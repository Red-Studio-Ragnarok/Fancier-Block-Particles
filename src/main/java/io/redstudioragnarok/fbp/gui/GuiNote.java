package io.redstudioragnarok.fbp.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;

import static io.redstudioragnarok.fbp.FBP.mc;

public class GuiNote extends Gui {
	public GuiNote() {
		ScaledResolution scaledResolutionIn = new ScaledResolution(mc);
		int width = scaledResolutionIn.getScaledWidth();

		drawCenteredString(mc.fontRenderer, I18n.format("hud.freeze"), width / 2, 5, Integer.parseInt("FFAA00", 16));
	}
}
