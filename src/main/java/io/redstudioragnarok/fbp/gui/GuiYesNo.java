package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

import static io.redstudioragnarok.fbp.gui.Button.ButtonSize.medium;

public class GuiYesNo extends GuiScreen {

	private static int mouseX;
	private static int mouseY;

	GuiButton Yes, No;

	GuiScreen parent;

	public GuiYesNo(GuiScreen s) {
		parent = s;
	}

	@Override
	public void initGui() {
		Yes = new Button(1, this.width / 2 - 75, (int) (this.height / 1.85), medium, I18n.format("menu.yes"), false, false);
		No = new Button(0, this.width / 2 + 26, (int) (this.height / 1.85), medium, I18n.format("menu.no"), false, false);
		Yes.width = No.width = 50;

		this.buttonList.addAll(java.util.Arrays.asList(Yes, No));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 1) {
			ConfigHandler.defaults();
		}
		this.mc.displayGuiScreen(parent);

		ConfigHandler.writeMainConfig();
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
	public void updateScreen() {
		buttonList.forEach(button -> {
			if (button instanceof Button)
				((Button) button).update(mouseX, mouseY);
		});
	}

	@Override
	public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks) {
		mouseX = mouseXIn;
		mouseY = mouseYIn;

		parent.width = this.width;
		parent.height = this.height;

		this.drawDefaultBackground();

		this.drawCenteredString(fontRenderer, I18n.format("menu.confirmation"), this.width / 2, Yes.y - 30, Integer.parseInt("FFFFFF", 16));
		super.drawScreen(mouseXIn, mouseYIn, partialTicks);
	}
}
