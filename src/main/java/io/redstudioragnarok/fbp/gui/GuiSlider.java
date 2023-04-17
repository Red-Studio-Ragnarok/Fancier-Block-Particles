package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.utils.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

import static io.redstudioragnarok.fbp.utils.MathUtil.boolToInt;

public class GuiSlider extends GuiButton {

	public float value;
	double sliderPosX;
	double mouseGap;

	boolean dragging = false;
	boolean mouseDown = false;

	public GuiSlider(int x, int y, float inputValue) {
		super(Integer.MIN_VALUE, x, y, "");
		value = inputValue;
		width = 200;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		FontRenderer fontrenderer = mc.fontRenderer;

		int handleState = enabled ? (isMouseOverSlider(mouseX, mouseY) || dragging ? 2 : 1) : 0;

		// Draws the text
		drawCenteredString(fontrenderer, displayString, x + width / 2, y + 6 - 9, fontrenderer.getColorCode('f'));

		mc.getTextureManager().bindTexture(FBP.menuTexture);

		// Draws the slider
		drawTexturedModalRect(x, y, 0, 60 + boolToInt(enabled) * 20, width / 2, height);
		drawTexturedModalRect(x + width / 2, y, 200 - width / 2, 60 + boolToInt(enabled) * 20, width / 2, height);

		// slider
		mouseDown = Mouse.isButtonDown(0);

		if (!mouseDown && dragging) {
			dragging = false;
		}

		sliderPosX = x + (15 + value * (width - 30));

		if (dragging) {
			double max = x + width - 15;
			double min = x + 15;

			sliderPosX = MathUtil.clampMinFirst((float) (mouseX - mouseGap), (float) min, (float) max);

			double val = sliderPosX - min;

			value = MathUtil.clampMinFirst(MathUtil.absolute((float) (val / (width - 30))), 0, 1);
		}

		// Draws the slider handle
		drawTexturedModalRect((float) sliderPosX - 15, y, 0, 100 + handleState * 20, 15, height);
		drawTexturedModalRect((float) sliderPosX, y, 185, 100 + handleState * 20, 15, height);
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (!enabled)
			return false;

		if (dragging == isMouseOverSlider(mouseX, mouseY))
			mouseGap = mouseX - sliderPosX;
		else {
			if (isMouseOverBar(mouseX, mouseY)) {
				float posX = mouseX - (x + 4);

				value = MathUtil.clampMinFirst(posX / (width - 10), 0, 1);

				dragging = true;

				mouseGap = 0;
			}
		}

		return false;
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return isMouseOverBar(mouseX, mouseY) || isMouseOverSlider(mouseX, mouseY);
	}

	boolean isMouseOverBar(int mouseX, int mouseY) {
		return isMouseInsideEllipse(mouseX, mouseY, x + 4, y + 4, x + width - 6, y + 15);
	}

	boolean isMouseOverSlider(int mouseX, int mouseY) {
		return isMouseInsideEllipse(mouseX, mouseY, (int) (sliderPosX - 15 + 5), y + 4, (int) (sliderPosX + 15 - 5), y + 15);
	}

	private static boolean isMouseInsideEllipse(int mouseX, int mouseY, int x, int y, int x2, int y2) {
		final int deltaX = x - mouseX;
		final int deltaX2 = x2 - mouseX;

		final int deltaY = (y + 5) - mouseY;

		final boolean insideRectangle = mouseX > x && mouseX < x2 && mouseY > y && mouseY <= y2;
		final boolean insideLeftEllipse = deltaX * deltaX + deltaY * deltaY <= 25;
		final boolean insideRightEllipse = deltaX2 * deltaX2 + deltaY * deltaY <= 25;

		return insideRectangle || insideLeftEllipse || insideRightEllipse;
	}
}
