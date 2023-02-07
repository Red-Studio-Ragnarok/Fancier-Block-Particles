package io.redstudioragnarok.FBP.gui.menu;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.gui.*;
import io.redstudioragnarok.FBP.handler.ConfigHandler;
import io.redstudioragnarok.FBP.util.ModReference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.awt.*;

public class Page1 extends GuiScreen {

	GuiButton b1, b2, b3, b4, b5, b6, Defaults, Done, Reload, Back, Next, Enable, ReportBug;

	String description;

	final int GUIOffsetY = 4;

	@Override
	public void initGui() {
		int x = this.width / 2 - (96 * 2 + 8) / 2;

		b1 = new FBPGuiButton(1, x, this.height / 5 - 10 + GUIOffsetY, I18n.format("menu.randomrotation.info"), FBP.randomRotation, true, true);
		b2 = new FBPGuiButton(2, x, b1.y + b1.height + 1, I18n.format("menu.cartonmode.info"), false, false, false);
		b3 = new FBPGuiButton(3, x, b2.y + b2.height + 6, I18n.format("menu.randomizedscale.info"), FBP.randomizedScale, true, true);
		b4 = new FBPGuiButton(4, x, b3.y + b3.height + 1, I18n.format("menu.randomfadespeed.info"), FBP.randomFadingSpeed, true, true);
		b5 = new FBPGuiButton(5, x, b4.y + b4.height + 6, I18n.format("menu.redstoneblock.info"), FBP.spawnRedstoneBlockParticles, true, true);
		b6 = new FBPGuiButton(6, x, b5.y + b5.height + 1, I18n.format("menu.spawnfreeze.info"), FBP.spawnWhileFrozen, true, true);

		Defaults = new FBPGuiButton(0, this.width / 2 + 2, b6.y + b6.height + 24 - GUIOffsetY, I18n.format("menu.defaults"), false, false, true);
		Done = new FBPGuiButton(-1, this.width / 2 - 100, Defaults.y, I18n.format("menu.done"), false, false, true);
		Defaults.width = Done.width = 98;
		Reload = new FBPGuiButton(-2, this.width / 2 - 100, Defaults.y + Defaults.height + 1, I18n.format("menu.reloadconfig"), false, false, true);
		Reload.width = b1.width = b2.width = b3.width = b4.width = b5.width = b6.width = 200;

		Back = new FBPGuiButton(-3, b6.x - 44, b6.y + 2 - GUIOffsetY, "<<", false, false, true);
		Next = new FBPGuiButton(-5, b6.x + b6.width + 25, b6.y + 2 - GUIOffsetY, ">>", false, false, true);
		Back.width = Next.width = 20;

		Enable = new GuiButtonEnable(-6, (this.width - 25 - 27) - 4, 2, this.fontRenderer);
		ReportBug = new GuiButtonBugReport(-4, this.width - 27, 2, new Dimension(width, height), this.fontRenderer);

		this.buttonList.addAll(java.util.Arrays.asList(b1, b2, b3, b4, b5, b6, Defaults, Done, Reload, Back, Next, Enable, ReportBug));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case -6:
			FBP.setEnabled(!FBP.enabled);
			break;
		case -5:
			this.mc.displayGuiScreen(new Page2());
			break;
		case -4:
			try {
				Desktop.getDesktop().browse(ModReference.ISSUE);
			} catch (Exception e) {
				// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			}
			break;
		case -3:
			this.mc.displayGuiScreen(new Page0());
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
		case 1:
			FBP.randomRotation = !FBP.randomRotation;
			break;
		case 2:
			break;
		case 3:
			FBP.randomizedScale = !FBP.randomizedScale;
			break;
		case 4:
			FBP.randomFadingSpeed = !FBP.randomFadingSpeed;
			break;
		case 5:
			FBP.spawnRedstoneBlockParticles = !FBP.spawnRedstoneBlockParticles;
			break;
		case 6:
			FBP.spawnWhileFrozen = !FBP.spawnWhileFrozen;
			break;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GuiHelper.background(b1.y - 6 - GUIOffsetY, Done.y - 4, width, height);

		int posY = Done.y - 18;

		getDescription();

		if ((mouseX >= b1.x && mouseX < b1.x + b1.width) && (mouseY >= b1.y && mouseY < b6.y + b1.height)) {
			this.drawCenteredString(fontRenderer, description, this.width / 2, posY, fontRenderer.getColorCode('f'));
		}

		GuiHelper.drawTitle(b1.y - GUIOffsetY, width, fontRenderer);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void getDescription() {
		for (GuiButton b : this.buttonList) {
			if (b.isMouseOver()) {
				switch (b.id) {
					case 1:
						description = I18n.format("menu.randomrotation.description");
						break;
					case 2:
						description = I18n.format("menu.cartonmode.description");
						break;
					case 3:
						description = I18n.format("menu.randomizedscale.description");
						break;
					case 4:
						description = I18n.format("menu.randomfadespeed.description");
						break;
					case 5:
						description = I18n.format("menu.redstoneblock.description");
						break;
					case 6:
						description = I18n.format("menu.spawnfreeze.description");
						break;
					default:
						description = "No description available please report this";
				}
			}
		}
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

	@Override
	public void onGuiClosed() {
		ConfigHandler.writeMainConfig();
	}
}
