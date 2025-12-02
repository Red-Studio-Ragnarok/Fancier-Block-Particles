package dev.redstudio.fbp.gui;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.gui.elements.Button;
import dev.redstudio.fbp.handlers.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

import static dev.redstudio.fbp.gui.elements.Button.ButtonSize.medium;

public class GuiConfirmation extends GuiBase {

	public enum Action {
		DefaultConfig,
		EnableDebug,
		EnableExperiments
	}

	Button Yes, No;

	private final GuiBase parent;

	private final Action action;

	private final String text;

	public GuiConfirmation(final GuiBase parent, final Action action, final String text) {
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

		buttonList.addAll(java.util.Arrays.asList(Yes, No));
	}

	@Override
	protected void actionPerformed(final GuiButton button) {
		if (button.id == 0) {
			switch (action) {
				case DefaultConfig:
					ConfigHandler.defaults();
					ConfigHandler.writeMainConfig();
					break;
				case EnableDebug:
					FBP.debug = true;
					break;
				case EnableExperiments:
					FBP.experiments = true;
					break;
			}
		}

		mc.displayGuiScreen(parent);
	}

	@Override
	protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
		if (keyCode == 1) {
			mc.displayGuiScreen(parent);
			return;
		}

		super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void drawScreen(final int mouseXIn, final int mouseYIn, final float partialTicks) {
		drawBackground(mouseXIn, mouseYIn);

		drawCenteredString(text, GuiUtils.WHITE, middleX, Yes.y - 30);

		super.drawScreen(mouseXIn, mouseYIn, partialTicks);
	}

	@Override
	public void onGuiClosed() {
	}
}
