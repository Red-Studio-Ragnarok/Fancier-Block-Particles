package io.redstudioragnarok.FBP.renderer;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.vector.FBPVector3d;
import net.jafama.FastMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class FBPRenderer {

	public static boolean render = false;
	public static List<Particle> queuedParticles = new ArrayList<>();

	static Minecraft mc = Minecraft.getMinecraft();

	static FBPVector3d sin = new FBPVector3d(0, 0, 0);
	static FBPVector3d cos = new FBPVector3d(0, 0, 0);

	public static void renderCubeShaded(BufferBuilder buffer, Vec2f[] particle, float x, float y, float z, double scale, FBPVector3d rotation, int skyLight, int blockLight, float r, float g, float b, float alpha) {
		buffer.setTranslation(x, y, z);

		putCube(buffer, particle, scale, rotation, skyLight, blockLight, r, g, b, alpha);

		buffer.setTranslation(0, 0, 0);
	}

	public static void renderCubeShadedWidthHeight(BufferBuilder buffer, Vec2f[] particle, float x, float y, float z, double width, double height, FBPVector3d rotation, int skyLight, int blockLight, float r, float g, float b, float alpha) {
		// switch to vertex format that supports normals
		Tessellator.getInstance().draw();
		buffer.begin(GL11.GL_QUADS, FBP.POSITION_TEX_COLOR_LMAP_NORMAL);

		// some GL commands
		RenderHelper.enableStandardItemLighting();

		// render particle
		buffer.setTranslation(x, y, z);

		putCubeWidthHeight(buffer, particle, width, height, rotation, skyLight, blockLight, r, g, b, alpha);

		buffer.setTranslation(0, 0, 0);

		// continue with the regular vertex format
		Tessellator.getInstance().draw();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		RenderHelper.disableStandardItemLighting();
	}

	public static void renderCubeFlame(BufferBuilder buffer, Vec2f particle, float x, float y, float z, double scale, int brightness, float r, float g, float b, float alpha, Vec3d[] cube) {
		Tessellator.getInstance().draw();
		mc.getRenderManager().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		buffer.setTranslation(x, y, z);

		putCubeGas(buffer, particle,scale / 80, brightness >> 16 & 65535, brightness & 65535, r, g, b, alpha, cube, 0.95F);

		buffer.setTranslation(0, 0, 0);

		Tessellator.getInstance().draw();
		Minecraft.getMinecraft().getTextureManager().bindTexture(FBP.LOCATION_PARTICLE_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
	}

	public static void renderCubeSmoke(BufferBuilder buffer, Vec2f particle, float x, float y, float z, double scale, int brightness, float r, float g, float b, float alpha, Vec3d[] cube) {
		buffer.setTranslation(x, y, z);

		putCubeGas(buffer, particle,scale / 20, brightness >> 16 & 65535, brightness & 65535, r, g, b, alpha, cube,0.875F);

		buffer.setTranslation(0, 0, 0);
	}

	static void putCube(BufferBuilder buffer, Vec2f[] particle, double scale, FBPVector3d rotation, int skyLight, int blockLight, float r, float g, float b, float alpha) {
		float radsX = (float) Math.toRadians(rotation.x);
		float radsY = (float) Math.toRadians(rotation.y);
		float radsZ = (float) Math.toRadians(rotation.z);

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			Vec3d v1 = FBP.CUBE[i];
			Vec3d v2 = FBP.CUBE[i + 1];
			Vec3d v3 = FBP.CUBE[i + 2];
			Vec3d v4 = FBP.CUBE[i + 3];

			v1 = rotateVec(v1, radsX, radsY, radsZ);
			v2 = rotateVec(v2, radsX, radsY, radsZ);
			v3 = rotateVec(v3, radsX, radsY, radsZ);
			v4 = rotateVec(v4, radsX, radsY, radsZ);

			Vec3d normal = rotateVec(FBP.CUBE_NORMALS[i / 4], radsX, radsY, radsZ);

			addVertex(buffer, scale, v1, particle[0].x, particle[0].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVertex(buffer, scale, v2, particle[1].x, particle[1].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVertex(buffer, scale, v3, particle[2].x, particle[2].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVertex(buffer, scale, v4, particle[3].x, particle[3].y, skyLight, blockLight, r, g, b, alpha, normal);
		}
	}

	static void putCubeWidthHeight(BufferBuilder buffer, Vec2f[] particle, double width, double height, FBPVector3d rotation, int skyLight, int blockLight, float r, float g, float b, float alpha) {
		float radsX = (float) Math.toRadians(rotation.x);
		float radsY = (float) Math.toRadians(rotation.y);
		float radsZ = (float) Math.toRadians(rotation.z);

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			Vec3d v1 = FBP.CUBE[i];
			Vec3d v2 = FBP.CUBE[i + 1];
			Vec3d v3 = FBP.CUBE[i + 2];
			Vec3d v4 = FBP.CUBE[i + 3];

			v1 = rotateVec(v1, radsX, radsY, radsZ);
			v2 = rotateVec(v2, radsX, radsY, radsZ);
			v3 = rotateVec(v3, radsX, radsY, radsZ);
			v4 = rotateVec(v4, radsX, radsY, radsZ);

			Vec3d normal = rotateVec(FBP.CUBE_NORMALS[i / 4], radsX, radsY, radsZ);

			addVertexWidthHeight(buffer, width, height, v1, particle[0].x, particle[0].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVertexWidthHeight(buffer, width, height, v2, particle[1].x, particle[1].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVertexWidthHeight(buffer, width, height, v3, particle[2].x, particle[2].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVertexWidthHeight(buffer, width, height, v4, particle[3].x, particle[3].y, skyLight, blockLight, r, g, b, alpha, normal);
		}
	}

	/**
	 * This method puts a 3D cube on the screen with a particular color, brightness, and transparency.
	 *
	 * @param buffer the buffer builder used to render the cube
	 * @param particle the position of the particle that the cube represents
	 * @param scale the scale of the cube
	 * @param skyLight the amount of sky light to apply to the cube
	 * @param blockLight the amount of block light to apply to the cube
	 * @param r the red component of the color of the cube
	 * @param g the green component of the color of the cube
	 * @param b the blue component of the color of the cube
	 * @param alpha the transparency of the cube
	 * @param cube the vertices of the cube
	 * @param brightnessMultiplier the multiplier for the brightness of the cube
	 */
	public static void putCubeGas(BufferBuilder buffer, Vec2f particle, double scale, int skyLight, int blockLight, float r, float g, float b, float alpha, Vec3d[] cube, float brightnessMultiplier) {
		float brightnessForRender = 1;

		float R, B, G;

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			Vec3d v1 = cube[i];
			Vec3d v2 = cube[i + 1];
			Vec3d v3 = cube[i + 2];
			Vec3d v4 = cube[i + 3];

			R = r * brightnessForRender;
			G = g * brightnessForRender;
			B = b * brightnessForRender;

			brightnessForRender *= brightnessMultiplier;

			addVertex(buffer, scale, v1, particle.x, particle.y, skyLight, blockLight, R, G, B, alpha, null);
			addVertex(buffer, scale, v2, particle.x, particle.y, skyLight, blockLight, R, G, B, alpha, null);
			addVertex(buffer, scale, v3, particle.x, particle.y, skyLight, blockLight, R, G, B, alpha, null);
			addVertex(buffer, scale, v4, particle.x, particle.y, skyLight, blockLight, R, G, B, alpha, null);
		}
	}

	/**
	 * Adds a vertex to the given buffer builder with the given position, texture coordinates, light levels, color, and optional normal.
	 *
	 * @param buffer The buffer builder to add the vertex to.
	 * @param scale The scale to apply to the position.
	 * @param position The position of the vertex.
	 * @param u The U coordinate of the texture.
	 * @param v The V coordinate of the texture.
	 * @param skyLight The sky light level.
	 * @param blockLight The block light level.
	 * @param r The red color component.
	 * @param g The green color component.
	 * @param b The blue color component.
	 * @param alpha The alpha value.
	 * @param normals the normal vector for the vertex, or null if not specified
	 */
	static void addVertex(BufferBuilder buffer, double scale, Vec3d position, double u, double v, int skyLight, int blockLight, float r, float g, float b, float alpha, Vec3d normals) {
		if (normals == null) {
			buffer.pos(position.x * scale, position.y * scale, position.z * scale).tex(u, v).color(r, g, b, alpha).lightmap(skyLight, blockLight).endVertex();
		} else {
			buffer.pos(position.x * scale, position.y * scale, position.z * scale).tex(u, v).color(r, g, b, alpha).lightmap(skyLight, blockLight).normal((float) normals.x, (float) normals.y, (float) normals.z).endVertex();
		}
	}

	/**
	 * Adds a vertex to the given buffer builder with the given position, texture coordinates, light levels, color, and normal.
	 * The position is scaled by the given width and height values.
	 *
	 * @param buffer The buffer builder to add the vertex to.
	 * @param width The scale to apply to the X and Z coordinates of the position.
	 * @param height The scale to apply to the Y coordinate of the position.
	 * @param position The position of the vertex.
	 * @param u The U coordinate of the texture.
	 * @param v The V coordinate of the texture.
	 * @param skyLight The sky light level.
	 * @param blockLight The block light level.
	 * @param r The red color component.
	 * @param g The green color component.
	 * @param b The blue color component.
	 * @param alpha The alpha value.
	 * @param normals The normal vector.
	 */
	static void addVertexWidthHeight(BufferBuilder buffer, double width, double height, Vec3d position, double u, double v, int skyLight, int blockLight, float r, float g, float b, float alpha, Vec3d normals) {
		buffer.pos(position.x * width, position.y * height, position.z * width).tex(u, v).color(r, g, b, alpha).lightmap(skyLight, blockLight).normal((float) normals.x, (float) normals.y, (float) normals.z).endVertex();
	}

	/**
	 * Rotates the given vector around the X, Y, and Z axes by the specified angles.
	 *
	 * @param vec The vector to rotate.
	 * @param AngleX The angle to rotate around the X axis (in radians).
	 * @param AngleY The angle to rotate around the Y axis (in radians).
	 * @param AngleZ The angle to rotate around the Z axis (in radians).
	 * @return The rotated vector.
	 */
	public static Vec3d rotateVec(Vec3d vec, float AngleX, float AngleY, float AngleZ) {
		sin.set(FastMath.sinQuick(AngleX), FastMath.sinQuick(AngleY), FastMath.sinQuick(AngleZ));
		cos.set(FastMath.cosQuick(AngleX), FastMath.cosQuick(AngleY), FastMath.cosQuick(AngleZ));

		vec = new Vec3d(vec.x * cos.y + vec.z * sin.y, vec.y, vec.x * sin.y - vec.z * cos.y);

		return vec;
	}
}
