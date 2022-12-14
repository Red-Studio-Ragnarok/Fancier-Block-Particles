package com.TominoCZ.FBP.gui;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.handler.FBPConfigHandler;
import com.TominoCZ.FBP.handler.FBPKeyInputHandler;
import com.TominoCZ.FBP.keys.FBPKeyBindings;
import com.TominoCZ.FBP.model.FBPModelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Arrays;

public class FBPGuiBlacklist extends GuiScreen {

	FBPGuiButtonBlacklist animation, particle;

	final BlockPos selectedPos;
	final IBlockState selectedBlock;

	ItemStack displayItemStack;

	boolean closing = false;

	public FBPGuiBlacklist(BlockPos selected) {
		this.mc = Minecraft.getMinecraft();

		selectedPos = selected;
		IBlockState state = mc.world.getBlockState(selectedPos);

		selectedBlock = state.getBlock() == FBP.FBPBlock ? FBP.FBPBlock.blockNodes.get(selectedPos).state : state;

		ItemStack is = selectedBlock.getActualState(mc.world, selectedPos).getBlock().getPickBlock(selectedBlock, mc.objectMouseOver, mc.world, selectedPos, mc.player);

		TileEntity te = mc.world.getTileEntity(selectedPos);

		try {
			if (te != null)
				mc.storeTEInStack(is, te);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}

		displayItemStack = is.copy();
	}

	public FBPGuiBlacklist(ItemStack is) {
		this.mc = Minecraft.getMinecraft();

		selectedPos = null;
		selectedBlock = Block.getBlockFromName(is.getItem().getRegistryName().toString()).getStateFromMeta(is.getMetadata());

		displayItemStack = is.copy();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void initGui() {
		this.buttonList.clear();

		animation = new FBPGuiButtonBlacklist(0, this.width / 2 - 100 - 30, this.height / 2 - 30 + 35, "", false, FBP.INSTANCE.isBlacklisted(selectedBlock.getBlock(), false));
		particle = new FBPGuiButtonBlacklist(1, this.width / 2 + 100 - 30, this.height / 2 - 30 + 35, "", true, FBP.INSTANCE.isBlacklisted(selectedBlock.getBlock(), true));

		Item ib = Item.getItemFromBlock(selectedBlock.getBlock());
		Block b = ib instanceof ItemBlock ? ((ItemBlock) ib).getBlock() : null;

		animation.enabled = b != null && !(b instanceof BlockDoublePlant) && FBPModelHelper.isModelValid(b.getDefaultState());
		particle.enabled = selectedBlock.getBlock() != Blocks.REDSTONE_BLOCK;

		FBPGuiButton guide = new FBPGuiButton(-1, animation.x + 30, animation.y + 30 - 10, (animation.enabled ? "\u00A7a<" : "\u00A7c<") + "             " + (particle.enabled ? "\u00A7a>" : "\u00A7c>"), false, false, true);
		guide.enabled = false;

		this.buttonList.addAll(Arrays.asList(guide, animation, particle));
	}

	@Override
	public void updateScreen() {
		Mouse.setGrabbed(true);

		boolean keyUp = false;

		if (selectedPos != null && (mc.objectMouseOver == null || !mc.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.BLOCK) || mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() != selectedBlock.getBlock() && mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() != FBP.FBPBlock)) {
			keyUp = true;
			FBPKeyInputHandler.INSTANCE.onInput();
		}
		try {
			if (!Keyboard.isKeyDown(FBPKeyBindings.FBPFastAdd.getKeyCode()) || (selectedPos == null && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
				keyUp = true;
			}
		} catch (Exception e) {
			try {
				if (!Mouse.isButtonDown(FBPKeyBindings.FBPFastAdd.getKeyCode() + 100)
						|| (selectedPos == null && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
					keyUp = true;
				}
			} catch (Exception e1) {
				closing = true;
				e.printStackTrace();
			}
		}

		if (closing || keyUp) {
			Block b = selectedBlock.getBlock();

			GuiButton selected = animation.isMouseOver() ? animation : (particle.isMouseOver() ? particle : null);

			if (selected != null) {
				boolean isParticle = particle.isMouseOver();

				if (selected.enabled) {
					if (!FBP.INSTANCE.isBlacklisted(b, isParticle))
						FBP.INSTANCE.addToBlacklist(b, isParticle);
					else
						FBP.INSTANCE.removeFromBlacklist(b, isParticle);

					if (isParticle)
						FBPConfigHandler.writeParticleExceptions();
					else
						FBPConfigHandler.writeAnimExceptions();

					mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				}
			}

			if (keyUp)
				FBPKeyInputHandler.INSTANCE.onInput();

			mc.displayGuiScreen(null);
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		GuiButton clicked = animation.isMouseOver() ? animation : (particle.isMouseOver() ? particle : null);

		if (clicked != null && clicked.enabled)
			closing = true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		// LIMIT MOUSE POS
		int optionRadius = 30;
		mouseX = MathHelper.clamp(mouseX, animation.x + optionRadius, particle.x + optionRadius);
		mouseY = height / 2 + 35;

		// RENDER BLOCK
		int x = width / 2 - 32;
		int y = height / 2 - 30 - 60;

		GlStateManager.enableDepth();
		GlStateManager.enableLight(0);
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(4, 4, 4);
		GlStateManager.enableColorMaterial();
		this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, displayItemStack, 0, 0);

		this.itemRender.zLevel = 0.0F;
		this.zLevel = 0.0F;

		GlStateManager.scale(0.25, 0.25, 0.25);
		GlStateManager.translate(-x, -y, 0);

		// BLOCK INFO
		String itemName = (selectedPos == null ? displayItemStack.getItem() : selectedBlock.getBlock()).getRegistryName().toString();
		itemName = ((itemName.contains(":") ? "\u00A76\u00A7l" : "\u00A7a\u00A7l") + itemName).replaceAll(":", "\u00A7c\u00A7l:\u00A7a\u00A7l");

		FBPGuiHelper._drawCenteredString(fontRenderer, itemName, width / 2, height / 2 - 19, 0);

		// EXCEPTIONS INFO
		String animationText1 = animation.enabled ? (animation.isMouseOver() ? (animation.isInExceptions ? I18n.format("menu.blacklist.remove") : I18n.format("menu.blacklist.add")) : "") : I18n.format("menu.blacklist.cantanimate");
		String particleText1 = particle.enabled ? (particle.isMouseOver() ? (particle.isInExceptions ? I18n.format("menu.blacklist.remove") : I18n.format("menu.blacklist.add")) : "") : I18n.format("menu.blacklist.cantadd");

		FBPGuiHelper._drawCenteredString(fontRenderer, animationText1, animation.x + 30, animation.y + 65, 0);
		FBPGuiHelper._drawCenteredString(fontRenderer, particleText1, particle.x + 30, particle.y + 65, 0);

		if (animation.isMouseOver())
			FBPGuiHelper._drawCenteredString(fontRenderer, I18n.format("menu.blacklist.placeanimation"), animation.x + 30, animation.y - 12, 0);
		if (particle.isMouseOver())
			FBPGuiHelper._drawCenteredString(fontRenderer, I18n.format("menu.blacklist.particles"), particle.x + 30, particle.y - 12, 0);

		this.drawCenteredString(fontRenderer, I18n.format("menu.blacklist.title"), width / 2, 20, fontRenderer.getColorCode('a'));

		mc.getTextureManager().bindTexture(FBP.FBP_WIDGETS);

		// RENDER SCREEN
		super.drawScreen(mouseX, mouseY, partialTicks);

		// RENDER MOUSE
		mc.getTextureManager().bindTexture(FBP.FBP_WIDGETS);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.enableBlend();

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		GuiButton mouseOver = animation.isMouseOver() ? animation : (particle.isMouseOver() ? particle : null);

		int imageDiameter = 20;

		this.drawTexturedModalRect(mouseX - imageDiameter / 2, mouseY - imageDiameter / 2, mouseOver != null && !mouseOver.enabled ? 256 - imageDiameter * 2 : 256 - imageDiameter, 256 - imageDiameter, imageDiameter, imageDiameter);
	}
}
