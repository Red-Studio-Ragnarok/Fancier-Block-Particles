package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.utils.MathUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import static io.redstudioragnarok.fbp.utils.MathUtil.boolToInt;

public class Slider extends InteractiveElement {

	private boolean dragging = false;

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

	public Slider(final int id, final int x, final int y, final float minValue, final float inputValue, final float maxValue, boolean enabled) {
		super(id, x, y, "");

		this.enabled = enabled;

		this.minValue = minValue;
		this.maxValue = maxValue;
		originalValue = inputValue;
		value = inputValue;

		width = 200;

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
		// Title
		drawCenteredString(mc.fontRenderer, displayString, x + width / 2, y + 6 - 9, mc.fontRenderer.getColorCode('f'));

		mc.getTextureManager().bindTexture(FBP.menuTexture);

		// Bar
		drawTexturedModalRect(x, y, 0, 60 + boolToInt(enabled) * 20, width / 2, height);
		drawTexturedModalRect(x + width / 2, y, 200 - width / 2, 60 + boolToInt(enabled) * 20, width / 2, height);

		// Handle
		drawTexturedModalRect(sliderPosX - 15, y, 0, 100 + handleState * 20, 15, height);
		drawTexturedModalRect(sliderPosX, y, 185, 100 + handleState * 20, 15, height);
	}

	@Override
	public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
		if (GuiUtils.isMouseInsideStadium(mouseX, mouseY, x, y, width, 15) && enabled)
			dragging = true;

		return false;
	}
}
