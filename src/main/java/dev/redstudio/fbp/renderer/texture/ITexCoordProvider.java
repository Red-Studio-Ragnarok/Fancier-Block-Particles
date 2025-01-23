package dev.redstudio.fbp.renderer.texture;

import net.minecraft.util.EnumFacing;

public interface ITexCoordProvider {

    float u0(EnumFacing facing);

    float v0(EnumFacing facing);

    float u1(EnumFacing facing);

    float v1(EnumFacing facing);

}
