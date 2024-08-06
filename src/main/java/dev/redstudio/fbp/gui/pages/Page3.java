package dev.redstudio.fbp.gui.pages;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.gui.BasePage;
import dev.redstudio.fbp.gui.elements.Slider;
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

		super.updateScreen();
	}

	@Override
	protected void onActionPerformed(final GuiButton button) {
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

				if (mc.world != null) {
					if (FBP.fancyWeather && FBP.enabled)
						mc.world.provider.setWeatherRenderer(FBP.fancyWeatherRenderer);
					else
						mc.world.provider.setWeatherRenderer(FBP.originalWeatherRenderer);
				}

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

	protected String updateDescription() {
		for (GuiButton button : buttonList) {
			if (button.isMouseOver()) {
				switch (button.id) {
					case 1:
						return I18n.format("menu.fancyFlame.description");
					case 2:
						return I18n.format("menu.fancySmoke.description");
					case 3:
						return I18n.format("menu.fancyWeather.description");
					case 4:
						return I18n.format("menu.dynamicWeather.description");
					case 5:
						return I18n.format("menu.weatherDensity.description") + (int) (FBP.weatherParticleDensity * 100) + "%" + I18n.format("menu.period");
					case 6:
						return I18n.format("menu.weatherRenderDistance.description") + (int) (FBP.weatherRenderDistance * 100) + "%" + I18n.format("menu.period");
				}
			}
		}

		return descriptionFallBack;
	}

	@Override
	protected void updateTitles() {
		weatherParticleDensity.displayString = I18n.format("menu.weatherDensity.title")+" [§6" + (int) (FBP.weatherParticleDensity * 100) + "%";

		weatherRenderDistance.displayString = I18n.format("menu.weatherRenderDistance.title")+" [§6" + (int) (FBP.weatherRenderDistance * 100) + "%";
	}
}
