package dev.redstudio.fbp.compat;

import net.minecraft.util.math.BlockPos;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class CeleritasDynamicLightsHandler {
    private static MethodHandle getInstanceHandle;
    private static MethodHandle getDynamicLightLevelHandle;

    static {
        try {
            Class<?> cls = Class.forName("toni.sodiumdynamiclights.SodiumDynamicLights");

            getInstanceHandle = MethodHandles.lookup().findStatic(
                    cls, "get", MethodType.methodType(cls)
            );

            getDynamicLightLevelHandle = MethodHandles.lookup().findVirtual(
                    cls, "getDynamicLightLevel", MethodType.methodType(double.class, BlockPos.class)
            );

        } catch (Exception ignored) {

        }
    }

    public static int getDynamicLight(int x, int y, int z) {
        if (getInstanceHandle == null || getDynamicLightLevelHandle == null) return 0;

        try {
            Object instance = getInstanceHandle.invoke();
            double level = (double) getDynamicLightLevelHandle.invoke(instance, new BlockPos(x, y, z));
            return (int) level;
        } catch (Throwable t) {
            return 0;
        }
    }
}
