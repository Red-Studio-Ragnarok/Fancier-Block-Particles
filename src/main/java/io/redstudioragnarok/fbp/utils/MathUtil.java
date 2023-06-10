package io.redstudioragnarok.fbp.utils;

public class MathUtil {
  
	public static float lerp(float x, float y, float t) {
		return x + (y - x) * t;
	}

	public static double lerp(double x, double y, double t) {
		return x + (y - x) * t;
	}
}
