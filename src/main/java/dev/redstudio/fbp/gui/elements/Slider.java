package dev.redstudio.fbp.gui.elements;

import dev.redstudio.fbp.gui.GuiUtils;
import dev.redstudio.fbp.gui.InteractiveElement;
import io.redstudioragnarok.redcore.utils.MathUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

public class Slider extends InteractiveElement {

    private boolean dragging = false;

    private int handleState = 1;

    public final float originalValue;
    public final float minValue;
    public final float maxValue;
    public float value;
    private final float minX = x + 15;
    private float sliderPosX;

    public Slider(final int id, final int x, final int y, final float minValue, final float inputValue, final float maxValue, final boolean... disabled) {
        super(id, x, y, "", disabled);

        this.minValue = minValue;
        this.maxValue = maxValue;
        originalValue = inputValue;
        value = inputValue;

        width = 200;
        height = 20;

        sliderPosX = x + 15 + ((value - minValue) / (maxValue - minValue)) * (width - 30);
    }

    public void update(final int mouseX, final int mouseY) {
        hovered = GuiUtils.isMouseInsideStadium(mouseX, mouseY, x - 6, y - 6, width + 12, 27);
        handleState = enabled ? (dragging || GuiUtils.isMouseInsideStadium(mouseX, mouseY, (int) (sliderPosX - 15), y + 4, 30, 11) ? 2 : 1) : 0;

        sliderPosX = x + 15 + ((value - minValue) / (maxValue - minValue)) * (width - 30);

        if (dragging && !Mouse.isButtonDown(0))
            dragging = false;

        if (dragging) {
            sliderPosX = MathUtil.clampMinFirst((float) mouseX, minX, (float) x + width - 15);

            value = MathUtil.round(minValue + ((sliderPosX - minX) / (width - 30)) * (maxValue - minValue), 2);
        }
    }

    @Override
    public void drawButton(final Minecraft mc, final int mouseXIn, final int mouseYIn, final float partialTicks) {
        startDrawing(true);

        // Draw the bar
        drawTexturedModalRect(x, y + 5, 0, 60 + MathUtil.boolToInt(enabled) * 10, width / 2, height / 2);
        drawTexturedModalRect(x + width / 2, y + 5, 200 - width / 2, 60 + MathUtil.boolToInt(enabled) * 10, width / 2, height / 2);

        // Draw the handle
        drawTexturedModalRect(sliderPosX - 15, y + 5, 0, 80 + handleState * 10, 15, height / 2);
        drawTexturedModalRect(sliderPosX, y + 5, 185, 80 + handleState * 10, 15, height / 2);

        // Draw the title
        drawCenteredString(displayString, enabled ? GuiUtils.WHITE : GuiUtils.GREY, x + width / 2, y + 6 - 9);
        drawCenteredString("]", enabled ? GuiUtils.WHITE : GuiUtils.GREY, (x + width / 2) + (fontRenderer.getStringWidth(displayString) / 2) + 3, y + 6 - 9);
    }

    @Override
    public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
        if (hovered && enabled)
            dragging = true;

        return false;
    }
}
