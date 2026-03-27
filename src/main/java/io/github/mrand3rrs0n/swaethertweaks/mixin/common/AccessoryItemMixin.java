package io.github.mrand3rrs0n.swaethertweaks.mixin.common;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.accessories.AccessoryItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AccessoryItem.class)
public class AccessoryItemMixin {
    @Inject(method = "canEquipFromUse", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$onlyAllowKeptAccessories(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(stack.is(AetherTags.Items.ACCESSORIES));
    }
}
