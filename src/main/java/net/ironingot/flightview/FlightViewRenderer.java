package net.ironingot.flightview;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlightViewRenderer
{
    private static final Logger logger = FlightViewMod.logger;

    public FlightViewRenderer()
    {
        MinecraftForge.EVENT_BUS.register(Minecraft.getInstance());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event)
    {
        if (!FlightViewMod.isActive())
            return;

        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR)
        {
            EntityPlayerSP player = Minecraft.getInstance().player;
            ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            GuiScreen screen = Minecraft.getInstance().currentScreen;

            renderFlightInfo(8, 32, player, itemstack);
        }
    }

    protected List<String> getFlightInfoString(EntityPlayer player)
    {
        List<String> stringArray = new ArrayList<String>();
        double dx = player.posX - player.prevPosX;
        double dy = player.posY - player.prevPosY;
        double dz = player.posZ - player.prevPosZ;
        double groundSpeed = Math.sqrt(dx * dx + dz * dz);
        double airSpeed = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double yaw = player.rotationYaw;
        double pitch = player.rotationPitch;

        while(yaw < 0D) yaw += 360D;
        while(yaw > 360D) yaw -= 360D;

        stringArray.add(String.format("GS: %.2f(km/h)", groundSpeed * 20.0D * 3.6D));
        stringArray.add(String.format("AS: %.2f(km/h)", airSpeed * 20.0D * 3.6D));
        stringArray.add(String.format("Y: %.1f P: %.1f", yaw, pitch));

        return stringArray;
    }

    protected int getElytraLife(ItemStack stack)
    {
        return stack.getMaxDamage() - stack.getDamage();
    }

    protected String getElytraInfoString(ItemStack stack)
    {
        return String.format("%3d", getElytraLife(stack));
    }

    protected int getElytraInfoColor(ItemStack stack)
    {
        int color = 0xffffff;
        float left = stack.getDamage() / stack.getMaxDamage();
        if (left > 0.95D) color = 0xff0000;
        else if (left > 0.9D) color = 0xff8000;
        return color;
    }

    protected int renderFlightInfo(int x, int y, EntityPlayer player, ItemStack stack)
    {
        Minecraft mc = Minecraft.getInstance();
        List<String> flightInfoString = getFlightInfoString(player);
        String elytraInfoString = getElytraInfoString(stack);

        final int lineHeight = 10;
        int height = 0;
        int width = 0;

        for (int i = 0; i < flightInfoString.size(); i++) {
            width = Math.max(mc.fontRenderer.getStringWidth(flightInfoString.get(i)), width);
        }
        height = flightInfoString.size() * lineHeight;
        height += lineHeight * 1.6;

        Gui.drawRect(x - 2, y - 2, x + width + 2, y + height + 2, 0x60000000);
        for (int i = 0; i < flightInfoString.size(); i++) {
            mc.fontRenderer.drawStringWithShadow(flightInfoString.get(i), x, y + lineHeight * i, 0xffffff);
        }

        if (stack.getItem() == Items.ELYTRA) {
            mc.fontRenderer.drawStringWithShadow(elytraInfoString, x + 20, y + flightInfoString.size() * lineHeight + lineHeight * 0.5f, 0xffffff);
            drawElytraIcon(x, y + flightInfoString.size() * lineHeight, stack);
        }

        return height;
    }

    protected void drawElytraIcon(int x, int y, ItemStack stack)
    {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.enableGUIStandardItemLighting();

        itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
        itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, stack, x, y, null);

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
    }
}
