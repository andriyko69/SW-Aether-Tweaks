package io.github.mrand3rrs0n.swaethertweaks.mixin.common;

import com.aetherteam.aether.item.EquipmentUtil;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EquipmentUtil.class)
public class EquipmentUtilMixin {
    @Inject(method = "getZaniteRings", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$disableZaniteRings(LivingEntity entity, CallbackInfoReturnable<List<SlotEntryReference>> cir) {
        cir.setReturnValue(List.of());
    }

    @Inject(method = "getZanitePendant", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$disableZanitePendant(LivingEntity entity, CallbackInfoReturnable<SlotEntryReference> cir) {
        cir.setReturnValue(null);
    }

    @Inject(method = "hasFreezingAccessory", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$disableFreezingAccessories(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
