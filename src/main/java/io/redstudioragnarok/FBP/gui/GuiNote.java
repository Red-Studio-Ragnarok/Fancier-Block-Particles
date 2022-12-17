package io.redstudioragnarok.FBP.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;

public class GuiNote extends Gui {

	Minecraft mc;

	public GuiNote() {
		this.mc = Minecraft.getMinecraft();

		ScaledResolution scaledResolutionIn = new ScaledResolution(mc);
		int width = scaledResolutionIn.getScaledWidth();

		drawCenteredString(mc.fontRenderer, I18n.format("hud.freeze"), width / 2, 5, Integer.parseInt("FFAA00", 16));
	}
}
