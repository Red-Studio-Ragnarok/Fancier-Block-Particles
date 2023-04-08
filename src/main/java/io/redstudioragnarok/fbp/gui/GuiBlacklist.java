package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import io.redstudioragnarok.fbp.handlers.KeyInputHandler;
import io.redstudioragnarok.fbp.keys.KeyBindings;
import io.redstudioragnarok.fbp.models.ModelHelper;
import io.redstudioragnarok.fbp.utils.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Arrays;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.small;

public class GuiBlacklist extends GuiScreen {

	GuiButtonBlacklist animation, particle;

	final BlockPos selectedPos;
	final IBlockState selectedBlock;

	ItemStack displayItemStack;

	boolean closing = false;

	public GuiBlacklist(BlockPos selected) {
		selectedPos = selected;
		IBlockState state = FBP.mc.world.getBlockState(selectedPos);

		selectedBlock = state.getBlock() == FBP.dummyBlock ? FBP.dummyBlock.blockNodes.get(selectedPos).state : state;

		ItemStack is = selectedBlock.getActualState(FBP.mc.world, selectedPos).getBlock().getPickBlock(selectedBlock, FBP.mc.objectMouseOver, FBP.mc.world, selectedPos, FBP.mc.player);

		TileEntity te = FBP.mc.world.getTileEntity(selectedPos);

		try {
			if (te != null)
				FBP.mc.storeTEInStack(is, te);
		} catch (Throwable t) {
			// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
		}

		displayItemStack = is.copy();
	}

	public GuiBlacklist(ItemStack is) {
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

		animation = new GuiButtonBlacklist(0, this.width / 2 - 100 - 30, this.height / 2 - 30 + 35, "", false, ConfigHandler.isBlacklisted(selectedBlock.getBlock(), false));
		particle = new GuiButtonBlacklist(1, this.width / 2 + 100 - 30, this.height / 2 - 30 + 35, "", true, ConfigHandler.isBlacklisted(selectedBlock.getBlock(), true));

		Item ib = Item.getItemFromBlock(selectedBlock.getBlock());
		Block b = ib instanceof ItemBlock ? ((ItemBlock) ib).getBlock() : null;

		animation.enabled = b != null && !(b instanceof BlockDoublePlant) && ModelHelper.isModelValid(b.getDefaultState());
		particle.enabled = selectedBlock.getBlock() != Blocks.REDSTONE_BLOCK;

		FBPGuiButton guide = new FBPGuiButton(-1, animation.x + 30, animation.y + 30 - 10, small, (animation.enabled ? "§a<" : "§c<") + "             " + (particle.enabled ? "§a>" : "§c>"), false, false, true);
		guide.enabled = false;

		this.buttonList.addAll(Arrays.asList(guide, animation, particle));
	}

	@Override
	public void updateScreen() {
		Mouse.setGrabbed(true);

		boolean keyUp = false;

		if (selectedPos != null && (FBP.mc.objectMouseOver == null || !FBP.mc.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.BLOCK) || FBP.mc.world.getBlockState(FBP.mc.objectMouseOver.getBlockPos()).getBlock() != selectedBlock.getBlock() && FBP.mc.world.getBlockState(FBP.mc.objectMouseOver.getBlockPos()).getBlock() != FBP.dummyBlock)) {
			keyUp = true;
			KeyInputHandler.onInput();
		}
		try {
			if (!Keyboard.isKeyDown(KeyBindings.blacklistGUI.getKeyCode()) || (selectedPos == null && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
				keyUp = true;
			}
		} catch (Exception e) {
			try {
				if (!Mouse.isButtonDown(KeyBindings.blacklistGUI.getKeyCode() + 100) || (selectedPos == null && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))) {
					keyUp = true;
				}
			} catch (Exception e1) {
				closing = true;
				// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			}
		}

		if (closing || keyUp) {
			Block b = selectedBlock.getBlock();

			GuiButton selected = animation.isMouseOver() ? animation : (particle.isMouseOver() ? particle : null);

			if (selected != null) {
				boolean isParticle = particle.isMouseOver();

				if (selected.enabled) {
					if (!ConfigHandler.isBlacklisted(b, isParticle))
						ConfigHandler.addToBlacklist(b, isParticle);
					else
						ConfigHandler.removeFromBlacklist(b, isParticle);

					if (isParticle)
						ConfigHandler.writeParticleBlacklist();
					else
						ConfigHandler.writeAnimBlacklist();

					FBP.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				}
			}

			if (keyUp)
				KeyInputHandler.onInput();

			FBP.mc.displayGuiScreen(null);
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
		mouseX = (int) MathUtil.clampMinFirst(mouseX, animation.x + optionRadius, particle.x + optionRadius);
		mouseY = height / 2 + 35;

		// RENDER BLOCK
		int x = width / 2 - 32;
		int y = height / 2 - 30 - 60;

		GlStateManager.enableDepth();
		GlStateManager.enableLight(0);
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(4, 4, 4);
		GlStateManager.enableColorMaterial();
		this.itemRender.renderItemAndEffectIntoGUI(FBP.mc.player, displayItemStack, 0, 0);

		this.itemRender.zLevel = 0.0F;
		this.zLevel = 0.0F;

		GlStateManager.scale(0.25, 0.25, 0.25);
		GlStateManager.translate(-x, -y, 0);

		// BLOCK INFO
		String itemName = (selectedPos == null ? displayItemStack.getItem() : selectedBlock.getBlock()).getRegistryName().toString();
		itemName = ((itemName.contains(":") ? "§6§l" : "§a§l") + itemName).replaceAll(":", "§c§l:§a§l");

		drawCenteredString(fontRenderer, itemName, width / 2, height / 2 - 19, 0);

		// EXCEPTIONS INFO
		String animationText1 = animation.enabled ? (animation.isMouseOver() ? (animation.isInExceptions ? I18n.format("menu.blacklist.remove") : I18n.format("menu.blacklist.add")) : "") : I18n.format("menu.blacklist.cantAnimate");
		String particleText1 = particle.enabled ? (particle.isMouseOver() ? (particle.isInExceptions ? I18n.format("menu.blacklist.remove") : I18n.format("menu.blacklist.add")) : "") : I18n.format("menu.blacklist.cantAdd");

		drawCenteredString(fontRenderer, animationText1, animation.x + 30, animation.y + 65, 0);
		drawCenteredString(fontRenderer, particleText1, particle.x + 30, particle.y + 65, 0);

		if (animation.isMouseOver())
			drawCenteredString(fontRenderer, I18n.format("menu.blacklist.placeAnimation"), animation.x + 30, animation.y - 12, 0);
		if (particle.isMouseOver())
			drawCenteredString(fontRenderer, I18n.format("menu.blacklist.particles"), particle.x + 30, particle.y - 12, 0);

		drawCenteredString(fontRenderer, I18n.format("menu.blacklist.title"), width / 2, 20, fontRenderer.getColorCode('a'));

		FBP.mc.getTextureManager().bindTexture(FBP.menuTexture);

		// RENDER SCREEN
		super.drawScreen(mouseX, mouseY, partialTicks);

		// RENDER MOUSE
		FBP.mc.getTextureManager().bindTexture(FBP.menuTexture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.enableBlend();

		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		GuiButton mouseOver = animation.isMouseOver() ? animation : (particle.isMouseOver() ? particle : null);

		int imageDiameter = 20;

		this.drawTexturedModalRect(mouseX - imageDiameter / 2, mouseY - imageDiameter / 2, mouseOver != null && !mouseOver.enabled ? 256 - imageDiameter * 2 : 256 - imageDiameter, 256 - imageDiameter, imageDiameter, imageDiameter);
	}
}
