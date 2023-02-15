package io.redstudioragnarok.FBP.renderer;

import io.redstudioragnarok.FBP.renderer.color.IColorProvider;
import io.redstudioragnarok.FBP.renderer.light.ILightCoordProvider;
import io.redstudioragnarok.FBP.renderer.texture.ITexCoordProvider;
import meldexun.matrixutil.Matrix3f;
import meldexun.matrixutil.Matrix4f;
import meldexun.matrixutil.MatrixStack;
import meldexun.matrixutil.MemoryUtil;
import meldexun.matrixutil.Quaternion;
import meldexun.matrixutil.UnsafeUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumFacing;
import sun.misc.Unsafe;

@SuppressWarnings("sunapi")
public class FastCubeUploader {

	private static final MatrixStack MATRIX_STACK = new MatrixStack();

	public static void putCube(BufferBuilder buffer, float x, float y, float z, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ, ITexCoordProvider texCoordProvider, IColorProvider colorProvider, ILightCoordProvider lightCoordProvider) {
		MATRIX_STACK.modelMatrix().setIdentity();
		MATRIX_STACK.normalMatrix().setIdentity();
		MATRIX_STACK.translate(x, y, z);
		Quaternion rotation = Quaternion.createRotateX((float) Math.toRadians(rotX));
		rotation.rotateZ((float) Math.toRadians(rotZ));
		rotation.rotateY((float) Math.toRadians(rotY));
		MATRIX_STACK.rotate(rotation);
		MATRIX_STACK.scale(scaleX, scaleY, scaleZ);

		putCube(buffer, MATRIX_STACK, texCoordProvider, colorProvider, lightCoordProvider);
	}

	public static void putCube(BufferBuilder buffer, MatrixStack stack, ITexCoordProvider texCoordProvider, IColorProvider colorProvider, ILightCoordProvider lightCoordProvider) {
		putCube(buffer, stack.modelMatrix(), stack.normalMatrix(), texCoordProvider, colorProvider, lightCoordProvider);
	}

	public static void putCube(BufferBuilder buffer, Matrix4f modelMatrix, Matrix3f normalMatrix, ITexCoordProvider texCoordProvider, IColorProvider colorProvider, ILightCoordProvider lightCoordProvider) {
		buffer.growBuffer(24 * 31);
		long address = MemoryUtil.getAddress(buffer.getByteBuffer()) + buffer.vertexCount * 31;
		putCube(address, modelMatrix, normalMatrix, texCoordProvider, colorProvider, lightCoordProvider);
		buffer.vertexCount += 24;
	}

	private static void putCube(long address, Matrix4f modelMatrix, Matrix3f normalMatrix, ITexCoordProvider texCoordProvider, IColorProvider colorProvider, ILightCoordProvider lightCoordProvider) {
		float v000x = modelMatrix.m03 - modelMatrix.m00 - modelMatrix.m01 - modelMatrix.m02;
		float v000y = modelMatrix.m13 - modelMatrix.m10 - modelMatrix.m11 - modelMatrix.m12;
		float v000z = modelMatrix.m23 - modelMatrix.m20 - modelMatrix.m21 - modelMatrix.m22;

		float v001x = modelMatrix.m03 - modelMatrix.m00 - modelMatrix.m01 + modelMatrix.m02;
		float v001y = modelMatrix.m13 - modelMatrix.m10 - modelMatrix.m11 + modelMatrix.m12;
		float v001z = modelMatrix.m23 - modelMatrix.m20 - modelMatrix.m21 + modelMatrix.m22;

		float v010x = modelMatrix.m03 - modelMatrix.m00 + modelMatrix.m01 - modelMatrix.m02;
		float v010y = modelMatrix.m13 - modelMatrix.m10 + modelMatrix.m11 - modelMatrix.m12;
		float v010z = modelMatrix.m23 - modelMatrix.m20 + modelMatrix.m21 - modelMatrix.m22;

		float v011x = modelMatrix.m03 - modelMatrix.m00 + modelMatrix.m01 + modelMatrix.m02;
		float v011y = modelMatrix.m13 - modelMatrix.m10 + modelMatrix.m11 + modelMatrix.m12;
		float v011z = modelMatrix.m23 - modelMatrix.m20 + modelMatrix.m21 + modelMatrix.m22;

		float v100x = modelMatrix.m03 + modelMatrix.m00 - modelMatrix.m01 - modelMatrix.m02;
		float v100y = modelMatrix.m13 + modelMatrix.m10 - modelMatrix.m11 - modelMatrix.m12;
		float v100z = modelMatrix.m23 + modelMatrix.m20 - modelMatrix.m21 - modelMatrix.m22;

		float v101x = modelMatrix.m03 + modelMatrix.m00 - modelMatrix.m01 + modelMatrix.m02;
		float v101y = modelMatrix.m13 + modelMatrix.m10 - modelMatrix.m11 + modelMatrix.m12;
		float v101z = modelMatrix.m23 + modelMatrix.m20 - modelMatrix.m21 + modelMatrix.m22;

		float v110x = modelMatrix.m03 + modelMatrix.m00 + modelMatrix.m01 - modelMatrix.m02;
		float v110y = modelMatrix.m13 + modelMatrix.m10 + modelMatrix.m11 - modelMatrix.m12;
		float v110z = modelMatrix.m23 + modelMatrix.m20 + modelMatrix.m21 - modelMatrix.m22;

		float v111x = modelMatrix.m03 + modelMatrix.m00 + modelMatrix.m01 + modelMatrix.m02;
		float v111y = modelMatrix.m13 + modelMatrix.m10 + modelMatrix.m11 + modelMatrix.m12;
		float v111z = modelMatrix.m23 + modelMatrix.m20 + modelMatrix.m21 + modelMatrix.m22;

		byte nxx = (byte) ((int) (normalMatrix.m00 * 127.0F) & 255);
		byte nxy = (byte) ((int) (normalMatrix.m10 * 127.0F) & 255);
		byte nxz = (byte) ((int) (normalMatrix.m20 * 127.0F) & 255);

		byte nyx = (byte) ((int) (normalMatrix.m01 * 127.0F) & 255);
		byte nyy = (byte) ((int) (normalMatrix.m11 * 127.0F) & 255);
		byte nyz = (byte) ((int) (normalMatrix.m21 * 127.0F) & 255);

		byte nzx = (byte) ((int) (normalMatrix.m02 * 127.0F) & 255);
		byte nzy = (byte) ((int) (normalMatrix.m12 * 127.0F) & 255);
		byte nzz = (byte) ((int) (normalMatrix.m22 * 127.0F) & 255);

		putQuad(address + 31 * 4 * 0, EnumFacing.WEST, v000x, v000y, v000z, v001x, v001y, v001z, v011x, v011y, v011z, v010x, v010y, v010z, texCoordProvider, colorProvider, lightCoordProvider, (byte) -nxx, (byte) -nxy, (byte) -nxz);
		putQuad(address + 31 * 4 * 1, EnumFacing.EAST, v101x, v101y, v101z, v100x, v100y, v100z, v110x, v110y, v110z, v111x, v111y, v111z, texCoordProvider, colorProvider, lightCoordProvider, nxx, nxy, nxz);
		putQuad(address + 31 * 4 * 2, EnumFacing.DOWN, v000x, v000y, v000z, v100x, v100y, v100z, v101x, v101y, v101z, v001x, v001y, v001z, texCoordProvider, colorProvider, lightCoordProvider, (byte) -nyx, (byte) -nyy, (byte) -nyz);
		putQuad(address + 31 * 4 * 3, EnumFacing.UP, v011x, v011y, v011z, v111x, v111y, v111z, v110x, v110y, v110z, v010x, v010y, v010z, texCoordProvider, colorProvider, lightCoordProvider, nyx, nyy, nyz);
		putQuad(address + 31 * 4 * 4, EnumFacing.NORTH, v100x, v100y, v100z, v000x, v000y, v000z, v010x, v010y, v010z, v110x, v110y, v110z, texCoordProvider, colorProvider, lightCoordProvider, (byte) -nzx, (byte) -nzy, (byte) -nzz);
		putQuad(address + 31 * 4 * 5, EnumFacing.SOUTH, v001x, v001y, v001z, v101x, v101y, v101z, v111x, v111y, v111z, v011x, v011y, v011z, texCoordProvider, colorProvider, lightCoordProvider, nzx, nzy, nzz);
	}

	private static void putQuad(long address, EnumFacing facing, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, ITexCoordProvider texCoordProvider, IColorProvider colorProvider, ILightCoordProvider lightCoordProvider, byte nx, byte ny, byte nz) {
		float u0 = texCoordProvider.u0(facing);
		float v0 = texCoordProvider.v0(facing);
		float u1 = texCoordProvider.u1(facing);
		float v1 = texCoordProvider.v1(facing);
		int color = colorProvider.getColor(facing);
		int light = lightCoordProvider.getLightCoord(facing);
		putVertex(address + 31 * 0, x0, y0, z0, u0, v1, color, light, nx, ny, nz);
		putVertex(address + 31 * 1, x1, y1, z1, u1, v1, color, light, nx, ny, nz);
		putVertex(address + 31 * 2, x2, y2, z2, u1, v0, color, light, nx, ny, nz);
		putVertex(address + 31 * 3, x3, y3, z3, u0, v0, color, light, nx, ny, nz);
	}

	private static void putVertex(long address, float x, float y, float z, float u, float v, int color, int light, byte nx, byte ny, byte nz) {
		Unsafe unsafe = UnsafeUtil.UNSAFE;
		unsafe.putFloat(address, x);
		unsafe.putFloat(address + 4, y);
		unsafe.putFloat(address + 8, z);
		unsafe.putFloat(address + 12, u);
		unsafe.putFloat(address + 16, v);
		unsafe.putInt(address + 20, color);
		unsafe.putInt(address + 24, light);
		unsafe.putByte(address + 28, nx);
		unsafe.putByte(address + 29, ny);
		unsafe.putByte(address + 30, nz);
	}

}
