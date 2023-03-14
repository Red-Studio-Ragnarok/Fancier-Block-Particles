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

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.*;

public class Page2 extends GuiScreen {

	GuiButton defaults, done, reload, enable, reportBug;
	GuiButton back, next;

	GuiButton entityCollision, bounceOffWalls, lowTraction, smartBreaking, fancyPlaceAnim, spawnPlaceParticles;

	String description;

	final int GUIOffsetY = 4;

	@Override
	public void initGui() {
		int x = this.width / 2 - 200 / 2;

		entityCollision = new FBPGuiButton(1, x, (this.height / 5) - 10 + GUIOffsetY, large, I18n.format("menu.entitycollide.info"), FBP.entityCollision, true, true);
		bounceOffWalls = new FBPGuiButton(2, x, entityCollision.y + entityCollision.height + 1, large, I18n.format("menu.bounceoffwalls.info"), FBP.bounceOffWalls, true, true);
		lowTraction = new FBPGuiButton(3, x, bounceOffWalls.y + bounceOffWalls.height + 6, large, I18n.format("menu.lowtraction.info"), FBP.lowTraction, true, true);
		smartBreaking = new FBPGuiButton(4, x, lowTraction.y + lowTraction.height + 1, large, I18n.format("menu.smartbreaking.info"), FBP.smartBreaking, true, true);
		fancyPlaceAnim = new FBPGuiButton(5, x, smartBreaking.y + smartBreaking.height + 6, large, I18n.format("menu.fancyplaceanimation.info"), FBP.fancyPlaceAnim, true, true);
		spawnPlaceParticles = new FBPGuiButton(6, x, fancyPlaceAnim.y + fancyPlaceAnim.height + 1, large, I18n.format("menu.spawnplaceparticles.info"), FBP.spawnPlaceParticles, true, true);

		defaults = new FBPGuiButton(0, this.width / 2 + 2, spawnPlaceParticles.y + spawnPlaceParticles.height + 24 - GUIOffsetY, medium, I18n.format("menu.defaults"), false, false, true);
		done = new FBPGuiButton(-1, this.width / 2 - 100, defaults.y, medium, I18n.format("menu.done"), false, false, true);
		reload = new FBPGuiButton(-2, this.width / 2 - 100, defaults.y + defaults.height + 1, large, I18n.format("menu.reloadconfig"), false, false, true);
		enable = new GuiButtonEnable(-6, (this.width - 25 - 27) - 4, 2, this.fontRenderer);
		reportBug = new GuiButtonBugReport(-4, this.width - 27, 2, new Dimension(width, height), this.fontRenderer);

		back = new FBPGuiButton(-3, spawnPlaceParticles.x - 45, spawnPlaceParticles.y - 4 - GUIOffsetY, small, "\u00A76<<", false, false, true);
		next = new FBPGuiButton(-5, spawnPlaceParticles.x + spawnPlaceParticles.width + 25, spawnPlaceParticles.y - 4 - GUIOffsetY, small, "\u00A76>>", false, false, true);

		this.buttonList.addAll(Arrays.asList(defaults, done, reload, enable, reportBug, back, next));
		this.buttonList.addAll(java.util.Arrays.asList(entityCollision, bounceOffWalls, lowTraction, smartBreaking, fancyPlaceAnim, spawnPlaceParticles));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case -6:
			FBP.setEnabled(!FBP.enabled);
			break;
		case -5:
			this.mc.displayGuiScreen(new Page3());
			break;
		case -4:
			try {
				Desktop.getDesktop().browse(ModReference.newIssueLink);
			} catch (Exception e) {
				// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			}
			break;
		case -3:
			this.mc.displayGuiScreen(new Page1());
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
			FBP.entityCollision = !FBP.entityCollision;
			break;
		case 2:
			FBP.bounceOffWalls = !FBP.bounceOffWalls;
			break;
		case 3:
			FBP.lowTraction = !FBP.lowTraction;
			break;
		case 4:
			FBP.smartBreaking = !FBP.smartBreaking;
			break;
		case 5:
			FBP.fancyPlaceAnim = !FBP.fancyPlaceAnim;
			ConfigHandler.reloadAnimBlacklist();
			break;
		case 6:
			FBP.spawnPlaceParticles = !FBP.spawnPlaceParticles;
			break;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GuiHelper.background(entityCollision.y - 6 - GUIOffsetY, done.y - 4, width, height);

		int posY = done.y - 18;

		getDescription();

		if ((mouseX >= entityCollision.x && mouseX < entityCollision.x + entityCollision.width) && (mouseY >= entityCollision.y && mouseY < spawnPlaceParticles.y + entityCollision.height)) {
			this.drawCenteredString(fontRenderer, description, this.width / 2, posY, fontRenderer.getColorCode('f'));
		}

		GuiHelper.drawTitle(entityCollision.y - GUIOffsetY, width, fontRenderer);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void getDescription() {
		for (GuiButton b : this.buttonList) {
			if (b.isMouseOver()) {
				switch (b.id) {
				case 1:
					description = I18n.format("menu.entitycollide.description");
					break;
				case 2:
					description = I18n.format("menu.bounceoffwalls.description");
					break;
				case 3:
					description = I18n.format("menu.lowtraction.description");
					break;
				case 4:
					description = I18n.format("menu.smartbreaking.description");
					break;
				case 5:
					description = I18n.format("menu.fancyplaceanimation.description");
					break;
				case 6:
					description = I18n.format("menu.spawnplaceparticles.description");
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
