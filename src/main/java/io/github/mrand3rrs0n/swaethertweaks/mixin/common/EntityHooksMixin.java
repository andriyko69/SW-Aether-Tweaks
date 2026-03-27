package io.github.mrand3rrs0n.swaethertweaks.mixin.common;

import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.event.hooks.EntityHooks;
import com.aetherteam.aether.item.AetherItems;
import io.github.mrand3rrs0n.swaethertweaks.util.SWAetherSlotRefs;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Mixin(EntityHooks.class)
public abstract class EntityHooksMixin {
    @Inject(method = "spawnWithAccessories", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$spawnOnlyGloves(Entity entity, DifficultyInstance difficulty, CallbackInfo ci) {
        if (entity instanceof Mob mob && mob.level() instanceof ServerLevel) {
            SlotTypeReference gloves = SWAetherSlotRefs.gloves();
            if (gloves != null) {
                RandomSource random = mob.getRandom();
                if (mob.getType() == EntityType.PIGLIN) {
                    if (mob instanceof AbstractPiglin abstractPiglin && abstractPiglin.isAdult() && random.nextFloat() < 0.1F) {
                        swt$equipAccessory(mob, gloves, ArmorMaterials.GOLD);
                    }
                } else {
                    boolean fullyArmored = true;
                    for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                        if (equipmentSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && mob.getItemBySlot(equipmentSlot).isEmpty()) {
                            fullyArmored = false;
                            break;
                        }
                    }
                    if (fullyArmored && random.nextInt(4) == 1) {
                        if (mob.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem armorItem) {
                            swt$equipAccessory(mob, gloves, armorItem.getMaterial());
                        }
                    }
                }
                swt$enchantAccessory(mob, difficulty, gloves);
            }
        }
        ci.cancel();
    }

    @Inject(method = "handleEntityAccessoryDrops", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$dropOnlyGloves(net.minecraft.world.entity.LivingEntity entity, List<ItemStack> itemStacks, boolean recentlyHit, int looting, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (entity instanceof Mob mob) {
            SlotTypeReference gloves = SWAetherSlotRefs.gloves();
            if (gloves != null && !itemStacks.isEmpty()) {
                ItemStack itemStack = itemStacks.getFirst();
                float chance = mob.getData(AetherDataAttachments.MOB_ACCESSORY).getEquipmentDropChance(gloves);
                boolean guaranteedDrop = chance > 1.0F;
                if (!itemStack.isEmpty()) {
                    itemStacks.removeIf(stack -> ItemStack.isSameItemSameComponents(stack, itemStack));
                }
                if (!itemStack.isEmpty()
                        && itemStack.getEnchantmentLevel(entity.level().holderOrThrow(Enchantments.VANISHING_CURSE)) == 0
                        && recentlyHit
                        && Math.max(mob.getRandom().nextFloat() - (float) looting * 0.01F, 0.0F) < chance) {
                    if (!guaranteedDrop && itemStack.isDamageableItem()) {
                        itemStack.setDamageValue(itemStack.getMaxDamage() - mob.getRandom().nextInt(1 + mob.getRandom().nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
                    }
                    itemStacks.add(itemStack);
                }
            }
        }
        cir.setReturnValue(itemStacks);
    }

    @Inject(method = "modifyExperience", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$onlyCountGlovesForExperience(net.minecraft.world.entity.LivingEntity entity, int experience, CallbackInfoReturnable<Integer> cir) {
        int modifiedExperience = experience;
        if (entity instanceof Mob mob && mob.hasData(AetherDataAttachments.MOB_ACCESSORY) && experience > 0) {
            SlotTypeReference gloves = SWAetherSlotRefs.gloves();
            AccessoriesCapability accessories = AccessoriesCapability.get(entity);
            if (gloves != null && accessories != null) {
                AccessoriesContainer accessoriesContainer = accessories.getContainer(gloves);
                if (accessoriesContainer != null) {
                    ItemStack stack = accessoriesContainer.getAccessories().getItem(0);
                    if (!stack.isEmpty() && mob.getData(AetherDataAttachments.MOB_ACCESSORY).getEquipmentDropChance(gloves) <= 1.0F) {
                        modifiedExperience += 1 + mob.getRandom().nextInt(3);
                    }
                }
            }
        }
        cir.setReturnValue(modifiedExperience);
    }

    @Inject(method = "slotToUnequip", at = @At("HEAD"), cancellable = true, remap = false)
    private static void swt$onlyUseCapeAndGlovesOnArmorStands(ArmorStand armorStand, Vec3 pos, CallbackInfoReturnable<SlotTypeReference> cir) {
        boolean isSmall = armorStand.isSmall();
        Direction.Axis axis = armorStand.getDirection().getAxis();
        double x = isSmall ? pos.x * 2.0 : pos.x;
        double z = isSmall ? pos.z * 2.0 : pos.z;
        double front = axis == Direction.Axis.X ? z : x;
        double vertical = isSmall ? pos.y * 2.0 : pos.y;
        SlotTypeReference gloves = SWAetherSlotRefs.gloves();
        SlotTypeReference cape = SWAetherSlotRefs.cape();
        if (!swt$getItemByIdentifier(armorStand, gloves).isEmpty()
                && Math.abs(front) >= (isSmall ? 0.15 : 0.2)
                && vertical >= (isSmall ? 0.65 : 0.75)
                && vertical < 1.15) {
            cir.setReturnValue(gloves);
        } else if (!swt$getItemByIdentifier(armorStand, cape).isEmpty()
                && vertical >= (isSmall ? 1.0 : 1.1)
                && vertical < (isSmall ? 1.7 : 1.4)) {
            cir.setReturnValue(cape);
        } else {
            cir.setReturnValue(null);
        }
    }

    @Unique
    private static void swt$equipAccessory(Mob mob, SlotTypeReference identifier, Holder<ArmorMaterial> armorMaterial) {
        AccessoriesCapability accessories = AccessoriesCapability.get(mob);
        if (accessories == null) {
            return;
        }
        AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
        if (accessoriesContainer == null) {
            return;
        }
        boolean empty = true;
        for (SlotEntryReference slotResult : accessoriesContainer.capability().getAllEquipped()) {
            if (!slotResult.stack().isEmpty()) {
                empty = false;
                break;
            }
        }
        if (!empty) {
            return;
        }
        Item item = swt$getGlovesForMaterial(armorMaterial);
        if (item != null) {
            accessoriesContainer.getAccessories().setItem(0, new ItemStack(item));
        }
    }

    @Unique
    private static void swt$enchantAccessory(Mob mob, DifficultyInstance difficulty, SlotTypeReference identifier) {
        AccessoriesCapability accessories = AccessoriesCapability.get(mob);
        if (accessories == null) {
            return;
        }
        AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
        if (accessoriesContainer == null) {
            return;
        }
        ItemStack stack = accessoriesContainer.getAccessories().getItem(0);
        float chanceMultiplier = difficulty.getSpecialMultiplier();
        RandomSource random = mob.getRandom();
        if (!stack.isEmpty() && random.nextFloat() < 0.5F * chanceMultiplier) {
            accessoriesContainer.getAccessories().setItem(
                    0,
                    EnchantmentHelper.enchantItem(
                            random,
                            stack,
                            (int) (5.0F + chanceMultiplier * (float) random.nextInt(18)),
                            mob.registryAccess(),
                            Optional.of(mob.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT))
                    )
            );
        }
    }

    @Unique
    private static Item swt$getGlovesForMaterial(Holder<ArmorMaterial> armorMaterial) {
        if (armorMaterial.is(Objects.requireNonNull(ArmorMaterials.LEATHER.getKey()))) {
            return AetherItems.LEATHER_GLOVES.get();
        }
        if (armorMaterial.is(Objects.requireNonNull(ArmorMaterials.GOLD.getKey()))) {
            return AetherItems.GOLDEN_GLOVES.get();
        }
        if (armorMaterial.is(Objects.requireNonNull(ArmorMaterials.CHAIN.getKey()))) {
            return AetherItems.CHAINMAIL_GLOVES.get();
        }
        if (armorMaterial.is(Objects.requireNonNull(ArmorMaterials.IRON.getKey()))) {
            return AetherItems.IRON_GLOVES.get();
        }
        if (armorMaterial.is(Objects.requireNonNull(ArmorMaterials.DIAMOND.getKey()))) {
            return AetherItems.DIAMOND_GLOVES.get();
        }
        return null;
    }

    @Unique
    private static ItemStack swt$getItemByIdentifier(ArmorStand armorStand, SlotTypeReference identifier) {
        if (identifier == null) {
            return ItemStack.EMPTY;
        }
        AccessoriesCapability accessories = AccessoriesCapability.get(armorStand);
        if (accessories == null) {
            return ItemStack.EMPTY;
        }
        AccessoriesContainer accessoriesContainer = accessories.getContainer(identifier);
        if (accessoriesContainer == null) {
            return ItemStack.EMPTY;
        }
        return accessoriesContainer.getAccessories().getItem(0);
    }
}
