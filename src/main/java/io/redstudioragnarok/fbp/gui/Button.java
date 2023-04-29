package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.utils.ModReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class Button extends InteractiveElement {

	boolean toggleButton;
	boolean toggle;

	public int offsetX;

	public enum ButtonSize {
		small,
		medium,
		large
	}

	public Button(int buttonId, int x, int y, ButtonSize buttonSize, String buttonText, boolean toggle, boolean toggleButton, boolean enabled) {
		super(buttonId, x, y, buttonText);

		this.enabled = enabled;

		if (buttonText.equals("§6<<")){
			offsetX = (this.height - 10) / 2;
		} else if (buttonText.equals("§6>>")) {
			offsetX = (this.height - 7) / 2;
		}

		switch (buttonSize) {
			case small:
                this.width = 20;
                break;
            case medium:
                this.width = 98;
                break;
            case large:
                this.width = 200;
                break;
		}

		if (this.toggleButton = toggleButton)
			this.toggle = toggle;
	}

	public void update(final int mouseX, final int mouseY) {
		hovered = GuiUtils.isMouseInsideStadium(mouseX, mouseY, x, y, width, height);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseXIn, int mouseYIn, float partialTicks) {
		mc.getTextureManager().bindTexture(FBP.menuTexture);
		if (enabled) {
			GlStateManager.color(1, 1, 1, 1);
		} else {
			GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
		}

		int i = this.getHoverState(hovered);

		GlStateManager.enableBlend();

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		this.drawTexturedModalRect(this.x, this.y, 0, i * 20, this.width / 2, this.height);
		this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, i * 20, this.width / 2, this.height);

		this.mouseDragged(mc, mouseXIn, mouseYIn);

		int textColor = 14737632;

		if (hovered) {
			textColor = 16777120;
		} else if (packedFGColour != 0) {
			textColor = packedFGColour;
		} else if (!enabled) {
			textColor = 10526880;
		}

		final FontRenderer fontRenderer = mc.fontRenderer;

		if (toggleButton) {
			this.drawString(fontRenderer, this.displayString, this.x + 8, this.y + (this.height - 8) / 2, textColor);

			this.drawString(fontRenderer, toggle ? I18n.format("menu.on") : I18n.format("menu.off"), this.x + this.width - 25, this.y + (this.height - 8) / 2, textColor);
		} else {
			if (offsetX == 0)
				this.drawCenteredString(fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
			else
				this.drawString(fontRenderer, this.displayString, this.x + offsetX, this.y + (this.height - 8) / 2, textColor);
		}
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (hovered && enabled) {
			playPressSound(mc.getSoundHandler());
\			toggle = !toggle;
			return true;
		} else
			return false;
	}
}
