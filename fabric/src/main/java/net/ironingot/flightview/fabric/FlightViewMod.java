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
    public static FabricConfig config;
    private static int mode = 0;

    @Override
    public void onInitializeClient() {
        config = FabricConfig.register();

        KeyMapping KEYBINDING_MODE = KeyBindingHelper.registerKeyBinding(
                new KeyMapping("flightview.keybinding.desc.toggle", GLFW.GLFW_KEY_H, "flightview.keybinding.category"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (KEYBINDING_MODE.consumeClick()) {
                toggle();
                switch (mode) {
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
        });

        new WorldRenderLastEventListener();
        new FlightViewRenderer();
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
        return mode > 0;
    }

    public static boolean isCameraChange() {
        return mode == 2;
    }

    public static void toggle() {
        mode += 1;
        mode %= 3;
    }
}
