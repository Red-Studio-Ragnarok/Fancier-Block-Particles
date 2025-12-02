package dev.redstudio.fbp.gui.pages;

import dev.redstudio.fbp.gui.BasePage;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class PageExperiments extends BasePage {

	@Override
	public void initGui() {
		isExperiments = true;
		super.initPage(null, null);

		super.updateScreen();
	}

	@Override
	protected void onActionPerformed(final GuiButton button) {
		switch (button.id) {
		}
	}

	protected String updateDescription() {
		for (GuiButton button : buttonList) {
			if (button.isMouseOver()) {
				switch (button.id) {
				}
			}
		}

		return descriptionFallBack;
	}

	@Override
	protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
		if (keyCode == 1) {
			mc.displayGuiScreen(new Page0());
			return;
		}

		super.keyTyped(typedChar, keyCode);
	}
}
