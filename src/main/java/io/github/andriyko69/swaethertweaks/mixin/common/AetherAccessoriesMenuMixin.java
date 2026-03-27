package io.github.andriyko69.swaethertweaks.mixin.common;

import com.aetherteam.aether.inventory.menu.AetherAccessoriesMenu;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import io.github.andriyko69.swaethertweaks.inventory.SWTArmorSlot;
import io.github.andriyko69.swaethertweaks.mixin.accessor.AbstractContainerMenuAccessor;
import io.github.andriyko69.swaethertweaks.util.SWAetherSlotRefs;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.menu.AccessoriesSlotGenerator;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.api.slot.SlotTypeReference;

import java.util.*;
import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AetherAccessoriesMenu.class)
public abstract class AetherAccessoriesMenuMixin extends InventoryMenu {
    @Unique
    private static final int SWT_RESULT_SLOT = 0;
    @Unique
    private static final int SWT_CRAFT_END = 5;
    @Unique
    private static final int SWT_CAPE_SLOT = 5;
    @Unique
    private static final int SWT_GLOVES_SLOT = 6;
    @Unique
    private static final int SWT_ARMOR_END = 11;
    @Unique
    private static final int SWT_INVENTORY_START = 11;
    @Unique
    private static final int SWT_HOTBAR_START = 38;
    @Unique
    private static final int SWT_HOTBAR_END = 47;
    @Unique
    private static final int SWT_OFFHAND_SLOT = 47;

    @Shadow(remap = false)
    @Final
    private static Map<EquipmentSlot, ResourceLocation> TEXTURE_EMPTY_SLOTS;

    @Shadow(remap = false)
    @Final
    private static EquipmentSlot[] SLOT_IDS;

    @Shadow(remap = false)
    @Final
    private CraftingContainer craftSlots;

    @Shadow(remap = false)
    @Final
    private ResultContainer resultSlots;

    @Shadow(remap = false)
    @Final
    private Player owner;

    protected AetherAccessoriesMenuMixin(Inventory playerInventory, boolean active, Player owner) {
        super(playerInventory, active, owner);
    }

    @WrapOperation(
        method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lio/wispforest/accessories/api/menu/AccessoriesSlotGenerator;of(Ljava/util/function/Consumer;IILnet/minecraft/world/entity/LivingEntity;[Lio/wispforest/accessories/api/slot/SlotTypeReference;)Lio/wispforest/accessories/api/menu/AccessoriesSlotGenerator;",
            ordinal = 0,
            remap = false
        ),
        remap = false
    )
    private AccessoriesSlotGenerator swt$keepOnlyCapeAndGloves(Consumer<Slot> slotConsumer, int startX, int startY, LivingEntity livingEntity, SlotTypeReference[] references, Operation<AccessoriesSlotGenerator> original) {
        return original.call(slotConsumer, 77, 8, owner, SWAetherSlotRefs.present(SWAetherSlotRefs.cape(), SWAetherSlotRefs.gloves()));
    }

    @WrapOperation(
        method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lio/wispforest/accessories/api/menu/AccessoriesSlotGenerator;of(Ljava/util/function/Consumer;IILnet/minecraft/world/entity/LivingEntity;[Lio/wispforest/accessories/api/slot/SlotTypeReference;)Lio/wispforest/accessories/api/menu/AccessoriesSlotGenerator;",
            ordinal = 1,
            remap = false
        ),
        remap = false
    )
    private AccessoriesSlotGenerator swt$skipRemovedColumn(Consumer<Slot> slotConsumer, int startX, int startY, LivingEntity livingEntity, SlotTypeReference[] references, Operation<AccessoriesSlotGenerator> original) {
        return original.call(slotConsumer, startX, startY, owner, SWAetherSlotRefs.NO_SLOTS);
    }

    @WrapOperation(
        method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Z)V",
        at = @At(
            value = "INVOKE",
            target = "Lio/wispforest/accessories/api/menu/AccessoriesSlotGenerator;of(Ljava/util/function/Consumer;IILnet/minecraft/world/entity/LivingEntity;[Lio/wispforest/accessories/api/slot/SlotTypeReference;)Lio/wispforest/accessories/api/menu/AccessoriesSlotGenerator;",
            ordinal = 2,
            remap = false
        ),
        remap = false
    )
    private AccessoriesSlotGenerator swt$skipRemovedRow(Consumer<Slot> slotConsumer, int startX, int startY, LivingEntity livingEntity, SlotTypeReference[] references, Operation<AccessoriesSlotGenerator> original) {
        return original.call(slotConsumer, startX, startY, owner, SWAetherSlotRefs.NO_SLOTS);
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Z)V", at = @At("RETURN"), remap = false)
    private void swt$rebuildReducedLayout(int containerId, Inventory playerInventory, boolean hasButton, CallbackInfo ci) {
        this.slots.clear();
        AbstractContainerMenuAccessor accessor = (AbstractContainerMenuAccessor) this;
        accessor.swt$getLastSlots().clear();
        accessor.swt$getRemoteSlots().clear();

        this.addSlot(new ResultSlot(playerInventory.player, this.craftSlots, this.resultSlots, 0, 148, 30));

        for (int row = 0; row < 2; row++) {
            for (int column = 0; column < 2; column++) {
                this.addSlot(new Slot(this.craftSlots, column + row * 2, 110 + column * 18, 20 + row * 18));
            }
        }

        SlotTypeReference[] accessorySlots = SWAetherSlotRefs.present(SWAetherSlotRefs.cape(), SWAetherSlotRefs.gloves());
        if (accessorySlots.length > 0) {
            Objects.requireNonNull(AccessoriesSlotGenerator.of(this::addSlot, 77, 8, this.owner, accessorySlots)).column();
        }

        for (int armorIndex = 0; armorIndex < 4; armorIndex++) {
            EquipmentSlot equipmentSlot = SLOT_IDS[armorIndex];
            ResourceLocation emptyTexture = TEXTURE_EMPTY_SLOTS.get(equipmentSlot);
            this.addSlot(new SWTArmorSlot(playerInventory, this.owner, equipmentSlot, 36 + (3 - armorIndex), 8, 8 + armorIndex * 18, emptyTexture));
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + (row + 1) * 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            this.addSlot(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 142));
        }

        this.addSlot(new Slot(playerInventory, 40, 77, 62) {
            @Override
            public void setByPlayer(@NotNull ItemStack newStack, @NotNull ItemStack oldStack) {
                AetherAccessoriesMenuMixin.this.owner.onEquipItem(EquipmentSlot.OFFHAND, oldStack, newStack);
                super.setByPlayer(newStack, oldStack);
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }
        });
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$useReducedQuickMove(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(this.swt$quickMoveStack(player, index));
    }

    @Unique
    private ItemStack swt$quickMoveStack(Player player, int index) {
        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }
        ItemStack copiedStack;
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        copiedStack = stack.copy();
        EquipmentSlot equipmentSlot = player.getEquipmentSlotForItem(copiedStack);
        Collection<SlotType> accessorySlots = AccessoriesAPI.getValidSlotTypes(player, copiedStack);

        if (index == SWT_RESULT_SLOT) {
            if (!this.moveItemStackTo(stack, SWT_INVENTORY_START, SWT_HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, copiedStack);
        } else if (index < SWT_CRAFT_END) {
            if (!this.moveItemStackTo(stack, SWT_INVENTORY_START, SWT_HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < SWT_ARMOR_END) {
            if (!this.moveItemStackTo(stack, SWT_INVENTORY_START, SWT_HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean moved = false;
            if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                int armorSlot = 10 - equipmentSlot.getIndex();
                if (!this.slots.get(armorSlot).hasItem()) {
                    moved = this.moveItemStackTo(stack, armorSlot, armorSlot + 1, false);
                }
            }
            if (!moved) {
                moved = this.swt$moveIntoAccessorySlot(stack, accessorySlots);
            }
            if (!moved && equipmentSlot == EquipmentSlot.OFFHAND && !this.slots.get(SWT_OFFHAND_SLOT).hasItem()) {
                moved = this.moveItemStackTo(stack, SWT_OFFHAND_SLOT, SWT_OFFHAND_SLOT + 1, false);
            }
            if (!moved) {
                if (index < SWT_HOTBAR_START) {
                    moved = this.moveItemStackTo(stack, SWT_HOTBAR_START, SWT_HOTBAR_END, false);
                } else if (index < SWT_HOTBAR_END) {
                    moved = this.moveItemStackTo(stack, SWT_INVENTORY_START, SWT_HOTBAR_START, false);
                } else if (index == SWT_OFFHAND_SLOT) {
                    moved = this.moveItemStackTo(stack, SWT_INVENTORY_START, SWT_HOTBAR_END, false);
                }
            }
            if (!moved) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == copiedStack.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        if (index == SWT_RESULT_SLOT) {
            player.drop(stack, false);
        }
        return copiedStack;
    }

    @Unique
    private boolean swt$moveIntoAccessorySlot(ItemStack stack, Collection<SlotType> accessorySlots) {
        for (int accessorySlot : this.swt$getEmptyAccessorySlots(accessorySlots)) {
            if (this.moveItemStackTo(stack, accessorySlot, accessorySlot + 1, false)) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private List<Integer> swt$getEmptyAccessorySlots(Collection<SlotType> accessorySlots) {
        List<Integer> openSlots = new ArrayList<>(2);
        if (SWAetherSlotRefs.hasSlotType(accessorySlots, SWAetherSlotRefs.CAPE_SLOT_NAME) && !this.slots.get(SWT_CAPE_SLOT).hasItem()) {
            openSlots.add(SWT_CAPE_SLOT);
        }
        if (SWAetherSlotRefs.hasSlotType(accessorySlots, SWAetherSlotRefs.GLOVES_SLOT_NAME) && !this.slots.get(SWT_GLOVES_SLOT).hasItem()) {
            openSlots.add(SWT_GLOVES_SLOT);
        }
        return openSlots;
    }
}
