package io.redstudioragnarok.FBP.gui;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

import static io.redstudioragnarok.FBP.util.MathUtil.boolToInt;

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

		mc.getTextureManager().bindTexture(FBP.FBP_WIDGETS);

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

	boolean isMouseOverBar(int mouseX, int mouseY) {
		int X1 = x + 4;
		int X2 = x + width - 6;

		int Y1 = y + 4;
		int Y2 = y + 15;

		boolean inRectangle = mouseX > X1 && mouseX < X2 && mouseY > Y1 && mouseY <= Y2;

		boolean inCircle1 = GuiHelper.isMouseInsideCircle(mouseX, mouseY, X1, Y1 + 5, 5);
		boolean inCircle2 = GuiHelper.isMouseInsideCircle(mouseX, mouseY, X2, Y1 + 5, 5);

		return inRectangle || inCircle1 || inCircle2;
	}

	boolean isMouseOverSlider(int mouseX, int mouseY) {
		int X1 = (int) (sliderPosX - 15 + 5);
		int X2 = (int) (sliderPosX + 15 - 5);

		int Y1 = y + 4;
		int Y2 = y + 15;

		boolean inRectangle = mouseX > X1 && mouseX < X2 && mouseY > Y1 && mouseY <= Y2;

		boolean inCircle1 = GuiHelper.isMouseInsideCircle(mouseX, mouseY, X1, Y1 + 5, 5);
		boolean inCircle2 = GuiHelper.isMouseInsideCircle(mouseX, mouseY, X2, Y1 + 5, 5);

		return inRectangle || inCircle1 || inCircle2;
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return isMouseOverBar(mouseX, mouseY) || isMouseOverSlider(mouseX, mouseY);
	}
}
