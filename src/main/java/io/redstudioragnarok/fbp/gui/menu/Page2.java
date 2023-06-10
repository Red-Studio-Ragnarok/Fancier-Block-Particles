package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.BasePage;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class Page2 extends BasePage {

	@Override
	public void initGui() {
		super.initPage(new Page1(), new Page3());

		addButton(1, I18n.format("menu.entityCollide.title"), FBP.entityCollision, true);
		addButton(2, I18n.format("menu.bounceOffWalls.title"), FBP.bounceOffWalls, true);
		addButton(3, I18n.format("menu.lowTraction.title"), FBP.lowTraction, true);
		addButton(4, I18n.format("menu.waterPhysics.title"), FBP.waterPhysics, true);
		addButton(5, I18n.format("menu.smartBreaking.title"), FBP.smartBreaking, true);
		addButton(6, I18n.format("menu.fancyPlaceAnimation.title"), FBP.fancyPlaceAnim, true);

		super.updateScreen();
	}

	@Override
	protected void onActionPerformed(final GuiButton button) {
		switch (button.id) {
			case 1:
				FBP.entityCollision = !FBP.entityCollision;
				writeConfig = true;
				break;
			case 2:
				FBP.bounceOffWalls = !FBP.bounceOffWalls;
				writeConfig = true;
				break;
			case 3:
				FBP.lowTraction = !FBP.lowTraction;
				writeConfig = true;
				break;
			case 4:
				FBP.waterPhysics = !FBP.waterPhysics;
				ConfigHandler.reloadMaterials();
				writeConfig = true;
				break;
			case 5:
				FBP.smartBreaking = !FBP.smartBreaking;
				writeConfig = true;
				break;
			case 6:
				FBP.fancyPlaceAnim = !FBP.fancyPlaceAnim;
				ConfigHandler.reloadAnimBlacklist();
				writeConfig = true;
				break;
		}
	}

	protected String updateDescription() {
		for (GuiButton button : buttonList) {
			if (button.isMouseOver()) {
				switch (button.id) {
					case 1:
						return I18n.format("menu.entityCollide.description");
					case 2:
						return I18n.format("menu.bounceOffWalls.description");
					case 3:
						return I18n.format("menu.lowTraction.description");
					case 4:
						return I18n.format("menu.waterPhysics.description");
					case 5:
						return I18n.format("menu.smartBreaking.description");
					case 6:
						return I18n.format("menu.fancyPlaceAnimation.description");
				}
			}
		}

		return descriptionFallBack;
	}
}
