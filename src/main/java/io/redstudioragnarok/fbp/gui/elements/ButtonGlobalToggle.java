package io.redstudioragnarok.fbp.gui.elements;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.GuiUtils;
import io.redstudioragnarok.fbp.gui.InteractiveElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ButtonGlobalToggle extends InteractiveElement {

    private static final String hoverTextEnable = I18n.format("menu.disable") + " FBP";
    private static final String hoverTextDisable = I18n.format("menu.enable") + " FBP";

    public ButtonGlobalToggle(final int id, final int x, final int y) {
        super(id, x, y, "");
    }

    public void update(final int mouseX, final int mouseY) {
        hovered = GuiUtils.isMouseInsideStadium(mouseX, mouseY, x, y, width, height);
    }

    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
        startDrawing(FBP.fbpIcon, true);

        int v = FBP.enabled ? 0 : height * 2;

        if (hovered)
            v += height;

        drawModalRectWithCustomSizedTexture(x, y, 0, v, width, height, width, height * 4);

        drawHoverText(FBP.enabled ? hoverTextEnable : hoverTextDisable, mouseX, mouseY);
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