package io.redstudioragnarok.fbp.utils;

import io.redstudioragnarok.fbp.Tags;
import io.redstudioragnarok.redcore.logging.RedLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class defines constants for FBP.
 * <p>
 * They are automatically updated by Gradle on compile time, except for the name as Gradle would remove spaces.
 */
public class ModReference {

    public static final String ID = Tags.ID;
    public static final String NAME = "Fancier Block Particles";
    public static final String VERSION = Tags.VERSION;
    public static final Logger LOG = LogManager.getLogger(ID);
    public static final RedLogger RED_LOG = new RedLogger(NAME, "https://linkify.cz/ValkyrieBugReport", LOG, "Hang in there, just a minor bump on the road to particle greatness!");
}
