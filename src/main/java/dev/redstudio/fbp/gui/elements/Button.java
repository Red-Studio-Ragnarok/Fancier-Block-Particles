package dev.redstudio.fbp.gui.elements;

import dev.redstudio.fbp.gui.GuiUtils;
import dev.redstudio.fbp.gui.InteractiveElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.HashMap;
import java.util.Map;

public class Button extends InteractiveElement {

	public enum ButtonSize {
		small,
		medium,
		large,
		guideSize
	}

	private static final Map<ButtonSize, Integer> buttonSizeIntegerMap;

	static {
		buttonSizeIntegerMap = new HashMap<>();
		buttonSizeIntegerMap.put(ButtonSize.small, 20);
		buttonSizeIntegerMap.put(ButtonSize.medium, 98);
		buttonSizeIntegerMap.put(ButtonSize.large, 200);
		buttonSizeIntegerMap.put(ButtonSize.guideSize, 225);
	}

	private final boolean toggleButton;
	private boolean toggle;

	private int offsetX;

	public Button(final int id, final int x, final int y, final ButtonSize size, final String text, final boolean toggleButton, final boolean toggleState, final boolean... disabled) {
		super(id, x, y, text, disabled);

		this.toggleButton = toggleButton;
		this.toggle = toggleState;

		width = buttonSizeIntegerMap.get(size);
		height = 20;

		if (text.equals(">>") || text.equals("∞"))
			offsetX = (height - 7) / 2;
		else if (text.equals("<<"))
			offsetX = (height - 10) / 2;
	}

	@Override
	public void update(final int mouseX, final int mouseY) {
		hovered = GuiUtils.isMouseInsideStadium(mouseX, mouseY, x, y, width, height);
	}

	@Override
	public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partialTicks) {
		startDrawing(true);

		final int hovering = getHoverState(hovered);

		if (width == 200)
			drawTexturedModalRect(x, y, 0, hovering * 20, width, height);
		else {
			drawTexturedModalRect(x, y, 0, hovering * 20, width / 2, height);
			drawTexturedModalRect(x + width / 2, y, 200 - width / 2, hovering * 20, width / 2, height);
		}

		final String textColor = enabled ? (hovered ? "#FFFFA0" : GuiUtils.WHITE) : (hovered ? "#E7E7B6" : GuiUtils.GREY);

		if (toggleButton) {
			drawString(displayString, textColor, x + 8, y + (height - 8) / 2);

			drawString(toggle ? I18n.format("menu.on") : I18n.format("menu.off"), enabled ? (toggle ? GuiUtils.GREEN : GuiUtils.RED) : (toggle ? "#6FE76F" : "#E76F6F"), x + width - 25, y + (height - 8) / 2);
		} else if (offsetX == 0)
			drawCenteredString(displayString, textColor, x + width / 2, y + (height - 8) / 2);
		else
			drawString(displayString, "#FFAA00", x + offsetX, y + (height - 8) / 2);
	}

	@Override
	public boolean mousePressed(final Minecraft mc, final int mouseX, final int mouseY) {
		if (hovered && enabled) {
			playPressSound(mc.getSoundHandler());
			toggle = !toggle;
			return true;
		} else
			return false;
	}
}
