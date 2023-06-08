package io.redstudioragnarok.fbp.gui.elements;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.GuiUtils;
import io.redstudioragnarok.fbp.gui.InteractiveElement;
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
		startDrawing(FBP.guiTexture, true);

		// Draw the bar
		drawTexturedModalRect(x, y, 0, 60 + MathUtil.boolToInt(enabled) * 20, width / 2, height);
		drawTexturedModalRect(x + width / 2, y, 200 - width / 2, 60 + MathUtil.boolToInt(enabled) * 20, width / 2, height);

		// Draw the handle
		drawTexturedModalRect(sliderPosX - 15, y, 0, 100 + handleState * 20, 15, height);
		drawTexturedModalRect(sliderPosX, y, 185, 100 + handleState * 20, 15, height);

		// Draw the title
		drawCenteredString(displayString, enabled ? "#FFFCFC" : "#C9C9C9", x + width / 2, y + 6 - 9);
		drawCenteredString("]", enabled ? "#FFFCFC" : "#C9C9C9", (x + width / 2) + (fontRenderer.getStringWidth(displayString) / 2) + 3, y + 6 - 9);
	}

	@Override
	public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
		if (hovered && enabled)
			dragging = true;

		return false;
	}
}
