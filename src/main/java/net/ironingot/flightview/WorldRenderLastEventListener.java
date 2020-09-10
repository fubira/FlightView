package net.ironingot.flightview;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldRenderLastEventListener
{
    private static final Logger logger = LogManager.getLogger();
    private boolean isLastFlying = false;
    private PointOfView lastViewState = null;
    private boolean isViewChanged = false;

    public WorldRenderLastEventListener()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onWorldRenderLast(RenderWorldLastEvent event)
    {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (!FlightViewMod.isActive())
            return;

        if (!isLastFlying && player.isElytraFlying())
        {
            startElytraFlying();
            isLastFlying = true;
        }

        if (isLastFlying && !player.isElytraFlying())
        {
            endElytraFlying();
            isLastFlying = false;
        }

        if (isLastFlying && player.isElytraFlying())
        {
            updateElytraFlying(player.getTicksElytraFlying());
        }
    }

    public void startElytraFlying()
    {
        lastViewState = Minecraft.getInstance().gameSettings.func_243230_g();
        isViewChanged = false;
    }

    public void endElytraFlying()
    {
        if (FlightViewMod.isCameraChange() && lastViewState != null)
            Minecraft.getInstance().gameSettings.func_243229_a(lastViewState);
    }

    public void updateElytraFlying(long ticks)
    {
        if (!isViewChanged && ticks > 15)
        {
            if (FlightViewMod.isCameraChange())
                Minecraft.getInstance().gameSettings.func_243229_a(PointOfView.FIRST_PERSON);
            isViewChanged = true;
        }
    }
}
