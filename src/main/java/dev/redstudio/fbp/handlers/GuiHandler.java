package dev.redstudio.fbp.handlers;

import dev.redstudio.fbp.FBP;
import dev.redstudio.fbp.gui.GuiHud;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class GuiHandler {

	@SubscribeEvent
	public void onRenderGui(final RenderGameOverlayEvent.Post overlay) {
		if (overlay.getType() != ElementType.EXPERIENCE)
			return;

		if (!FBP.enabled)
			return;

		if (FBP.frozen)
			new GuiHud(I18n.format("hud.freeze"), "#44FFFF");
	}
}
