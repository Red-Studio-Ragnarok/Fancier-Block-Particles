package com.TominoCZ.FBP.gui;

import com.TominoCZ.FBP.handler.FBPConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class FBPGuiYesNo extends GuiScreen {

	GuiButton Yes, No;

	GuiScreen parent;

	public FBPGuiYesNo(GuiScreen s) {
		parent = s;
	}

	@Override
	public void initGui() {
		Yes = new FBPGuiButton(1, this.width / 2 - 75, (int) (this.height / 1.85), I18n.format("menu.yes"), false, false, true);
		No = new FBPGuiButton(0, this.width / 2 + 26, (int) (this.height / 1.85), I18n.format("menu.no"), false, false, true);
		Yes.width = No.width = 50;

		this.buttonList.addAll(java.util.Arrays.asList(Yes, No));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 1) {
			FBPConfigHandler.defaults(true);
		}
		this.mc.displayGuiScreen(parent);

		FBPConfigHandler.write();
	}

	@Override
	protected void keyTyped(char c, int keyCode) throws IOException {
		if (keyCode == 1) {
			mc.displayGuiScreen(parent);
			return;
		}

		super.keyTyped(c, keyCode);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		parent.width = this.width;
		parent.height = this.height;

		this.drawDefaultBackground();

		this.drawCenteredString(fontRenderer, I18n.format("menu.confirmation"), this.width / 2, Yes.y - 30, Integer.parseInt("FFAA00", 16));
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}