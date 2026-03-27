package io.github.andriyko69.swaethertweaks.mixin.common;

import com.aetherteam.aether.item.accessories.miscellaneous.GoldenFeatherItem;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoldenFeatherItem.class)
public class GoldenFeatherItemMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$disableGoldenFeatherTick(ItemStack stack, SlotReference reference, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "allowWalkingOnSnow", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$disableGoldenFeatherSnowWalk(ItemStack stack, SlotReference reference, CallbackInfoReturnable<TriState> cir) {
        cir.setReturnValue(TriState.DEFAULT);
    }
}
