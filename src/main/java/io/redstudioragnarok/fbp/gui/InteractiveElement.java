package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import static io.redstudioragnarok.fbp.FBP.mc;

public abstract class InteractiveElement extends GuiButton {

    protected static final FontRenderer fontRenderer = mc.fontRenderer;

    public InteractiveElement(final int id, final int x, final int y, final String text, boolean... disabled) {
        super(id, x, y, text);

        if (disabled.length != 0)
            this.enabled = !disabled[0];
    }

    protected abstract void update(final int mouseX, final int mouseY);

    public void startDrawing(final boolean lighterOnHover) {
        mc.getTextureManager().bindTexture(FBP.menuTexture);

        if (enabled)
            GlStateManager.color(1, 1, 1, 1);
        else if (hovered)
            GlStateManager.color(0.6F, 0.6F, 0.6F, 1);
        else
            GlStateManager.color(0.5F, 0.5F, 0.5F, 1);

        GlStateManager.enableBlend();
    }

    public void drawString(final String text, final String color, final int x, final int y) {
        fontRenderer.drawStringWithShadow(text, x, y, GuiUtils.hexToDecimalColor(color));
    }

    public void drawCenteredString(final String text, final String color, final int x, final int y) {
        fontRenderer.drawStringWithShadow(text, (x - (float) fontRenderer.getStringWidth(text) / 2), y, GuiUtils.hexToDecimalColor(color));
    }
}
