package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import io.redstudioragnarok.fbp.utils.ModReference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Desktop;
import java.io.IOException;
import java.util.Arrays;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.*;

public abstract class BasePage extends GuiScreen {

    protected boolean writeConfig;

    protected int x;

    protected GuiButtonEnable toggle;

    protected GuiButtonBugReport issue;

    private GuiScreen previousPage, nextPage;

    public void initPage(GuiScreen previousPage, GuiScreen nextPage) {
        x = width / 2 - 100;

        final int y = height / 5 + 149;

        addButton(0, x, y, medium, I18n.format("menu.defaults"), false, false, true);
        addButton(-1, x + 102, y, medium, I18n.format("menu.reloadConfig"), false, false, true);

        addButton(-2, x, y + 20 + 1, large, I18n.format("menu.done"), false, false, true);

        issue = new GuiButtonBugReport(-3, width - 32, 6);
        toggle = new GuiButtonEnable(-4, width - 64, 6);

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
            drawRectangle(0, 0, width, height, 0, 0, 0, 191);
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

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int scrollAmount = Mouse.getEventDWheel();

        if (scrollAmount != 0) {
            if (scrollAmount > 0) {
                if (previousPage != null)
                    mc.displayGuiScreen(previousPage);
            } else {
                if (nextPage != null)
                    mc.displayGuiScreen(nextPage);
            }
        }
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

    protected static void drawRectangle(double x, double y, double x2, double y2, int red, int green, int blue, int alpha) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(x, y + y2, 0).color(red, green, blue, alpha).endVertex();
        buffer.pos(x + x2, y + y2, 0).color(red, green, blue, alpha).endVertex();
        buffer.pos(x + x2, y, 0).color(red, green, blue, alpha).endVertex();
        buffer.pos(x, y, 0).color(red, green, blue, alpha).endVertex();

        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }
}
