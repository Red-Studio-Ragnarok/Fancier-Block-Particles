package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.utils.LogUtils;

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

    // Todo: Javadoc, and maybe comments???
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
            LogUtils.printFramedError("GUI Rendering", "Hexadecimal to decimal color conversion failed", "Non critical exception falling back to white", numberFormatException.getMessage());

            // Return the decimal value for white
            return 16777215;
        }
    }
}
