package net.ironingot.flightview;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import org.dimdev.rift.listener.client.OverlayRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldRenderLastEventListener implements OverlayRenderer
{
    private static final Logger logger = LogManager.getLogger();
    private boolean isLastFlying = false;
    private int lastViewState = 0;
    private boolean isViewChanged = false;

    @Override
    public void renderOverlay() {
        EntityPlayer player = Minecraft.getInstance().player;

        if (!FlightViewMod.isActive())
            return;

        if (!isLastFlying && player.isElytraFlying()) {
            startElytraFlying();
            isLastFlying = true;
        }

        if (isLastFlying && !player.isElytraFlying()) {
            endElytraFlying();
            isLastFlying = false;
        }

        if (isLastFlying && player.isElytraFlying()) {
            updateElytraFlying(player.getTicksElytraFlying());
        }
    }

    public void startElytraFlying() {
        lastViewState = Minecraft.getInstance().gameSettings.thirdPersonView;
        isViewChanged = false;
    }

    public void endElytraFlying() {
        if (FlightViewMod.isCameraChange())
            Minecraft.getInstance().gameSettings.thirdPersonView = lastViewState;
    }

    public void updateElytraFlying(long ticks) {
        if (!isViewChanged && ticks > 15) {
            if (FlightViewMod.isCameraChange())
                Minecraft.getInstance().gameSettings.thirdPersonView = 1;
            isViewChanged = true;
        }
    }
}
