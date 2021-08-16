package net.ironingot.flightview.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@Mod(FlightViewMod.modId)
public class FlightViewMod {
    public static final Logger logger = LogManager.getLogger();

    public static final String modId = "flightview";
    public static final String buildId = "2019-6";
    public static String modVersion;

    public static final KeyMapping KEYBINDING_MODE = new KeyMapping("flightview.keybinding.desc.toggle",
            GLFW.GLFW_KEY_V, "flightview.keybinding.category");

    public FlightViewMod() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, KeyInputEvent.class, this::onKeyInput);

        FlightViewConfig.register(ModLoadingContext.get());

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
    public void onKeyInput(KeyInputEvent event) {
        if (KEYBINDING_MODE.consumeClick()) {
            toggle();
            showModStateMessage(FlightViewConfig.mode.get());
        }
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
        mc.player.sendMessage(
                new TextComponent("").append(new TextComponent("[").withStyle(ChatFormatting.GRAY))
                        .append(new TextComponent("FlightView").withStyle(ChatFormatting.GREEN))
                        .append(new TextComponent("] ").withStyle(ChatFormatting.GRAY)).append(new TextComponent(s)),
                UUID.randomUUID());
    }

    public static boolean isActive() {
        return FlightViewConfig.mode.get() > 0;
    }

    public static boolean isCameraChange() {
        return FlightViewConfig.mode.get() == 2;
    }

    public static void toggle() {
        int mode = (FlightViewConfig.mode.get() + 1) % 3;

        FlightViewConfig.mode.set(mode);
    }
}
