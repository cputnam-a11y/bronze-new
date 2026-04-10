package com.khazoda.bronze.mixin.client;

import com.khazoda.bronze.registry.MainRegistry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconScreen.class)
public abstract class BeaconScreenMixin extends AbstractContainerScreen<BeaconMenu> {
  protected BeaconScreenMixin(BeaconMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
  }

  @Inject(method = "extractBackground", at = @At("TAIL"))
  private void drawBeaconPayments(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
    graphics.item(new ItemStack(MainRegistry.TIN_INGOT.get()), this.leftPos + 42 + 68, this.topPos + 106);
    graphics.item(new ItemStack(MainRegistry.BRONZE_INGOT.get()), this.leftPos + 42 + 64, this.topPos + 112);
  }
}
