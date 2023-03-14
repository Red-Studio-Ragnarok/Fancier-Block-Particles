package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import net.jafama.FastMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.medium;

public class GuiButtonBlacklist extends FBPGuiButton {

	public boolean particle;
	public boolean isInExceptions;

	public GuiButtonBlacklist(int buttonId, int x, int y, String buttonText, boolean particle, boolean isInExceptions) {
		super(buttonId, x, y, medium, buttonText,false, false, true);

		this.particle = particle;
		this.isInExceptions = isInExceptions;

		this.width = 60;
		this.height = 60;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		int centerX1 = x + this.height / 2;
		int centerY1 = y + this.height / 2 - 1;

		double distance = FastMath.sqrtQuick((mouseX - centerX1) * (mouseX - centerX1) + (mouseY - centerY1) * (mouseY - centerY1));
		int radius = (this.height - 1) / 2;

		hovered = distance <= radius;

		mc.getTextureManager().bindTexture(FBP.menuTexture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.enableBlend();

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		this.drawTexturedModalRect(x, y, enabled ? (isInExceptions ? 60 : 0) : 120, 196, 60, 60);

		if (!enabled) GlStateManager.color(0.25f, 0.25f, 0.25f);
		// render icon
		this.drawTexturedModalRect(x + width / 2.0f - 22.5f + (particle ? 0 : 2), y + height / 2.0f - 22.5f, 256 - 45, particle ? 45 : 0, 45, 45);

		this.mouseDragged(mc, mouseX, mouseY);
	}
}
