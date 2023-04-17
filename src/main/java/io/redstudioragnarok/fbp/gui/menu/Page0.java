package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.BasePage;
import io.redstudioragnarok.fbp.gui.GuiSlider;
import io.redstudioragnarok.fbp.utils.MathUtil;
import io.redstudioragnarok.fbp.vectors.Vector2F;
import net.jafama.FastMath;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.small;

public class Page0 extends BasePage {

	GuiButton infiniteDuration, timeUnit;
	GuiSlider minAge, maxAge, particlesPerAxis, scaleMult, gravityMult, rotationMult;

	Vector2F lastHandle = new Vector2F();
	Vector2F lastSize = new Vector2F();

	Vector2F handle = new Vector2F();
	Vector2F size = new Vector2F();

	long time, lastTime;

	int selected = 0;

	@Override
	public void initGui() {
		super.initPage(null, new Page1());

		minAge = addSlider(x, this.height / 5 - 6, (float) ((FBP.minAge - 10) / 90.0));
		maxAge = addSlider(x, minAge.y + minAge.height + 1, (float) ((FBP.maxAge - 10) / 90.0));

		particlesPerAxis = addSlider(x, maxAge.y + 6 + maxAge.height, (float) ((FBP.particlesPerAxis - 2) / 3.0));
		scaleMult = addSlider(x, particlesPerAxis.y + particlesPerAxis.height + 1, (float) ((FBP.scaleMult - 0.75) / 0.5));
		gravityMult = addSlider(x, scaleMult.y + scaleMult.height + 6, (float) ((FBP.gravityMult - 0.05) / 2.95));
		rotationMult = addSlider(x, gravityMult.y + gravityMult.height + 1, (float) (FBP.rotationMult / 1.5));

		infiniteDuration = addButton(11, x + 205, minAge.y + 10, small, (FBP.infiniteDuration ? "§a" : "§c") + "∞", false, false, true);
		timeUnit = addButton(12, x - 25, minAge.y + 10, small, "§a§L" + (FBP.showInMillis ? "ms" : "ti"), false, false, true);

		update();
	}

	@Override
	protected void onActionPerformed(GuiButton button) {
		switch (button.id) {
		case 11:
			infiniteDuration.displayString = ((FBP.infiniteDuration = !FBP.infiniteDuration) ? "§a" : "§c") + "∞";
			update();
			break;
		case 12:
			timeUnit.displayString = "§a§L" + ((FBP.showInMillis = !FBP.showInMillis) ? "ms" : "ti");
			break;
		}

		writeConfig = true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		int sParticleCountBase = FastMath.round(2 + 3 * particlesPerAxis.value);

		int sMinAge = (int) (10 + 90 * minAge.value);
		int sMaxAge = (int) (10 + 90 * maxAge.value);

		double sScaleMult = MathUtil.round((float) (0.75 + 0.5 * scaleMult.value), 2);
		double sGravityForce = MathUtil.round((float) (0.05 + 2.95 * gravityMult.value), 2);
		double sRotSpeed = MathUtil.round((float) (1.5 * rotationMult.value), 2);

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

		FBP.scaleMult = (float) sScaleMult;
		FBP.gravityMult = (float) sGravityForce;
		FBP.rotationMult = (float) sRotSpeed;
		FBP.particlesPerAxis = sParticleCountBase;

		particlesPerAxis.value = (float) ((FBP.particlesPerAxis - 2) / 3.0);

		drawMouseOverSelection(mouseX, mouseY);

		drawInfo();

		issue.checkHover(width, height);
	}

	private void drawMouseOverSelection(int mouseX, int mouseY) {
		if (minAge.isMouseOver(mouseX, mouseY) || maxAge.isMouseOver(mouseX, mouseY)) {
			handle.y = minAge.y;
			size = new Vector2F(minAge.width, 39);
			selected = 1;
		} else if (particlesPerAxis.isMouseOver(mouseX, mouseY)) {
			handle.y = particlesPerAxis.y;
			size = new Vector2F(particlesPerAxis.width, 18);
			selected = 2;
		} else if (scaleMult.isMouseOver(mouseX, mouseY)) {
			handle.y = scaleMult.y;
			size = new Vector2F(scaleMult.width, 18);
			selected = 3;
		} else if (gravityMult.isMouseOver(mouseX, mouseY)) {
			handle.y = gravityMult.y;
			size = new Vector2F(gravityMult.width, 18);
			selected = 4;
		} else if (rotationMult.isMouseOver(mouseX, mouseY)) {
			handle.y = rotationMult.y;
			size = new Vector2F(rotationMult.x - (rotationMult.x + rotationMult.width), 18);
			selected = 5;
		} else if (infiniteDuration.isMouseOver())
			selected = 6;
		else if (timeUnit.isMouseOver())
			selected = 7;

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

			lastHandle.x = minAge.x;
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

			lastSize.x = gravityMult.width;
		}

		String text;

		switch (selected) {
		case 1:
			if (!FBP.infiniteDuration) {
				String _text = (FBP.minAge != FBP.maxAge ? (I18n.format("menu.particleLife.description.duration.range") + (FBP.showInMillis ? FBP.minAge * 50 : FBP.minAge) + I18n.format("menu.particleLife.description.duration.and") + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + " " + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks"))) : (I18n.format("menu.particleLife.description.duration.to") + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + " " + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks"))));

				text = I18n.format("menu.particleLife.description.duration") + _text + I18n.format("menu.period");
			} else {
				text = I18n.format("menu.particleLife.description.infinity");
			}
			break;
		case 2:
			text = I18n.format("menu.destroyParticles.description") + (int) Math.pow(FBP.particlesPerAxis, 3) + " §c[§6" + FBP.particlesPerAxis + "^3§c]"+ I18n.format("menu.period");
			break;
		case 3:
			text = I18n.format("menu.particleScale.description") + FBP.scaleMult +  I18n.format("menu.period");
			break;
		case 4:
			text = I18n.format("menu.particleGravity.description")  + FBP.gravityMult + I18n.format("menu.period");
			break;
		case 5:
			text = I18n.format("menu.particleRotation.description")  + FBP.rotationMult + I18n.format("menu.period");
			break;
		case 6:
			text = (FBP.infiniteDuration ? I18n.format("menu.disable") : I18n.format("menu.enable")) + I18n.format("menu.infiniteDuration.description");
			break;
		case 7:
			text = I18n.format("menu.time.description") + (!FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks")) + I18n.format("menu.period");
			break;
		default:
			text = I18n.format("menu.noDescriptionFound");
		}

		if (mouseX >= minAge.x - 2 && mouseX <= minAge.x + minAge.width + 2 && mouseY < rotationMult.y + rotationMult.height && mouseY >= minAge.y && (lastSize.y <= 20 || lastSize.y < 50) && lastHandle.y >= minAge.y || infiniteDuration.isMouseOver() || timeUnit.isMouseOver()) {
			if (selected <= 5)
				drawRectangle(lastHandle.x - 2, lastHandle.y + 2, lastSize.x + 4, lastSize.y - 2, 200, 200, 200, 35);

			this.drawCenteredString(fontRenderer, text, this.width / 2, height / 5 + 131, fontRenderer.getColorCode('f'));

			writeConfig = true;
		}
	}

	private void drawInfo() {

		String title = I18n.format("menu.destroyParticles.title") + " [§6" + (int) Math.pow(FBP.particlesPerAxis, 3) + "§f]";
		particlesPerAxis.displayString = title;

		if (FBP.infiniteDuration)
			title = I18n.format("menu.minDuration.title") + " [§6" + "∞ " + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks")) + "§f]";
		else
			title = I18n.format("menu.minDuration.title") + " [§6" + (FBP.showInMillis ? FBP.minAge * 50 : FBP.minAge) + " " + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks")) + "§f]";

		minAge.displayString = title;

		if (FBP.infiniteDuration)
			title = I18n.format("menu.maxDuration.title") + " [§6" + "∞" + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks")) + "§f]";
		else
			title = I18n.format("menu.maxDuration.title") + " [§6" + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + " " + (FBP.showInMillis ? I18n.format("menu.time.description.milliseconds") : I18n.format("menu.time.description.ticks")) + "§f]";

		maxAge.displayString = title;

		scaleMult.displayString = I18n.format("menu.scaleMult.title") + " [§6" + FBP.scaleMult + "§f]";

		gravityMult.displayString = I18n.format("menu.gravityScale.title") + " [§6" + FBP.gravityMult + "§f]";

		rotationMult.displayString = I18n.format("menu.rotationSpeed.title") + " [§6" + (FBP.rotationMult != 0 ? FBP.rotationMult : I18n.format("menu.off")) + "§f]";
	}

	private void update() {
		minAge.enabled = !FBP.infiniteDuration;
		maxAge.enabled = !FBP.infiniteDuration;
	}
}
