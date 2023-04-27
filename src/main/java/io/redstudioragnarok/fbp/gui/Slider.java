package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.utils.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

import static io.redstudioragnarok.fbp.utils.MathUtil.boolToInt;

public class Slider extends GuiButton {

	private boolean dragging = false;

	private static int mouseX;
	private static int mouseY;
	private int handleState = 1;

	public final float originalValue;
	public final float minValue;
	public final float maxValue;
	public float value;
	private final float minX = x + 15;
	private float sliderPosX;

	public Slider() {
		super(Integer.MIN_VALUE, 0, 0, "");
		originalValue = 0;

		this.minValue = 0;
		this.maxValue = 0;
	}

	public Slider(final int id, final int x, final int y, final float minValue, final float inputValue, final float maxValue) {
		super(id, x, y, "");
		this.minValue = minValue;
		this.maxValue = maxValue;
		originalValue = inputValue;
		value = inputValue;
		width = 200;

		sliderPosX = x + 15 + ((value - minValue) / (maxValue - minValue)) * (width - 30);
	}

	public void update() {
		handleState = enabled ? (dragging || isMouseOverSlider(mouseX, mouseY, 0) ? 2 : 1) : 0;

		if (dragging && !Mouse.isButtonDown(0))
			dragging = false;

		if (dragging) {
			sliderPosX = MathUtil.clampMinFirst((float) mouseX, minX, (float) x + width - 15);

			value = MathUtil.round(minValue + ((sliderPosX - minX) / (width - 30)) * (maxValue - minValue), 2);
		}
	}

	@Override
	public void drawButton(final Minecraft mc, final int mouseXIn, final int mouseYIn, final float partialTicks) {
		mouseX = mouseXIn;
		mouseY = mouseYIn;

		// Slider Title
		drawCenteredString(mc.fontRenderer, displayString, x + width / 2, y + 6 - 9, mc.fontRenderer.getColorCode('f'));

		mc.getTextureManager().bindTexture(FBP.menuTexture);

		// Slider Bar
		drawTexturedModalRect(x, y, 0, 60 + boolToInt(enabled) * 20, width / 2, height);
		drawTexturedModalRect(x + width / 2, y, 200 - width / 2, 60 + boolToInt(enabled) * 20, width / 2, height);

		// Slider Handle
		drawTexturedModalRect(sliderPosX - 15, y, 0, 100 + handleState * 20, 15, height);
		drawTexturedModalRect(sliderPosX, y, 185, 100 + handleState * 20, 15, height);
	}

	@Override
	public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
		if (!enabled)
			return false;

		if (isMouseOverBar(mouseX, mouseY, 0))
			dragging = true;

		return false;
	}

	public boolean isMouseOver(final int mouseX, final int mouseY, final int offset) {
		return isMouseOverBar(mouseX, mouseY, offset) || isMouseOverSlider(mouseX, mouseY, offset);
	}

	private boolean isMouseOverBar(final int mouseX, final int mouseY, final int offset) {
		return isMouseInsideEllipse(mouseX, mouseY, x + 4 - offset, y + 4 - offset, x + width - 6 + offset, y + 15 + offset);
	}

	private boolean isMouseOverSlider(final int mouseX, final int mouseY, final int offset) {
		return isMouseInsideEllipse(mouseX, mouseY, (int) (sliderPosX - 15 + 5) - offset, y + 4 - offset, (int) (sliderPosX + 15 - 5) + offset, y + 15 + offset);
	}

	private static boolean isMouseInsideEllipse(final int mouseX, final int mouseY, final int x, final int y, final int x2, final int y2) {
		final int deltaX = x - mouseX;
		final int deltaX2 = x2 - mouseX;

		final int deltaY = (y + 5) - mouseY;

		final boolean insideRectangle = mouseX > x && mouseX < x2 && mouseY > y && mouseY <= y2;
		final boolean insideLeftEllipse = deltaX * deltaX + deltaY * deltaY <= 25;
		final boolean insideRightEllipse = deltaX2 * deltaX2 + deltaY * deltaY <= 25;

		return insideRectangle || insideLeftEllipse || insideRightEllipse;
	}
}
