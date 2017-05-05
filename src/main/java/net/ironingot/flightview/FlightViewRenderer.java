package net.ironingot.flightview;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.apache.logging.log4j.Logger;

public class FlightViewRenderer
{
    private static final Logger logger = FMLLog.getLogger();

    public FlightViewRenderer(Minecraft mcIn)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event)
    {
        if (!FlightView.isActive())
            return;

        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR)
        {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (itemstack.getItem() == Items.ELYTRA)
                renderElytra(16, 16, player, itemstack);
        }
    }

    protected void renderElytra(int x, int y, EntityPlayer player, ItemStack stack)
    {
        Minecraft mc = Minecraft.getMinecraft();
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.enableGUIStandardItemLighting();

        itemRenderer.renderItemAndEffectIntoGUI(player, stack, x + 16, y + 23);
        itemRenderer.renderItemOverlays(mc.fontRendererObj, stack, x + 16, y + 23);

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();

        int life = stack.getMaxDamage() - stack.getItemDamage();
        String s = "" + life;
        int color = (life < 30) ? 0xff0000 : 0xffffff;
        mc.fontRendererObj.drawStringWithShadow(s, x + 36, y + 28, color);

        Vec3d vec3d = player.getLookVec();
        double direction = Math.sqrt(vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord);

        double dx = player.posX - player.prevPosX;
        double dy = player.posY - player.prevPosY;
        double dz = player.posZ - player.prevPosZ;

        double groundSpeed = Math.sqrt(dx * dx + dz * dz);
        s = String.format("GS: %4.2f(km/h)", groundSpeed * 20.0D);
        mc.fontRendererObj.drawStringWithShadow(s, x + 0, y + 2, 0xffffff);

        double airSpeed = Math.sqrt(dx * dx + dy * dy + dz * dz);
        s = String.format("AS: %4.2f(km/h)", airSpeed * 20.0D);
        mc.fontRendererObj.drawStringWithShadow(s, x + 0, y + 15, 0xffffff);
    }

}
