package com.khazoda.bronze.mixin.client;

import com.khazoda.bronze.registry.MainRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {
  @Shadow
  @Final
  public ModelPart rightArm;

  @Shadow
  @Final
  public ModelPart leftArm;

  @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
  private void bronze$applyTrowelDigPose(HumanoidRenderState state, CallbackInfo ci) {
    if (!state.isUsingItem) return;

    HumanoidArm activeArm = state.useItemHand == InteractionHand.MAIN_HAND ? state.mainArm : state.mainArm.getOpposite();
    ItemStack itemStack = state.getUseItemStackForArm(activeArm);
    if (!itemStack.is(MainRegistry.TROWEL.get())) return;

    float progress = Mth.clamp(state.ticksUsingItem / 10.0F, 0.0F, 1.0F);
    float forward = bronze$easeOutQuart(Mth.clamp(progress / 0.3F, 0.0F, 1.0F));
    float resetProgress = Mth.clamp((progress - 0.3F) / 0.7F, 0.0F, 1.0F);
    float reset = bronze$easeOut(resetProgress);
    float resetArc = Mth.sin(resetProgress * (float) Math.PI);
    float thrust = forward * (1.0F - reset);

    ModelPart arm = activeArm == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;
    arm.xRot = -0.55F + 0.85F * thrust - 0.25F * resetArc;
    arm.yRot = 0.0F;
    arm.zRot = 0.0F;
  }

  @Unique
  private static float bronze$easeOut(float value) {
    return 1.0F - (1.0F - value) * (1.0F - value);
  }

  @Unique
  private static float bronze$easeOutQuart(float value) {
    float inverse = 1.0F - value;
    return 1.0F - inverse * inverse * inverse * inverse;
  }
}
