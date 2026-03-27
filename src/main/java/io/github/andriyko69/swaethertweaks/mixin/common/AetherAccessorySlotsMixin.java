package io.github.andriyko69.swaethertweaks.mixin.common;

import com.aetherteam.aether.Aether;
import com.aetherteam.aether.inventory.AetherAccessorySlots;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import io.wispforest.accessories.api.slot.UniqueSlotHandling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AetherAccessorySlots.class)
public abstract class AetherAccessorySlotsMixin {
    @Unique
    private static final ResourceLocation SWT_GLOVES_PREDICATE = ResourceLocation.fromNamespaceAndPath(Aether.MODID, "gloves_items");
    @Unique
    private static final ResourceLocation SWT_CAPE_PREDICATE = ResourceLocation.fromNamespaceAndPath(Aether.MODID, "cape_items");

    @Shadow(remap = false)
    @Mutable
    private static SlotTypeReference GLOVES_SLOT;

    @Shadow(remap = false)
    @Mutable
    private static SlotTypeReference RING_SLOT;

    @Shadow(remap = false)
    @Mutable
    private static SlotTypeReference PENDANT_SLOT;

    @Shadow(remap = false)
    @Mutable
    private static SlotTypeReference CAPE_SLOT;

    @Shadow(remap = false)
    @Mutable
    private static SlotTypeReference SHIELD_SLOT;

    @Shadow(remap = false)
    @Mutable
    private static SlotTypeReference ACCESSORY_SLOT;

    @Inject(method = "registerSlots", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$registerOnlyCapeAndGloves(UniqueSlotHandling.UniqueSlotBuilderFactory factory, CallbackInfo ci) {
        GLOVES_SLOT = factory.create(AetherAccessorySlots.GLOVES_SLOT_LOCATION, 1)
            .slotPredicates(SWT_GLOVES_PREDICATE)
            .validTypes(
                EntityType.PLAYER,
                EntityType.ARMOR_STAND,
                EntityType.ZOMBIE,
                EntityType.ZOMBIE_VILLAGER,
                EntityType.HUSK,
                EntityType.SKELETON,
                EntityType.STRAY,
                EntityType.PIGLIN,
                EntityType.ZOMBIFIED_PIGLIN
            )
            .allowEquipFromUse(true)
            .build();
        CAPE_SLOT = factory.create(AetherAccessorySlots.CAPE_SLOT_LOCATION, 1)
            .slotPredicates(SWT_CAPE_PREDICATE)
            .validTypes(EntityType.PLAYER, EntityType.ARMOR_STAND)
            .allowEquipFromUse(true)
            .build();
        RING_SLOT = null;
        PENDANT_SLOT = null;
        SHIELD_SLOT = null;
        ACCESSORY_SLOT = null;
        ci.cancel();
    }
}
