package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import static io.redstudioragnarok.fbp.FBP.mc;

public class ButtonBugReport extends GuiButton {

	private static final String hoverText = I18n.format("menu.bugReport");

	private static final FontRenderer fontRenderer = mc.fontRenderer;

	public ButtonBugReport(int buttonID, int x, int y) {
		super(buttonID, x, y, 25, 25, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		mc.getTextureManager().bindTexture(FBP.bugIcon);
		GlStateManager.color(1, 1, 1, 1);

		hovered = GuiUtils.isMouseInsideStadium(mouseX, mouseY, x, y, width, height);

		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, hovered ? height : 0, width, height, width, height * 2);

		if (hovered)
			drawString(fontRenderer, hoverText, mouseX - fontRenderer.getStringWidth(hoverText) - width, mouseY - 3, fontRenderer.getColorCode('a'));
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (hovered) {
			playPressSound(mc.getSoundHandler());
			return true;
		} else
			return false;
	}
}
