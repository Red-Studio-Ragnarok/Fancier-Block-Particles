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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static io.redstudioragnarok.FBP.FBP.mc;

/**
 * This class provides methods for rendering particles in a 3D environment using a BufferBuilder.
 * The class defines several static methods that can be called to render different types of particles, such as fire, smoke and general particles.
 * These methods use a BufferBuilder object and the Tessellator class to construct and draw 3D shapes made up of individual quads (four-sided polygons).
 */
public class FBPRenderer {

	public static List<Particle> queuedParticles = new ArrayList<>();

	public static boolean render;

	private static float r, g, b, a;
	private static float radsX, radsY, radsZ;

	private static final Vector3D sin = new Vector3D();
	private static final Vector3D cos = new Vector3D();

	private static Vector3D v1, v2, v3, v4, normal;

	/**
	 * Renders a particle using the given BufferBuilder.
	 *
	 * @param buffer The buffer to render the particle to
	 * @param particle The vertices of the particle
	 * @param x The x position of the particle
	 * @param y The y position of the particle
	 * @param z the z position of the particle
	 * @param scale The scale of the particle
	 * @param rotation The rotation of the particle
	 * @param brightness The brightness of the particle
	 * @param color The color of the particle
	 */
	public static void renderParticle(final BufferBuilder buffer, Vector2D[] particle, float x, float y, float z, double scale, Vector3D rotation, int brightness, Color color) {
		buffer.setTranslation(x, y, z);

		putParticle(buffer, particle, scale, rotation, brightness, color);

		buffer.setTranslation(0, 0, 0);
	}

	/**
	 * Renders a particle using the given BufferBuilder with a custom width and height.
	 *
	 * @param buffer The buffer to render the particle to
	 * @param particle The vertices of the particle
	 * @param x The x position of the particle
	 * @param y The y position of the particle
	 * @param z the z position of the particle
	 * @param width The width of the particle
	 * @param height The height of the particle
	 * @param rotation The rotation of the particle
	 * @param brightness The brightness of the particle
	 * @param color The color of the particle
	 */
	public static void renderParticleWidthHeight(final BufferBuilder buffer, Vector2D[] particle, float x, float y, float z, double width, double height, Vector3D rotation, int brightness, Color color) {
		Tessellator.getInstance().draw();
		buffer.begin(GL11.GL_QUADS, FBP.POSITION_TEX_COLOR_LMAP_NORMAL);

		RenderHelper.enableStandardItemLighting();

		buffer.setTranslation(x, y, z);

		putParticleWidthHeight(buffer, particle, width, height, rotation, brightness, color);

		buffer.setTranslation(0, 0, 0);

		Tessellator.getInstance().draw();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		RenderHelper.disableStandardItemLighting();
	}

	/**
	 * Renders a flame particle using the given BufferBuilder.
	 *
	 * @param buffer The buffer to render the particle to
	 * @param particle The shape of the particle
	 * @param x The x position of the particle
	 * @param y The y position of the particle
	 * @param z The z position of the particle
	 * @param scale The scale of the particle
	 * @param brightness The brightness of the particle
	 * @param color The color of the particle
	 * @param cube The shape of the particle as an array of Vector3D objects
	 */
	public static void renderParticleFlame(final BufferBuilder buffer, Vector2D particle, float x, float y, float z, double scale, int brightness, Color color, Vector3D[] cube) {
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
	 * Renders a smoke particle using the given BufferBuilder.
	 *
	 * @param buffer The buffer to render the particle to
	 * @param particle The shape of the particle
	 * @param x The x position of the particle
	 * @param y The y position of the particle
	 * @param z The z position of the particle
	 * @param scale The scale of the particle
	 * @param brightness The brightness of the particle
	 * @param color The color of the particle
	 * @param cube The shape of the particle as an array of Vector3D objects
	 */
	public static void renderParticleSmoke(final BufferBuilder buffer, Vector2D particle, float x, float y, float z, double scale, int brightness, Color color, Vector3D[] cube) {
		buffer.setTranslation(x, y, z);

		putParticleGas(buffer, particle,scale / 20, brightness, color, cube,0.875F);

		buffer.setTranslation(0, 0, 0);
	}

	/**
	 * Adds the vertices of a particle to the given buffer.
	 * <p>
	 * The particle is a cube with the given scale factor, and is rotated by the given angles.
	 * The brightness and color of the particle are also set.
	 *
	 * @param buffer The buffer to which the vertices of the particle will be added
	 * @param particle An array of Vector2D objects representing the texture coordinates for each face of the particle
	 * @param scale The scale factor for the particle
	 * @param rotation A Vector3D object containing the rotation angles for the particle (in degrees)
	 * @param brightness The brightness of the particle
	 * @param color The color of the particle
	 */
	static void putParticle(final BufferBuilder buffer, Vector2D[] particle, double scale, Vector3D rotation, int brightness, Color color) {
		degreesToRadians(rotation);

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			v1 = FBP.CUBE[i];
			v2 = FBP.CUBE[i + 1];
			v3 = FBP.CUBE[i + 2];
			v4 = FBP.CUBE[i + 3];

			v1 = rotateVector(v1, radsX, radsY, radsZ);
			v2 = rotateVector(v2, radsX, radsY, radsZ);
			v3 = rotateVector(v3, radsX, radsY, radsZ);
			v4 = rotateVector(v4, radsX, radsY, radsZ);

			normal = rotateVector(FBP.CUBE_NORMALS[i / 4], radsX, radsY, radsZ);

			addVertex(buffer, scale, v1, particle[0].x, particle[0].y, brightness, color, normal);
			addVertex(buffer, scale, v2, particle[1].x, particle[1].y, brightness, color, normal);
			addVertex(buffer, scale, v3, particle[2].x, particle[2].y, brightness, color, normal);
			addVertex(buffer, scale, v4, particle[3].x, particle[3].y, brightness, color, normal);
		}
	}

	/**
	 * Adds the vertices of a particle to the given buffer.
	 * <p>
	 * The particle is a cube with the given width and height, and is rotated by the given angles.
	 * The brightness and color of the particle are also set.
	 *
	 * @param buffer The buffer to which the vertices of the particle will be added
	 * @param particle An array of Vector2D objects representing the texture coordinates for each face of the particle
	 * @param width The width of the particle
	 * @param height The height of the particle
	 * @param rotation A Vector3D object containing the rotation angles for the particle (in degrees)
	 * @param brightness The brightness of the particle
	 * @param color The color of the particle
	 */
	static void putParticleWidthHeight(final BufferBuilder buffer, Vector2D[] particle, double width, double height, Vector3D rotation, int brightness, Color color) {
		degreesToRadians(rotation);

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			v1 = FBP.CUBE[i];
			v2 = FBP.CUBE[i + 1];
			v3 = FBP.CUBE[i + 2];
			v4 = FBP.CUBE[i + 3];

			v1 = rotateVector(v1, radsX, radsY, radsZ);
			v2 = rotateVector(v2, radsX, radsY, radsZ);
			v3 = rotateVector(v3, radsX, radsY, radsZ);
			v4 = rotateVector(v4, radsX, radsY, radsZ);

			normal = rotateVector(FBP.CUBE_NORMALS[i / 4], radsX, radsY, radsZ);

			addVertexWidthHeight(buffer, width, height, v1, particle[0].x, particle[0].y, brightness, color, normal);
			addVertexWidthHeight(buffer, width, height, v2, particle[1].x, particle[1].y, brightness, color, normal);
			addVertexWidthHeight(buffer, width, height, v3, particle[2].x, particle[2].y, brightness, color, normal);
			addVertexWidthHeight(buffer, width, height, v4, particle[3].x, particle[3].y, brightness, color, normal);
		}
	}

	/**
	 * Adds the vertices of a gas particle to the given buffer.
	 * <p>
	 * The particle is a cube with the given scale factor, and is rotated by the given angles.
	 * The brightness and color of the particle are also set, also the brightness is multiplied.
	 *
	 * @param buffer The buffer to which the vertices of the particle will be added
	 * @param particle A Vector2D object representing the texture coordinate for the particle
	 * @param scale The scale factor for the particle
	 * @param brightness The brightness of the particle
	 * @param color The color of the particle
	 * @param cube The shape of the particle as an array of Vector3D objects
	 * @param brightnessMultiplier The brightness multiplier for the particle
	 */
	public static void putParticleGas(final BufferBuilder buffer, Vector2D particle, double scale, int brightness, Color color, Vector3D[] cube, float brightnessMultiplier) {
		float brightnessForRender = 1;

		HexToFloats(color);

		float R, B, G;

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			v1 = cube[i];
			v2 = cube[i + 1];
			v3 = cube[i + 2];
			v4 = cube[i + 3];

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
	 * Adds a vertex to the given buffer, with the given position, texture coordinates, brightness, and color.
	 * The position of the vertex is scaled by the given scale factor.
	 *
	 * @param buffer The buffer to which the vertex will be added
	 * @param scale The scale factor for the vertex position
	 * @param position The position of the vertex
	 * @param u The U coordinate of the vertex's texture
	 * @param v The V coordinate of the vertex's texture
	 * @param brightness The brightness of the vertex
	 * @param color The color of the vertex
	 * @param normals The normal vector for the vertex
	 */
	static void addVertex(BufferBuilder buffer, double scale, Vector3D position, double u, double v, int brightness, Color color, Vector3D normals) {
		HexToFloats(color);

		if (normals == null) {
			buffer.pos(position.x * scale, position.y * scale, position.z * scale).tex(u, v).color(r, g, b, a).lightmap(brightness >> 16 & 65535, brightness & 65535).endVertex();
		} else {
			buffer.pos(position.x * scale, position.y * scale, position.z * scale).tex(u, v).color(r, g, b, a).lightmap(brightness >> 16 & 65535, brightness & 65535).normal(normals.x, normals.y, normals.z).endVertex();
		}
	}

	/**
	 * Adds a vertex to the given buffer, with the given position, texture coordinates, brightness, and color.
	 * The position of the vertex is scaled by the given width and height.
	 *
	 * @param buffer The buffer to which the vertex will be added
	 * @param width The width scale factor for the vertex position
	 * @param height The height scale factor for the vertex position
	 * @param position The position of the vertex
	 * @param u The U coordinate of the vertex's texture
	 * @param v The V coordinate of the vertex's texture
	 * @param brightness The brightness of the vertex
	 * @param color The color of the vertex
	 * @param normals The normal vector for the vertex
	 */
	static void addVertexWidthHeight(BufferBuilder buffer, double width, double height, Vector3D position, double u, double v, int brightness, Color color, Vector3D normals) {
		HexToFloats(color);

		buffer.pos(position.x * width, position.y * height, position.z * width).tex(u, v).color(r, g, b, a).lightmap(brightness >> 16 & 65535, brightness & 65535).normal(normals.x, normals.y, normals.z).endVertex();
	}

	/**
	 * Rotates the given vector by the given angles around the X, Y, and Z axes.
	 *
	 * @param vector The vector to rotate
	 * @param angleX The angle to rotate around the X axis (in degrees)
	 * @param angleY The angle to rotate around the Y axis (in degrees)
	 * @param angleZ The angle to rotate around the Z axis (in degrees)
	 * @return The rotated vector
	 */
	public static Vector3D rotateVector(Vector3D vector, float angleX, float angleY, float angleZ) {
		sin.set((float) FastMath.sinQuick(angleX), (float) FastMath.sinQuick(angleY), (float) FastMath.sinQuick(angleZ));
		cos.set((float) FastMath.cosQuick(angleX), (float) FastMath.cosQuick(angleY), (float) FastMath.cosQuick(angleZ));

		// I do not understand why a new vector is required instead of using FBPVector3d.set, but I've verified it is required for this method to work correctly
		vector = new Vector3D(vector.x * cos.y + vector.z * sin.y, vector.y, vector.x * sin.y - vector.z * cos.y);

		return vector;
	}

	/**
	 * Converts the color values from the given Color object to float values between 0 and 1, and stores them in the r, g, b, and a fields.
	 *
	 * @param inputColor The Color object to convert.
	 */
	private static void HexToFloats (Color inputColor) {
		r = (float)inputColor.getRed() / 255;
		g = (float)inputColor.getGreen() / 255;
		b = (float)inputColor.getBlue() / 255;
		a = (float)inputColor.getAlpha() / 255;
	}

	/**
	 * Converts the x, y, and z fields of a Vector3D object from degrees to radians.
	 *
	 * @param inputRotation The Vector3D object whose fields will be converted
	 */
	private static void degreesToRadians (Vector3D inputRotation) {
		radsX = (float) FastMath.toRadians(inputRotation.x);
		radsY = (float) FastMath.toRadians(inputRotation.y);
		radsZ = (float) FastMath.toRadians(inputRotation.z);
	}
}
