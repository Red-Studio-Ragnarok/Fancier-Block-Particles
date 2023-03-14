package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.*;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import io.redstudioragnarok.fbp.utils.ModReference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.awt.Desktop;
import java.awt.Dimension;
import java.util.Arrays;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.small;

public class Page1 extends GuiScreen {

	GuiButton defaults, done, reload, enable, reportBug;
	GuiButton back, next;

	GuiButton randomRotation, cartoonMode, randomizedScale, randomFadingSpeed, spawnRedstoneBlockParticles, spawnWhileFrozen;

	String description;

	final int GUIOffsetY = 4;

	@Override
	public void initGui() {
		int x = this.width / 2 - 200 / 2;

		randomRotation = new FBPGuiButton(1, x, this.height / 5 - 10 + GUIOffsetY, I18n.format("menu.randomrotation.info"), FBP.randomRotation, true, true);
		cartoonMode = new FBPGuiButton(2, x, randomRotation.y + randomRotation.height + 1, I18n.format("menu.cartonmode.info"), false, false, false);
		randomizedScale = new FBPGuiButton(3, x, cartoonMode.y + cartoonMode.height + 6, I18n.format("menu.randomizedscale.info"), FBP.randomizedScale, true, true);
		randomFadingSpeed = new FBPGuiButton(4, x, randomizedScale.y + randomizedScale.height + 1, I18n.format("menu.randomfadespeed.info"), FBP.randomFadingSpeed, true, true);
		spawnRedstoneBlockParticles = new FBPGuiButton(5, x, randomFadingSpeed.y + randomFadingSpeed.height + 6, I18n.format("menu.redstoneblock.info"), FBP.spawnRedstoneBlockParticles, true, true);
		spawnWhileFrozen = new FBPGuiButton(6, x, spawnRedstoneBlockParticles.y + spawnRedstoneBlockParticles.height + 1, I18n.format("menu.spawnfreeze.info"), FBP.spawnWhileFrozen, true, true);

		defaults = new FBPGuiButton(0, this.width / 2 + 2, spawnWhileFrozen.y + spawnWhileFrozen.height + 24 - GUIOffsetY, I18n.format("menu.defaults"), false, false, true);
		done = new FBPGuiButton(-1, this.width / 2 - 100, defaults.y, I18n.format("menu.done"), false, false, true);
		defaults.width = done.width = 98;
		reload = new FBPGuiButton(-2, this.width / 2 - 100, defaults.y + defaults.height + 1, I18n.format("menu.reloadconfig"), false, false, true);
		reload.width = randomRotation.width = cartoonMode.width = randomizedScale.width = randomFadingSpeed.width = spawnRedstoneBlockParticles.width = spawnWhileFrozen.width = 200;
		enable = new GuiButtonEnable(-6, (this.width - 25 - 27) - 4, 2, this.fontRenderer);
		reportBug = new GuiButtonBugReport(-4, this.width - 27, 2, new Dimension(width, height), this.fontRenderer);

		back = new FBPGuiButton(-3, spawnWhileFrozen.x - 45, spawnWhileFrozen.y - 4 - GUIOffsetY, small, "\u00A76<<", false, false, true);
		next = new FBPGuiButton(-5, spawnWhileFrozen.x + spawnWhileFrozen.width + 25, spawnWhileFrozen.y - 4 - GUIOffsetY, small, "\u00A76>>", false, false, true);

		this.buttonList.addAll(Arrays.asList(defaults, done, reload, enable, reportBug, back, next));
		this.buttonList.addAll(Arrays.asList(randomRotation, cartoonMode, randomizedScale, randomFadingSpeed, spawnRedstoneBlockParticles, spawnWhileFrozen));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case -6:
			FBP.setEnabled(!FBP.enabled);
			break;
		case -5:
			this.mc.displayGuiScreen(new Page2());
			break;
		case -4:
			try {
				Desktop.getDesktop().browse(ModReference.newIssueLink);
			} catch (Exception e) {
				// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			}
			break;
		case -3:
			this.mc.displayGuiScreen(new Page0());
			break;
		case -2:
			ConfigHandler.init();
			break;
		case -1:
			this.mc.displayGuiScreen(null);
			break;
		case 0:
			this.mc.displayGuiScreen(new GuiYesNo(this));
			break;
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
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GuiHelper.background(randomRotation.y - 6 - GUIOffsetY, done.y - 4, width, height);

		int posY = done.y - 18;

		getDescription();

		if ((mouseX >= randomRotation.x && mouseX < randomRotation.x + randomRotation.width) && (mouseY >= randomRotation.y && mouseY < spawnWhileFrozen.y + randomRotation.height)) {
			this.drawCenteredString(fontRenderer, description, this.width / 2, posY, fontRenderer.getColorCode('f'));
		}

		GuiHelper.drawTitle(randomRotation.y - GUIOffsetY, width, fontRenderer);

		super.drawScreen(mouseX, mouseY, partialTicks);
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
