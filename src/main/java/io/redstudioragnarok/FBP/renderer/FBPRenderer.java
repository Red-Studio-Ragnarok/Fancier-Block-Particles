package io.redstudioragnarok.FBP.renderer;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.vector.Vector2D;
import io.redstudioragnarok.FBP.vector.Vector3D;
import net.jafama.FastMath;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static io.redstudioragnarok.FBP.FBP.mc;

/**
 * This class provides methods for rendering 3D objects using a BufferBuilder.
 */
public class FBPRenderer {

	public static boolean render = false;
	public static List<Particle> queuedParticles = new ArrayList<>();

	static Vector3D sin = new Vector3D();
	static Vector3D cos = new Vector3D();

	/**
	 * Renders a 3D cube on a screen using a BufferBuilder object.
	 *
	 * @param buffer The BufferBuilder object to use for rendering
	 * @param particle An array of 2D vectors representing the cube's vertices
	 * @param x The x coordinate of the cube's position
	 * @param y The y coordinate of the cube's position
	 * @param z The z coordinate of the cube's position
	 * @param scale The scaling factor for the cube
	 * @param rotation The rotation of the cube as a 3D vector
	 * @param brightness The brightness for the cube
	 * @param r The red component of the cube's color
	 * @param g The green component of the cube's color
	 * @param b The blue component of the cube's color
	 * @param alpha The alpha value of the cube's color
	 */
	public static void renderCubeShaded(BufferBuilder buffer, Vector2D[] particle, float x, float y, float z, double scale, Vector3D rotation, int brightness, float r, float g, float b, float alpha) {
		buffer.setTranslation(x, y, z);

		putCube(buffer, particle, scale, rotation, brightness, r, g, b, alpha);

		buffer.setTranslation(0, 0, 0);
	}

	/**
	 * Renders a 3D cube on a screen using a BufferBuilder object and with the specified width and height.
	 *
	 * @param buffer The BufferBuilder object to use for rendering
	 * @param particle An array of 2D vectors representing the cube's vertices
	 * @param x The x coordinate of the cube's position
	 * @param y The y coordinate of the cube's position
	 * @param z The z coordinate of the cube's position
	 * @param width The width of the cube
	 * @param height The height of the cube
	 * @param rotation The rotation of the cube as a 3D vector
	 * @param brightness The brightness for the cube
	 * @param r The red component of the cube's color
	 * @param g The green component of the cube's color
	 * @param b The blue component of the cube's color
	 * @param alpha The alpha value of the cube's color
	 */
	public static void renderCubeShadedWidthHeight(BufferBuilder buffer, Vector2D[] particle, float x, float y, float z, double width, double height, Vector3D rotation, int brightness, float r, float g, float b, float alpha) {
		// switch to vertex format that supports normals
		Tessellator.getInstance().draw();
		buffer.begin(GL11.GL_QUADS, FBP.POSITION_TEX_COLOR_LMAP_NORMAL);

		// some GL commands
		RenderHelper.enableStandardItemLighting();

		// render particle
		buffer.setTranslation(x, y, z);

		putCubeWidthHeight(buffer, particle, width, height, rotation, brightness, r, g, b, alpha);

		buffer.setTranslation(0, 0, 0);

		// continue with the regular vertex format
		Tessellator.getInstance().draw();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		RenderHelper.disableStandardItemLighting();
	}

	/**
	 * Renders a 3D cube for flame on a screen using a BufferBuilder object.
	 *
	 * @param buffer The BufferBuilder object to use for rendering
	 * @param particle A 2D vector representing the flame's vertices
	 * @param x The x coordinate of the cube's position
	 * @param y The y coordinate of the cube's position
	 * @param z The z coordinate of the cube's position
	 * @param scale The scaling factor for the flame
	 * @param brightness The brightness of the flame
	 * @param r The red component of the flame's color
	 * @param g The green component of the flame's color
	 * @param b The blue component of the flame's color
	 * @param alpha The alpha value of the flame's color
	 * @param cube The vertices of the cube on which the flame is being rendered
	 */
	public static void renderCubeFlame(BufferBuilder buffer, Vector2D particle, float x, float y, float z, double scale, int brightness, float r, float g, float b, float alpha, Vector3D[] cube) {
		Tessellator.getInstance().draw();
		mc.getRenderManager().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		buffer.setTranslation(x, y, z);

		putCubeGas(buffer, particle,scale / 80, brightness, r, g, b, alpha, cube, 0.95F);

		buffer.setTranslation(0, 0, 0);

		Tessellator.getInstance().draw();
		mc.getTextureManager().bindTexture(FBP.LOCATION_PARTICLE_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
	}

	/**
	 * Renders a 3D cube for smoke on a screen using a BufferBuilder object.
	 *
	 * @param buffer The BufferBuilder object to use for rendering
	 * @param particle A 2D vector representing the flame's vertices
	 * @param x The x coordinate of the cube's position
	 * @param y The y coordinate of the cube's position
	 * @param z The z coordinate of the cube's position
	 * @param scale The scaling factor for the flame
	 * @param brightness The brightness of the flame
	 * @param r The red component of the flame's color
	 * @param g The green component of the flame's color
	 * @param b The blue component of the flame's color
	 * @param alpha The alpha value of the flame's color
	 * @param cube The vertices of the cube on which the flame is being rendered
	 */
	public static void renderCubeSmoke(BufferBuilder buffer, Vector2D particle, float x, float y, float z, double scale, int brightness, float r, float g, float b, float alpha, Vector3D[] cube) {
		buffer.setTranslation(x, y, z);

		putCubeGas(buffer, particle,scale / 20, brightness, r, g, b, alpha, cube,0.875F);

		buffer.setTranslation(0, 0, 0);
	}

	/**
	 * Adds the vertices for a cube to the given buffer, with the specified scale, rotation, light levels, color, and texture coordinates.
	 *
	 * @param buffer The buffer to add the vertices to
	 * @param particle An array of texture coordinates for the cube
	 * @param scale The scale to apply to the vertices
	 * @param rotation The rotation to apply to the vertices
	 * @param brightness The brightness for the vertices
	 * @param r The red component of the color for the vertices
	 * @param g The green component of the color for the vertices
	 * @param b The blue component of the color for the vertices
	 * @param alpha The alpha value for the vertices
	 */
	static void putCube(BufferBuilder buffer, Vector2D[] particle, double scale, Vector3D rotation, int brightness, float r, float g, float b, float alpha) {
		float radsX = (float) Math.toRadians(rotation.x);
		float radsY = (float) Math.toRadians(rotation.y);
		float radsZ = (float) Math.toRadians(rotation.z);

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			Vector3D v1 = FBP.CUBE[i];
			Vector3D v2 = FBP.CUBE[i + 1];
			Vector3D v3 = FBP.CUBE[i + 2];
			Vector3D v4 = FBP.CUBE[i + 3];

			v1 = rotateVec(v1, radsX, radsY, radsZ);
			v2 = rotateVec(v2, radsX, radsY, radsZ);
			v3 = rotateVec(v3, radsX, radsY, radsZ);
			v4 = rotateVec(v4, radsX, radsY, radsZ);

			Vector3D normal = rotateVec(FBP.CUBE_NORMALS[i / 4], radsX, radsY, radsZ);

			addVertex(buffer, scale, v1, particle[0].x, particle[0].y, brightness, r, g, b, alpha, normal);
			addVertex(buffer, scale, v2, particle[1].x, particle[1].y, brightness, r, g, b, alpha, normal);
			addVertex(buffer, scale, v3, particle[2].x, particle[2].y, brightness, r, g, b, alpha, normal);
			addVertex(buffer, scale, v4, particle[3].x, particle[3].y, brightness, r, g, b, alpha, normal);
		}
	}

	/**
	 * Adds the vertices for a cube to the given buffer, with the specified width, height, rotation, light levels, color, and texture coordinates.
	 *
	 * @param buffer The buffer to add the vertices to
	 * @param particle An array of texture coordinates for the cube
	 * @param width The width of the cube
	 * @param height The height of the cube
	 * @param rotation The rotation to apply to the vertices
	 * @param brightness The brightness for the vertices
	 * @param r The red component of the color for the vertices
	 * @param g The green component of the color for the vertices
	 * @param b The blue component of the color for the vertices
	 * @param alpha The alpha value for the vertices
	 */
	static void putCubeWidthHeight(BufferBuilder buffer, Vector2D[] particle, double width, double height, Vector3D rotation, int brightness, float r, float g, float b, float alpha) {
		float radsX = (float) Math.toRadians(rotation.x);
		float radsY = (float) Math.toRadians(rotation.y);
		float radsZ = (float) Math.toRadians(rotation.z);

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			Vector3D v1 = FBP.CUBE[i];
			Vector3D v2 = FBP.CUBE[i + 1];
			Vector3D v3 = FBP.CUBE[i + 2];
			Vector3D v4 = FBP.CUBE[i + 3];

			v1 = rotateVec(v1, radsX, radsY, radsZ);
			v2 = rotateVec(v2, radsX, radsY, radsZ);
			v3 = rotateVec(v3, radsX, radsY, radsZ);
			v4 = rotateVec(v4, radsX, radsY, radsZ);

			Vector3D normal = rotateVec(FBP.CUBE_NORMALS[i / 4], radsX, radsY, radsZ);

			addVertexWidthHeight(buffer, width, height, v1, particle[0].x, particle[0].y, brightness, r, g, b, alpha, normal);
			addVertexWidthHeight(buffer, width, height, v2, particle[1].x, particle[1].y, brightness, r, g, b, alpha, normal);
			addVertexWidthHeight(buffer, width, height, v3, particle[2].x, particle[2].y, brightness, r, g, b, alpha, normal);
			addVertexWidthHeight(buffer, width, height, v4, particle[3].x, particle[3].y, brightness, r, g, b, alpha, normal);
		}
	}

	/**
	 * Adds the vertices for a gas cube to the given buffer, with the specified scale, light levels, color, texture coordinates, and brightness multiplier.
	 *
	 * @param buffer The buffer to add the vertices to
	 * @param particle The texture coordinates for the cube
	 * @param scale The scale to apply to the vertices
	 * @param brightness The brightness for the vertices
	 * @param r The red component of the color for the vertices
	 * @param g The green component of the color for the vertices
	 * @param b The blue component of the color for the vertices
	 * @param alpha The alpha value for the vertices
	 * @param cube The vertices of the cube on which the gas is being rendered
	 * @param brightnessMultiplier The brightness multiplier to apply to the color of each set of four vertices
	 */
	public static void putCubeGas(BufferBuilder buffer, Vector2D particle, double scale, int brightness, float r, float g, float b, float alpha, Vector3D[] cube, float brightnessMultiplier) {
		float brightnessForRender = 1;

		float R, B, G;

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			Vector3D v1 = cube[i];
			Vector3D v2 = cube[i + 1];
			Vector3D v3 = cube[i + 2];
			Vector3D v4 = cube[i + 3];

			R = r * brightnessForRender;
			G = g * brightnessForRender;
			B = b * brightnessForRender;

			brightnessForRender *= brightnessMultiplier;

			addVertex(buffer, scale, v1, particle.x, particle.y, brightness, R, G, B, alpha, null);
			addVertex(buffer, scale, v2, particle.x, particle.y, brightness, R, G, B, alpha, null);
			addVertex(buffer, scale, v3, particle.x, particle.y, brightness, R, G, B, alpha, null);
			addVertex(buffer, scale, v4, particle.x, particle.y, brightness, R, G, B, alpha, null);
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
	 * @param brightness The brightness.
	 * @param r The red color component.
	 * @param g The green color component.
	 * @param b The blue color component.
	 * @param alpha The alpha value.
	 * @param normals the normal vector for the vertex, or null if not specified
	 */
	static void addVertex(BufferBuilder buffer, double scale, Vector3D position, double u, double v, int brightness, float r, float g, float b, float alpha, Vector3D normals) {
		if (normals == null) {
			buffer.pos(position.x * scale, position.y * scale, position.z * scale).tex(u, v).color(r, g, b, alpha).lightmap(brightness >> 16 & 65535, brightness & 65535).endVertex();
		} else {
			buffer.pos(position.x * scale, position.y * scale, position.z * scale).tex(u, v).color(r, g, b, alpha).lightmap(brightness >> 16 & 65535, brightness & 65535).normal(normals.x, normals.y, normals.z).endVertex();
		}
	}

	/**
	 * Adds a vertex to the given buffer builder with the given position, texture coordinates, light levels, color, and normal.
	 * <p>
	 * The position is scaled by the given width and height values.
	 *
	 * @param buffer The buffer builder to add the vertex to.
	 * @param width The scale to apply to the X and Z coordinates of the position.
	 * @param height The scale to apply to the Y coordinate of the position.
	 * @param position The position of the vertex.
	 * @param u The U coordinate of the texture.
	 * @param v The V coordinate of the texture.
	 * @param brightness The brightness.
	 * @param r The red color component.
	 * @param g The green color component.
	 * @param b The blue color component.
	 * @param alpha The alpha value.
	 * @param normals The normal vector.
	 */
	static void addVertexWidthHeight(BufferBuilder buffer, double width, double height, Vector3D position, double u, double v, int brightness, float r, float g, float b, float alpha, Vector3D normals) {
		buffer.pos(position.x * width, position.y * height, position.z * width).tex(u, v).color(r, g, b, alpha).lightmap(brightness >> 16 & 65535, brightness & 65535).normal(normals.x, normals.y, normals.z).endVertex();
	}

	/**
	 * Rotates the given vector around the X, Y, and Z axes by the specified angles.
	 *
	 * @param vector The vector to rotate.
	 * @param angleX The angle to rotate around the X axis (in radians).
	 * @param angleY The angle to rotate around the Y axis (in radians).
	 * @param angleZ The angle to rotate around the Z axis (in radians).
	 * @return The rotated vector.
	 */
	public static Vector3D rotateVec(Vector3D vector, float angleX, float angleY, float angleZ) {
		sin.set((float) FastMath.sinQuick(angleX), (float) FastMath.sinQuick(angleY), (float) FastMath.sinQuick(angleZ));
		cos.set((float) FastMath.cosQuick(angleX), (float) FastMath.cosQuick(angleY), (float) FastMath.cosQuick(angleZ));

		// I do not understand why a new vector is required instead of using FBPVector3d.set, but I've verified it is required for this method to work correctly
		vector = new Vector3D(vector.x * cos.y + vector.z * sin.y, vector.y, vector.x * sin.y - vector.z * cos.y);

		return vector;
	}
}
