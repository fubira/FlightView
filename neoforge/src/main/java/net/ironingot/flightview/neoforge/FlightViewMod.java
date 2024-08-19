package net.ironingot.flightview.neoforge;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Mod(FlightViewMod.modId)
public class FlightViewMod {
    public static final Logger logger = LogManager.getLogger();

    public static final String modId = "flightview";
    public static final String buildId = "2019-6";
    public static String modVersion;

    public static final KeyMapping KEYBINDING_MODE = new KeyMapping("flightview.keybinding.desc.toggle",
            GLFW.GLFW_KEY_V, "flightview.keybinding.category");

    public FlightViewMod(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onRegisterKeyMappings);
        NeoForge.EVENT_BUS.addListener(this::onKeyInput);

        modContainer.registerConfig(ModConfig.Type.CLIENT, NeoForgeConfig.SPEC);

        modVersion = ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString();
        FlightViewMod.logger.info("*** FlightView " + modVersion + " initialized ***");
    }

    @OnlyIn(Dist.CLIENT)
    public void onClientSetup(FMLClientSetupEvent event) {
        new WorldRenderLastEventListener();
        new FlightViewRenderer();
        logger.info("[FlightView] Initialized.");
    }

    @OnlyIn(Dist.CLIENT)
    public void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KEYBINDING_MODE);
    }

    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(Key event) {
        if (KEYBINDING_MODE.consumeClick()) {
            toggle();
            showModStateMessage(NeoForgeConfig.mode.get());
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

        mc.player.sendSystemMessage(
            Component.Serializer.fromJson("[\"\",{\"text\":\"[\",\"color\":\"gray\"},{\"text\":\"FlightView\",\"color\":\"dark_green\"},{\"text\":\"]\",\"color\":\"gray\"},{\"text\":\" " + s + "\"}]", mc.player.registryAccess())
        );
    }

    public static boolean isActive() {
        return NeoForgeConfig.mode.get() > 0;
    }

    public static boolean isCameraChange() {
        return NeoForgeConfig.mode.get() == 2;
    }

    public static void toggle() {
        int mode = (NeoForgeConfig.mode.get() + 1) % 3;

        NeoForgeConfig.mode.set(mode);
        NeoForgeConfig.SPEC.save();
    }
}
