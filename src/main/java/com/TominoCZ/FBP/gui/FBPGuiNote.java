package com.TominoCZ.FBP.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;

public class FBPGuiNote extends Gui {

	Minecraft mc;

	public FBPGuiNote() {
		this.mc = Minecraft.getMinecraft();

		ScaledResolution scaledResolutionIn = new ScaledResolution(mc);
		int width = scaledResolutionIn.getScaledWidth();

		drawCenteredString(mc.fontRenderer, I18n.format("hud.freeze"), width / 2, 5, Integer.parseInt("FFAA00", 16));
	}
}