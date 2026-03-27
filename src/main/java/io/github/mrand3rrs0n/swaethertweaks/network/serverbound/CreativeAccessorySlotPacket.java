package io.github.mrand3rrs0n.swaethertweaks.network.serverbound;

import io.github.mrand3rrs0n.swaethertweaks.SWAetherTweaks;
import io.github.mrand3rrs0n.swaethertweaks.util.SWAetherSlotRefs;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CreativeAccessorySlotPacket(ResourceLocation slotName, ItemStack accessoryStack, ItemStack carriedStack) implements CustomPacketPayload {
    public static final Type<CreativeAccessorySlotPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SWAetherTweaks.MOD_ID, "creative_accessory_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CreativeAccessorySlotPacket> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        CreativeAccessorySlotPacket::slotName,
        ItemStack.OPTIONAL_STREAM_CODEC,
        CreativeAccessorySlotPacket::accessoryStack,
        ItemStack.OPTIONAL_STREAM_CODEC,
        CreativeAccessorySlotPacket::carriedStack,
        CreativeAccessorySlotPacket::new
    );

    @Override
    public Type<CreativeAccessorySlotPacket> type() {
        return TYPE;
    }

    public static void execute(CreativeAccessorySlotPacket payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        AccessoriesCapability accessories = AccessoriesCapability.get(serverPlayer);
        if (accessories == null) {
            return;
        }

        AccessoriesContainer container = accessories.getContainer(new SlotTypeReference(payload.slotName().toString()));
        if (container == null) {
            return;
        }

        if (!payload.accessoryStack().isEmpty() && !SWAetherSlotRefs.matches(new SlotTypeReference(payload.slotName().toString()), payload.accessoryStack())) {
            return;
        }

        container.getAccessories().setItem(0, payload.accessoryStack().copy());
        serverPlayer.inventoryMenu.setCarried(payload.carriedStack().copy());
        if (serverPlayer.containerMenu != serverPlayer.inventoryMenu) {
            serverPlayer.containerMenu.setCarried(payload.carriedStack().copy());
        }
        serverPlayer.inventoryMenu.broadcastChanges();
        serverPlayer.containerMenu.broadcastChanges();
    }
}
