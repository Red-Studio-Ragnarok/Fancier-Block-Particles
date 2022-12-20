package io.redstudioragnarok.FBP.gui;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.util.ModReference;
import net.jafama.FastMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

public class GuiHelper extends GuiScreen {

	public static void background(int top, int bottom, int width, int height) {
		Tessellator tessellator = Tessellator.getInstance();

		drawContainerBackground(tessellator, top, bottom, width);

		overlayBackground(0, top, width);
		overlayBackground(bottom, height, width);
	}

	public static void drawRect(double x, double y, double x2, double y2, int red, int green, int blue, int alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder BufferBuilder = tessellator.getBuffer();

		GlStateManager.disableTexture2D();

		BufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		BufferBuilder.pos(x, y + y2, 0.0D).color(red, green, blue, alpha).endVertex();
		BufferBuilder.pos(x + x2, y + y2, 0.0D).color(red, green, blue, alpha).endVertex();
		BufferBuilder.pos(x + x2, y, 0.0D).color(red, green, blue, alpha).endVertex();
		BufferBuilder.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
		tessellator.draw();

		GlStateManager.enableTexture2D();
	}

	public static void drawTitle(int y, int screenWidth, FontRenderer fr) {
		_drawCenteredString(fr, I18n.format("menu.fbp"), screenWidth / 2, y - 27, fr.getColorCode('6'));
		_drawCenteredString(fr, "\u00A7L= " + ModReference.VERSION + " =", screenWidth / 2, y - 17, fr.getColorCode('a'));

		if (!FBP.isEnabled())
			_drawCenteredString(fr, I18n.format("menu.disabled"), screenWidth / 2, y - 35, fr.getColorCode('c'));
	}

	protected static void _drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.drawStringWithShadow(text, x - fontRendererIn.getStringWidth(text) / 2.0F, y, color);
	}

	protected static void overlayBackground(int startY, int endY, int right) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder BufferBuilder = tessellator.getBuffer();

		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);

		BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		BufferBuilder.pos(0, endY, 0.0D).tex(0.0D, endY / 32.0F).color(64, 64, 64, 255).endVertex();
		BufferBuilder.pos(right, endY, 0.0D).tex(right / 32.0F, endY / 32.0F).color(64, 64, 64, 255).endVertex();
		BufferBuilder.pos(right, startY, 0.0D).tex(right / 32.0F, startY / 32.0F).color(64, 64, 64, 255).endVertex();
		BufferBuilder.pos(0, startY, 0.0D).tex(0.0D, startY / 32.0F).color(64, 64, 64, 255).endVertex();
		tessellator.draw();
	}

	protected static void drawContainerBackground(Tessellator tessellator, int top, int bottom, int right) {
		BufferBuilder buffer = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);

		buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		buffer.pos(0, bottom, 0.0D).tex(0 / 32.0F, bottom / 32.0F).color(32, 32, 32, 255).endVertex();
		buffer.pos(right, bottom, 0.0D).tex(right / 32.0F, bottom / 32.0F).color(32, 32, 32, 255).endVertex();
		buffer.pos(right, top, 0.0D).tex(right / 32.0F, top / 32.0F).color(32, 32, 32, 255).endVertex();
		buffer.pos(0, top, 0.0D).tex(0 / 32.0F, top / 32.0F).color(32, 32, 32, 255).endVertex();

		tessellator.draw();
	}

	public static boolean isMouseInsideCircle(int mouseX, int mouseY, double d, double e, double radius) {
		double X = d - mouseX;
		double Y = e - mouseY;

		return FastMath.sqrtQuick(X * X + Y * Y) <= radius;
	}
}
