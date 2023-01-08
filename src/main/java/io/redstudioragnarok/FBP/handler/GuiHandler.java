package io.redstudioragnarok.FBP.handler;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.gui.GuiNote;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiHandler {

	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post overlay) {
		if (overlay.getType() != ElementType.EXPERIENCE)
			return;

		if (FBP.isEnabled()) {
			if (FBP.frozen)
				new GuiNote();

			// TODO Display Important Issues here when debug is enabled
		}

	}
}
