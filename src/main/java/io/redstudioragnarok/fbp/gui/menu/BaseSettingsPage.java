package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.*;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import io.redstudioragnarok.fbp.utils.ModReference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.awt.Desktop;
import java.awt.Dimension;
import java.util.Arrays;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.*;

// Todo: Rename this class to something else I don't like the name.
public abstract class BaseSettingsPage extends GuiScreen {

    protected boolean writeConfig;

    protected int x;

    protected GuiButton toggle, issue;

    private GuiScreen previousPage, nextPage;

    public void initPage(GuiScreen previousPage, GuiScreen nextPage) {
        x = width / 2 - 100;

        final int y = height / 5 + 149;

        addButton(0, x, y, medium, I18n.format("menu.defaults"), false, false, true);
        addButton(-1, x + 102, y, medium, I18n.format("menu.reloadconfig"), false, false, true);

        addButton(-2, x, y + 20 + 1, large, I18n.format("menu.done"), false, false, true);

        issue = new GuiButtonBugReport(-3, width - 32, 6, new Dimension(width, height), this.fontRenderer);
        toggle = new GuiButtonEnable(-4, width - 64, 6, this.fontRenderer);

        this.buttonList.addAll(Arrays.asList(toggle, issue));

        this.previousPage = previousPage;
        this.nextPage = nextPage;

        if (previousPage!= null)
            addButton(-5, x - 45, y - 50, small, "§6<<", false, false, true);

        if (nextPage!= null)
            addButton(-6, x + 225, y - 50, small, "§6>>", false, false, true);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new GuiYesNo(this));
                break;
            case -1:
                ConfigHandler.init();
                break;
            case -2:
                mc.displayGuiScreen(null);
                break;
            case -3:
                try {
                    Desktop.getDesktop().browse(ModReference.newIssueLink);
                } catch (Exception e) {
                    // TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
                }
                break;
            case -4:
                FBP.setEnabled(!FBP.enabled);
                writeConfig = true;
                break;
            case -5:
                mc.displayGuiScreen(previousPage);
                break;
            case -6:
                mc.displayGuiScreen(nextPage);
                break;
        }

        onActionPerformed(button);
    }

    protected void onActionPerformed(GuiButton button) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int y = this.height / 5 - 10;

        if (FBP.mc.world != null)
            GuiHelper.drawRectangle(0, 0, width, height, 0, 0, 0, 191);
        else
            drawBackground(0);

        drawCenteredString(fontRenderer, "§L= " + I18n.format("name") + " =", width / 2, y - 27, fontRenderer.getColorCode('6'));
        drawCenteredString(fontRenderer, "§L= " + ModReference.version + " =", width / 2, y - 17, fontRenderer.getColorCode('a'));

        if (!FBP.enabled)
            drawCenteredString(fontRenderer, "§L= " + I18n.format("menu.disabled") + " =", width / 2, y - 35, fontRenderer.getColorCode('c'));

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (GuiButton guibutton : this.buttonList) {
                if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                    if (!guibutton.isMouseOver())
                        return;

                    actionPerformed(guibutton);
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        if (writeConfig)
            ConfigHandler.writeMainConfig();
    }

    protected FBPGuiButton addButton(int id, int x, int y, FBPGuiButton.ButtonSize size, String text, Boolean toggle, Boolean toggleButton, Boolean enabled) {
        FBPGuiButton button = new FBPGuiButton(id, x, y, size, text, toggle, toggleButton, enabled);
        this.buttonList.add(button);
        return button;
    }

    protected GuiSlider addSlider(int x, int y, float value) {
        GuiSlider slider = new GuiSlider(x, y, value);
        this.buttonList.add(slider);
        return slider;
    }
}
