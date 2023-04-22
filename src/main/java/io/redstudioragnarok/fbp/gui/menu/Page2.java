package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.BasePage;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.large;

public class Page2 extends BasePage {

	GuiButton entityCollision, bounceOffWalls, lowTraction, smartBreaking, fancyPlaceAnim, spawnPlaceParticles;

	@Override
	public void initGui() {
		super.initPage(new Page1(), new Page3());

		entityCollision = addButton(1, x, (this.height / 5) - 6, large, I18n.format("menu.entityCollide.title"), FBP.entityCollision, true, true);
		bounceOffWalls = addButton(2, x, entityCollision.y + entityCollision.height + 1, large, I18n.format("menu.bounceOffWalls.title"), FBP.bounceOffWalls, true, true);
		lowTraction = addButton(3, x, bounceOffWalls.y + bounceOffWalls.height + 6, large, I18n.format("menu.lowTraction.title"), FBP.lowTraction, true, true);
		smartBreaking = addButton(4, x, lowTraction.y + lowTraction.height + 1, large, I18n.format("menu.smartBreaking.title"), FBP.smartBreaking, true, true);
		fancyPlaceAnim = addButton(5, x, smartBreaking.y + smartBreaking.height + 6, large, I18n.format("menu.fancyPlaceAnimation.title"), FBP.fancyPlaceAnim, true, true);
		spawnPlaceParticles = addButton(6, x, fancyPlaceAnim.y + fancyPlaceAnim.height + 1, large, I18n.format("menu.spawnPlaceParticles.title"), FBP.spawnPlaceParticles, true, true);
	}

	@Override
	protected void onActionPerformed(GuiButton button) {
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
				FBP.smartBreaking = !FBP.smartBreaking;
				writeConfig = true;
				break;
			case 5:
				FBP.fancyPlaceAnim = !FBP.fancyPlaceAnim;
				ConfigHandler.reloadAnimBlacklist();
				writeConfig = true;
				break;
			case 6:
				FBP.spawnPlaceParticles = !FBP.spawnPlaceParticles;
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
						description = I18n.format("menu.entityCollide.description");
						break;
					case 2:
						description = I18n.format("menu.bounceOffWalls.description");
						break;
					case 3:
						description = I18n.format("menu.lowTraction.description");
						break;
					case 4:
						description = I18n.format("menu.smartBreaking.description");
						break;
					case 5:
						description = I18n.format("menu.fancyPlaceAnimation.description");
						break;
					case 6:
						description = I18n.format("menu.spawnPlaceParticles.description");
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
