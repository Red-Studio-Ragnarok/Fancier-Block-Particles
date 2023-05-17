package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.gui.elements.Button;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

import static io.redstudioragnarok.fbp.gui.elements.Button.ButtonSize.medium;

public class GuiConfirmation extends GuiBase {

	public enum Action {
		DefaultConfig
	}

	Button Yes, No;

	private final GuiBase parent;

	private final Action action;

	private final String text;

	public GuiConfirmation(GuiBase parent, Action action,String text) {
		this.parent = parent;
		this.action = action;
		this.text = text;
	}

	@Override
	public void initGui() {
		middleX = width / 2;
		middleY = height / 2;

		Yes = new Button(0, middleX - 75, middleY, medium, I18n.format("menu.yes"), false, false);
		No = new Button(1, middleX + 26, middleY, medium, I18n.format("menu.no"), false, false);

		Yes.width = No.width = 50;

		this.buttonList.addAll(java.util.Arrays.asList(Yes, No));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			switch (action) {
				case DefaultConfig:
					ConfigHandler.defaults();
					ConfigHandler.writeMainConfig();
					break;
			}
		}

		this.mc.displayGuiScreen(parent);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) {
			mc.displayGuiScreen(parent);
			return;
		}

		super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks) {
		drawBackground(mouseXIn, mouseYIn);

		// Draw the text
		drawCenteredString(text, "#FFFFFF", middleX, Yes.y - 30);

		super.drawScreen(mouseXIn, mouseYIn, partialTicks);
	}
}
