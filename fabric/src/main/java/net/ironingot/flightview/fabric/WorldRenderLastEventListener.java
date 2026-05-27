package net.ironingot.flightview.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class WorldRenderLastEventListener {
    // 飛行開始からこの tick 数を超えたら自動でカメラを切り替える
    private static final long CAMERA_SWITCH_DELAY_TICKS = 15;

    private boolean isLastFlying = false;
    private CameraType lastCameraType = null;
    private boolean isCameraChanged = false;

    public WorldRenderLastEventListener() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onWorldRenderLast);
    }

    public void onWorldRenderLast(Minecraft client) {
        if (!FlightViewMod.isActive()) {
            return;
        }

        LocalPlayer player = client.player;
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

        if (!isCameraChanged && ticks > CAMERA_SWITCH_DELAY_TICKS) {
            if (FlightViewMod.isCameraChange()) {
                mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
            }
            isCameraChanged = true;
        }
    }
}
