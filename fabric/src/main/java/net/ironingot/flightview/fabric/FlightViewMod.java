package net.ironingot.flightview.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import org.lwjgl.glfw.GLFW;

public class FlightViewMod implements ClientModInitializer {
    public static FabricConfig config;

    @Override
    public void onInitializeClient() {
        config = FabricConfig.register();

        KeyMapping KEYBINDING_MODE = KeyBindingHelper.registerKeyBinding(
                new KeyMapping("flightview.keybinding.desc.toggle", GLFW.GLFW_KEY_V, "flightview.keybinding.category"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (KEYBINDING_MODE.consumeClick()) {
                toggle();
                showModStateMessage(config.mode);
            }
        });

        new WorldRenderLastEventListener();
        new FlightViewRenderer();
    }

    public static void showModStateMessage(int mode) {
        switch (mode) {
            case 0:
                message("Disabled");
                break;
            case 1:
                message("HUD mode");
                break;
            case 2:
                message("HUD + AutoCamera mode");
                break;
        }
    }


    public static void message(String s) {
        Minecraft mc = Minecraft.getInstance();
        mc.player.sendSystemMessage(
            Component.Serializer.fromJson("[\"\",{\"text\":\"[\",\"color\":\"gray\"},{\"text\":\"FlightView\",\"color\":\"dark_green\"},{\"text\":\"]\",\"color\":\"gray\"},{\"text\":\" " + s + "\"}]")
        );
    }

    public static boolean isActive() {
        return FlightViewMod.config.mode > 0;
    }

    public static boolean isCameraChange() {
        return FlightViewMod.config.mode == 2;
    }

    public static void toggle() {
        FlightViewMod.config.mode = (config.mode + 1) % 3;
        FlightViewMod.config.save();
    }
}
