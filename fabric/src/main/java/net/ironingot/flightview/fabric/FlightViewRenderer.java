package net.ironingot.flightview.fabric;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FlightViewRenderer
{
    private static final int HUD_X = 8;
    private static final int HUD_Y = 32;
    private static final int LINE_HEIGHT = 10;
    private static final int BACKGROUND_PADDING = 2;
    private static final int ELYTRA_INFO_X_OFFSET = 20;
    private static final float BACKGROUND_OPACITY = 0.4F;

    // 速度換算: ブロック/tick から km/h へ (20 tick/秒 × 3.6)
    private static final double BLOCKS_PER_TICK_TO_KMH = 20.0D * 3.6D;

    // エリトラ耐久の消耗率しきい値。この割合を超えると警告色で表示する
    private static final double ELYTRA_WEAR_CRITICAL = 0.95D;
    private static final double ELYTRA_WEAR_WARNING = 0.9D;

    private static final int COLOR_TEXT = ARGB.color(224, 224, 224);
    private static final int COLOR_CRITICAL = ARGB.color(224, 64, 64);
    private static final int COLOR_WARNING = ARGB.color(224, 128, 64);

    public FlightViewRenderer() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("flightview", "hud"), (context, tickDelta) -> {
            Minecraft mc = Minecraft.getInstance();

            if (!FlightViewMod.isActive())
                return;

            LocalPlayer player = mc.player;
            if (player == null)
                return;

            ItemStack itemstack = player.getItemBySlot(EquipmentSlot.CHEST);
            renderFlightInfo(context, HUD_X, HUD_Y, player, itemstack);
        });
    }

    protected List<String> getFlightInfoString(Player player) {
        List<String> stringArray = new ArrayList<>();
        double dx = player.position().x - player.xOld;
        double dy = player.position().y - player.yOld;
        double dz = player.position().z - player.zOld;
        double groundSpeed = Math.sqrt(dx * dx + dz * dz);
        double airSpeed = Math.sqrt(dx * dx + dy * dy + dz * dz);
        float yaw = Mth.wrapDegrees(player.getYRot());
        float pitch = Mth.wrapDegrees(player.getXRot());

        stringArray.add(String.format("GS: %.2f(km/h)", groundSpeed * BLOCKS_PER_TICK_TO_KMH));
        stringArray.add(String.format("AS: %.2f(km/h)", airSpeed * BLOCKS_PER_TICK_TO_KMH));
        stringArray.add(String.format("Y: %.1f P: %.1f", yaw, pitch));

        return stringArray;
    }

    protected int getElytraLife(ItemStack stack) {
        // エリトラは残り耐久が1に達した時点で飛行不能になるため、飛行可能な残量として1を引く
        return stack.getMaxDamage() - stack.getDamageValue() - 1;
    }

    protected String getElytraInfoString(ItemStack stack) {
        return String.format("%3d", getElytraLife(stack));
    }

    protected int getElytraInfoColor(ItemStack stack) {
        double wear = (double) stack.getDamageValue() / stack.getMaxDamage();
        if (wear > ELYTRA_WEAR_CRITICAL)
            return COLOR_CRITICAL;
        if (wear > ELYTRA_WEAR_WARNING)
            return COLOR_WARNING;
        return COLOR_TEXT;
    }

    protected int renderFlightInfo(GuiGraphicsExtractor drawContext, int x, int y, Player player, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        List<String> flightInfoString = getFlightInfoString(player);
        String elytraInfoString = getElytraInfoString(stack);

        int backgroundColor = mc.options.getBackgroundColor(BACKGROUND_OPACITY);
        final boolean hasElytra = stack.getItem() == Items.ELYTRA;
        final int areaWidth = flightInfoString.stream().mapToInt(mc.font::width).max().orElse(0);
        final int areaHeight = LINE_HEIGHT * flightInfoString.size() + (hasElytra ? (int)(LINE_HEIGHT * 1.5) : 0);

        drawContext.pose().pushMatrix();
        drawContext.fill(x - BACKGROUND_PADDING, y - BACKGROUND_PADDING, x + areaWidth + BACKGROUND_PADDING, y + areaHeight + BACKGROUND_PADDING, backgroundColor);

        for (int i = 0; i < flightInfoString.size(); i++) {
            drawContext.text(mc.font, flightInfoString.get(i), x, y + LINE_HEIGHT * i, COLOR_TEXT, false);
        }

        if (hasElytra) {
            int px = x + ELYTRA_INFO_X_OFFSET;
            int py = (int)(y + flightInfoString.size() * LINE_HEIGHT + LINE_HEIGHT * 0.5f);
            drawContext.text(mc.font, elytraInfoString, px, py, getElytraInfoColor(stack), false);
            drawElytraIcon(drawContext, x, y + flightInfoString.size() * LINE_HEIGHT, stack);
        }

        drawContext.pose().popMatrix();

        return areaHeight;
    }

    protected void drawElytraIcon(GuiGraphicsExtractor drawContext, int x, int y, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();

        drawContext.item(stack, x, y);
        drawContext.itemDecorations(mc.font, stack, x, y);
    }
}
