package net.ironingot.flightview.fabric;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

public class FlightViewRenderer
{
    public FlightViewRenderer() {
        HudRenderCallback.EVENT.register(this::onHudRender);
    }

	public void onHudRender(PoseStack matrixStack, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();

        if (!FlightViewMod.isActive())
            return;

        LocalPlayer player = mc.player;
        ItemStack itemstack = player.getItemBySlot(EquipmentSlot.CHEST);
        renderFlightInfo(matrixStack, 8, 32, player, itemstack);
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
        float left = stack.getDamageValue() / stack.getMaxDamage();
        if (left > 0.95D)
            color = 0xff0000;
        else if (left > 0.9D)
            color = 0xff8000;
        return color;
    }

    protected int renderFlightInfo(PoseStack matrixStack, int x, int y, Player player, ItemStack stack) {
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

        GuiComponent.fill(matrixStack, x - 2, y - 2, x + width + 2, y + height + 2, 0x60000000);
        for (int i = 0; i < flightInfoString.size(); i++) {
            mc.font.drawShadow(matrixStack, flightInfoString.get(i), x, y + lineHeight * i, 0xffffff, true);
        }

        if (stack.getItem() == Items.ELYTRA) {
            mc.font.drawShadow(matrixStack, elytraInfoString, x + 20,
                    y + flightInfoString.size() * lineHeight + lineHeight * 0.5f, 0xffffff, true);
            drawElytraIcon(x, y + flightInfoString.size() * lineHeight, stack);
        }

        return height;
    }

    protected void drawElytraIcon(int x, int y, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        mc.getProfiler().push("FlightView");
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);

        itemRenderer.renderAndDecorateItem(stack, x, y);
        itemRenderer.renderGuiItemDecorations(mc.font, stack, x, y);

        mc.getProfiler().pop();
        RenderSystem.disableBlend();
    }
}
