package net.ironingot.flightview;

import org.dimdev.rift.listener.client.KeyBindingAdder;
import org.dimdev.rift.listener.client.KeybindHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.glfw.GLFW;
import java.util.Collection;
import java.util.Arrays;

public class FlightViewKeyBinding implements KeyBindingAdder, KeybindHandler
{
    public static final KeyBinding KEYBINDING_MODE =
        new KeyBinding("flightview.keybinding.desc.toggle", GLFW.GLFW_KEY_V, "flightview.keybinding.category");

    @Override
    public Collection<? extends KeyBinding> getKeyBindings() {
        KeyBinding [] bindings = {
            KEYBINDING_MODE
        };
        return Arrays.asList(bindings);
    }

    @Override
    public void processKeybinds() {
        if (KEYBINDING_MODE.isPressed()) {
            FlightViewMod.toggle();
        }
    }
}
