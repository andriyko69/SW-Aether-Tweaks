package io.github.andriyko69.swaethertweaks.mixin.common;

import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import io.github.andriyko69.swaethertweaks.util.SWAetherSlotRefs;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlovesItem.class)
public class GlovesItemMixin {
    @Inject(method = "getStaticIdentifier", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$alwaysUseGlovesSlot(CallbackInfoReturnable<SlotTypeReference> cir) {
        cir.setReturnValue(SWAetherSlotRefs.gloves());
    }
}
