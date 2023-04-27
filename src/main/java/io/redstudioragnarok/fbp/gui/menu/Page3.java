package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.BasePage;
import io.redstudioragnarok.fbp.gui.Slider;
import io.redstudioragnarok.fbp.utils.MathUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class Page3 extends BasePage {

	Slider weatherParticleDensity, weatherRenderDistance;

	@Override
	public void initGui() {
		super.initPage(new Page2(), null);

		addButton(1, I18n.format("menu.fancyFlame.title"), FBP.fancyFlame, true);
		addButton(2, I18n.format("menu.fancySmoke.title"), FBP.fancySmoke, true);
		addButton(3, I18n.format("menu.fancyWeather.title"), FBP.fancyWeather, true);
		addButton(4, I18n.format("menu.dynamicWeather.title"), FBP.dynamicWeather, true);

		weatherParticleDensity = addSlider(5, 0.75F, FBP.weatherParticleDensity, 5);
		weatherRenderDistance = addSlider(6, 0.75F, FBP.weatherRenderDistance, 2.5F);
	}

	@Override
	protected void onActionPerformed(GuiButton button) {
		switch (button.id) {
			case 1:
				FBP.fancyFlame = !FBP.fancyFlame;
				writeConfig = true;
				break;
			case 2:
				FBP.fancySmoke = !FBP.fancySmoke;
				writeConfig = true;
				break;
			case 3:
				FBP.fancyWeather = !FBP.fancyWeather;

				if (FBP.fancyWeather && FBP.enabled)
					mc.world.provider.setWeatherRenderer(FBP.fancyWeatherRenderer);
				else
					mc.world.provider.setWeatherRenderer(FBP.originalWeatherRenderer);

				writeConfig = true;
				break;
			case 4:
				FBP.dynamicWeather = !FBP.dynamicWeather;
				writeConfig = true;
				break;
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		FBP.weatherParticleDensity = weatherParticleDensity.value;
		FBP.weatherRenderDistance = weatherRenderDistance.value;
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
						description = I18n.format("menu.fancyFlame.description");
						break;
					case 2:
						description = I18n.format("menu.fancySmoke.description");
						break;
					case 3:
						description = I18n.format("menu.fancyWeather.description");
						break;
					case 4:
						description = I18n.format("menu.dynamicWeather.description");
						break;
					case 5:
						description = I18n.format("menu.weatherDensity.description") + (int) (FBP.weatherParticleDensity * 100) + "%" + I18n.format("menu.period");
						break;
					case 6:
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
