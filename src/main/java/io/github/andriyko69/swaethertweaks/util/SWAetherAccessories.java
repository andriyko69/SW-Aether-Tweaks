package io.github.andriyko69.swaethertweaks.util;

import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class SWAetherAccessories {
    private static final Set<ResourceLocation> DISABLED_ACCESSORY_IDS = Set.of(
            ResourceLocation.fromNamespaceAndPath("aether", "iron_ring"),
            ResourceLocation.fromNamespaceAndPath("aether", "golden_ring"),
            ResourceLocation.fromNamespaceAndPath("aether", "zanite_ring"),
            ResourceLocation.fromNamespaceAndPath("aether", "ice_ring"),
            ResourceLocation.fromNamespaceAndPath("aether", "iron_pendant"),
            ResourceLocation.fromNamespaceAndPath("aether", "golden_pendant"),
            ResourceLocation.fromNamespaceAndPath("aether", "zanite_pendant"),
            ResourceLocation.fromNamespaceAndPath("aether", "ice_pendant"),
            ResourceLocation.fromNamespaceAndPath("aether", "golden_feather"),
            ResourceLocation.fromNamespaceAndPath("aether", "regeneration_stone"),
            ResourceLocation.fromNamespaceAndPath("aether", "iron_bubble")
    );
    private static final Set<ResourceLocation> DISABLED_RECIPE_IDS = Set.of(
            ResourceLocation.fromNamespaceAndPath("aether", "golden_pendant"),
            ResourceLocation.fromNamespaceAndPath("aether", "golden_ring"),
            ResourceLocation.fromNamespaceAndPath("aether", "ice_pendant_from_freezing"),
            ResourceLocation.fromNamespaceAndPath("aether", "ice_ring_from_freezing"),
            ResourceLocation.fromNamespaceAndPath("aether", "iron_pendant"),
            ResourceLocation.fromNamespaceAndPath("aether", "iron_ring"),
            ResourceLocation.fromNamespaceAndPath("aether", "zanite_pendant"),
            ResourceLocation.fromNamespaceAndPath("aether", "zanite_pendant_repairing"),
            ResourceLocation.fromNamespaceAndPath("aether", "zanite_ring"),
            ResourceLocation.fromNamespaceAndPath("aether", "zanite_ring_repairing")
    );

    private SWAetherAccessories() {
    }

    public static boolean isDisabledAccessory(ItemStack stack) {
        return DISABLED_ACCESSORY_IDS.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public static boolean isDisabledRecipe(ResourceLocation recipeId) {
        return DISABLED_RECIPE_IDS.contains(recipeId);
    }
}
