package io.github.andriyko69.swaethertweaks.mixin.client;

import com.aetherteam.aether.network.packet.serverbound.OpenAccessoriesPacket;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @WrapOperation(
        method = "handleKeybinds",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V",
            ordinal = 1
        )
    )
    private void swt$openAccessoriesInsteadOfInventory(Minecraft instance, Screen screen, Operation<Void> original) {
        if (this.player != null && !this.player.isCreative()) {
            PacketDistributor.sendToServer(new OpenAccessoriesPacket(ItemStack.EMPTY));
            return;
        }
        original.call(instance, screen);
    }
}
