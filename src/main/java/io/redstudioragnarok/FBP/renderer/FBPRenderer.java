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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static io.redstudioragnarok.FBP.FBP.mc;

/**
 * This class provides methods for rendering 3D objects using a BufferBuilder.
 */
public class FBPRenderer {

	public static boolean render = false;
	public static List<Particle> queuedParticles = new ArrayList<>();

	static float r, g, b, a;

	static Vector3D sin = new Vector3D();
	static Vector3D cos = new Vector3D();

	/**
	 * Renders a 3D particle on a screen using a BufferBuilder object.
	 *
	 * @param buffer The BufferBuilder object to use for rendering
	 * @param particle An array of 2D vectors representing the cube's vertices
	 * @param x The x coordinate of the cube's position
	 * @param y The y coordinate of the cube's position
	 * @param z The z coordinate of the cube's position
	 * @param scale The scaling factor for the cube
	 * @param rotation The rotation of the cube as a 3D vector
	 * @param brightness The brightness for the cube
	 * @param color The red component of the cube's color
	 */
	public static void renderParticle(BufferBuilder buffer, Vector2D[] particle, float x, float y, float z, double scale, Vector3D rotation, int brightness, Color color) {
		buffer.setTranslation(x, y, z);

		putParticle(buffer, particle, scale, rotation, brightness, color);

		buffer.setTranslation(0, 0, 0);
	}

	/**
	 * Renders a 3D particle on a screen using a BufferBuilder object and with the specified width and height.
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
	 */
	public static void renderParticleShadedWidthHeight(BufferBuilder buffer, Vector2D[] particle, float x, float y, float z, double width, double height, Vector3D rotation, int brightness, Color color) {
		// switch to vertex format that supports normals
		Tessellator.getInstance().draw();
		buffer.begin(GL11.GL_QUADS, FBP.POSITION_TEX_COLOR_LMAP_NORMAL);

		// some GL commands
		RenderHelper.enableStandardItemLighting();

		// render particle
		buffer.setTranslation(x, y, z);

		putParticleWidthHeight(buffer, particle, width, height, rotation, brightness, color);

		buffer.setTranslation(0, 0, 0);

		// continue with the regular vertex format
		Tessellator.getInstance().draw();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		RenderHelper.disableStandardItemLighting();
	}

	/**
	 * Renders a 3D particle for flame on a screen using a BufferBuilder object.
	 *
	 * @param buffer The BufferBuilder object to use for rendering
	 * @param particle A 2D vector representing the flame's vertices
	 * @param x The x coordinate of the cube's position
	 * @param y The y coordinate of the cube's position
	 * @param z The z coordinate of the cube's position
	 * @param scale The scaling factor for the flame
	 * @param brightness The brightness of the flame
	 * @param cube The vertices of the cube on which the flame is being rendered
	 */
	public static void renderParticleFlame(BufferBuilder buffer, Vector2D particle, float x, float y, float z, double scale, int brightness, Color color, Vector3D[] cube) {
		Tessellator.getInstance().draw();
		mc.getRenderManager().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		buffer.setTranslation(x, y, z);

		putParticleGas(buffer, particle,scale / 80, brightness, color, cube, 0.95F);

		buffer.setTranslation(0, 0, 0);

		Tessellator.getInstance().draw();
		mc.getTextureManager().bindTexture(FBP.LOCATION_PARTICLE_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
	}

	/**
	 * Renders a 3D particle for smoke on a screen using a BufferBuilder object.
	 *
	 * @param buffer The BufferBuilder object to use for rendering
	 * @param particle A 2D vector representing the flame's vertices
	 * @param x The x coordinate of the cube's position
	 * @param y The y coordinate of the cube's position
	 * @param z The z coordinate of the cube's position
	 * @param scale The scaling factor for the flame
	 * @param brightness The brightness of the flame
	 * @param cube The vertices of the cube on which the flame is being rendered
	 */
	public static void renderParticleSmoke(BufferBuilder buffer, Vector2D particle, float x, float y, float z, double scale, int brightness, Color color, Vector3D[] cube) {
		buffer.setTranslation(x, y, z);

		putParticleGas(buffer, particle,scale / 20, brightness, color, cube,0.875F);

		buffer.setTranslation(0, 0, 0);
	}

	/**
	 * Adds the vertices for a particle to the given buffer, with the specified scale, rotation, light levels, color, and texture coordinates.
	 *
	 * @param buffer The buffer to add the vertices to
	 * @param particle An array of texture coordinates for the cube
	 * @param scale The scale to apply to the vertices
	 * @param rotation The rotation to apply to the vertices
	 * @param brightness The brightness for the vertices
	 */
	static void putParticle(BufferBuilder buffer, Vector2D[] particle, double scale, Vector3D rotation, int brightness, Color color) {
		float radsX = (float) Math.toRadians(rotation.x);
		float radsY = (float) Math.toRadians(rotation.y);
		float radsZ = (float) Math.toRadians(rotation.z);

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			Vector3D v1 = FBP.CUBE[i];
			Vector3D v2 = FBP.CUBE[i + 1];
			Vector3D v3 = FBP.CUBE[i + 2];
			Vector3D v4 = FBP.CUBE[i + 3];

			v1 = rotateVector(v1, radsX, radsY, radsZ);
			v2 = rotateVector(v2, radsX, radsY, radsZ);
			v3 = rotateVector(v3, radsX, radsY, radsZ);
			v4 = rotateVector(v4, radsX, radsY, radsZ);

			Vector3D normal = rotateVector(FBP.CUBE_NORMALS[i / 4], radsX, radsY, radsZ);

			addVertex(buffer, scale, v1, particle[0].x, particle[0].y, brightness, color, normal);
			addVertex(buffer, scale, v2, particle[1].x, particle[1].y, brightness, color, normal);
			addVertex(buffer, scale, v3, particle[2].x, particle[2].y, brightness, color, normal);
			addVertex(buffer, scale, v4, particle[3].x, particle[3].y, brightness, color, normal);
		}
	}

	/**
	 * Adds the vertices for a particle to the given buffer, with the specified width, height, rotation, light levels, color, and texture coordinates.
	 *
	 * @param buffer The buffer to add the vertices to
	 * @param particle An array of texture coordinates for the cube
	 * @param width The width of the cube
	 * @param height The height of the cube
	 * @param rotation The rotation to apply to the vertices
	 * @param brightness The brightness for the vertices
	 */
	static void putParticleWidthHeight(BufferBuilder buffer, Vector2D[] particle, double width, double height, Vector3D rotation, int brightness, Color color) {
		float radsX = (float) Math.toRadians(rotation.x);
		float radsY = (float) Math.toRadians(rotation.y);
		float radsZ = (float) Math.toRadians(rotation.z);

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			Vector3D v1 = FBP.CUBE[i];
			Vector3D v2 = FBP.CUBE[i + 1];
			Vector3D v3 = FBP.CUBE[i + 2];
			Vector3D v4 = FBP.CUBE[i + 3];

			v1 = rotateVector(v1, radsX, radsY, radsZ);
			v2 = rotateVector(v2, radsX, radsY, radsZ);
			v3 = rotateVector(v3, radsX, radsY, radsZ);
			v4 = rotateVector(v4, radsX, radsY, radsZ);

			Vector3D normal = rotateVector(FBP.CUBE_NORMALS[i / 4], radsX, radsY, radsZ);

			addVertexWidthHeight(buffer, width, height, v1, particle[0].x, particle[0].y, brightness, color, normal);
			addVertexWidthHeight(buffer, width, height, v2, particle[1].x, particle[1].y, brightness, color, normal);
			addVertexWidthHeight(buffer, width, height, v3, particle[2].x, particle[2].y, brightness, color, normal);
			addVertexWidthHeight(buffer, width, height, v4, particle[3].x, particle[3].y, brightness, color, normal);
		}
	}

	/**
	 * Adds the vertices for a gas particle to the given buffer, with the specified scale, light levels, color, texture coordinates, and brightness multiplier.
	 *
	 * @param buffer The buffer to add the vertices to
	 * @param particle The texture coordinates for the cube
	 * @param scale The scale to apply to the vertices
	 * @param brightness The brightness for the vertices
	 * @param cube The vertices of the cube on which the gas is being rendered
	 * @param brightnessMultiplier The brightness multiplier to apply to the color of each set of four vertices
	 */
	public static void putParticleGas(BufferBuilder buffer, Vector2D particle, double scale, int brightness, Color color, Vector3D[] cube, float brightnessMultiplier) {
		float brightnessForRender = 1;

		r = (float)color.getRed() / 255;
		g = (float)color.getGreen() / 255;
		b = (float)color.getBlue() / 255;
		a = (float)color.getAlpha() / 255;

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

			color = new Color(R, G, B, a);

			addVertex(buffer, scale, v1, particle.x, particle.y, brightness, color, null);
			addVertex(buffer, scale, v2, particle.x, particle.y, brightness, color, null);
			addVertex(buffer, scale, v3, particle.x, particle.y, brightness, color, null);
			addVertex(buffer, scale, v4, particle.x, particle.y, brightness, color, null);
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
	 * @param normals the normal vector for the vertex, or null if not specified
	 */
	static void addVertex(BufferBuilder buffer, double scale, Vector3D position, double u, double v, int brightness, Color color, Vector3D normals) {
		r = (float)color.getRed() / 255;
		g = (float)color.getGreen() / 255;
		b = (float)color.getBlue() / 255;
		a = (float)color.getAlpha() / 255;

		if (normals == null) {
			buffer.pos(position.x * scale, position.y * scale, position.z * scale).tex(u, v).color(r, g, b, a).lightmap(brightness >> 16 & 65535, brightness & 65535).endVertex();
		} else {
			buffer.pos(position.x * scale, position.y * scale, position.z * scale).tex(u, v).color(r, g, b, a).lightmap(brightness >> 16 & 65535, brightness & 65535).normal(normals.x, normals.y, normals.z).endVertex();
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
	 * @param normals The normal vector.
	 */
	static void addVertexWidthHeight(BufferBuilder buffer, double width, double height, Vector3D position, double u, double v, int brightness, Color color, Vector3D normals) {
		r = (float)color.getRed() / 255;
		g = (float)color.getGreen() / 255;
		b = (float)color.getBlue() / 255;
		a = (float)color.getAlpha() / 255;

		buffer.pos(position.x * width, position.y * height, position.z * width).tex(u, v).color(r, g, b, a).lightmap(brightness >> 16 & 65535, brightness & 65535).normal(normals.x, normals.y, normals.z).endVertex();
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
	public static Vector3D rotateVector(Vector3D vector, float angleX, float angleY, float angleZ) {
		sin.set((float) FastMath.sinQuick(angleX), (float) FastMath.sinQuick(angleY), (float) FastMath.sinQuick(angleZ));
		cos.set((float) FastMath.cosQuick(angleX), (float) FastMath.cosQuick(angleY), (float) FastMath.cosQuick(angleZ));

		// I do not understand why a new vector is required instead of using FBPVector3d.set, but I've verified it is required for this method to work correctly
		vector = new Vector3D(vector.x * cos.y + vector.z * sin.y, vector.y, vector.x * sin.y - vector.z * cos.y);

		return vector;
	}
}
