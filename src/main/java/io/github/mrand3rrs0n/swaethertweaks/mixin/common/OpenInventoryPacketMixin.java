package io.github.mrand3rrs0n.swaethertweaks.mixin.common;

import com.aetherteam.aether.inventory.menu.AetherAccessoriesMenu;
import com.aetherteam.aether.network.packet.clientbound.ClientGrabItemPacket;
import com.aetherteam.aether.network.packet.serverbound.OpenInventoryPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenInventoryPacket.class)
public class OpenInventoryPacketMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$keepNonCreativePlayersInAccessories(OpenInventoryPacket payload, IPayloadContext context, CallbackInfo ci) {
        if (context.player() instanceof ServerPlayer serverPlayer && !serverPlayer.isCreative()) {
            ItemStack carriedStack = serverPlayer.containerMenu.getCarried();
            serverPlayer.containerMenu.setCarried(ItemStack.EMPTY);
            serverPlayer.openMenu(new SimpleMenuProvider((id, inventory, player) -> new AetherAccessoriesMenu(id, inventory), Component.translatable("container.crafting")));
            if (!carriedStack.isEmpty()) {
                serverPlayer.containerMenu.setCarried(carriedStack);
                PacketDistributor.sendToPlayer(serverPlayer, new ClientGrabItemPacket(carriedStack));
            }
            ci.cancel();
        }
    }
}
