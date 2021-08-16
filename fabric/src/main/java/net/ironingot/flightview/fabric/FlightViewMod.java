package net.ironingot.flightview.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;

import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class FlightViewMod implements ClientModInitializer {
    public static FlightViewConfig config;

    @Override
    public void onInitializeClient() {
        config = FlightViewConfig.register();

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
                message("disabled");
                break;
            case 1:
                message("information mode");
                break;
            case 2:
                message("information + auto-camera mode");
                break;
        }
    }

    public static void message(String s) {
        Minecraft mc = Minecraft.getInstance();
        mc.player.sendMessage(
                new TextComponent("").append(new TextComponent("[").withStyle(ChatFormatting.GRAY))
                        .append(new TextComponent("FlightView").withStyle(ChatFormatting.GREEN))
                        .append(new TextComponent("] ").withStyle(ChatFormatting.GRAY)).append(new TextComponent(s)),
                UUID.randomUUID());
    }

    public static boolean isActive() {
        return config.mode > 0;
    }

    public static boolean isCameraChange() {
        return config.mode == 2;
    }

    public static void toggle() {
        config.mode = (config.mode + 1) % 3;
    }
}
