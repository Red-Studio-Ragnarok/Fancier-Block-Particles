package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.utils.LogUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class GuiUtils {

    /**
     * Determines if the mouse cursor is inside a stadium defined by its bounding rectangle.
     *
     * @param mouseX The x-coordinate of the mouse cursor.
     * @param mouseY The y-coordinate of the mouse cursor.
     * @param x The x-coordinate of the top-left corner of the bounding rectangle of the stadium.
     * @param y The y-coordinate of the top-left corner of the bounding rectangle of the stadium.
     * @param width The width of the bounding rectangle of the stadium.
     * @param height The height of the bounding rectangle of the stadium.
     * @return true if the mouse cursor is inside the stadium, false otherwise.
     */
    public static boolean isMouseInsideStadium(final int mouseX, final int mouseY, final int x, final int y, final int width, final int height) {
        final int middleY = height / 2;

        // Calculate the distance between the mouse and the left and right semicircle centers
        final int deltaXLeft = mouseX - (x + middleY);
        final int deltaYLeft = mouseY - (y + middleY - 1);
        final int deltaXRight = mouseX - (x + width - middleY);
        final int deltaYRight = mouseY - (y + middleY);

        // Calculate the radius and its square
        final int radius = (height - 1) / 2;
        final int radiusSquared = radius * radius;

        // Calculate the squared distances for left and right semicircle centers
        final double distanceLeftSquared = deltaXLeft * deltaXLeft + deltaYLeft * deltaYLeft;
        final double distanceRightSquared = deltaXRight * deltaXRight + deltaYRight * deltaYRight;

        // Check if the mouse is over the rectangular part of the stadium
        final boolean isOverRectangle = mouseX >= x + middleY - 2 && mouseY >= y + 1 && mouseX < x + width - middleY + 3 && mouseY < y + height;

        // Return true if the mouse is inside either of the semicircle ends or the rectangular part
        return (distanceLeftSquared <= radiusSquared || distanceRightSquared <= radiusSquared) || isOverRectangle;
    }
    
    /**
     * Converts a hexadecimal color string to its decimal equivalent.
     * The input string should start with a '#' character followed by a 6-character hexadecimal value.
     * <p>
     * If the input is invalid, the method returns the decimal value for white (16777215) and logs an error.
     *
     * @param hexColor The hexadecimal color string to convert.
     * @return The decimal equivalent of the hexadecimal color or the decimal value for white if the input is invalid.
     */
    public static int hexToDecimalColor(String hexColor) {
        try {
            // Parse the hexadecimal string (excluding the '#' character) to an integer
            return Integer.parseInt(hexColor.substring(1), 16);
        } catch (NumberFormatException numberFormatException) {
            // TODO: (Debug Mode) This should count to the problem counter

            LogUtils.printFramedError("GUI Rendering", "Hexadecimal to decimal color conversion failed", "Non critical exception falling back to white", numberFormatException.getMessage(), "At:" + numberFormatException.getStackTrace()[3].toString());

            // Return the decimal value for white
            return 16777215;
        }
    }

    /**
     * Draws a rectangle on the screen using the specified coordinates and color.
     *
     * @param x The x coordinate of the top left corner of the rectangle.
     * @param y The y coordinate of the top left corner of the rectangle.
     * @param x2 The width of the rectangle.
     * @param y2 The height of the rectangle.
     * @param rgba The color of the rectangle.
     */
    protected static void drawRectangle(final double x, final double y, final double x2, final double y2, final Color rgba) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        // Disable textures and enable blending for smooth color transitions
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();

        // Begin drawing a triangle strip with the specified vertex format
        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        // Get the red green blue and alpha components of the rgba argument
        final int red = rgba.getRed();
        final int green = rgba.getGreen();
        final int blue = rgba.getBlue();
        final int alpha = rgba.getAlpha();

        // Add vertices to form a rectangle using the given coordinates and color
        buffer.pos(x, y, 0).color(red, green, blue, alpha).endVertex();
        buffer.pos(x, y + y2, 0).color(red, green, blue, alpha).endVertex();
        buffer.pos(x + x2, y, 0).color(red, green, blue, alpha).endVertex();
        buffer.pos(x + x2, y + y2, 0).color(red, green, blue, alpha).endVertex();

        tessellator.draw();

        // Disable blending and re-enable textures after drawing
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }
}
