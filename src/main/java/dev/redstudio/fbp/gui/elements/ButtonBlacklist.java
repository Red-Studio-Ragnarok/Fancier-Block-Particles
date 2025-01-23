package dev.redstudio.fbp.gui.elements;

import dev.redstudio.fbp.gui.GuiUtils;
import dev.redstudio.fbp.gui.InteractiveElement;
import net.minecraft.client.Minecraft;

public class ButtonBlacklist extends InteractiveElement {

    public boolean isParticle;
    public boolean isBlacklisted;

    public ButtonBlacklist(final int x, final int y, final boolean particle, final boolean isBlacklisted) {
        super(Integer.MIN_VALUE, x, y, "");

        isParticle = particle;
        this.isBlacklisted = isBlacklisted;

        width = 60;
        height = 60;
    }

    public void update(final int mouseX, final int mouseY) {
        hovered = GuiUtils.isMouseInsideStadium(mouseX, mouseY, x, y, width, height);
    }

    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
        startDrawing(true);

        // Draw the button
        drawTexturedModalRect(x, y, enabled ? (isBlacklisted ? 60 : 0) : 120, 196, width, height);

        // Draw the icon
        drawTexturedModalRect(x + width / 2F - 22.5F + (isParticle ? 0 : 2), y + height / 2F - 22.5F, 211, isParticle ? 45 : 0, 45, 45);
    }
}
