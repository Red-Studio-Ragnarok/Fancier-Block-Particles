package dev.redstudio.fbp.config;

import dev.redstudio.fbp.gui.pages.Page0;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public final class FBPConfigGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(final Minecraft minecraft) {
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(final GuiScreen parentScreen) {
        return new Page0();
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
