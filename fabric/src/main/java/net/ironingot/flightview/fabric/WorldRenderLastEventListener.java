package net.ironingot.flightview.fabric;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldRenderLastEventListener {
    private static final Logger logger = LogManager.getLogger();
    private boolean isLastFlying = false;
    private CameraType lastCameraType = null;
    private boolean isCameraChanged = false;

    public WorldRenderLastEventListener() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onWorldRenderLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (!FlightViewMod.isActive())
            return;

        if (!isLastFlying && player.isFallFlying()) {
            startElytraFlying();
            isLastFlying = true;
        }

        if (isLastFlying && !player.isFallFlying()) {
            endElytraFlying();
            isLastFlying = false;
        }

        if (isLastFlying && player.isFallFlying()) {
            updateElytraFlying(player.getFallFlyingTicks());
        }
    }

    public void startElytraFlying() {
        Minecraft mc = Minecraft.getInstance();

        lastCameraType = mc.options.getCameraType();
        isCameraChanged = false;
    }

    public void endElytraFlying() {
        Minecraft mc = Minecraft.getInstance();

        if (FlightViewMod.isCameraChange() && lastCameraType != null) {
            mc.options.setCameraType(lastCameraType);
        }
    }

    public void updateElytraFlying(long ticks) {
        Minecraft mc = Minecraft.getInstance();

        if (!isCameraChanged && ticks > 15) {
            if (FlightViewMod.isCameraChange()) {
                mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            }
            isCameraChanged = true;
        }
    }
}
