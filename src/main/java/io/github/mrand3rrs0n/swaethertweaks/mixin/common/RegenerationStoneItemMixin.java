package io.github.mrand3rrs0n.swaethertweaks.mixin.common;

import com.aetherteam.aether.item.accessories.miscellaneous.RegenerationStoneItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegenerationStoneItem.class)
public class RegenerationStoneItemMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$disableRegenerationStoneTick(ItemStack stack, SlotReference reference, CallbackInfo ci) {
        ci.cancel();
    }
}
