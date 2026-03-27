package io.github.andriyko69.swaethertweaks.mixin.common;

import com.aetherteam.aether.network.packet.serverbound.SunAltarUpdatePacket;
import io.github.andriyko69.swaethertweaks.Config;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SunAltarUpdatePacket.class)
public class SunAltarUpdatePacketMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$requireConfiguredPermission(SunAltarUpdatePacket payload, IPayloadContext context, CallbackInfo ci) {
        if (!Config.canUseSunAltar(context.player())) {
            ci.cancel();
        }
    }
}
