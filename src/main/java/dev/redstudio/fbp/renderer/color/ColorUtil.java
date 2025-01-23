package dev.redstudio.fbp.renderer.color;

import net.minecraft.util.EnumFacing;

public class ColorUtil {

    private static int color;
    private static final IColorProvider UNIFORM_COLOR_PROVIDER = facing -> color;
    private static final int[] colors = new int[EnumFacing.values().length];
    private static final IColorProvider PER_FACE_COLOR_PROVIDER = facing -> colors[facing.ordinal()];

    public static int intColor(float c) {
        return (int) (c * 255.0F);
    }

    public static int packColor(int red, int green, int blue, int alpha) {
        return IColorPacker.INSTANCE.pack(red, green, blue, alpha);
    }

    public static IColorProvider uniformColorProvider(float red, float green, float blue, float alpha) {
        return uniformColorProvider(intColor(red), intColor(green), intColor(blue), intColor(alpha));
    }

    public static IColorProvider uniformColorProvider(int red, int green, int blue, int alpha) {
        return uniformColorProvider(packColor(red, green, blue, alpha));
    }

    public static IColorProvider uniformColorProvider(int color) {
        ColorUtil.color = color;
        return UNIFORM_COLOR_PROVIDER;
    }

    public static IColorProvider multiplyingColorProvider(float red, float green, float blue, float alpha,
                                                          float brightnessMultiplier) {
        float f = 1.0F;
        setColor(EnumFacing.UP, red * f, green * f, blue * f, alpha);
        f *= brightnessMultiplier;
        setColor(EnumFacing.DOWN, red * f, green * f, blue * f, alpha);
        f *= brightnessMultiplier;
        setColor(EnumFacing.SOUTH, red * f, green * f, blue * f, alpha);
        f *= brightnessMultiplier;
        setColor(EnumFacing.NORTH, red * f, green * f, blue * f, alpha);
        f *= brightnessMultiplier;
        setColor(EnumFacing.WEST, red * f, green * f, blue * f, alpha);
        f *= brightnessMultiplier;
        setColor(EnumFacing.EAST, red * f, green * f, blue * f, alpha);
        return PER_FACE_COLOR_PROVIDER;
    }

    public static void setColor(EnumFacing facing, float red, float green, float blue, float alpha) {
        setColor(facing, intColor(red), intColor(green), intColor(blue), intColor(alpha));
    }

    public static void setColor(EnumFacing facing, int red, int green, int blue, int alpha) {
        setColor(facing, packColor(red, green, blue, alpha));
    }

    public static void setColor(EnumFacing facing, int color) {
        colors[facing.ordinal()] = color;
    }

    public static IColorProvider perFaceColorProvider() {
        return PER_FACE_COLOR_PROVIDER;
    }

}
