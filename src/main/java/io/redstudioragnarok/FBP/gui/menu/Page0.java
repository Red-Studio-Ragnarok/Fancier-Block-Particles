package io.redstudioragnarok.FBP.gui.menu;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.gui.*;
import io.redstudioragnarok.FBP.handler.ConfigHandler;
import io.redstudioragnarok.FBP.util.MathUtil;
import io.redstudioragnarok.FBP.util.ModReference;
import io.redstudioragnarok.FBP.vector.Vector2D;
import net.jafama.FastMath;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.util.Arrays;

public class Page0 extends GuiScreen {

	GuiButton InfiniteDuration, TimeUnit, Defaults, Done, Reload, Next, Enable, ReportBug;
	GuiSlider MinDurationSlider, MaxDurationSlider, ParticleCountBase, ScaleMultSlider, GravitiyForceSlider, RotSpeedSlider;

	Vector2D lastHandle = new Vector2D();
	Vector2D lastSize = new Vector2D();

	Vector2D handle = new Vector2D();
	Vector2D size = new Vector2D();

	long time, lastTime;

	int selected = 0;

	final int GUIOffsetY = 8;

	@Override
	public void initGui() {
		int X = this.width / 2 - 100;

		MinDurationSlider = new GuiSlider(X, this.height / 5 - 10 + GUIOffsetY, (float) ((FBP.minAge - 10) / 90.0));
		MaxDurationSlider = new GuiSlider(X, MinDurationSlider.y + MinDurationSlider.height + 1, (float) ((FBP.maxAge - 10) / 90.0));

		ParticleCountBase = new GuiSlider(X, MaxDurationSlider.y + 6 + MaxDurationSlider.height, (float) ((FBP.particlesPerAxis - 2) / 3.0));
		ScaleMultSlider = new GuiSlider(X, ParticleCountBase.y + ParticleCountBase.height + 1, (float) ((FBP.scaleMult - 0.75) / 0.5));
		GravitiyForceSlider = new GuiSlider(X, ScaleMultSlider.y + ScaleMultSlider.height + 6, (float) ((FBP.gravityMult - 0.05) / 2.95));
		RotSpeedSlider = new GuiSlider(X, GravitiyForceSlider.y + GravitiyForceSlider.height + 1, (float) (FBP.rotationMult / 1.5));
		InfiniteDuration = new FBPGuiButton(11, X + 205, MinDurationSlider.y + 10, (FBP.infiniteDuration ? "\u00A7a" : "\u00A7c") + "\u221e", false, false, true);

		TimeUnit = new FBPGuiButton(12, X - 25, MinDurationSlider.y + 10, "\u00A7a\u00A7L" + (FBP.showInMillis ? "ms" : "ti"), false, false, true);

		Defaults = new FBPGuiButton(0, this.width / 2 + 2, RotSpeedSlider.y + RotSpeedSlider.height + 24 - GUIOffsetY, I18n.format("menu.defaults"), false, false, true);
		Done = new FBPGuiButton(-1, X, Defaults.y, I18n.format("menu.done"), false, false, true);
		Defaults.width = Done.width = 98;
		Reload = new FBPGuiButton(-2, X, Defaults.y + Defaults.height + 1, I18n.format("menu.reloadconfig"), false, false, true);
		Reload.width = 96 * 2 + 8;

		Next = new FBPGuiButton(-3, RotSpeedSlider.x + RotSpeedSlider.width + 25, RotSpeedSlider.y + 2 - GUIOffsetY, ">>", false, false, true);

		Enable = new GuiButtonEnable(-6, (this.width - 25 - 27) - 4, 2, this.fontRenderer);
		ReportBug = new GuiButtonBugReport(-4, this.width - 27, 2, new Dimension(width, height), this.fontRenderer);

		InfiniteDuration.width = TimeUnit.width = Next.width = 20;

		this.buttonList.addAll(Arrays.asList(MinDurationSlider, MaxDurationSlider, ParticleCountBase, ScaleMultSlider, GravitiyForceSlider, RotSpeedSlider, InfiniteDuration, TimeUnit, Defaults, Done, Reload, Next, Enable, ReportBug));

		update();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case -6:
			FBP.setEnabled(!FBP.enabled);
			break;
		case -5:
			FBP.showInMillis = !FBP.showInMillis;
			break;
		case -4:
			try {
				Desktop.getDesktop().browse(ModReference.ISSUE);
			} catch (Exception e) {
				// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			}
			break;
		case -3:
			this.mc.displayGuiScreen(new Page1());
			break;
		case -2:
			ConfigHandler.init();
			break;
		case -1:
			this.mc.displayGuiScreen(null);
			break;
		case 0:
			this.mc.displayGuiScreen(new GuiYesNo(this));
			break;
		case 11:
			InfiniteDuration.displayString = ((FBP.infiniteDuration = !FBP.infiniteDuration) ? "\u00A7a" : "\u00A7c") + "\u221e";
			update();
			break;
		case 12:
			TimeUnit.displayString = "\u00A7a\u00A7L" + ((FBP.showInMillis = !FBP.showInMillis) ? "ms" : "ti");
			break;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GuiHelper.background(MinDurationSlider.y - 6 - GUIOffsetY, Done.y - 4, width, height);

		int sParticleCountBase = FastMath.round(2 + 3 * ParticleCountBase.value);

		int sMinAge = (int) (10 + 90 * MinDurationSlider.value);
		int sMaxAge = (int) (10 + 90 * MaxDurationSlider.value);

		double sScaleMult = MathUtil.round((float) (0.75 + 0.5 * ScaleMultSlider.value), 2);
		double sGravityForce = MathUtil.round((float) (0.05 + 2.95 * GravitiyForceSlider.value), 2);
		double sRotSpeed = MathUtil.round((float) (1.5 * RotSpeedSlider.value), 2);

		if (FBP.maxAge < sMinAge) {
			FBP.maxAge = sMinAge;

			MaxDurationSlider.value = (float) ((FBP.maxAge - 10) / 90.0);
		}

		if (FBP.minAge > sMaxAge) {
			FBP.minAge = sMaxAge;

			MinDurationSlider.value = (float) ((FBP.minAge - 10) / 90.0);
		}

		FBP.minAge = sMinAge;
		FBP.maxAge = sMaxAge;

		FBP.scaleMult = (float) sScaleMult;
		FBP.gravityMult = (float) sGravityForce;
		FBP.rotationMult = (float) sRotSpeed;
		FBP.particlesPerAxis = sParticleCountBase;

		ParticleCountBase.value = (float) ((FBP.particlesPerAxis - 2) / 3.0);

		drawMouseOverSelection(mouseX, mouseY);

		GuiHelper.drawTitle(MinDurationSlider.y - GUIOffsetY, width, fontRenderer);

		drawInfo();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawMouseOverSelection(int mouseX, int mouseY) {
		final int posY = Done.y - 18;

		if (MinDurationSlider.isMouseOver(mouseX, mouseY) || MaxDurationSlider.isMouseOver(mouseX, mouseY)) {
			handle.y = MinDurationSlider.y;
			size = new Vector2D(MinDurationSlider.width, 39);
			selected = 1;
		} else if (ParticleCountBase.isMouseOver(mouseX, mouseY)) {
			handle.y = ParticleCountBase.y;
			size = new Vector2D(ParticleCountBase.width, 18);
			selected = 2;
		} else if (ScaleMultSlider.isMouseOver(mouseX, mouseY)) {
			handle.y = ScaleMultSlider.y;
			size = new Vector2D(ScaleMultSlider.width, 18);
			selected = 3;
		} else if (GravitiyForceSlider.isMouseOver(mouseX, mouseY)) {
			handle.y = GravitiyForceSlider.y;
			size = new Vector2D(GravitiyForceSlider.width, 18);
			selected = 4;
		} else if (RotSpeedSlider.isMouseOver(mouseX, mouseY)) {
			handle.y = RotSpeedSlider.y;
			size = new Vector2D(RotSpeedSlider.x - (RotSpeedSlider.x + RotSpeedSlider.width), 18);
			selected = 5;
		} else if (InfiniteDuration.isMouseOver())
			selected = 6;
		else if (TimeUnit.isMouseOver())
			selected = 7;

		int step = 1;
		time = System.currentTimeMillis();

		if (lastTime > 0)
			step = (int) (time - lastTime);

		lastTime = time;

		if (lastHandle != new Vector2D()) {
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

			lastHandle.x = MinDurationSlider.x;
		}

		if (lastSize != new Vector2D()) {
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

			lastSize.x = GravitiyForceSlider.width;
		}

		String text;

		switch (selected) {
		case 1:
			if (!FBP.infiniteDuration) {
				String _text = (FBP.minAge != FBP.maxAge ? (I18n.format("menu.particlelife.description.duration.range") + (FBP.showInMillis ? FBP.minAge * 50 : FBP.minAge) + I18n.format("menu.particlelife.description.duration.and") + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + (FBP.showInMillis ? "ms" : " ticks")) : (I18n.format("menu.particlelife.description.duration.to") + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + (FBP.showInMillis ? "ms" : " ticks")));

				text = I18n.format("menu.particlelife.description.duration") + _text + I18n.format("menu.period");
			} else {
				text = I18n.format("menu.particlelife.description.infinity");
			}
			break;
		case 2:
			text = I18n.format("menu.destroyparticles.description") + (int) Math.pow(FBP.particlesPerAxis, 3) + " \u00A7c[\u00A76" + FBP.particlesPerAxis + "^3\u00A7c]"+ I18n.format("menu.period");
			break;
		case 3:
			text = I18n.format("menu.particlescale.description") + FBP.scaleMult +  I18n.format("menu.period");
			break;
		case 4:
			text = I18n.format("menu.particlegravity.description")  + FBP.gravityMult + I18n.format("menu.period");
			break;
		case 5:
			text = I18n.format("menu.particlerotation.description")  + FBP.rotationMult + I18n.format("menu.period");
			break;
		case 6:
			text = (FBP.infiniteDuration ? I18n.format("menu.disable") : I18n.format("menu.enable")) + I18n.format("menu.infiniteduration.description");
			break;
		case 7:
			text = I18n.format("menu.time.description") + (!FBP.showInMillis ? I18n.format("menu.timems.description") : "ticks") + I18n.format("menu.period");
			break;
		default:
			text = "No description available please report this";
		}

		if (mouseX >= MinDurationSlider.x - 2 && mouseX <= MinDurationSlider.x + MinDurationSlider.width + 2 && mouseY < RotSpeedSlider.y + RotSpeedSlider.height && mouseY >= MinDurationSlider.y && (lastSize.y <= 20 || lastSize.y < 50) && lastHandle.y >= MinDurationSlider.y || InfiniteDuration.isMouseOver() || TimeUnit.isMouseOver()) {
			if (selected <= 5)
				GuiHelper.drawRect(lastHandle.x - 2, lastHandle.y + 2, lastSize.x + 4, lastSize.y - 2, 200, 200, 200, 35);

			this.drawCenteredString(fontRenderer, text, this.width / 2, posY, fontRenderer.getColorCode('f'));
		}
	}

	private void drawInfo() {

		String s = I18n.format("menu.destroyparticles.info") + " [\u00A76" + (int) Math.pow(FBP.particlesPerAxis, 3) + "\u00A7f]";
		ParticleCountBase.displayString = s;

		if (FBP.infiniteDuration)
			s = I18n.format("menu.minduration.info") + " [\u00A76" + "\u221e" + (FBP.showInMillis ? " ms" : " ticks") + "\u00A7f]";
		else
			s = I18n.format("menu.minduration.info") + " [\u00A76" + (FBP.showInMillis ? ((FBP.minAge * 50) + "ms") : (FBP.minAge + " tick")) + "\u00A7f]";

		MinDurationSlider.displayString = s;

		if (FBP.infiniteDuration)
			s = I18n.format("menu.maxduration.info") + " [\u00A76" + "\u221e" + (FBP.showInMillis ? " ms" : " ticks") + "\u00A7f]";
		else
			s = I18n.format("menu.maxduration.info") + " [\u00A76" + (FBP.showInMillis ? ((FBP.maxAge * 50) + "ms") : (FBP.maxAge + (FBP.maxAge > 1 ? " ticks" : " tick"))) + "\u00A7f]";

		MaxDurationSlider.displayString = s;

		ScaleMultSlider.displayString = I18n.format("menu.scalemult.info") + " [\u00A76" + FBP.scaleMult + "\u00A7f]";

		GravitiyForceSlider.displayString = I18n.format("menu.gravityscale.info") + " [\u00A76" + FBP.gravityMult + "\u00A7f]";

		RotSpeedSlider.displayString = I18n.format("menu.rotationspeed.info") + " [\u00A76" + (FBP.rotationMult != 0 ? FBP.rotationMult : I18n.format("menu.off")) + "\u00A7f]";
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			for (GuiButton guibutton : this.buttonList) {
				if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
					if (!guibutton.isMouseOver())
						return;

					this.actionPerformed(guibutton);
				}
			}
		}
	}

	void update() {
		MinDurationSlider.enabled = !FBP.infiniteDuration;
		MaxDurationSlider.enabled = !FBP.infiniteDuration;
	}

	@Override
	public void onGuiClosed() {
		ConfigHandler.writeMainConfig();
	}
}
