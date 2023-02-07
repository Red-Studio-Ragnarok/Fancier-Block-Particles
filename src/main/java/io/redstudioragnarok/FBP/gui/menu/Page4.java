package io.redstudioragnarok.FBP.gui.menu;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.gui.*;
import io.redstudioragnarok.FBP.handler.ConfigHandler;
import io.redstudioragnarok.FBP.util.MathUtil;
import io.redstudioragnarok.FBP.util.ModReference;
import io.redstudioragnarok.FBP.vector.Vector2D;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.util.Arrays;

public class Page4 extends GuiScreen {

	GuiButton Reload, Done, Defaults, Back, ReportBug, Enable;
	GuiSlider WeatherParticleDensity, WeatherRenderDistance;

	Vector2D lastHandle = new Vector2D();
	Vector2D lastSize = new Vector2D();

	Vector2D handle = new Vector2D();
	Vector2D size = new Vector2D();

	long time, lastTime;

	int selected = 0;

	final int GUIOffsetY = 8;

	@Override
	public void initGui() {
		int X = this.width / 2 - 100;

		WeatherParticleDensity = new GuiSlider(X, this.height / 5 - 10 + GUIOffsetY, (float) ((FBP.weatherParticleDensity - 0.75) / 4.25));
		WeatherRenderDistance = new GuiSlider(X, WeatherParticleDensity.y + WeatherParticleDensity.height + 1, (float) ((FBP.weatherRenderDistance - 0.75) / 3.25));
		int Y = WeatherParticleDensity.y + WeatherParticleDensity.height + 2 + 4 * (WeatherParticleDensity.height + 1) + 5;

		Defaults = new FBPGuiButton(0, this.width / 2 + 2, Y + 48 - GUIOffsetY, I18n.format("menu.defaults"), false, false, true);
		Done = new FBPGuiButton(-1, X, Defaults.y, I18n.format("menu.done"), false, false, true);
		Defaults.width = Done.width = 98;
		Reload = new FBPGuiButton(-2, X, Defaults.y + Defaults.height + 1, I18n.format("menu.reloadconfig"), false, false, true);
		Reload.width = 96 * 2 + 8;

		Back = new FBPGuiButton(-7, X - 44, Y + 2 - GUIOffsetY + 4, "<<", false, false, true);
		Back.width = 20;

		Enable = new GuiButtonEnable(-6, (this.width - 25 - 27) - 4, 2, this.fontRenderer);
		ReportBug = new GuiButtonBugReport(-4, this.width - 27, 2, new Dimension(width, height), this.fontRenderer);

		this.buttonList.addAll(Arrays.asList(WeatherParticleDensity, WeatherRenderDistance, Defaults, Done, Reload, Back, Enable, ReportBug));
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
				// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			}
			break;
		case -7:
			this.mc.displayGuiScreen(new Page3());
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
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GuiHelper.background(WeatherParticleDensity.y - 6 - GUIOffsetY, Done.y - 4, width, height);

		FBP.weatherParticleDensity = MathUtil.round((float) (0.75 + 4.25 * WeatherParticleDensity.value), 2);
		FBP.weatherRenderDistance = MathUtil.round((float) (0.75 + 3.25 * WeatherRenderDistance.value), 2);

		drawMouseOverSelection(mouseX, mouseY);

		GuiHelper.drawTitle(WeatherParticleDensity.y - GUIOffsetY, width, fontRenderer);

		drawInfo();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawMouseOverSelection(int mouseX, int mouseY) {
		int posY = Done.y - 18;

		if (WeatherParticleDensity.isMouseOver(mouseX, mouseY)) {
			handle.y = WeatherParticleDensity.y;
			size = new Vector2D(WeatherParticleDensity.width, 18);
			selected = 1;
		} else if (WeatherRenderDistance.isMouseOver(mouseX, mouseY)) {
			handle.y = WeatherRenderDistance.y;
			size = new Vector2D(WeatherRenderDistance.width, 18);
			selected = 2;
		}

		int step = 1;
		time = System.currentTimeMillis();

		if (lastTime > 0)
			step = (int) (time - lastTime);

		lastTime = time;

		if (lastHandle != new Vector2D()) {
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

		if (lastSize != new Vector2D()) {
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

			lastSize.x = WeatherRenderDistance.width;
		}

		String text;

		if (selected == 1) {
			text = I18n.format("menu.weatherdensity.description") + (int) (FBP.weatherParticleDensity * 100) + "%" + I18n.format("menu.period");
		} else if (selected == 2){
			text = I18n.format("menu.weatherRenderDistance.description") + (int) (FBP.weatherRenderDistance * 100) + "%" + I18n.format("menu.period");
		} else {
			text = "No description available please report this";
		}

		if (mouseX >= WeatherParticleDensity.x - 2 && mouseX <= WeatherParticleDensity.x + WeatherParticleDensity.width + 2 && mouseY < WeatherRenderDistance.y + WeatherRenderDistance.height && mouseY >= WeatherParticleDensity.y && (lastSize.y <= 20 || lastSize.y < 50) && lastHandle.y >= WeatherParticleDensity.y) {

			if (selected <= 5)
				GuiHelper.drawRect(lastHandle.x - 2, lastHandle.y + 2, lastSize.x + 4, lastSize.y - 2, 200, 200, 200, 35);

			this.drawCenteredString(fontRenderer, text, this.width / 2, posY, fontRenderer.getColorCode('f'));
		}
	}

	private void drawInfo() {
		WeatherParticleDensity.displayString = I18n.format("menu.weatherdensity.info")+" [\u00A76" + (int) (FBP.weatherParticleDensity * 100) + "%\u00A7f]";
		WeatherRenderDistance.displayString = I18n.format("menu.weatherRenderDistance.title")+" [\u00A76" + (int) (FBP.weatherRenderDistance * 100) + "%\u00A7f]";
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
