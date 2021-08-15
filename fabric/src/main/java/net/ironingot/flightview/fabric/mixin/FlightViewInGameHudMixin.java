package net.ironingot.flightview.fabric.mixin;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class FlightViewInGameHudMixin {
  public FlightViewInGameHudMixin() {
    super();
  }

  @Inject(at = { @At("RETURN") }, method = { "render" }, cancellable = true)
  public void onRender(PoseStack matrices, float tickDelta, CallbackInfo info) {
    Minecraft.getInstance().textRenderer.draw(matrices, "Text", 5, 5, -1);
    }
  }
}
