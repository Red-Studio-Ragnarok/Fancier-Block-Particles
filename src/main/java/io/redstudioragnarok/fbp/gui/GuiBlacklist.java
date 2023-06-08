package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.elements.Button;
import io.redstudioragnarok.fbp.gui.elements.ButtonBlacklist;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import io.redstudioragnarok.fbp.handlers.KeyInputHandler;
import io.redstudioragnarok.fbp.keys.KeyBindings;
import io.redstudioragnarok.fbp.models.ModelHelper;
import io.redstudioragnarok.redcore.utils.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Arrays;

import static io.redstudioragnarok.fbp.gui.elements.Button.ButtonSize.guideSize;

public class GuiBlacklist extends GuiBase {

	private static boolean hovering;
	boolean closing = false;

	ButtonBlacklist animation, particle;

	Button guide;

	final BlockPos targetBlockPos;
	final IBlockState targetBlockState;
	final Block targetBlock;
	final ItemStack targetItemStack;

	public GuiBlacklist(BlockPos target) {
		mc = FBP.mc;

		targetBlockPos = target;
		targetBlockState = mc.world.getBlockState(targetBlockPos);
		targetBlock = targetBlockState.getBlock();
		targetItemStack = targetBlock.getPickBlock(targetBlockState, mc.objectMouseOver, mc.world, targetBlockPos, mc.player);
	}

	public GuiBlacklist(ItemStack itemStack) {
		mc = FBP.mc;

		targetBlockPos = null;
		targetBlockState = Block.getStateById(Item.getIdFromItem(itemStack.getItem()));
		targetBlock = targetBlockState.getBlock();
		targetItemStack = itemStack.copy();
	}

	@Override
	public void initGui() {
		middleX = width / 2;
		middleY = height / 2;
		
		animation = new ButtonBlacklist(middleX - 130, middleY + 5, false, ConfigHandler.isBlacklisted(targetBlock, false));
		particle = new ButtonBlacklist(middleX + 70, middleY + 5, true, ConfigHandler.isBlacklisted(targetBlock, true));

		guide = new Button(-1, animation.x + 15, animation.y + 20, guideSize, (animation.enabled ? "§a<" : "§c<") + "             " + (particle.enabled ? "§a>" : "§c>"), false, false, true);

		Item item = Item.getItemFromBlock(targetBlock);
		Block block = item instanceof ItemBlock ? ((ItemBlock) item).getBlock() : null;

		animation.enabled = FBP.fancyPlaceAnim && block != null && !(block instanceof BlockDoublePlant) && ModelHelper.isModelValid(block.getDefaultState());

		this.buttonList.addAll(Arrays.asList(guide, animation, particle));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		hovering = (animation.isMouseOver() && animation.enabled) || (particle.isMouseOver() && particle.enabled);

		Mouse.setGrabbed(true);

		boolean keyReleased = false;

		if (targetBlockPos != null && (mc.objectMouseOver == null || !mc.objectMouseOver.typeOfHit.equals(RayTraceResult.Type.BLOCK) || mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock() != targetBlock)) {
			keyReleased = true;
			KeyInputHandler.onInput();
		}

		if (!Keyboard.isKeyDown(KeyBindings.blacklistGUI.getKeyCode()) || (targetBlockPos == null && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)))
			keyReleased = true;

		if (closing || keyReleased) {
			if (hovering) {
				ConfigHandler.blacklist(targetBlock, particle.isMouseOver());

				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			}

			if (keyReleased)
				KeyInputHandler.onInput();

			mc.displayGuiScreen(null);
		}
	}

	@Override
	public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks) {
		drawBackground(mouseXIn, mouseYIn);

		final int optionRadius = 30;
		mouseX = (int) MathUtil.clampMinFirst(mouseXIn, animation.x + optionRadius, particle.x + optionRadius);
		mouseY = middleY + 35;

		// Draw the title
		drawCenteredString(I18n.format("menu.blacklist.title"), "#55FF55", middleX, 20);

		drawPreview(middleX - 32, middleY - 90);

		// Draw the block name
		final ResourceLocation registryName = targetBlock.getRegistryName();
		drawCenteredString("§6§l" + registryName.getNamespace() + "§c§l:§a§l" + registryName.getPath(), "#000000", middleX, middleY - 19);

		// Draw animation related text
		if (animation.isMouseOver()) {
			drawCenteredString(I18n.format("menu.blacklist.placeAnimation"), "#FFFCFC", animation.x + 30, animation.y - 12);

			final String text = animation.enabled ? (animation.isBlacklisted ? I18n.format("menu.blacklist.remove") : I18n.format("menu.blacklist.add")) : FBP.fancyPlaceAnim ? I18n.format("menu.blacklist.cantAnimate") : I18n.format("menu.blacklist.animationDisabled");
			final String color = animation.enabled ? (animation.isBlacklisted ? "#E44444" : "#55FF55") : "#E44444";

			drawCenteredString(text, color, animation.x + 30, animation.y + 65);
		}

		// Draw particle related text
		if (particle.isMouseOver()) {
			drawCenteredString(I18n.format("menu.blacklist.particles"), "#FFFCFC", particle.x + 30, particle.y - 12);

			final String text = particle.enabled ? (particle.isBlacklisted ? I18n.format("menu.blacklist.remove") : I18n.format("menu.blacklist.add")) : I18n.format("menu.blacklist.cantAdd");
			final String color = particle.enabled? (particle.isBlacklisted ? "#E44444" : "55FF55") : "#E44444";

			drawCenteredString(text, color, particle.x + 30, particle.y + 65);
		}

		super.drawScreen(mouseX, mouseY, partialTicks);

		// Draw the cursor
		final int cursorDiameter = 20;
		drawTexturedModalRect(mouseX - cursorDiameter / 2, mouseY - cursorDiameter / 2, hovering ? 236 : 236 * 2, 236, cursorDiameter, cursorDiameter);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (animation.isMouseOver() || particle.isMouseOver())
			closing = true;
	}


	/**
	 * Draws a preview of the item on screen at the specified coordinates.
	 *
	 * @param x The x coordinate of the top left corner of the preview.
	 * @param y The y coordinate of the top left corner of the preview.
	 */
	private void drawPreview(final int x, final int y) {
		GlStateManager.enableLight(0);
		GlStateManager.translate(x, y, 0);
		GlStateManager.scale(4, 4, 4);
		GlStateManager.enableColorMaterial();

		itemRender.renderItemAndEffectIntoGUI(mc.player, targetItemStack, 0, 0);

		GlStateManager.scale(0.25, 0.25, 0.25);
		GlStateManager.translate(-x, -y, 0);
	}
}
