package io.redstudioragnarok.fbp.gui;

import net.minecraft.client.gui.GuiButton;

public abstract class InteractiveElement extends GuiButton {

    public InteractiveElement(final int id, final int x, final int y, final String text) {
        super(id, x, y, text);
    }

    protected abstract void update(final int mouseX, final int mouseY);
}
