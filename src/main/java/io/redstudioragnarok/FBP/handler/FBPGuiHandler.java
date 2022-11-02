package io.redstudioragnarok.FBP.handler;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.gui.FBPGuiNote;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FBPGuiHandler {

	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post evt) {
		if (evt.getType() != ElementType.EXPERIENCE)
			return;

		if (FBP.frozen && FBP.isEnabled())
			new FBPGuiNote();
	}
}
