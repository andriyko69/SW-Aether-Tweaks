package io.github.andriyko69.swaethertweaks.mixin.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.github.andriyko69.swaethertweaks.util.SWAetherAccessories;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Shadow
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Shadow
    private Map<ResourceLocation, RecipeHolder<?>> byName;

    @Inject(method = "apply", at = @At("TAIL"))
    private void swt$removeDisabledAccessoryRecipes(Map<ResourceLocation, com.google.gson.JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> filteredByType = ImmutableMultimap.builder();
        ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> filteredByName = ImmutableMap.builder();

        for (RecipeHolder<?> holder : this.byName.values()) {
            if (SWAetherAccessories.isDisabledRecipe(holder.id())) {
                continue;
            }
            filteredByType.put(holder.value().getType(), holder);
            filteredByName.put(holder.id(), holder);
        }

        this.byType = filteredByType.build();
        this.byName = filteredByName.build();
    }
}
