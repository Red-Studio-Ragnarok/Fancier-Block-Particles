package io.redstudioragnarok.fbp.gui;

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
}
