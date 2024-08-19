package net.ironingot.flightview.forge;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

public class FlightViewRenderer {
    public FlightViewRenderer() {
        NeoForge.EVENT_BUS.addListener(this::onRenderGuiLayer);
    }

    @OnlyIn(Dist.CLIENT)
    public void onRenderGuiLayer(RenderGuiLayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();

        if (!FlightViewMod.isActive())
            return;

        LocalPlayer player = mc.player;
        ItemStack itemstack = player.getItemBySlot(EquipmentSlot.CHEST);
        renderFlightInfo(event.getGuiGraphics(), new PoseStack(), 8, 32, player, itemstack);
    }

    protected List<String> getFlightInfoString(Player player) {
        List<String> stringArray = new ArrayList<String>();
        double dx = player.position().x - player.xOld;
        double dy = player.position().y - player.yOld;
        double dz = player.position().z - player.zOld;
        double groundSpeed = Math.sqrt(dx * dx + dz * dz);
        double airSpeed = Math.sqrt(dx * dx + dy * dy + dz * dz);
        float yaw = Mth.wrapDegrees(player.getYRot());
        float pitch = Mth.wrapDegrees(player.getXRot());

        stringArray.add(String.format("GS: %.2f(km/h)", groundSpeed * 20.0D * 3.6D));
        stringArray.add(String.format("AS: %.2f(km/h)", airSpeed * 20.0D * 3.6D));
        stringArray.add(String.format("Y: %.1f P: %.1f", yaw, pitch));

        return stringArray;
    }

    protected int getElytraLife(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue() - 1;
    }

    protected String getElytraInfoString(ItemStack stack) {
        return String.format("%3d", getElytraLife(stack));
    }

    protected int getElytraInfoColor(ItemStack stack) {
        int color = 0xffffff;
        double left = (double)stack.getDamageValue() / stack.getMaxDamage();
        if (left > 0.95D)
            color = 0xff4040;
        else if (left > 0.9D)
            color = 0xff8040;
        return color;
    }

    protected int renderFlightInfo(GuiGraphics drawContext, PoseStack matrixStack, int x, int y, Player player, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        List<String> flightInfoString = getFlightInfoString(player);
        String elytraInfoString = getElytraInfoString(stack);

        final int lineHeight = 10;
        int height = 0;
        int width = 0;

        for (int i = 0; i < flightInfoString.size(); i++) {
            width = Math.max(mc.font.width(flightInfoString.get(i)), width);
        }
        height = flightInfoString.size() * lineHeight;
        height += lineHeight * 1.6;

        // drawContext.fill(RenderType.guiOverlay(), x - 2, y - 2, x + width + 2, y + height + 2, 0x60000000);
        for (int i = 0; i < flightInfoString.size(); i++) {
            drawContext.drawString(mc.font, flightInfoString.get(i), x, y + lineHeight * i, 0xffffff, true);
        }

        if (stack.getItem() == Items.ELYTRA) {
            int px = x + 20;
            int py = (int)(y + flightInfoString.size() * lineHeight + lineHeight * 0.5f);
            drawContext.drawString(mc.font, elytraInfoString, px, py, getElytraInfoColor(stack), true);
            drawElytraIcon(drawContext, x, y + flightInfoString.size() * lineHeight, stack);
        }

        return height;
    }

    protected void drawElytraIcon(GuiGraphics drawContext, int x, int y, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();

        mc.getProfiler().push("FlightView");
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);

        drawContext.renderItem(stack, x, y);
        drawContext.renderItemDecorations(mc.font, stack, x, y);

        mc.getProfiler().pop();
        RenderSystem.disableBlend();
    }
}
