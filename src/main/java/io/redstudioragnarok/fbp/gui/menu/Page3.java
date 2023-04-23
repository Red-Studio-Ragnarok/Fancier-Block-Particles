package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.BasePage;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class Page3 extends BasePage {

	GuiButton fancyFlame, fancySmoke, fancyWeather, dynamicWeather, waterPhysics, restOnFloor;

	@Override
	public void initGui() {
		super.initPage(new Page2(), new Page4());

		fancyFlame = addButton(1, I18n.format("menu.fancyFlame.title"), FBP.fancyFlame, true);
		fancySmoke = addButton(2, I18n.format("menu.fancySmoke.title"), FBP.fancySmoke, true);
		fancyWeather = addButton(3, I18n.format("menu.fancyWeather.title"), FBP.fancyWeather, true);
		dynamicWeather = addButton(4, I18n.format("menu.dynamicWeather.title"), FBP.dynamicWeather, true);
		waterPhysics = addButton(5, I18n.format("menu.waterPhysics.title"), FBP.waterPhysics, true);
		restOnFloor = addButton(6, I18n.format("menu.restOnFloor.title"), false,false, false);
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
			case 5:
				FBP.waterPhysics = !FBP.waterPhysics;
				ConfigHandler.reloadMaterials();
				writeConfig = true;
				break;
		}
	}

	protected String getDescription() {
		String description = "";

		for (GuiButton button : this.buttonList) {
			if (button.isMouseOver()) {
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
						description = I18n.format("menu.waterPhysics.description");
						break;
					case 6:
						description = I18n.format("menu.restOnFloor.description");
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
