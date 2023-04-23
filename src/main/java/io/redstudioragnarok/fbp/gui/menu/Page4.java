package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.BasePage;
import io.redstudioragnarok.fbp.gui.Slider;
import io.redstudioragnarok.fbp.utils.MathUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class Page4 extends BasePage {

	Slider weatherParticleDensity, weatherRenderDistance;

	@Override
	public void initGui() {
		super.initPage(new Page3(), null);

		weatherParticleDensity = addSlider(1, (float) ((FBP.weatherParticleDensity - 0.75) / 4.25));
		weatherRenderDistance = addSlider(2, (float) ((FBP.weatherRenderDistance - 0.75) / 1.75));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		FBP.weatherParticleDensity = MathUtil.round((float) (0.75 + 4.25 * weatherParticleDensity.value), 2);
		FBP.weatherRenderDistance = MathUtil.round((float) (0.75 + 1.75 * weatherRenderDistance.value), 2);
	}

	@Override
	protected void drawTitle() {
		weatherParticleDensity.displayString = I18n.format("menu.weatherDensity.title")+" [§6" + (int) (FBP.weatherParticleDensity * 100) + "%§f]";

		weatherRenderDistance.displayString = I18n.format("menu.weatherRenderDistance.title")+" [§6" + (int) (FBP.weatherRenderDistance * 100) + "%§f]";
	}

	protected String getDescription() {
		String description = "";

		for (GuiButton button : this.buttonList) {
			Slider slider = button instanceof Slider ? (Slider) button : new Slider();

			if (button.isMouseOver() || slider.isMouseOver(mouseX, mouseY, 6)) {
				switch (button.id) {
					case 1:
						description = I18n.format("menu.weatherDensity.description") + (int) (FBP.weatherParticleDensity * 100) + "%" + I18n.format("menu.period");
						break;
					case 2:
						description = I18n.format("menu.weatherRenderDistance.description") + (int) (FBP.weatherRenderDistance * 100) + "%" + I18n.format("menu.period");
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
