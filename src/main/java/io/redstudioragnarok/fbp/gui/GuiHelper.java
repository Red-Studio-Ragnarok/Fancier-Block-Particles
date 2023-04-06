package io.redstudioragnarok.fbp.gui;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class GuiHelper {

	public static void drawRectangle(double x, double y, double x2, double y2, int red, int green, int blue, int alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

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

	public static boolean isMouseInsideRectangle(int mouseX, int mouseY, double x, double y, double x2, double y2) {
		return mouseX > x && mouseX < x2 && mouseY > y && mouseY <= y2;
	}

	public static boolean isMouseInsideCircle(int mouseX, int mouseY, double circleX, double circleY, double radius) {
		final double x = circleX - mouseX;
		final double y = circleY - mouseY;

		return x * x + y * y <= radius * radius;
	}
}
