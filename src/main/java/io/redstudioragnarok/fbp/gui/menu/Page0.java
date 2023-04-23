package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.BasePage;
import io.redstudioragnarok.fbp.gui.Slider;
import io.redstudioragnarok.fbp.utils.MathUtil;
import net.jafama.FastMath;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.small;

public class Page0 extends BasePage {

	Slider minAge, maxAge, particlesPerAxis, scaleMult, gravityMult, rotationMult;

	GuiButton infiniteDuration, timeUnit;

	@Override
	public void initGui() {
		super.initPage(null, new Page1());

		minAge = addSlider(1, (float) ((FBP.minAge - 10) / 90.0));
		maxAge = addSlider(2, (float) ((FBP.maxAge - 10) / 90.0));

		particlesPerAxis = addSlider(3, (float) ((FBP.particlesPerAxis - 2) / 3.0));
		scaleMult = addSlider(4, (float) ((FBP.scaleMult - 0.75) / 0.5));
		gravityMult = addSlider(5, (float) ((FBP.gravityMult - 0.05) / 2.95));
		rotationMult = addSlider(6, (float) (FBP.rotationMult / 1.5));

		infiniteDuration = addButton(7, x + 205, minAge.y + 10, small, (FBP.infiniteDuration ? "§a" : "§c") + "∞");
		timeUnit = addButton(8, x - 25, minAge.y + 10, small, "§a§L" + (FBP.showInMillis ? "ms" : "ti"));

		update();
	}

	@Override
	protected void onActionPerformed(GuiButton button) {
		switch (button.id) {
			case 7:
				infiniteDuration.displayString = ((FBP.infiniteDuration = !FBP.infiniteDuration) ? "§a" : "§c") + "∞";
				update();
				writeConfig = true;
				break;
			case 8:
				timeUnit.displayString = "§a§L" + ((FBP.showInMillis = !FBP.showInMillis) ? "ms" : "ti");
				writeConfig = true;
				break;
		}
	}

	@Override
    public void updateScreen() {
		super.updateScreen();

		final int sMinAge = (int) (10 + 90 * minAge.value);
		final int sMaxAge = (int) (10 + 90 * maxAge.value);

		if (FBP.maxAge < sMinAge) {
			FBP.maxAge = sMinAge;

			maxAge.value = (float) ((FBP.maxAge - 10) / 90.0);
		}

		if (FBP.minAge > sMaxAge) {
			FBP.minAge = sMaxAge;

			minAge.value = (float) ((FBP.minAge - 10) / 90.0);
		}

		FBP.minAge = sMinAge;
		FBP.maxAge = sMaxAge;
		FBP.scaleMult = MathUtil.round((float) (0.75 + 0.5 * scaleMult.value), 2);
		FBP.gravityMult = MathUtil.round((float) (0.05 + 2.95 * gravityMult.value), 2);
		FBP.rotationMult = MathUtil.round((float) (1.5 * rotationMult.value), 2);
		FBP.particlesPerAxis = FastMath.round(2 + 3 * particlesPerAxis.value);

		particlesPerAxis.value = (float) ((FBP.particlesPerAxis - 2) / 3.0);
	}

	@Override
	protected void drawTitle() {
		minAge.displayString = FBP.infiniteDuration ? I18n.format("menu.minDuration.title") + " [§6" + "∞ " + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks")) + "§f]" : I18n.format("menu.minDuration.title") + " [§6" + (FBP.showInMillis ? FBP.minAge * 50 : FBP.minAge) + " " + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks")) + "§f]";

		maxAge.displayString = FBP.infiniteDuration ? I18n.format("menu.maxDuration.title") + " [§6" + "∞ " + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks")) + "§f]" : I18n.format("menu.maxDuration.title") + " [§6" + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + " " + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks")) + "§f]";

		particlesPerAxis.displayString = I18n.format("menu.destroyParticles.title") + " [§6" + (int) Math.pow(FBP.particlesPerAxis, 3) + "§f]";

		scaleMult.displayString = I18n.format("menu.scaleMult.title") + " [§6" + FBP.scaleMult + "§f]";

		gravityMult.displayString = I18n.format("menu.gravityScale.title") + " [§6" + FBP.gravityMult + "§f]";

		rotationMult.displayString = I18n.format("menu.rotationSpeed.title") + " [§6" + (FBP.rotationMult != 0 ? FBP.rotationMult : I18n.format("menu.off")) + "§f]";
	}

	protected String getDescription() {
		String description = "";

		for (GuiButton button : this.buttonList) {
			Slider slider = button instanceof Slider ? (Slider) button : new Slider();

			if (button.isMouseOver() || slider.isMouseOver(mouseX, mouseY, 6)) {
				switch (button.id) {
					case 1:
					case 2:
						if (!FBP.infiniteDuration)
							description = I18n.format("menu.particleLife.description.duration") + (FBP.minAge != FBP.maxAge ? (I18n.format("menu.particleLife.description.duration.range") + (FBP.showInMillis ? FBP.minAge * 50 : FBP.minAge) + I18n.format("menu.particleLife.description.duration.and") + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + " " + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks"))) : (I18n.format("menu.particleLife.description.duration.to") + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + " " + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks")))) + I18n.format("menu.period");
						else
							description = I18n.format("menu.particleLife.description.infinity");
						break;
					case 3:
						description = I18n.format("menu.destroyParticles.description") + (int) Math.pow(FBP.particlesPerAxis, 3) + " §c[§6" + FBP.particlesPerAxis + "^3§c]" + I18n.format("menu.period");
						break;
					case 4:
						description = I18n.format("menu.particleScale.description") + FBP.scaleMult + I18n.format("menu.period");
						break;
					case 5:
						description = I18n.format("menu.particleGravity.description") + FBP.gravityMult + I18n.format("menu.period");
						break;
					case 6:
						description = I18n.format("menu.particleRotation.description") + FBP.rotationMult + I18n.format("menu.period");
						break;
					case 7:
						description = (FBP.infiniteDuration ? I18n.format("menu.disable") : I18n.format("menu.enable")) + I18n.format("menu.infiniteDuration.description");
						break;
					case 8:
						description = I18n.format("menu.time.description") + (!FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks")) + I18n.format("menu.period");
						break;
					default:
						description = I18n.format("menu.noDescriptionFound");
						break;
				}
			}
		}

		return description;
	}

	private void update() {
		minAge.enabled = !FBP.infiniteDuration;
		maxAge.enabled = !FBP.infiniteDuration;
	}
}
