package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.BasePage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.large;

public class Page1 extends BasePage {

	GuiButton randomRotation, cartoonMode, randomizedScale, randomFadingSpeed, spawnRedstoneBlockParticles, spawnWhileFrozen;

	@Override
	public void initGui() {
		super.initPage(new Page0(), new Page2());

		randomRotation = addButton(1, x, this.height / 5 - 6, large, I18n.format("menu.randomRotation.title"), FBP.randomRotation, true, true);
		cartoonMode = addButton(2, x, randomRotation.y + randomRotation.height + 1, large, I18n.format("menu.cartonMode.title"), false, false, false);
		randomizedScale = addButton(3, x, cartoonMode.y + cartoonMode.height + 6, large, I18n.format("menu.randomizedScale.title"), FBP.randomizedScale, true, true);
		randomFadingSpeed = addButton(4, x, randomizedScale.y + randomizedScale.height + 1, large, I18n.format("menu.randomFadeSpeed.title"), FBP.randomFadingSpeed, true, true);
		spawnRedstoneBlockParticles = addButton(5, x, randomFadingSpeed.y + randomFadingSpeed.height + 6, large, I18n.format("menu.redstoneBlock.title"), FBP.spawnRedstoneBlockParticles, true, true);
		spawnWhileFrozen = addButton(6, x, spawnRedstoneBlockParticles.y + spawnRedstoneBlockParticles.height + 1, large, I18n.format("menu.spawnFreeze.title"), FBP.spawnWhileFrozen, true, true);
	}

	@Override
	protected void onActionPerformed(GuiButton button) {
		switch (button.id) {
			case 1:
				FBP.randomRotation = !FBP.randomRotation;
				writeConfig = true;
				break;
			case 3:
				FBP.randomizedScale = !FBP.randomizedScale;
				writeConfig = true;
				break;
			case 4:
				FBP.randomFadingSpeed = !FBP.randomFadingSpeed;
				writeConfig = true;
				break;
			case 5:
				FBP.spawnRedstoneBlockParticles = !FBP.spawnRedstoneBlockParticles;
				writeConfig = true;
				break;
			case 6:
				FBP.spawnWhileFrozen = !FBP.spawnWhileFrozen;
				writeConfig = true;
				break;
		}
	}

	protected String getDescription() {
		String description = "";

		for (GuiButton button : this.buttonList) {
			if (button.isMouseOver()) {
				switch (button.id) {
					case 1:
						description = I18n.format("menu.randomRotation.description");
						break;
					case 2:
						description = I18n.format("menu.cartonMode.description");
						break;
					case 3:
						description = I18n.format("menu.randomizedScale.description");
						break;
					case 4:
						description = I18n.format("menu.randomFadeSpeed.description");
						break;
					case 5:
						description = I18n.format("menu.redstoneBlock.description");
						break;
					case 6:
						description = I18n.format("menu.spawnFreeze.description");
						break;
					default:
						description = I18n.format("menu.noDescriptionFound");
						break;
				}
			}
		}

		return description;
	}
}
