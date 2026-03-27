package io.github.andriyko69.swaethertweaks.inventory;

import com.mojang.datafixers.util.Pair;
import io.github.andriyko69.swaethertweaks.util.SWAetherSlotRefs;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SWTCreativeAccessorySlot extends Slot {
    private final Player player;
    private final SlotTypeReference slotReference;
    private final Component emptyTooltip;

    public SWTCreativeAccessorySlot(Player player, SlotTypeReference slotReference, int x, int y, Component emptyTooltip) {
        super(new SimpleContainer(1), 0, x, y);
        this.player = player;
        this.slotReference = slotReference;
        this.emptyTooltip = emptyTooltip;
    }

    public SlotTypeReference getSlotReference() {
        return this.slotReference;
    }

    public Component getEmptyTooltip() {
        return this.emptyTooltip;
    }

    @Nullable
    private AccessoriesContainer swt$getContainer() {
        AccessoriesCapability accessories = AccessoriesCapability.get(this.player);
        if (accessories == null) {
            return null;
        }
        return accessories.getContainer(this.slotReference);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return SWAetherSlotRefs.matches(this.slotReference, stack);
    }

    @Override
    public @NotNull ItemStack getItem() {
        AccessoriesContainer container = this.swt$getContainer();
        return container != null ? container.getAccessories().getItem(0) : ItemStack.EMPTY;
    }

    @Override
    public boolean hasItem() {
        return !this.getItem().isEmpty();
    }

    @Override
    public void setByPlayer(@NotNull ItemStack newStack, @NotNull ItemStack oldStack) {
        this.set(newStack);
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        AccessoriesContainer container = this.swt$getContainer();
        if (container != null) {
            container.getAccessories().setItem(0, stack);
        }
    }

    @Override
    public void setChanged() {
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 1;
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        ResourceLocation icon = null;
        if (SWAetherSlotRefs.CAPE_SLOT_NAME.equals(this.slotReference.slotName())) {
            icon = ResourceLocation.fromNamespaceAndPath("aether", "slot/cape");
        } else if (SWAetherSlotRefs.GLOVES_SLOT_NAME.equals(this.slotReference.slotName())) {
            icon = ResourceLocation.fromNamespaceAndPath("aether", "slot/gloves");
        }
        return icon != null ? Pair.of(InventoryMenu.BLOCK_ATLAS, icon) : null;
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        ItemStack current = this.getItem();
        if (current.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack removed = current.split(amount);
        this.set(current);
        return removed;
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
