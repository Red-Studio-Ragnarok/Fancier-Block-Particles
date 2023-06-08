package io.redstudioragnarok.fbp.gui.elements;

import io.redstudioragnarok.fbp.gui.GuiUtils;
import io.redstudioragnarok.fbp.gui.InteractiveElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ButtonExperiments extends InteractiveElement {

    private static final String hoverText = I18n.format("menu.experiments");

    public ButtonExperiments(int id, int x, int y) {
        super(id, x, y, "");
    }

    public void update(final int mouseX, final int mouseY) {
        hovered = GuiUtils.isMouseInsideStadium(mouseX, mouseY, x, y, width, height);
    }

    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
        startDrawing(true);

        drawTexturedModalRect(x, y, 0, 110 + (hovered ? height : 0), width, height);

        drawHoverText(hoverText, mouseX, mouseY, true);
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
