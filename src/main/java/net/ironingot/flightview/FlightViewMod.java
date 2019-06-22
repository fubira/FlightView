package net.ironingot.flightview;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;


@Mod(FlightViewMod.modId)
public class FlightViewMod
{
    public static final Logger logger = LogManager.getLogger();

    public static final String modId ="flightview";
    public static final String buildId ="2019-6";
    public static String modVersion;

    public static final KeyBinding KEYBINDING_MODE = new KeyBinding("flightview.keybinding.desc.toggle", GLFW.GLFW_KEY_V, "flightview.keybinding.category");

    private static int mode = 0;

    public FlightViewMod() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.register(this);

        modVersion = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
        FlightViewMod.logger.info("*** FlightView " + modVersion + " initialized ***");
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        new WorldRenderLastEventListener();
        new FlightViewRenderer();

        ClientRegistry.registerKeyBinding(KEYBINDING_MODE);
        logger.info("[FlightView] Initialized.");
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KEYBINDING_MODE.isPressed()) {
            toggle();
            switch(mode) {
                case 0:
                    message("FlightView is Deactivated.");
                    break;
                case 1:
                    message("FlightView is Activeated. (without Automatic Camera Change)");
                    break;
                case 2:
                    message("FlightView is Activeated. (with Automatic Camera Change)");
                    break;
            }
        }
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
    }
}
