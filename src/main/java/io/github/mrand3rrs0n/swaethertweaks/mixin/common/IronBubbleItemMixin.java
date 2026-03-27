package io.github.mrand3rrs0n.swaethertweaks.mixin.common;

import com.aetherteam.aether.item.accessories.miscellaneous.IronBubbleItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronBubbleItem.class)
public class IronBubbleItemMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$disableIronBubbleTick(ItemStack stack, SlotReference reference, CallbackInfo ci) {
        ci.cancel();
    }
}
