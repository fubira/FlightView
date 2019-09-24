package net.ironingot.flightview;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import org.dimdev.riftloader.listener.InitializationListener;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlightViewMod implements InitializationListener
{
    public static final Logger logger = LogManager.getLogger();
    private static int mode = 0;

    @Override
    public void onInitialization() {
    }

    public static void message(String s)
    {
        Minecraft.getInstance().player.sendMessage(new TextComponentString("")
            .appendSibling((new TextComponentString("[")).setStyle((new Style()).setColor(TextFormatting.GRAY)))
            .appendSibling((new TextComponentString("FlightView")).setStyle((new Style()).setColor(TextFormatting.GREEN)))
            .appendSibling((new TextComponentString("] ")).setStyle((new Style()).setColor(TextFormatting.GRAY)))
            .appendSibling((new TextComponentString(s))));
    }

    public static boolean isActive()
    {
        return mode > 0;
    }

    public static boolean isCameraChange()
    {
        return mode == 2;
    }

    public static void toggle()
    {
        mode += 1;
        mode %= 3;

        switch(FlightViewMod.mode) {
            case 0:
                FlightViewMod.message("FlightView is Disabled.");
                break;
            case 1:
                FlightViewMod.message("FlightView is Enabled.");
                break;
            case 2:
                FlightViewMod.message("FlightView is Enabled. (with Automatic Camera Change)");
                break;
        }
    }
}
