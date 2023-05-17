package io.redstudioragnarok.fbp.gui.elements;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.GuiUtils;
import io.redstudioragnarok.fbp.gui.InteractiveElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ButtonIssue extends InteractiveElement {

    private static final String hoverText = I18n.format("menu.bugReport");

    public ButtonIssue(final int id, final int x, final int y) {
        super(id, x, y, "");
    }

    public void update(final int mouseX, final int mouseY) {
        hovered = GuiUtils.isMouseInsideStadium(mouseX, mouseY, x, y, width, height);
    }

    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
        startDrawing(FBP.bugIcon, true);

        drawModalRectWithCustomSizedTexture(x, y, 0, hovered ? height : 0, width, height, width, height * 2);

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
