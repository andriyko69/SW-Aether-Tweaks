package io.github.andriyko69.swaethertweaks.inventory;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class SWTCreativeInventorySlot extends Slot {
    private final Slot target;

    public SWTCreativeInventorySlot(Slot target, int index, int x, int y) {
        super(target.container, index, x, y);
        this.target = target;
    }

    public int getTargetIndex() {
        return this.target.index;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.target.onTake(player, stack);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.target.mayPlace(stack);
    }

    @Override
    public ItemStack getItem() {
        return this.target.getItem();
    }

    @Override
    public boolean hasItem() {
        return this.target.hasItem();
    }

    @Override
    public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
        this.target.setByPlayer(newStack, oldStack);
    }

    @Override
    public void set(ItemStack stack) {
        this.target.set(stack);
    }

    @Override
    public void setChanged() {
        this.target.setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return this.target.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return this.target.getMaxStackSize(stack);
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return this.target.getNoItemIcon();
    }

    @Override
    public ItemStack remove(int amount) {
        return this.target.remove(amount);
    }

    @Override
    public boolean mayPickup(Player player) {
        return this.target.mayPickup(player);
    }

    @Override
    public boolean isActive() {
        return this.target.isActive();
    }
}
