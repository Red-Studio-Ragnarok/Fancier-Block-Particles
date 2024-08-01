package dev.redstudio.fbp.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions("io.redstudioragnarok.fbp.asm")
public class FBPPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(final Map<String, Object> data) {
		Mixins.addConfiguration("mixins.fbp.json");
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
