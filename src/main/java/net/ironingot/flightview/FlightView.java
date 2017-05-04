package net.ironingot.flightview;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

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
    private static boolean isActive = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(keybinding);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if (FlightView.isKeyDown())
            toggle();
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

    public static boolean isActive()
    {
        return isActive;
    }

    public static void toggle()
    {
        isActive = isActive ? false : true;
    }
}
