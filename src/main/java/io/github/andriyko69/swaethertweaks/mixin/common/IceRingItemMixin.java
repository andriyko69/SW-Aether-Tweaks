package io.github.andriyko69.swaethertweaks.mixin.common;

import com.aetherteam.aether.item.accessories.ring.IceRingItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceRingItem.class)
public class IceRingItemMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$disableIceRingTick(ItemStack stack, SlotReference reference, CallbackInfo ci) {
        ci.cancel();
    }
}
