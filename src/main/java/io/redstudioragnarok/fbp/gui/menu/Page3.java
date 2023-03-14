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

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.*;

public class Page3 extends GuiScreen {

	GuiButton defaults, done, reload, enable, reportBug;
	GuiButton back, next;

	GuiButton fancyFlame, fancySmoke, fancyWeather, dynamicWeather, waterPhysics, restOnFloor;

	String description;

	final int GUIOffsetY = 4;

	@Override
	public void initGui() {
		int x = this.width / 2 - 200 / 2;

		fancyFlame = new FBPGuiButton(1, x, (this.height / 5) - 10 + GUIOffsetY, large, I18n.format("menu.fancyflame.info"), FBP.fancyFlame, true, true);
		fancySmoke = new FBPGuiButton(2, x, fancyFlame.y + fancyFlame.height + 1, large, I18n.format("menu.fancysmoke.info"), FBP.fancySmoke, true, true);
		fancyWeather = new FBPGuiButton(3, x, fancySmoke.y + fancySmoke.height + 6, large, I18n.format("menu.fancyweather.info"), FBP.fancyWeather, true, true);
		dynamicWeather = new FBPGuiButton(4, x, fancyWeather.y + fancyWeather.height + 1, large, I18n.format("menu.dynamicWeather.title"), FBP.dynamicWeather, true, true);
		waterPhysics = new FBPGuiButton(5, x, dynamicWeather.y + dynamicWeather.height + 6, large, I18n.format("menu.waterphysics.info"), FBP.waterPhysics, true, true);
		restOnFloor = new FBPGuiButton(6, x, waterPhysics.y + fancyFlame.height + 1, large, I18n.format("menu.restonfloor.info"), false,false, false);

		defaults = new FBPGuiButton(0, this.width / 2 + 2, restOnFloor.y + restOnFloor.height + 24 - GUIOffsetY, medium, I18n.format("menu.defaults"), false, false, true);
		done = new FBPGuiButton(-1, this.width / 2 - 100, defaults.y, medium, I18n.format("menu.done"), false, false, true);
		reload = new FBPGuiButton(-2, this.width / 2 - 100, defaults.y + defaults.height + 1, large, I18n.format("menu.reloadconfig"), false, false, true);
		enable = new GuiButtonEnable(-5, (this.width - 25 - 27) - 4, 2, this.fontRenderer);
		reportBug = new GuiButtonBugReport(-4, this.width - 27, 2, new Dimension(width, height), this.fontRenderer);

		back = new FBPGuiButton(-3, restOnFloor.x - 45, restOnFloor.y - 4 - GUIOffsetY, small, "\u00A76<<", false, false, true);
		next = new FBPGuiButton(-6, restOnFloor.x + restOnFloor.width + 25, restOnFloor.y - 4 - GUIOffsetY, small, "\u00A76>>", false, false, true);
		
		this.buttonList.addAll(java.util.Arrays.asList(defaults, done, reload, back, next, enable, reportBug));
		this.buttonList.addAll(java.util.Arrays.asList(fancyFlame, fancySmoke, fancyWeather, dynamicWeather, waterPhysics, restOnFloor));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
			case -6:
			this.mc.displayGuiScreen(new Page4());
			break;
		case -5:
			FBP.setEnabled(!FBP.enabled);
			break;
		case -4:
			try {
				Desktop.getDesktop().browse(ModReference.newIssueLink);
			} catch (Exception e) {
				// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			}
			break;
		case -3:
			this.mc.displayGuiScreen(new Page2());
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
			FBP.fancyFlame = !FBP.fancyFlame;
			break;
		case 2:
			FBP.fancySmoke = !FBP.fancySmoke;
			break;
		case 3:
			FBP.fancyWeather = !FBP.fancyWeather;

			if (FBP.fancyWeather && FBP.enabled)
				mc.world.provider.setWeatherRenderer(FBP.fancyWeatherRenderer);
			else
				mc.world.provider.setWeatherRenderer(FBP.originalWeatherRenderer);
			break;
		case 4:
			FBP.dynamicWeather = !FBP.dynamicWeather;
			break;
		case 5:
			FBP.waterPhysics = !FBP.waterPhysics;
			ConfigHandler.reloadMaterials();
			break;
		case 6:
			break;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GuiHelper.background(fancyFlame.y - 6 - GUIOffsetY, done.y - 4, width, height);

		int posY = done.y - 18;

		getDescription();

		if ((mouseX >= fancyFlame.x && mouseX < fancyFlame.x + fancyFlame.width) && (mouseY >= fancyFlame.y && mouseY < restOnFloor.y + fancyFlame.height)) {
			this.drawCenteredString(fontRenderer, description, this.width / 2, posY, fontRenderer.getColorCode('f'));
		}

		GuiHelper.drawTitle(fancyFlame.y - GUIOffsetY, width, fontRenderer);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void getDescription() {
		for (GuiButton b : this.buttonList) {
			if (b.isMouseOver()) {
				switch (b.id) {
				case 1:
					description = I18n.format("menu.fancyflame.description");
					break;
				case 2:
					description = I18n.format("menu.fancysmoke.description");
					break;
				case 3:
					description = I18n.format("menu.fancyweather.description");
					break;
				case 4:
					description = I18n.format("menu.dynamicWeather.description");
					break;
				case 5:
					description = I18n.format("menu.waterphysics.description");
					break;
				case 6:
					description = I18n.format("menu.restonfloor.description");
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
