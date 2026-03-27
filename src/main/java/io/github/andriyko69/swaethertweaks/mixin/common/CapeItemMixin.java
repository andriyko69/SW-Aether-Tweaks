package io.github.andriyko69.swaethertweaks.mixin.common;

import com.aetherteam.aether.item.accessories.cape.CapeItem;
import io.github.andriyko69.swaethertweaks.util.SWAetherSlotRefs;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CapeItem.class)
public class CapeItemMixin {
    @Inject(method = "getStaticIdentifier", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$alwaysUseCapeSlot(CallbackInfoReturnable<SlotTypeReference> cir) {
        cir.setReturnValue(SWAetherSlotRefs.cape());
    }
}
