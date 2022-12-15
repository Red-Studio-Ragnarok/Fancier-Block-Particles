package io.redstudioragnarok.FBP.vector;

/**
 * Represents a two-dimensional vector with `x` and `y` coordinates.
 */
public class Vector2D {

    /** The x component of this vector. */
    public final float x;
    /** The y component of this vector. */
    public final float y;

    /**
     * Constructs a new vector with default coordinates (0, 0).
     */
    public Vector2D() {
        x = y = 0;
    }

    /**
     * Constructs a new vector with the given x and y coordinates.
     *
     * @param inputX The X coordinate
     * @param inputY The Y coordinate
     */
    public Vector2D(float inputX, float inputY) {
        x = inputX;
        y = inputY;
    }
}
