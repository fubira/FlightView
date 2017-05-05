package net.ironingot.flightview;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.apache.logging.log4j.Logger;

public class WorldRenderLastEventListener
{
    private static final Logger logger = FMLLog.getLogger();
    private boolean isLastFlying = false;
    private int lastViewState = 0;
    private boolean isViewChanged = false;

    public WorldRenderLastEventListener()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldRenderLast(RenderWorldLastEvent event)
    {
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        if (!FlightView.isActive())
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
        lastViewState = Minecraft.getMinecraft().gameSettings.thirdPersonView;
        isViewChanged = false;
    }

    public void endElytraFlying()
    {
        Minecraft.getMinecraft().gameSettings.thirdPersonView = lastViewState;
    }

    public void updateElytraFlying(long ticks)
    {
        if (!isViewChanged && ticks > 15)
        {
            Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
            isViewChanged = true;
        }
    }
}
