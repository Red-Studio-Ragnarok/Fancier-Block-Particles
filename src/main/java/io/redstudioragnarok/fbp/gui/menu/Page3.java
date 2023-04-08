package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.BasePage;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.large;

public class Page3 extends BasePage {

	GuiButton fancyFlame, fancySmoke, fancyWeather, dynamicWeather, waterPhysics, restOnFloor;

	String description;

	@Override
	public void initGui() {
		super.initPage(new Page2(), new Page4());

		fancyFlame = addButton(1, x, (this.height / 5) - 6, large, I18n.format("menu.fancyFlame.title"), FBP.fancyFlame, true, true);
		fancySmoke = addButton(2, x, fancyFlame.y + fancyFlame.height + 1, large, I18n.format("menu.fancySmoke.title"), FBP.fancySmoke, true, true);
		fancyWeather = addButton(3, x, fancySmoke.y + fancySmoke.height + 6, large, I18n.format("menu.fancyWeather.title"), FBP.fancyWeather, true, true);
		dynamicWeather = addButton(4, x, fancyWeather.y + fancyWeather.height + 1, large, I18n.format("menu.dynamicWeather.title"), FBP.dynamicWeather, true, true);
		waterPhysics = addButton(5, x, dynamicWeather.y + dynamicWeather.height + 6, large, I18n.format("menu.waterPhysics.title"), FBP.waterPhysics, true, true);
		restOnFloor = addButton(6, x, waterPhysics.y + fancyFlame.height + 1, large, I18n.format("menu.restOnFloor.title"), false,false, false);
	}

	@Override
	protected void onActionPerformed(GuiButton button) {
		switch (button.id) {
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
		}

		writeConfig = true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		getDescription();

		if ((mouseX >= fancyFlame.x && mouseX < fancyFlame.x + fancyFlame.width) && (mouseY >= fancyFlame.y && mouseY < restOnFloor.y + fancyFlame.height)) {
			this.drawCenteredString(fontRenderer, description, this.width / 2, height / 5 + 131, fontRenderer.getColorCode('f'));
		}
	}

	private void getDescription() {
		for (GuiButton b : this.buttonList) {
			if (b.isMouseOver()) {
				switch (b.id) {
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
				}
			}
		}
	}
}
