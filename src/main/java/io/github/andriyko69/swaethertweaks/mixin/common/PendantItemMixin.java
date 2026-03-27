package io.github.andriyko69.swaethertweaks.mixin.common;

import com.aetherteam.aether.item.accessories.pendant.PendantItem;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PendantItem.class)
public class PendantItemMixin {
    @Inject(method = "getStaticIdentifier", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$disablePendantSlot(CallbackInfoReturnable<SlotTypeReference> cir) {
        cir.setReturnValue(null);
    }
}
