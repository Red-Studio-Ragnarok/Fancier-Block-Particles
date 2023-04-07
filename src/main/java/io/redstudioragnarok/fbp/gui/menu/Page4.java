package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.GuiHelper;
import io.redstudioragnarok.fbp.gui.GuiSlider;
import io.redstudioragnarok.fbp.utils.MathUtil;
import io.redstudioragnarok.fbp.vectors.Vector2F;
import net.minecraft.client.resources.I18n;

public class Page4 extends BaseSettingsPage {

	GuiSlider weatherParticleDensity, weatherRenderDistance;

	Vector2F lastHandle = new Vector2F();
	Vector2F lastSize = new Vector2F();

	Vector2F handle = new Vector2F();
	Vector2F size = new Vector2F();

	long time, lastTime;

	int selected = 0;

	@Override
	public void initGui() {
		super.initPage(new Page3(), null);

		weatherParticleDensity = addSlider(x, this.height / 5 - 6, (float) ((FBP.weatherParticleDensity - 0.75) / 4.25));
		weatherRenderDistance = addSlider(x, weatherParticleDensity.y + weatherParticleDensity.height + 1, (float) ((FBP.weatherRenderDistance - 0.75) / 1.75));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		FBP.weatherParticleDensity = MathUtil.round((float) (0.75 + 4.25 * weatherParticleDensity.value), 2);
		FBP.weatherRenderDistance = MathUtil.round((float) (0.75 + 1.75 * weatherRenderDistance.value), 2);

		drawMouseOverSelection(mouseX, mouseY);

		drawInfo();
	}

	private void drawMouseOverSelection(int mouseX, int mouseY) {
		if (weatherParticleDensity.isMouseOver(mouseX, mouseY)) {
			handle.y = weatherParticleDensity.y;
			size = new Vector2F(weatherParticleDensity.width, 18);
			selected = 1;
		} else if (weatherRenderDistance.isMouseOver(mouseX, mouseY)) {
			handle.y = weatherRenderDistance.y;
			size = new Vector2F(weatherRenderDistance.width, 18);
			selected = 2;
		}

		int step = 1;
		time = System.currentTimeMillis();

		if (lastTime > 0)
			step = (int) (time - lastTime);

		lastTime = time;

		if (lastHandle != new Vector2F()) {
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

		if (lastSize != new Vector2F()) {
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

			if (selected <= 2)
				GuiHelper.drawRectangle(lastHandle.x - 2, lastHandle.y + 2, lastSize.x + 4, lastSize.y - 2, 200, 200, 200, 35);

			this.drawCenteredString(fontRenderer, text, this.width / 2, height / 5 + 131, fontRenderer.getColorCode('f'));

			writeConfig = true;
		}
	}

	private void drawInfo() {
		weatherParticleDensity.displayString = I18n.format("menu.weatherdensity.info")+" [\u00A76" + (int) (FBP.weatherParticleDensity * 100) + "%\u00A7f]";
		weatherRenderDistance.displayString = I18n.format("menu.weatherRenderDistance.title")+" [\u00A76" + (int) (FBP.weatherRenderDistance * 100) + "%\u00A7f]";
	}
}
