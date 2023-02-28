package io.redstudioragnarok.fbp.handlers;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.GuiNote;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiHandler {

	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post overlay) {
		if (overlay.getType() != ElementType.EXPERIENCE)
			return;

		if (FBP.enabled) {
			if (FBP.frozen)
				new GuiNote();

			// TODO Display Important Issues here when debug is enabled
		}

	}
}
