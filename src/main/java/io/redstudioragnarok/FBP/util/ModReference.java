package io.redstudioragnarok.FBP.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class ModReference {

    public static final String MOD_ID = "fbp";
    public static final String MOD_NAME = "Fancier Block Particles";
    public static final String VERSION = "@VERSION@";
    public static final Logger FBP_LOG = LogManager.getLogger(MOD_ID);
    public static URI ISSUE;

    static {
        try {
            ISSUE = new URI("https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/issues/new?assignees=JustDesoroxxx&labels=&template=bug_report.md&title=");
        } catch (URISyntaxException e) {
            // TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
        }
    }
}
