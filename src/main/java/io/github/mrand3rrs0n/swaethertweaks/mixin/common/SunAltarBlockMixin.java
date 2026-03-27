package io.github.mrand3rrs0n.swaethertweaks.mixin.common;

import io.github.mrand3rrs0n.swaethertweaks.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.aetherteam.aether.block.utility.SunAltarBlock;

@Mixin(SunAltarBlock.class)
public class SunAltarBlockMixin {
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$requireConfiguredPermission(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!level.isClientSide() && !Config.canUseSunAltar(player)) {
            player.displayClientMessage(Component.translatable("aether.sun_altar.no_permission"), true);
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
