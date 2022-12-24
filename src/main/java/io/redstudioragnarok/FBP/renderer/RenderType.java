package io.redstudioragnarok.FBP.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public enum RenderType {

	PARTICLE_TEXTURE {
		@Override
		public void setupRenderState() {
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
			GlStateManager.enableColorMaterial();
			GlStateManager.colorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableFog();
			Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLE_TEXTURES);
			Minecraft.getMinecraft().entityRenderer.enableLightmap();
		}

		@Override
		public void clearRenderState() {
			Minecraft.getMinecraft().entityRenderer.disableLightmap();
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.disableFog();
			GlStateManager.disableBlend();
			GlStateManager.disableColorMaterial();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		}
	},
	BLOCK_TEXTURE {
		@Override
		public void setupRenderState() {
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
			GlStateManager.enableColorMaterial();
			GlStateManager.colorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableFog();
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			Minecraft.getMinecraft().entityRenderer.enableLightmap();
		}

		@Override
		public void clearRenderState() {
			Minecraft.getMinecraft().entityRenderer.disableLightmap();
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.disableFog();
			GlStateManager.disableBlend();
			GlStateManager.disableColorMaterial();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		}
	},
	BLOCK_TEXTURE_ITEM_LIGHTING {
		@Override
		public void setupRenderState() {
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
			GlStateManager.enableColorMaterial();
			GlStateManager.colorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableFog();
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			Minecraft.getMinecraft().entityRenderer.enableLightmap();
			RenderHelper.enableStandardItemLighting();
		}

		@Override
		public void clearRenderState() {
			RenderHelper.disableStandardItemLighting();
			Minecraft.getMinecraft().entityRenderer.disableLightmap();
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.disableFog();
			GlStateManager.disableBlend();
			GlStateManager.disableColorMaterial();
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		}
	};

	private static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation("textures/particle/particles.png");

	public abstract void setupRenderState();

	public abstract void clearRenderState();

}
