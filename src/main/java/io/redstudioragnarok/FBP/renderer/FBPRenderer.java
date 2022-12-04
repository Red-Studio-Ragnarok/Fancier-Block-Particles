package io.redstudioragnarok.FBP.renderer;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.vector.FBPVector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class FBPRenderer {

	public static boolean render = false;
	public static List<Particle> queuedParticles = new ArrayList<>();

	static Minecraft mc = Minecraft.getMinecraft();

	public static void renderCubeShaded_S(BufferBuilder buffer, Vec2f[] particle, float x, float y, float z, double scale, FBPVector3d rotation, int skyLight, int blockLight, float r, float g, float b, float alpha) {
		buffer.setTranslation(x, y, z);

		putCube_S(buffer, particle, scale, rotation, skyLight, blockLight, r, g, b, alpha);

		buffer.setTranslation(0, 0, 0);
	}

	public static void renderCubeShaded_WH(BufferBuilder buffer, Vec2f[] particle, float x, float y, float z, double width, double height, FBPVector3d rotation, int skyLight, int blockLight, float r, float g, float b, float alpha) {
		// switch to vertex format that supports normals
		Tessellator.getInstance().draw();
		buffer.begin(GL11.GL_QUADS, FBP.POSITION_TEX_COLOR_LMAP_NORMAL);

		// some GL commands
		RenderHelper.enableStandardItemLighting();

		// render particle
		buffer.setTranslation(x, y, z);

		putCube_WH(buffer, particle, width, height, rotation, skyLight, blockLight, r, g, b, alpha);

		buffer.setTranslation(0, 0, 0);

		// continue with the regular vertex format
		Tessellator.getInstance().draw();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		RenderHelper.disableStandardItemLighting();
	}

	public static void renderCube_F(BufferBuilder buffer, Vec2f particle, float x, float y, float z, double scale, int brightness, float r, float g, float b, float alpha, Vec3d[] cube) {
		// render particle
		GlStateManager.enableCull();

		Tessellator.getInstance().draw();
		mc.getRenderManager().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

		buffer.setTranslation(x, y, z);
		putCube_Gas(buffer, particle,scale / 80, brightness >> 16 & 65535, brightness & 65535, r, g, b, alpha, cube, 0.95F);
		buffer.setTranslation(0, 0, 0);

		Tessellator.getInstance().draw();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
	}

	public static void renderCube_Smoke(BufferBuilder buffer, Vec2f particle, float x, float y, float z, double scale, int brightness, float r, float g, float b, float alpha, Vec3d[] cube) {
		buffer.setTranslation(x, y, z);
		putCube_Gas(buffer, particle,scale / 20, brightness >> 16 & 65535, brightness & 65535, r, g, b, alpha, cube,0.875F);
		buffer.setTranslation(0, 0, 0);
	}

	static void putCube_S(BufferBuilder buffer, Vec2f[] particle, double scale, FBPVector3d rotation, int skyLight, int blockLight, float r, float g, float b, float alpha) {
		float radsX = (float) Math.toRadians(rotation.x);
		float radsY = (float) Math.toRadians(rotation.y);
		float radsZ = (float) Math.toRadians(rotation.z);

		for (int i = 0; i < FBP.CUBE.length; i += 4) {
			Vec3d v1 = FBP.CUBE[i];
			Vec3d v2 = FBP.CUBE[i + 1];
			Vec3d v3 = FBP.CUBE[i + 2];
			Vec3d v4 = FBP.CUBE[i + 3];

			Vec3d normal = rotateVec(FBP.CUBE_NORMALS[i / 4], radsX, radsY, radsZ);

			addVt_S(buffer, scale, v1, particle[0].x, particle[0].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVt_S(buffer, scale, v2, particle[1].x, particle[1].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVt_S(buffer, scale, v3, particle[2].x, particle[2].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVt_S(buffer, scale, v4, particle[3].x, particle[3].y, skyLight, blockLight, r, g, b, alpha, normal);
		}
	}

	static void putCube_WH(BufferBuilder buffer, Vec2f[] particle, double width, double height, FBPVector3d rotation, int skyLight, int blockLight, float r, float g, float b, float alpha) {
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

			addVt_WH(buffer, width, height, v1, particle[0].x, particle[0].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVt_WH(buffer, width, height, v2, particle[1].x, particle[1].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVt_WH(buffer, width, height, v3, particle[2].x, particle[2].y, skyLight, blockLight, r, g, b, alpha, normal);
			addVt_WH(buffer, width, height, v4, particle[3].x, particle[3].y, skyLight, blockLight, r, g, b, alpha, normal);
		}
	}

	public static void putCube_Gas(BufferBuilder buffer, Vec2f particle, double scale, int skyLight, int blockLight, float r, float g, float b, float alpha, Vec3d[] cube, float brightnessMultiplier) {
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

			addVt(buffer, scale, v1, particle.x, particle.y, skyLight, blockLight, R, G, B, alpha);
			addVt(buffer, scale, v2, particle.x, particle.y, skyLight, blockLight, R, G, B, alpha);
			addVt(buffer, scale, v3, particle.x, particle.y, skyLight, blockLight, R, G, B, alpha);
			addVt(buffer, scale, v4, particle.x, particle.y, skyLight, blockLight, R, G, B, alpha);
		}
	}

	static void addVt_S(BufferBuilder buffer, double scale, Vec3d position, double u, double v, int skyLight, int blockLight, float r, float g, float b, float alpha, Vec3d normals) {
		buffer.pos(position.x * scale, position.y * scale, position.z * scale).tex(u, v).color(r, g, b, alpha).lightmap(skyLight, blockLight).normal((float) normals.x, (float) normals.y, (float) normals.z).endVertex();
	}

	static void addVt_WH(BufferBuilder buffer, double width, double height, Vec3d position, double u, double v, int skyLight, int blockLight, float r, float g, float b, float a, Vec3d normals) {
		buffer.pos(position.x * width, position.y * height, position.z * width).tex(u, v).color(r, g, b, a).lightmap(skyLight, blockLight).normal((float) normals.x, (float) normals.y, (float) normals.z).endVertex();
	}

	private static void addVt(BufferBuilder buffer, double scale, Vec3d position, double u, double v, int skyLight, int blockLight, float r, float g, float b, float a) { // add vertex to buffer
		buffer.pos(position.x * scale, position.y * scale, position.z * scale).tex(u, v).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
	}

	/**
	 *Rotate vec and make it a full 3D cube.
	 */
	public static Vec3d rotateVec(Vec3d vec, float AngleX, float AngleY, float AngleZ) {
		FBPVector3d sin = new FBPVector3d(MathHelper.sin(AngleX), MathHelper.sin(AngleY), MathHelper.sin(AngleZ));
		FBPVector3d cos = new FBPVector3d(MathHelper.cos(AngleX), MathHelper.cos(AngleY), MathHelper.cos(AngleZ));

		vec = new Vec3d(vec.x * cos.y + vec.z * sin.y, vec.y, vec.x * sin.y - vec.z * cos.y);

		return vec;
	}
}
