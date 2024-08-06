package dev.redstudio.fbp.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import static dev.redstudio.fbp.FBP.MC;
import static dev.redstudio.fbp.ProjectConstants.ID;

public abstract class InteractiveElement extends GuiButton {

    protected static final FontRenderer fontRenderer = MC.fontRenderer;

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ID + ":textures/gui/gui.png");

    public InteractiveElement(final int id, final int x, final int y, final String text, boolean... disabled) {
        super(id, x, y, text);

        width = 25;
        height = 25;

        if (disabled.length != 0)
            this.enabled = !disabled[0];
    }

    protected abstract void update(final int mouseX, final int mouseY);

    public void startDrawing(final boolean lighterOnHover) {
        MC.getTextureManager().bindTexture(GUI_TEXTURE);

        if (enabled && (!lighterOnHover || !hovered))
            GlStateManager.color(0.9F, 0.9F, 0.9F, 1);
        else if (enabled)
            GlStateManager.color(1, 1, 1, 1);
        else if (hovered)
            GlStateManager.color(0.6F, 0.6F, 0.6F, 1);
        else
            GlStateManager.color(0.5F, 0.5F, 0.5F, 1);

        GlStateManager.enableBlend();
    }

    public void drawHoverText(final String hoverText, final int mouseX, final int mouseY, final boolean... rightSided) {
        final int textWidth = fontRenderer.getStringWidth(hoverText);

        if (hovered)
            drawString(hoverText, GuiUtils.WHITE, rightSided.length > 0 ? (mouseX + textWidth) - width * 2 : (mouseX - textWidth) - width / 2, mouseY - 3);
    }

    public void drawString(final String text, final String color, final int x, final int y) {
        fontRenderer.drawStringWithShadow(text, x, y, GuiUtils.hexToDecimalColor(color));
    }

    public void drawCenteredString(final String text, final String color, final int x, final int y) {
        fontRenderer.drawStringWithShadow(text, (x - (float) fontRenderer.getStringWidth(text) / 2), y, GuiUtils.hexToDecimalColor(color));
    }
}
