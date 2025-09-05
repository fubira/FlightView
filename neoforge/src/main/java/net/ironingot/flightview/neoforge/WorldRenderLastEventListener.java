package net.ironingot.flightview.neoforge;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class WorldRenderLastEventListener {
    private boolean isLastFlying = false;
    private CameraType lastCameraType = null;
    private boolean isCameraChanged = false;

    public WorldRenderLastEventListener() {
        NeoForge.EVENT_BUS.addListener(this::onRenderLevelStage);
    }

    @SubscribeEvent
    public void onRenderLevelStage(final RenderLevelStageEvent.AfterLevel event) {
        Minecraft mc = Minecraft.getInstance();

        if (!FlightViewMod.isActive())
            return;

        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }

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
