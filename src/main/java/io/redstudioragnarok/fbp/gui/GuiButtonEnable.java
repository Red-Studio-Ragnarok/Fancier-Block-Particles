package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

import static io.redstudioragnarok.fbp.FBP.mc;

public class GuiButtonEnable extends GuiButton {

	private static final FontRenderer fontRenderer = mc.fontRenderer;

	public GuiButtonEnable(int buttonID, int x, int y) {
		super(buttonID, x, y, 25, 25, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		mc.getTextureManager().bindTexture(FBP.fbpIcon);
		GlStateManager.color(1, 1, 1, 1);

		final int deltaX = mouseX - (x + width / 2);
		final int deltaY = mouseY - (y + height / 2);

		hovered = deltaX * deltaX + deltaY * deltaY <= 128;

		int v = FBP.enabled ? 0 : 50;

		if (hovered)
			v += 25;

		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, v, width, height, width, height * 4);

		final String hoverText = (FBP.enabled ? I18n.format("menu.disable") : I18n.format("menu.enable")) + " FBP";

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
