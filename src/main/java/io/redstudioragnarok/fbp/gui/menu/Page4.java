package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.*;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import io.redstudioragnarok.fbp.utils.MathUtil;
import io.redstudioragnarok.fbp.utils.ModReference;
import io.redstudioragnarok.fbp.vectors.Vector2D;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.awt.Desktop;
import java.awt.Dimension;
import java.util.Arrays;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.*;

public class Page4 extends GuiScreen {

	GuiButton defaults, done, reload, enable, reportBug;
	GuiButton back;

	GuiSlider weatherParticleDensity, weatherRenderDistance;

	Vector2D lastHandle = new Vector2D();
	Vector2D lastSize = new Vector2D();

	Vector2D handle = new Vector2D();
	Vector2D size = new Vector2D();

	long time, lastTime;

	int selected = 0;

	final int GUIOffsetY = 8;

	@Override
	public void initGui() {
		int x = this.width / 2 - 100;

		weatherParticleDensity = new GuiSlider(x, this.height / 5 - 10 + GUIOffsetY, (float) ((FBP.weatherParticleDensity - 0.75) / 4.25));
		weatherRenderDistance = new GuiSlider(x, weatherParticleDensity.y + weatherParticleDensity.height + 1, (float) ((FBP.weatherRenderDistance - 0.75) / 1.75));
		int y = weatherParticleDensity.y + weatherParticleDensity.height + 2 + 4 * (weatherParticleDensity.height + 1) + 5;

		defaults = new FBPGuiButton(0, this.width / 2 + 2, y + 48 - GUIOffsetY, medium, I18n.format("menu.defaults"), false, false, true);
		done = new FBPGuiButton(-1, x, defaults.y, medium, I18n.format("menu.done"), false, false, true);
		reload = new FBPGuiButton(-2, x, defaults.y + defaults.height + 1, large, I18n.format("menu.reloadconfig"), false, false, true);
		enable = new GuiButtonEnable(-6, (this.width - 25 - 27) - 4, 2, this.fontRenderer);
		reportBug = new GuiButtonBugReport(-4, this.width - 27, 2, new Dimension(width, height), this.fontRenderer);

		back = new FBPGuiButton(-7, x - 45, y - 4 - GUIOffsetY + 4, small, "\u00A76<<", false, false, true);
		
		this.buttonList.addAll(Arrays.asList(defaults, done, reload, back, enable, reportBug));
		this.buttonList.addAll(Arrays.asList(weatherParticleDensity, weatherRenderDistance));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case -6:
			FBP.setEnabled(!FBP.enabled);
			break;
		case -4:
			try {
				Desktop.getDesktop().browse(ModReference.newIssueLink);
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
		GuiHelper.background(weatherParticleDensity.y - 6 - GUIOffsetY, done.y - 4, width, height);

		FBP.weatherParticleDensity = MathUtil.round((float) (0.75 + 4.25 * weatherParticleDensity.value), 2);
		FBP.weatherRenderDistance = MathUtil.round((float) (0.75 + 1.75 * weatherRenderDistance.value), 2);

		drawMouseOverSelection(mouseX, mouseY);

		GuiHelper.drawTitle(weatherParticleDensity.y - GUIOffsetY, width, fontRenderer);

		drawInfo();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawMouseOverSelection(int mouseX, int mouseY) {
		int posY = done.y - 18;

		if (weatherParticleDensity.isMouseOver(mouseX, mouseY)) {
			handle.y = weatherParticleDensity.y;
			size = new Vector2D(weatherParticleDensity.width, 18);
			selected = 1;
		} else if (weatherRenderDistance.isMouseOver(mouseX, mouseY)) {
			handle.y = weatherRenderDistance.y;
			size = new Vector2D(weatherRenderDistance.width, 18);
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

			lastHandle.x = weatherParticleDensity.x;
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

			lastSize.x = weatherRenderDistance.width;
		}

		String text;

		if (selected == 1) {
			text = I18n.format("menu.weatherdensity.description") + (int) (FBP.weatherParticleDensity * 100) + "%" + I18n.format("menu.period");
		} else if (selected == 2){
			text = I18n.format("menu.weatherRenderDistance.description") + (int) (FBP.weatherRenderDistance * 100) + "%" + I18n.format("menu.period");
		} else {
			text = I18n.format("menu.noDescriptionFound");
		}

		if (mouseX >= weatherParticleDensity.x - 2 && mouseX <= weatherParticleDensity.x + weatherParticleDensity.width + 2 && mouseY < weatherRenderDistance.y + weatherRenderDistance.height && mouseY >= weatherParticleDensity.y && (lastSize.y <= 20 || lastSize.y < 50) && lastHandle.y >= weatherParticleDensity.y) {

			if (selected <= 5)
				GuiHelper.drawRect(lastHandle.x - 2, lastHandle.y + 2, lastSize.x + 4, lastSize.y - 2, 200, 200, 200, 35);

			this.drawCenteredString(fontRenderer, text, this.width / 2, posY, fontRenderer.getColorCode('f'));
		}
	}

	private void drawInfo() {
		weatherParticleDensity.displayString = I18n.format("menu.weatherdensity.info")+" [\u00A76" + (int) (FBP.weatherParticleDensity * 100) + "%\u00A7f]";
		weatherRenderDistance.displayString = I18n.format("menu.weatherRenderDistance.title")+" [\u00A76" + (int) (FBP.weatherRenderDistance * 100) + "%\u00A7f]";
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
