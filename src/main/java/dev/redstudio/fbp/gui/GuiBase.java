package dev.redstudio.fbp.gui;

import dev.redstudio.fbp.FBP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Color;

public class GuiBase extends GuiScreen {

	protected static int middleX;
	protected static int middleY;
	protected static int mouseX;
	protected static int mouseY;

	@Override
	public void updateScreen() {
		buttonList.forEach(button -> {
			if (button instanceof InteractiveElement)
				((InteractiveElement) button).update(mouseX, mouseY);
		});
	}

	@Override
	public void onGuiClosed() {
		if (FBP.MC.world != null)
			FBP.MC.entityRenderer.stopUseShader();
	}

	protected void drawBackground(final int mouseXIn, final int mouseYIn) {
		mouseX = mouseXIn;
		mouseY = mouseYIn;

		if (FBP.MC.world != null) {
			final EntityRenderer entityRenderer = FBP.MC.entityRenderer;

			final String resourceName = "minecraft:shaders/post/blur.json";
			if (entityRenderer.getShaderGroup() == null || !entityRenderer.getShaderGroup().getShaderGroupName().equals(resourceName))
				entityRenderer.loadShader(new ResourceLocation(resourceName));

			GuiUtils.drawRectangle(0, 0, width, height, new Color(0, 0, 0, 191));
		} else {
			drawBackground(0);
		}
	}

	protected void drawCenteredString(final String text, final String color, final int x, final int y) {
		fontRenderer.drawStringWithShadow(text, (x - (float) fontRenderer.getStringWidth(text) / 2), y, GuiUtils.hexToDecimalColor(color));
	}
}
