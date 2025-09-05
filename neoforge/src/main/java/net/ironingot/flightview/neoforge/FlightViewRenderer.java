package net.ironingot.flightview.neoforge;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import com.mojang.blaze3d.vertex.PoseStack;

public class FlightViewRenderer {
    public FlightViewRenderer() {
        NeoForge.EVENT_BUS.addListener(this::onRenderGui);
    }

    @OnlyIn(Dist.CLIENT)
    public void onRenderGui(RenderGuiEvent.Post event) {
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
        int color = ARGB.color(224, 224, 224);
        double left = (double)stack.getDamageValue() / stack.getMaxDamage();
        if (left > 0.95D)
            color = ARGB.color(224, 64, 64);
        else if (left > 0.9D)
            color = ARGB.color(224, 128, 64);
        return color;
    }

    protected int renderFlightInfo(GuiGraphics drawContext, PoseStack matrixStack, int x, int y, Player player, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        List<String> flightInfoString = getFlightInfoString(player);
        String elytraInfoString = getElytraInfoString(stack);

        int backgroundColor = mc.options.getBackgroundColor(0.4f);
        final boolean hasElytra = stack.getItem() == Items.ELYTRA;
        final int lineHeight = 10;
        final int areaWidth = flightInfoString.stream().map(mc.font::width).max(Integer::compare).get();
        final int areaHeight = lineHeight * flightInfoString.size() + (hasElytra ? (int)(lineHeight * 1.5) : 0);

        drawContext.pose().pushMatrix();
        drawContext.fill(x - 2, y - 2, x + areaWidth + 2, y + areaHeight + 2, backgroundColor);

        flightInfoString.forEach(s -> drawContext.drawString(mc.font, s, x, y + lineHeight * flightInfoString.indexOf(s), ARGB.color(224, 224, 224), false));

        if (hasElytra) {
            int px = x + 20;
            int py = (int)(y + flightInfoString.size() * lineHeight + lineHeight * 0.5f);
            drawContext.drawString(mc.font, elytraInfoString, px, py, getElytraInfoColor(stack), false);
            drawElytraIcon(drawContext, x, y + flightInfoString.size() * lineHeight, stack);
        }

        drawContext.pose().popMatrix();

        return areaHeight;
    }

    protected void drawElytraIcon(GuiGraphics drawContext, int x, int y, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();

        drawContext.renderItem(stack, x, y);
        drawContext.renderItemDecorations(mc.font, stack, x, y);
    }
}
