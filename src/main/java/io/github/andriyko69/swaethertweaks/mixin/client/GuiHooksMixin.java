package io.github.andriyko69.swaethertweaks.mixin.client;

import com.aetherteam.aether.client.event.hooks.GuiHooks;
import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.util.Tuple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiHooks.class)
public class GuiHooksMixin {
    @Inject(method = "isAccessoryButtonEnabled", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$disableAccessoryButton(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "setupAccessoryButton", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$skipCreativeAccessoryButton(Screen screen, Tuple<Integer, Integer> offsets, CallbackInfoReturnable<AccessoryButton> cir) {
        if (screen instanceof CreativeModeInventoryScreen) {
            cir.setReturnValue(null);
        }
    }
}
