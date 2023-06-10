package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.BasePage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class Page1 extends BasePage {

	@Override
	public void initGui() {
		super.initPage(new Page0(), new Page2());

		addButton(1, I18n.format("menu.randomRotation.title"), FBP.randomRotation, true);
		addButton(2, I18n.format("menu.randomizedScale.title"), FBP.randomizedScale, true);
		addButton(3, I18n.format("menu.randomFadeSpeed.title"), FBP.randomFadingSpeed, true);
		addButton(4, I18n.format("menu.spawnPlaceParticles.title"), FBP.spawnPlaceParticles, true);
		addButton(5, I18n.format("menu.spawnFreeze.title"), FBP.spawnWhileFrozen, true);

		super.updateScreen();
	}

	@Override
	protected void onActionPerformed(final GuiButton button) {
		switch (button.id) {
			case 1:
				FBP.randomRotation = !FBP.randomRotation;
				writeConfig = true;
				break;
			case 2:
				FBP.randomizedScale = !FBP.randomizedScale;
				writeConfig = true;
				break;
			case 3:
				FBP.randomFadingSpeed = !FBP.randomFadingSpeed;
				writeConfig = true;
				break;
			case 4:
				FBP.spawnPlaceParticles = !FBP.spawnPlaceParticles;
				writeConfig = true;
				break;
			case 5:
				FBP.spawnWhileFrozen = !FBP.spawnWhileFrozen;
				writeConfig = true;
				break;
		}
	}

	protected String updateDescription() {
		for (GuiButton button : buttonList) {
			if (button.isMouseOver()) {
				switch (button.id) {
					case 1:
						return I18n.format("menu.randomRotation.description");
					case 2:
						return I18n.format("menu.randomizedScale.description");
					case 3:
						return I18n.format("menu.randomFadeSpeed.description");
					case 4:
						return I18n.format("menu.spawnPlaceParticles.description");
					case 5:
						return I18n.format("menu.spawnFreeze.description");
				}
			}
		}

		return descriptionFallBack;
	}
}
