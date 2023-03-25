package io.redstudioragnarok.fbp.gui.menu;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public abstract class MenuPage extends GuiScreen {

    protected void drawTitle(String titleKey, int y) {
        String title = I18n.format(titleKey);
        int titleWidth = this.fontRenderer.getStringWidth(title);
        int titleX = (this.width - titleWidth) / 2;
        this.drawCenteredString(this.fontRenderer, title, this.width / 2, y, 0xFFFFFF);
    }
}

