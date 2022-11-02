package io.redstudioragnarok.FBP.gui;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.handler.FBPConfigHandler;
import io.redstudioragnarok.FBP.util.FBPMathUtil;
import io.redstudioragnarok.FBP.util.ModReference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import javax.vecmath.Vector2d;
import java.awt.*;
import java.util.Arrays;

public class FBPGuiMenuPage4 extends GuiScreen {

	GuiButton Reload, Done, Defaults, Back, ReportBug, Enable;
	FBPGuiSlider WeatherParticleDensity;

	Vector2d lastHandle = new Vector2d(0, 0);
	Vector2d lastSize = new Vector2d(0, 0);

	Vector2d handle = new Vector2d(0, 0);
	Vector2d size = new Vector2d(0, 0);

	long time, lastTime;

	int selected = 0;

	final int GUIOffsetY = 8;

	@Override
	public void initGui() {
		int X = this.width / 2 - 100;

		WeatherParticleDensity = new FBPGuiSlider(X, this.height / 5 - 10 + GUIOffsetY, (FBP.weatherParticleDensity - 0.75) / 4.25);
		int Y = WeatherParticleDensity.y + WeatherParticleDensity.height + 2 + 4 * (WeatherParticleDensity.height + 1) + 5;

		Defaults = new FBPGuiButton(0, this.width / 2 + 2, Y + 48 - GUIOffsetY, I18n.format("menu.defaults"), false, false, true);
		Done = new FBPGuiButton(-1, X, Defaults.y, I18n.format("menu.done"), false, false, true);
		Defaults.width = Done.width = 98;
		Reload = new FBPGuiButton(-2, X, Defaults.y + Defaults.height + 1, I18n.format("menu.reloadconfig"), false, false, true);
		Reload.width = 96 * 2 + 8;

		Back = new FBPGuiButton(-7, X - 44, Y + 2 - GUIOffsetY + 4, "<<", false, false, true);
		Back.width = 20;

		Enable = new FBPGuiButtonEnable(-6, (this.width - 25 - 27) - 4, 2, this.fontRenderer);
		ReportBug = new FBPGuiButtonBugReport(-4, this.width - 27, 2, new Dimension(width, height), this.fontRenderer);

		this.buttonList.addAll(Arrays.asList(WeatherParticleDensity, Defaults, Done, Reload, Back, Enable, ReportBug));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case -6:
			FBP.setEnabled(!FBP.enabled);
			break;
		case -4:
			try {
				Desktop.getDesktop().browse(ModReference.ISSUE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case -7:
			this.mc.displayGuiScreen(new FBPGuiMenuPage3());
			break;
		case -2:
			FBPConfigHandler.init();
			break;
		case -1:
			this.mc.displayGuiScreen(null);
			break;
		case 0:
			this.mc.displayGuiScreen(new FBPGuiYesNo(this));
			break;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		FBPGuiHelper.background(WeatherParticleDensity.y - 6 - GUIOffsetY, Done.y - 4, width, height);

		FBP.weatherParticleDensity = FBPMathUtil.round(0.75 + 4.25 * WeatherParticleDensity.value, 2);

		drawMouseOverSelection(mouseX, mouseY);

		FBPGuiHelper.drawTitle(WeatherParticleDensity.y - GUIOffsetY, width, fontRenderer);

		drawInfo();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawMouseOverSelection(int mouseX, int mouseY) {
		int posY = Done.y - 18;

		if (WeatherParticleDensity.isMouseOver(mouseX, mouseY)) {
			handle.y = WeatherParticleDensity.y;
			size = new Vector2d(WeatherParticleDensity.width, 18);
			selected = 1;
		}

		int step = 1;
		time = System.currentTimeMillis();

		if (lastTime > 0)
			step = (int) (time - lastTime);

		lastTime = time;

		if (lastHandle != new Vector2d(0, 0)) {
			if (lastHandle.y > handle.y) {
				if (lastHandle.y - handle.y <= step)
					lastHandle.y = handle.y;
				else
					lastHandle.y -= step;
			}

			if (lastHandle.y < handle.y) {
				if (handle.y - lastHandle.y <= step)
					lastHandle.y = handle.y;
				else
					lastHandle.y += step;
			}

			lastHandle.x = WeatherParticleDensity.x;
		}

		if (lastSize != new Vector2d(0, 0)) {
			if (lastSize.y > size.y)
				if (lastSize.y - size.y <= step)
					lastSize.y = size.y;
				else
					lastSize.y -= step;

			if (lastSize.y < size.y)
				if (size.y - lastSize.y <= step)
					lastSize.y = size.y;
				else
					lastSize.y += step;

			lastSize.x = WeatherParticleDensity.width;
		}

		String text;

		if (selected == 1) {
			text = I18n.format("menu.weatherdensity.description") + (int) (FBP.weatherParticleDensity * 100) + "%" + I18n.format("menu.period");
		} else {
			text = "";
		}

		if (WeatherParticleDensity.isMouseOver(mouseX, mouseY) && (lastSize.y <= 20 || lastSize.y < 50) && lastHandle.y >= WeatherParticleDensity.y) {

			if (selected <= 5)
				FBPGuiHelper.drawRect(lastHandle.x - 2, lastHandle.y + 2, lastSize.x + 4, lastSize.y - 2, 200, 200, 200, 35);

			this.drawCenteredString(fontRenderer, text, this.width / 2, posY, fontRenderer.getColorCode('f'));
		}
	}

	private void drawInfo() {
		WeatherParticleDensity.displayString = I18n.format("menu.weatherdensity.info")+" [\u00A76" + (int) (FBP.weatherParticleDensity * 100) + "%\u00A7f]";
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
		FBPConfigHandler.write();
	}
}
