package io.redstudioragnarok.FBP.handler;

import io.redstudioragnarok.FBP.FBP;
import io.redstudioragnarok.FBP.util.ModReference;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

import static io.redstudioragnarok.FBP.FBP.*;

public class DebugHandler {

    private static final String latestMixinBooter = "7.0";

    @SubscribeEvent
    public static void onDebugList(RenderGameOverlayEvent.Text event) {
        if (mc.gameSettings.showDebugInfo) {
            ArrayList<String> list = event.getLeft();

            list.remove(4);
            list.add(4, "T: " + mc.world.getDebugLoadedEntities());

            if (!list.get(list.size() - 1).equals("")) {
                list.add("");
            }

            list.add(String.format("%s<FBP>%s Fancier Block Particles version is %s, Mixin Booter is %sup to date (%s).", TextFormatting.RED, TextFormatting.RESET, ModReference.VERSION, ModReference.MIXINBOOTER_VERSION.equals(latestMixinBooter) ? "" : "not ", ModReference.MIXINBOOTER_VERSION));
            list.add(String.format("%s<FBP>%s Running on %s", TextFormatting.RED, TextFormatting.RESET, System.getProperty("java.vm.name")));
            list.add(String.format("%s<FBP>%s Running on %s, version %s", TextFormatting.RED, TextFormatting.RESET, System.getProperty("os.name"), System.getProperty("os.version")));
            list.add("");
            list.add(String.format("%s<FBP>%s FBP is %s.", TextFormatting.RED, TextFormatting.RESET, FBP.enabled ? "enabled" : "disabled"));
            list.add("");
            list.add(String.format("%s<FBP>%s (CURRENTLY BROKEN) FBP is managing %s particles", TextFormatting.RED, TextFormatting.RESET, fancyEffectRenderer.getStatistics()));
            list.add(String.format("%s<FBP>%s (CURRENTLY BROKEN) MC is managing %s particles", TextFormatting.RED, TextFormatting.RESET, originalEffectRenderer.getStatistics()));
        }
    }

    // From https://github.com/criscky/OldJavaWarning/blob/1.12.2/src/main/java/net/darkhax/oldjava/OldJavaWarning.java#L116-L132
    private static boolean isJvm64bit () {

        final String[] propertyStrings = new String[] { "sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch" };

        for (final String property : propertyStrings) {

            final String value = System.getProperty(property);

            if (value != null && value.contains("64")) {

                return true;
            }
        }

        return false;
    }
}
