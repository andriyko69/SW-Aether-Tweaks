package io.github.andriyko69.swaethertweaks.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.andriyko69.swaethertweaks.util.SWAetherAccessories;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class RemoveDisabledAccessoriesLootModifier extends LootModifier {
    public static final MapCodec<RemoveDisabledAccessoriesLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(instance -> codecStart(instance).apply(instance, RemoveDisabledAccessoriesLootModifier::new));

    public RemoveDisabledAccessoriesLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, @NotNull LootContext context) {
        generatedLoot.removeIf(SWAetherAccessories::isDisabledAccessory);
        return generatedLoot;
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
