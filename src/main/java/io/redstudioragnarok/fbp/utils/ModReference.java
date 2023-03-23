package io.redstudioragnarok.fbp.utils;

import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class defines constants for FBP.
 * <p>
 * They are automatically updated by Gradle on compile time, except for the name as Gradle would remove spaces.
 */
public class ModReference {

    public static final String id = "@ID@";
    public static final String name = "Fancier Block Particles";
    public static final String version = "@VERSION@";
    public static final Logger log = LogManager.getLogger(id);
    public static URI newIssueLink;

    public static final String mixinBooterVersion = Loader.instance().getIndexedModList().get("mixinbooter").getVersion();

    static {
        try {
            newIssueLink = new URI("https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/issues/new?assignees=JustDesoroxxx&labels=&template=bug_report.md&title=");
        } catch (URISyntaxException e) {
            // TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
        }
    }
}
