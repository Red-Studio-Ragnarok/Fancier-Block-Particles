package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.GuiHelper;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class Page1 extends BaseSettingsPage {

	GuiButton randomRotation, cartoonMode, randomizedScale, randomFadingSpeed, spawnRedstoneBlockParticles, spawnWhileFrozen;

	String description;

	@Override
	public void initGui() {
		super.initGui();
		super.initNavigation(new Page0(), new Page2());

		int x = this.width / 2 - 200 / 2;

		randomRotation = addButton(1, x, this.height / 5 - 6, I18n.format("menu.randomrotation.info"), FBP.randomRotation, true, true);
		cartoonMode = addButton(2, x, randomRotation.y + randomRotation.height + 1, I18n.format("menu.cartonmode.info"), false, false, false);
		randomizedScale = addButton(3, x, cartoonMode.y + cartoonMode.height + 6, I18n.format("menu.randomizedscale.info"), FBP.randomizedScale, true, true);
		randomFadingSpeed = addButton(4, x, randomizedScale.y + randomizedScale.height + 1, I18n.format("menu.randomfadespeed.info"), FBP.randomFadingSpeed, true, true);
		spawnRedstoneBlockParticles = addButton(5, x, randomFadingSpeed.y + randomFadingSpeed.height + 6, I18n.format("menu.redstoneblock.info"), FBP.spawnRedstoneBlockParticles, true, true);
		spawnWhileFrozen = addButton(6, x, spawnRedstoneBlockParticles.y + spawnRedstoneBlockParticles.height + 1, I18n.format("menu.spawnfreeze.info"), FBP.spawnWhileFrozen, true, true);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 1:
			FBP.randomRotation = !FBP.randomRotation;
			break;
		case 2:
			break;
		case 3:
			FBP.randomizedScale = !FBP.randomizedScale;
			break;
		case 4:
			FBP.randomFadingSpeed = !FBP.randomFadingSpeed;
			break;
		case 5:
			FBP.spawnRedstoneBlockParticles = !FBP.spawnRedstoneBlockParticles;
			break;
		case 6:
			FBP.spawnWhileFrozen = !FBP.spawnWhileFrozen;
			break;
		}

		super.actionPerformed(button);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		getDescription();

		if ((mouseX >= randomRotation.x && mouseX < randomRotation.x + randomRotation.width) && (mouseY >= randomRotation.y && mouseY < spawnWhileFrozen.y + randomRotation.height)) {
			this.drawCenteredString(fontRenderer, description, this.width / 2, height / 5 + 131, fontRenderer.getColorCode('f'));
		}
	}

	private void getDescription() {
		for (GuiButton b : this.buttonList) {
			if (b.isMouseOver()) {
				switch (b.id) {
					case 1:
						description = I18n.format("menu.randomrotation.description");
						break;
					case 2:
						description = I18n.format("menu.cartonmode.description");
						break;
					case 3:
						description = I18n.format("menu.randomizedscale.description");
						break;
					case 4:
						description = I18n.format("menu.randomfadespeed.description");
						break;
					case 5:
						description = I18n.format("menu.redstoneblock.description");
						break;
					case 6:
						description = I18n.format("menu.spawnfreeze.description");
						break;
					default:
						description = I18n.format("menu.noDescriptionFound");
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			for (GuiButton guibutton : this.buttonList) {
				if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
					if (!guibutton.isMouseOver())
						return;

					this.actionPerformed(guibutton);
				}
			}
		}
	}

	@Override
	public void onGuiClosed() {
		ConfigHandler.writeMainConfig();
	}
}
