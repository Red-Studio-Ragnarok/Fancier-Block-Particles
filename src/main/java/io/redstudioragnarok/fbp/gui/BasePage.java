package io.redstudioragnarok.fbp.gui;

import com.google.common.collect.Lists;
import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import io.redstudioragnarok.fbp.utils.MathUtil;
import io.redstudioragnarok.fbp.utils.ModReference;
import io.redstudioragnarok.fbp.vectors.Vector2F;
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
import java.util.List;
import java.util.Objects;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.*;

public abstract class BasePage extends GuiScreen {

    protected static int mouseX;
    protected static int mouseY;
    protected int x;

    private long time, lastTime;

    protected boolean writeConfig;

    private final Vector2F lastHandle = new Vector2F();
    private final Vector2F lastSize = new Vector2F();

    private final Vector2F handle = new Vector2F();
    private Vector2F size = new Vector2F();

    protected GuiButtonEnable toggle;

    protected GuiButtonBugReport issue;

    private GuiScreen previousPage, nextPage;

    protected List<Slider> sliderList = Lists.newArrayList();

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
    public void updateScreen() {
        if (!sliderList.isEmpty()) {
            sliderList.forEach(slider -> {
                slider.update();

                if (slider.isMouseOver(mouseX, mouseY, 6)){
                    handle.y = slider.y;

                    if (lastHandle.y == 0)
                        lastHandle.y = slider.y;
                }

                if (!((MathUtil.round(slider.originalValue, 2))  == (MathUtil.round(slider.value, 2))))
                    writeConfig = true;
            });
        }
    }

    protected void updateSliderHoverBox() {
        size = new Vector2F(200, 18);

        float step = 0.5F;
        time = System.currentTimeMillis();

        if (lastTime > 0)
            step = (time - lastTime) / 3F;

        lastTime = time;

        if (!Objects.equals(lastHandle, new Vector2F())) {
            if (lastHandle.y > handle.y) {
                if (lastHandle.y - handle.y <= step)
                    lastHandle.y = handle.y;
                else
                    lastHandle.y -= step;
            }

            if (lastHandle.y < handle.y) {
                if (handle.y - lastHandle.y <= step)
                    lastHandle.y = handle.y;
                else
                    lastHandle.y += step;
            }

            lastHandle.x = x;
        }

        if (!Objects.equals(lastSize, new Vector2F())) {
            if (lastSize.y > size.y)
                if (lastSize.y - size.y <= step)
                    lastSize.y = size.y;
                else
                    lastSize.y -= step;

            if (lastSize.y < size.y)
                if (size.y - lastSize.y <= step)
                    lastSize.y = size.y;
                else
                    lastSize.y += step;

            lastSize.x = 200;
        }
    }

    @Override
    public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks) {
        mouseX = mouseXIn;
        mouseY = mouseYIn;

        int y = this.height / 5 - 10;

        if (FBP.mc.world != null)
            drawRectangle(0, 0, width, height, 0, 0, 0, 191);
        else
            drawBackground(0);

        drawCenteredString(fontRenderer, "§L= " + I18n.format("name") + " =", width / 2, y - 27, fontRenderer.getColorCode('6'));
        drawCenteredString(fontRenderer, "§L= " + ModReference.version + " =", width / 2, y - 17, fontRenderer.getColorCode('a'));

        if (!FBP.enabled)
            drawCenteredString(fontRenderer, "§L= " + I18n.format("menu.disabled") + " =", width / 2, y - 35, fontRenderer.getColorCode('c'));

        drawInfo();

        updateSliderHoverBox();

        for (GuiButton button : this.buttonList) {
            if (button.id <= 0)
                continue;

            Slider slider = button instanceof Slider ? (Slider) button : new Slider();

            if (slider.isMouseOver(mouseX, mouseY, 6))
                drawRectangle(lastHandle.x - 2, lastHandle.y + 2, lastSize.x + 4, lastSize.y - 2, 200, 200, 200, 35);

            if (button.isMouseOver() || slider.isMouseOver(mouseX, mouseY, 6)) {
                drawCenteredString(fontRenderer, getDescription(), this.width / 2, height / 5 + 131, fontRenderer.getColorCode('f'));
                break;
            }
        }

        super.drawScreen(mouseXIn, mouseYIn, partialTicks);
    }

    protected void drawInfo() {
    }

    protected abstract String getDescription();

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

        final int scrollAmount = Mouse.getEventDWheel();

        if (scrollAmount != 0)
            if (scrollAmount > 0)
                if (previousPage != null)
                    mc.displayGuiScreen(previousPage);
            else
                if (nextPage != null)
                    mc.displayGuiScreen(nextPage);
    }

    protected FBPGuiButton addButton(final int id, final int x, final int y, final FBPGuiButton.ButtonSize size, final String text, final Boolean toggle, final Boolean toggleButton, final Boolean enabled) {
        FBPGuiButton button = new FBPGuiButton(id, x, y, size, text, toggle, toggleButton, enabled);
        this.buttonList.add(button);
        return button;
    }

    protected Slider addSlider(final int id, final int x, final int y, final float value) {
        Slider slider = new Slider(id, x, y, value);
        this.sliderList.add(slider);
        this.buttonList.add(slider);
        return slider;
    }

    protected static void drawRectangle(final double x, final double y, final double x2, final double y2, final int red, final int green, final int blue, final int alpha) {
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
