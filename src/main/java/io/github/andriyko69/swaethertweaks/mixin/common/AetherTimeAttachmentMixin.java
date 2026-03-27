package io.github.andriyko69.swaethertweaks.mixin.common;

import com.aetherteam.aether.attachment.AetherTimeAttachment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AetherTimeAttachment.class)
public abstract class AetherTimeAttachmentMixin {
    @Shadow(remap = false)
    public abstract void setDayTime(long time);

    @Inject(method = "tickTime", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$disableAetherTimeTick(Level level, CallbackInfoReturnable<Long> cir) {
        long dayTime = level.getDayTime();
        this.setDayTime(dayTime);
        cir.setReturnValue(dayTime);
    }
}
