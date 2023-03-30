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

public abstract class BaseSettingsPage extends GuiScreen {

    private GuiScreen previousPage, nextPage;

    protected GuiButton toggle, issue;

    @Override
    public void initGui() {
        int x = width / 2 - 100;
        int xTop = width - 27;

        int y = height / 5 + 149;
        int yNavigation = height / 5 + 101;

        addButton(0, x, y, medium, I18n.format("menu.done"), false, false, true);
        addButton(-1, x + 102, y, medium, I18n.format("menu.defaults"), false, false, true);

        addButton(-2, x, y + 20 + 1, large, I18n.format("menu.reloadconfig"), false, false, true);

        toggle = new GuiButtonEnable(-3, xTop - 29, 2, this.fontRenderer);
        issue = new GuiButtonBugReport(-4, xTop, 2, new Dimension(width, height), this.fontRenderer);

        this.buttonList.addAll(Arrays.asList(toggle, issue));
    }

    public void initNavigation(GuiScreen previousPage, GuiScreen nextPage) {
        this.previousPage = previousPage;
        this.nextPage = nextPage;

        int x = width / 2 - 145;
        int y = height / 5 + 101;

        if (previousPage != null)
            addButton(-5, x, y, small, "\u00A76<<", false, false, true);

        if (nextPage!= null)
            addButton(-6, x + 270, y, small, "\u00A76>>", false, false, true);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(null);
                break;
            case -1:
                mc.displayGuiScreen(new GuiYesNo(this));
                break;
            case -2:
                ConfigHandler.init();
                break;
            case -3:
                FBP.setEnabled(!FBP.enabled);
                break;
            case -4:
                try {
                    Desktop.getDesktop().browse(ModReference.newIssueLink);
                } catch (Exception e) {
                    // TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
                }
                break;
            case -5:
                mc.displayGuiScreen(previousPage);
                break;
            case -6:
                mc.displayGuiScreen(nextPage);
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int y = this.height / 5 - 10;

        if (FBP.mc.world != null)
            GuiHelper.drawRectangle(0, 0, width, height, 0, 0, 0, 191);
        else
            drawBackground(0);

        drawCenteredString(fontRenderer, "\u00A7L= " + I18n.format("name") + " =", width / 2, y - 27, fontRenderer.getColorCode('6'));
        drawCenteredString(fontRenderer, "\u00A7L= " + ModReference.version + " =", width / 2, y - 17, fontRenderer.getColorCode('a'));

        if (!FBP.enabled)
            drawCenteredString(fontRenderer, "\u00A7L= " + I18n.format("menu.disabled") + " =", width / 2, y - 35, fontRenderer.getColorCode('c'));

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public FBPGuiButton addButton(int id, int x, int y, FBPGuiButton.ButtonSize size, String text, Boolean toggle, Boolean toggleButton, Boolean enabled) {
        FBPGuiButton button = new FBPGuiButton(id, x, y, size, text, toggle, toggleButton, enabled);
        this.buttonList.add(button);
        return button;
    }

    public FBPGuiButton addButton(int id, int x, int y, String text, Boolean toggle, Boolean toggleButton, Boolean enabled) {
        FBPGuiButton button = new FBPGuiButton(id, x, y, text, toggle, toggleButton, enabled);
        this.buttonList.add(button);
        return button;
    }

    public GuiSlider addSlider(int x, int y, float value) {
        GuiSlider slider = new GuiSlider(x, y, value);
        this.buttonList.add(slider);
        return slider;
    }
}
