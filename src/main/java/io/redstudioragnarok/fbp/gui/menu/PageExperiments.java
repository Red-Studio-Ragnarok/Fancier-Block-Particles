package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.gui.BasePage;
import io.redstudioragnarok.fbp.gui.GuiBase;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class PageExperiments extends BasePage {

    @Override
    public void initGui() {
        isExperiments = true;
        super.initPage(null, null);

        super.updateScreen();
    }

    @Override
    protected void onActionPerformed(GuiButton button) {
        switch (button.id) {
        }
    }

    protected String updateDescription() {
        for (GuiButton button : buttonList) {
            if (button.isMouseOver()) {
                switch (button.id) {
                }
            }
        }

        return descriptionFallBack;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(new Page0());
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }
}
