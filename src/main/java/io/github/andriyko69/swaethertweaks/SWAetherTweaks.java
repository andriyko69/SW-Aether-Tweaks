package io.github.andriyko69.swaethertweaks;

import com.mojang.serialization.MapCodec;
import io.github.andriyko69.swaethertweaks.loot.RemoveDisabledAccessoriesLootModifier;
import io.github.andriyko69.swaethertweaks.network.serverbound.CreativeAccessorySlotPacket;
import io.github.andriyko69.swaethertweaks.util.SWAetherAccessories;
import java.util.LinkedHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(SWAetherTweaks.MOD_ID)
public class SWAetherTweaks {
    public static final String MOD_ID = "swaethertweaks";
    private static final ResourceLocation AETHER_ARMOR_AND_ACCESSORIES_TAB = ResourceLocation.fromNamespaceAndPath("aether", "armor_and_accessories");
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);

    static {
        LOOT_MODIFIERS.register("remove_disabled_accessories", () -> RemoveDisabledAccessoriesLootModifier.CODEC);
    }

    public SWAetherTweaks(IEventBus modEventBus, ModContainer modContainer) {
        LOOT_MODIFIERS.register(modEventBus);
        modEventBus.addListener(this::stripDisabledAccessoriesFromCreativeTab);
        modEventBus.addListener(this::registerPackets);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void stripDisabledAccessoriesFromCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (!AETHER_ARMOR_AND_ACCESSORIES_TAB.equals(event.getTabKey().location())) {
            return;
        }
        LinkedHashSet<ItemStack> entries = new LinkedHashSet<>(event.getParentEntries());
        entries.addAll(event.getSearchEntries());
        for (ItemStack entry : entries) {
            if (SWAetherAccessories.isDisabledAccessory(entry)) {
                event.remove(entry, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }
    }

    private void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToServer(CreativeAccessorySlotPacket.TYPE, CreativeAccessorySlotPacket.STREAM_CODEC, CreativeAccessorySlotPacket::execute);
    }
}
