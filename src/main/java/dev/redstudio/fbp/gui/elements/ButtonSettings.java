package dev.redstudio.fbp.gui.elements;

import dev.redstudio.fbp.gui.GuiUtils;
import dev.redstudio.fbp.gui.InteractiveElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ButtonSettings extends InteractiveElement {

    private static final String hoverText = I18n.format("menu.settings");

    public ButtonSettings(final int id, final int x, final int y) {
        super(id, x, y, "");
    }

    public void update(final int mouseX, final int mouseY) {
        hovered = GuiUtils.isMouseInsideStadium(mouseX, mouseY, x, y, width, height);
    }

    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
        startDrawing(true);

        drawTexturedModalRect(x, y, 25, 110 + (hovered ? height : 0), width, height);

        drawHoverText(hoverText, mouseX, mouseY);
    }

    @Override
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        if (hovered) {
            playPressSound(mc.getSoundHandler());
            return true;
        } else
            return false;
    }
}
