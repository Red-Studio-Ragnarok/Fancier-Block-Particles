package io.redstudioragnarok.fbp.utils;

import io.redstudioragnarok.fbp.Tags;
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

    public static final String id = Tags.ID;
    public static final String name = "Fancier Block Particles";
    public static final String version = Tags.VERSION;
    public static final Logger log = LogManager.getLogger(id);
    public static URI newIssueLink;

    public static final String mixinBooterVersion = Loader.instance().getIndexedModList().get("mixinbooter").getVersion();

    static {
        try {
            newIssueLink = new URI("https://linkify.cz/FancierBugReport");
        } catch (URISyntaxException e) {
            // TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
        }
    }
}
