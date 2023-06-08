package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.BasePage;
import io.redstudioragnarok.fbp.gui.GuiBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class PageSettings extends BasePage {

    public GuiBase parent;

    public PageSettings(GuiBase parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        isSettings = true;
        super.initPage(null, null);

        addButton(1, I18n.format("menu.settings.experiments.title"), FBP.experiments, true);
        addButton(2, I18n.format("menu.settings.debug.title"), FBP.debug, true);

        super.updateScreen();
    }

    @Override
    protected void onActionPerformed(GuiButton button) {
        switch (button.id) {
            case 1:
                FBP.experiments =!FBP.experiments;
                writeConfig = true;
                break;
            case 2:
                FBP.debug = !FBP.debug;
                FBP.updateDebugHandler();
                writeConfig = true;
                break;
        }
    }

    protected String updateDescription() {
        for (GuiButton button : buttonList) {
            if (button.isMouseOver()) {
                switch (button.id) {
                    case 1:
                        return I18n.format("menu.settings.experiments.description");
                    case 2:
                        return I18n.format("menu.settings.debug.description");
                }
            }
        }

        return descriptionFallBack;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(parent);
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }
}