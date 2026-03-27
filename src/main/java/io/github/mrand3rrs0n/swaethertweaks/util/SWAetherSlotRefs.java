package io.github.mrand3rrs0n.swaethertweaks.util;

import com.aetherteam.aether.inventory.AetherAccessorySlots;
import com.aetherteam.aether.item.accessories.SlotIdentifierHolder;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class SWAetherSlotRefs {
    public static final String CAPE_SLOT_NAME = "aether:cape_slot";
    public static final String GLOVES_SLOT_NAME = "aether:gloves_slot";
    public static final SlotTypeReference[] NO_SLOTS = new SlotTypeReference[0];

    private SWAetherSlotRefs() {
    }

    @Nullable
    public static SlotTypeReference cape() {
        return AetherAccessorySlots.getCapeSlotType();
    }

    @Nullable
    public static SlotTypeReference gloves() {
        return AetherAccessorySlots.getGlovesSlotType();
    }

    public static SlotTypeReference[] present(@Nullable SlotTypeReference... slotReferences) {
        assert slotReferences != null;
        List<SlotTypeReference> present = new ArrayList<>(slotReferences.length);
        for (SlotTypeReference slotReference : slotReferences) {
            if (slotReference != null) {
                present.add(slotReference);
            }
        }
        return present.toArray(SlotTypeReference[]::new);
    }

    public static boolean hasSlotType(Collection<SlotType> slotTypes, String slotName) {
        for (SlotType slotType : slotTypes) {
            if (slotName.equals(slotType.name())) {
                return true;
            }
        }
        return false;
    }

    public static String capeTranslationKey() {
        return "accessories.slot.aether.cape_slot";
    }

    public static String glovesTranslationKey() {
        return "accessories.slot.aether.gloves_slot";
    }

    @Nullable
    public static ItemStackIdentifier getIdentifier(ItemStack stack) {
        if (stack.getItem() instanceof SlotIdentifierHolder holder) {
            SlotTypeReference identifier = holder.getIdentifier();
            if (identifier != null) {
                return new ItemStackIdentifier(identifier.slotName());
            }
        }
        return null;
    }

    public static boolean matches(@Nullable SlotTypeReference slotReference, ItemStack stack) {
        if (slotReference == null || stack.isEmpty()) {
            return false;
        }
        ItemStackIdentifier identifier = getIdentifier(stack);
        return identifier != null && slotReference.slotName().equals(identifier.name());
    }

    public static ResourceLocation location(SlotTypeReference slotReference) {
        return ResourceLocation.parse(slotReference.slotName());
    }

    public record ItemStackIdentifier(String name) {
    }
}
