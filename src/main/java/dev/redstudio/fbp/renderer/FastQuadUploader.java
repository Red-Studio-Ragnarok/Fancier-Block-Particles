package dev.redstudio.fbp.renderer;

import dev.redstudio.fbp.renderer.color.IColorProvider;
import dev.redstudio.fbp.renderer.light.ILightCoordProvider;
import dev.redstudio.fbp.renderer.texture.ITexCoordProvider;
import meldexun.matrixutil.*;
import meldexun.memoryutil.NIOBufferUtil;
import meldexun.memoryutil.UnsafeUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumFacing;
import org.lwjgl.MemoryUtil;
import sun.misc.Unsafe;

public class FastQuadUploader {

	private static final MatrixStack MATRIX_STACK = new MatrixStack();

	public static void putQuad(BufferBuilder buffer, float x, float y, float z, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ, ITexCoordProvider texCoordProvider, IColorProvider colorProvider, ILightCoordProvider lightCoordProvider) {
		MATRIX_STACK.setIdentity();

		MATRIX_STACK.translate(x, y, z);

		MATRIX_STACK.rotateY((float) Math.toRadians(rotY));
		MATRIX_STACK.rotateZ((float) Math.toRadians(rotZ));
		MATRIX_STACK.rotateX((float) Math.toRadians(rotX));

		MATRIX_STACK.scale(scaleX, scaleY, scaleZ);

		putQuad(buffer, MATRIX_STACK, texCoordProvider, colorProvider, lightCoordProvider);
	}

	public static void putQuad(BufferBuilder buffer, MatrixStack stack, ITexCoordProvider texCoordProvider, IColorProvider colorProvider, ILightCoordProvider lightCoordProvider) {
		putQuad(buffer, stack.modelMatrix(), stack.normalMatrix(), texCoordProvider, colorProvider, lightCoordProvider);
	}

	public static void putQuad(BufferBuilder buffer, Matrix4f modelMatrix, Matrix3f normalMatrix, ITexCoordProvider texCoordProvider, IColorProvider colorProvider, ILightCoordProvider lightCoordProvider) {
		buffer.growBuffer(4 * 31);
		long address = MemoryUtil.getAddress(buffer.getByteBuffer(), 0) + buffer.vertexCount * 31L;
		putQuad(address, modelMatrix, normalMatrix, texCoordProvider, colorProvider, lightCoordProvider);
		buffer.vertexCount += 4;
	}

	private static void putQuad(long address, Matrix4f modelMatrix, Matrix3f normalMatrix, ITexCoordProvider texCoordProvider, IColorProvider colorProvider, ILightCoordProvider lightCoordProvider) {
		float v001x = modelMatrix.m03 - modelMatrix.m00 - modelMatrix.m01;
		float v001y = modelMatrix.m13 - modelMatrix.m10 - modelMatrix.m11;
		float v001z = modelMatrix.m23 - modelMatrix.m20 - modelMatrix.m21;

		float v011x = modelMatrix.m03 - modelMatrix.m00 + modelMatrix.m01;
		float v011y = modelMatrix.m13 - modelMatrix.m10 + modelMatrix.m11;
		float v011z = modelMatrix.m23 - modelMatrix.m20 + modelMatrix.m21;

		float v101x = modelMatrix.m03 + modelMatrix.m00 - modelMatrix.m01;
		float v101y = modelMatrix.m13 + modelMatrix.m10 - modelMatrix.m11;
		float v101z = modelMatrix.m23 + modelMatrix.m20 - modelMatrix.m21;

		float v111x = modelMatrix.m03 + modelMatrix.m00 + modelMatrix.m01;
		float v111y = modelMatrix.m13 + modelMatrix.m10 + modelMatrix.m11;
		float v111z = modelMatrix.m23 + modelMatrix.m20 + modelMatrix.m21;

		byte nzx = (byte) ((int) (normalMatrix.m02 * 127.0F) & 255);
		byte nzy = (byte) ((int) (normalMatrix.m12 * 127.0F) & 255);
		byte nzz = (byte) ((int) (normalMatrix.m22 * 127.0F) & 255);

		putQuad(address, EnumFacing.SOUTH, v001x, v001y, v001z, v101x, v101y, v101z, v111x, v111y, v111z, v011x, v011y, v011z, texCoordProvider, colorProvider, lightCoordProvider, nzx, nzy, nzz);
	}

	private static void putQuad(long address, EnumFacing facing, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, ITexCoordProvider texCoordProvider, IColorProvider colorProvider, ILightCoordProvider lightCoordProvider, byte nx, byte ny, byte nz) {
		float u0 = texCoordProvider.u0(facing);
		float v0 = texCoordProvider.v0(facing);
		float u1 = texCoordProvider.u1(facing);
		float v1 = texCoordProvider.v1(facing);
		int color = colorProvider.getColor(facing);
		int light = lightCoordProvider.getLightCoord(facing);
		putVertex(address, x0, y0, z0, u0, v1, color, light, nx, ny, nz);
		putVertex(address + 31, x1, y1, z1, u1, v1, color, light, nx, ny, nz);
		putVertex(address + 31 * 2, x2, y2, z2, u1, v0, color, light, nx, ny, nz);
		putVertex(address + 31 * 3, x3, y3, z3, u0, v0, color, light, nx, ny, nz);
	}

	@SuppressWarnings("restriction")
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
