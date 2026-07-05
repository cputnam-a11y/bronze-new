package com.khazoda.bronze.mixin.client;

import com.khazoda.bronze.registry.MainRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
  @Inject(method = "submitArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;applyItemArmTransform(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/HumanoidArm;F)V", ordinal = 1, shift = At.Shift.AFTER))
  private void bronze$applyTrowelDigTransform(AbstractClientPlayer player, float frameInterp, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
    if (!itemStack.is(MainRegistry.TROWEL.get())) return;
    if (!player.isUsingItem() || player.getUseItemRemainingTicks() <= 0 || player.getUsedItemHand() != hand) return;

    float timeHeld = itemStack.getUseDuration(player) - (player.getUseItemRemainingTicks() - frameInterp + 1.0F);
    float progress = Mth.clamp(timeHeld / 10F, 0.0F, 1.0F);
    float forward = bronze$easeOutQuart(Mth.clamp(progress / 0.3F, 0.0F, 1.0F));
    float resetProgress = Mth.clamp((progress - 0.3F) / 0.7F, 0.0F, 1.0F);
    float reset = bronze$easeOut(resetProgress);
    float resetArc = Mth.sin(resetProgress * (float) Math.PI);
    float tipRaise = Mth.clamp((progress - 0.25F) / 0.05F, 0.0F, 1.0F) * (1.0F - resetProgress);

    poseStack.translate(0.0F, 0.15F * resetArc, -0.82F * forward * (1.0F - reset));
    poseStack.mulPose(Axis.XP.rotationDegrees(16.0F * tipRaise));
  }

  @Unique
  private static float bronze$easeOut(float value) {
    return 1.0F - (1.0F - value) * (1.0F - value);
  }

  @Unique
  private static float bronze$easeOutQuart(float value) {
    float i = 1.0F - value;
    return 1.0F - i * i * i * i;
  }
}
