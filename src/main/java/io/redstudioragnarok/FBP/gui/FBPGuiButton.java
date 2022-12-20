package io.redstudioragnarok.FBP.gui;

import io.redstudioragnarok.FBP.FBP;
import net.jafama.FastMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class FBPGuiButton extends GuiButton {

	boolean toggleButton;
	boolean toggle;

	public int offsetX;
	boolean fbpenabled;

	public FBPGuiButton(int buttonId, int x, int y, String buttonText, boolean toggle, boolean toggleButton, boolean enabled) {
		super(buttonId, x, y, buttonText);

		fbpenabled = enabled;

		if (buttonText.equals("<<")){
			this.displayString = "\u00A76" + this.displayString;
			offsetX = (this.height - 10) / 2;
		} else if (buttonText.equals(">>")) {
			this.displayString = "\u00A76" + this.displayString;
			offsetX = (this.height - 7) / 2;
		} else {
			offsetX = -1;
		}

		if (this.toggleButton = toggleButton)
			this.toggle = toggle;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		int centerX1 = x + this.height / 2;
		int centerY1 = y + this.height / 2 - 1;

		int centerX2 = x + this.width - this.height / 2;
		int centerY2 = y + this.height / 2;

		double distance1 = FastMath.sqrtQuick((mouseX - centerX1) * (mouseX - centerX1) + (mouseY - centerY1) * (mouseY - centerY1));
		int radius = (this.height - 1) / 2;

		double distance2 = FastMath.sqrtQuick((mouseX - centerX2) * (mouseX - centerX2) + (mouseY - centerY2) * (mouseY - centerY2));

		boolean isOverRectangle = mouseX >= this.x + this.height / 2 - 2 && mouseY >= this.y + 1 && mouseX < this.x + this.width - this.height / 2 + 3 && mouseY < this.y + this.height;

		hovered = (distance1 <= radius || distance2 <= radius) || isOverRectangle;

		FontRenderer fontrenderer = mc.fontRenderer;
		mc.getTextureManager().bindTexture(FBP.FBP_WIDGETS);
		if (fbpenabled) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
		}

		int i = this.getHoverState(this.hovered);

		GlStateManager.enableBlend();

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		this.drawTexturedModalRect(this.x, this.y, 0, i * 20, this.width / 2, this.height);
		this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, i * 20, this.width / 2, this.height);

		this.mouseDragged(mc, mouseX, mouseY);
		int j = 14737632;

		if (packedFGColour != 0) {
			j = packedFGColour;
		} else if (!fbpenabled) {
			j = 10526880;
		} else if (this.hovered) {
			j = 16777120;
		}

		if (!toggleButton) {
			if (offsetX == -1)
				this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
			else
				this.drawString(fontrenderer, this.displayString, this.x + offsetX, this.y + (this.height - 8) / 2, j);
		} else {
			this.drawString(fontrenderer, this.displayString, this.x + 8, this.y + (this.height - 8) / 2, j);

			this.drawString(fontrenderer, toggle ? I18n.format("menu.on") : I18n.format("menu.off"), this.x + this.width - 25, this.y + (this.height - 8) / 2, j);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (fbpenabled && this.enabled && this.visible && hovered) {
			playPressSound(mc.getSoundHandler());
			toggle = !toggle;
			return true;
		} else
			return false;
	}
}
