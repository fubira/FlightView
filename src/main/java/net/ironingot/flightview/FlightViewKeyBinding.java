package net.ironingot.flightview;

import org.dimdev.rift.listener.client.KeybindHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.glfw.GLFW;
import java.util.Collection;

public class FlightViewKeyBinding implements KeybindHandler
{
    public static final KeyBinding KEYBINDING_MODE =
        new KeyBinding("flightview.keybinding.desc.toggle", GLFW.GLFW_KEY_V, "flightview.keybinding.category");

    @Override
    public void processKeybinds() {
        if (KEYBINDING_MODE.isPressed()) {
            FlightViewMod.toggle();
        }
    }
}
