package net.ironingot.flightview;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.apache.logging.log4j.Logger;

@Mod(modid="flightview",
     name="FlightView",
     dependencies = "required-after:forge@[13.19.1,)",
     acceptableRemoteVersions = "*",
     acceptedMinecraftVersions = "",
     version="@VERSION@")
public class FlightView
{
    private static final Logger logger = FMLLog.getLogger();
    public static final KeyBinding keybinding = new KeyBinding("Toggle FlightView Mode", Keyboard.KEY_V, "FlightView");
    private static int mode = 0;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        new WorldRenderLastEventListener();
        new FlightViewRenderer(Minecraft.getMinecraft());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(keybinding);
        logger.info("[FlightView] Initialized.");
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if (FlightView.isKeyDown())
        {
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

    public static boolean isKeyDown()
    {
        int keyCode = keybinding.getKeyCode();
        if (keyCode > 0)
        {
            return Keyboard.isKeyDown(keyCode);
        }
        else
        {
            return Mouse.isButtonDown(100 + keyCode);
        }
    }

    public static void message(String s)
    {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("")
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
