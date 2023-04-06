package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.large;

public class Page2 extends BaseSettingsPage {

	GuiButton entityCollision, bounceOffWalls, lowTraction, smartBreaking, fancyPlaceAnim, spawnPlaceParticles;

	String description;

	@Override
	public void initGui() {
		super.initPage(new Page1(), new Page3());

		entityCollision = addButton(1, x, (this.height / 5) - 6, large, I18n.format("menu.entitycollide.info"), FBP.entityCollision, true, true);
		bounceOffWalls = addButton(2, x, entityCollision.y + entityCollision.height + 1, large, I18n.format("menu.bounceoffwalls.info"), FBP.bounceOffWalls, true, true);
		lowTraction = addButton(3, x, bounceOffWalls.y + bounceOffWalls.height + 6, large, I18n.format("menu.lowtraction.info"), FBP.lowTraction, true, true);
		smartBreaking = addButton(4, x, lowTraction.y + lowTraction.height + 1, large, I18n.format("menu.smartbreaking.info"), FBP.smartBreaking, true, true);
		fancyPlaceAnim = addButton(5, x, smartBreaking.y + smartBreaking.height + 6, large, I18n.format("menu.fancyplaceanimation.info"), FBP.fancyPlaceAnim, true, true);
		spawnPlaceParticles = addButton(6, x, fancyPlaceAnim.y + fancyPlaceAnim.height + 1, large, I18n.format("menu.spawnplaceparticles.info"), FBP.spawnPlaceParticles, true, true);
	}

	@Override
	protected void onActionPerformed(GuiButton button) {
		switch (button.id) {
		case 1:
			FBP.entityCollision = !FBP.entityCollision;
			break;
		case 2:
			FBP.bounceOffWalls = !FBP.bounceOffWalls;
			break;
		case 3:
			FBP.lowTraction = !FBP.lowTraction;
			break;
		case 4:
			FBP.smartBreaking = !FBP.smartBreaking;
			break;
		case 5:
			FBP.fancyPlaceAnim = !FBP.fancyPlaceAnim;
			ConfigHandler.reloadAnimBlacklist();
			break;
		case 6:
			FBP.spawnPlaceParticles = !FBP.spawnPlaceParticles;
			break;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		getDescription();

		if ((mouseX >= entityCollision.x && mouseX < entityCollision.x + entityCollision.width) && (mouseY >= entityCollision.y && mouseY < spawnPlaceParticles.y + entityCollision.height)) {
			this.drawCenteredString(fontRenderer, description, this.width / 2, height / 5 + 131, fontRenderer.getColorCode('f'));
		}
	}

	private void getDescription() {
		for (GuiButton b : this.buttonList) {
			if (b.isMouseOver()) {
				switch (b.id) {
				case 1:
					description = I18n.format("menu.entitycollide.description");
					break;
				case 2:
					description = I18n.format("menu.bounceoffwalls.description");
					break;
				case 3:
					description = I18n.format("menu.lowtraction.description");
					break;
				case 4:
					description = I18n.format("menu.smartbreaking.description");
					break;
				case 5:
					description = I18n.format("menu.fancyplaceanimation.description");
					break;
				case 6:
					description = I18n.format("menu.spawnplaceparticles.description");
					break;
				default:
					description = I18n.format("menu.noDescriptionFound");
				}
			}
		}
	}
}
