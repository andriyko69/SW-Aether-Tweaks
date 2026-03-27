package io.github.andriyko69.swaethertweaks.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.andriyko69.swaethertweaks.inventory.SWTCreativeAccessorySlot;
import io.github.andriyko69.swaethertweaks.inventory.SWTCreativeInventorySlot;
import io.github.andriyko69.swaethertweaks.mixin.accessor.ScreenAccessor;
import io.github.andriyko69.swaethertweaks.mixin.accessor.TooltipAccessor;
import io.github.andriyko69.swaethertweaks.network.serverbound.CreativeAccessorySlotPacket;
import io.github.andriyko69.swaethertweaks.util.SWAetherAccessories;
import io.github.andriyko69.swaethertweaks.util.SWAetherSlotRefs;
import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.owo.mixin.ui.access.ClickableWidgetAccessor;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.layers.Layer;
import io.wispforest.owo.util.pond.OwoScreenExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Unique
    private static final ResourceLocation SWT_SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");

    protected CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Shadow
    public abstract boolean isInventoryOpen();

    @Shadow
    @Nullable
    private Slot destroyItemSlot;

    @Shadow
    private float scrollOffs;

    @WrapOperation(
        method = "renderBg",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V"
        )
    )
    private void swt$renderCreativePreviewWithoutClipping(
        GuiGraphics guiGraphics,
        int x1,
        int y1,
        int x2,
        int y2,
        int scale,
        float yOffset,
        float mouseX,
        float mouseY,
        LivingEntity entity,
        Operation<Void> original
    ) {
        swt$renderEntityInInventoryWithoutScissor(guiGraphics, x1, y1, x2, y2, scale, yOffset, mouseX, mouseY, entity);
    }

    @Inject(method = "selectTab", at = @At("TAIL"))
    private void swt$embedAccessorySlotsInInventoryTab(CreativeModeTab tab, CallbackInfo ci) {
        if (tab.getType() != CreativeModeTab.Type.INVENTORY || this.minecraft == null || this.minecraft.player == null) {
            return;
        }

        AbstractContainerMenu inventoryMenu = this.minecraft.player.inventoryMenu;
        List<Slot> inventorySlots = inventoryMenu.slots;
        this.menu.slots.clear();

        for (int slotIndex = 0; slotIndex < Math.min(46, inventorySlots.size()); slotIndex++) {
            this.menu.slots.add(new SWTCreativeInventorySlot(inventorySlots.get(slotIndex), slotIndex, swt$getSlotX(slotIndex), swt$getSlotY(slotIndex)));
        }

        swt$addAccessorySlot(this.minecraft.player, SWAetherSlotRefs.cape(), 135, 6, Component.translatable(SWAetherSlotRefs.capeTranslationKey()));
        swt$addAccessorySlot(this.minecraft.player, SWAetherSlotRefs.gloves(), 135, 33, Component.translatable(SWAetherSlotRefs.glovesTranslationKey()));

        if (this.destroyItemSlot != null) {
            this.menu.slots.add(this.destroyItemSlot);
        }
    }

    @Inject(method = "selectTab", at = @At("TAIL"))
    private void swt$removeDisabledAccessoriesFromCreativeTab(CreativeModeTab tab, CallbackInfo ci) {
        swt$filterCreativeItems();
    }

    @Inject(method = "refreshCurrentTabContents", at = @At("TAIL"))
    private void swt$removeDisabledAccessoriesFromRefreshedTab(Collection<ItemStack> items, CallbackInfo ci) {
        swt$filterCreativeItems();
    }

    @Inject(method = "refreshSearchResults", at = @At("TAIL"))
    private void swt$removeDisabledAccessoriesFromSearch(CallbackInfo ci) {
        swt$filterCreativeItems();
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void swt$drawAccessorySlotFrames(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        if (!this.isInventoryOpen()) {
            return;
        }

        for (Slot slot : this.menu.slots) {
            if (slot instanceof SWTCreativeAccessorySlot) {
                guiGraphics.blitSprite(SWT_SLOT_SPRITE, this.leftPos + slot.x - 1, this.topPos + slot.y - 1, 18, 18);
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void swt$removeCreativeAccessoryButtonBeforeDraw(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        swt$removeAccessoryButtons();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void swt$renderAccessoryTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.hoveredSlot instanceof SWTCreativeAccessorySlot accessorySlot && !this.hoveredSlot.hasItem() && this.menu.getCarried().isEmpty()) {
            guiGraphics.renderTooltip(this.font, accessorySlot.getEmptyTooltip(), mouseX, mouseY);
        }
    }

    @Unique
    private void swt$removeAccessoryButtons() {
        this.renderables.removeIf(AccessoryButton.class::isInstance);
        this.children().removeIf(AccessoryButton.class::isInstance);
        ScreenAccessor accessor = (ScreenAccessor) this;
        accessor.swt$getChildren().removeIf(this::swt$isAccessoriesOpenWidget);
        accessor.swt$getNarratables().removeIf(this::swt$isAccessoriesOpenWidget);
        this.renderables.removeIf(this::swt$isAccessoriesOpenWidget);
        swt$removeAccessoriesOpenButtonFromLayers();
    }

    @Unique
    private void swt$removeAccessoriesOpenButtonFromLayers() {
        if (!((Object) this instanceof OwoScreenExtension extension)) {
            return;
        }

        for (Layer<?, ?>.Instance instance : extension.owo$getInstancesView()) {
            swt$removeAccessoriesOpenButtons(instance.adapter.rootComponent);
        }
    }

    @Unique
    private void swt$removeAccessoriesOpenButtons(ParentComponent parent) {
        for (io.wispforest.owo.ui.core.Component child : List.copyOf(parent.children())) {
            if (child instanceof ButtonComponent button && this.swt$isAccessoriesOpenButton(button)) {
                parent.removeChild(child);
                continue;
            }

            if (child instanceof ParentComponent childParent) {
                this.swt$removeAccessoriesOpenButtons(childParent);
            }
        }
    }

    @Unique
    private boolean swt$isAccessoriesOpenButton(ButtonComponent button) {
        return this.swt$isAccessoriesOpenWidget((Object) button);
    }

    @Unique
    private boolean swt$isAccessoriesOpenWidget(Object object) {
        if (!(object instanceof AbstractWidget widget)) {
            return false;
        }

        if (widget.getWidth() != 8 || widget.getHeight() != 8 || !widget.getMessage().getString().isEmpty()) {
            return false;
        }

        Tooltip tooltip = ((ClickableWidgetAccessor) widget).owo$getTooltip().get();
        if (tooltip != null && ((TooltipAccessor) tooltip).swt$getMessage().getString().equals(Component.translatable("accessories.open.screen").getString())) {
            return true;
        }

        int minX = this.leftPos + 92;
        int maxX = this.leftPos + 114;
        int minY = this.topPos;
        int maxY = this.topPos + 18;
        return widget.getX() >= minX && widget.getX() <= maxX && widget.getY() >= minY && widget.getY() <= maxY;
    }

    @Unique
    private boolean swt$isAccessoriesOpenWidget(Renderable renderable) {
        return this.swt$isAccessoriesOpenWidget((Object) renderable);
    }

    @Unique
    private boolean swt$isAccessoriesOpenWidget(GuiEventListener listener) {
        return this.swt$isAccessoriesOpenWidget((Object) listener);
    }

    @Unique
    private boolean swt$isAccessoriesOpenWidget(NarratableEntry entry) {
        return this.swt$isAccessoriesOpenWidget((Object) entry);
    }

    @Unique
    private void swt$filterCreativeItems() {
        if (this.menu.items.removeIf(SWAetherAccessories::isDisabledAccessory)) {
            this.menu.scrollTo(this.scrollOffs);
        }
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void swt$handleCreativeAccessorySlotClick(@Nullable Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        if (this.minecraft == null || this.minecraft.player == null || !this.isInventoryOpen()) {
            return;
        }

        Player player = this.minecraft.player;
        if (slot instanceof SWTCreativeAccessorySlot accessorySlot) {
            if (type == ClickType.QUICK_MOVE) {
                swt$handleAccessoryQuickMove(accessorySlot, player);
                ci.cancel();
                return;
            }

            swt$handleAccessorySlotClick(accessorySlot, player, mouseButton, type);
            ci.cancel();
            return;
        }

        int targetIndex = swt$getTargetIndex(slot);
        if (targetIndex < 0) {
            return;
        }

        if (!slot.mayPickup(player)) {
            ci.cancel();
            return;
        }

        if (type == ClickType.QUICK_MOVE && swt$handleInventoryAccessoryQuickMove(slot, player, targetIndex)) {
            ci.cancel();
            return;
        }

        if (type == ClickType.THROW && slot.hasItem()) {
            ItemStack removedStack = slot.remove(mouseButton == 0 ? 1 : slot.getItem().getMaxStackSize());
            ItemStack remainingStack = slot.getItem();
            player.drop(removedStack, true);
            this.minecraft.gameMode.handleCreativeModeItemDrop(removedStack);
            this.minecraft.gameMode.handleCreativeModeItemAdd(remainingStack, targetIndex);
        } else if (type == ClickType.THROW && !this.menu.getCarried().isEmpty()) {
            player.drop(this.menu.getCarried(), true);
            this.minecraft.gameMode.handleCreativeModeItemDrop(this.menu.getCarried());
            this.menu.setCarried(ItemStack.EMPTY);
        } else {
            player.inventoryMenu.clicked(targetIndex, mouseButton, type, player);
            player.inventoryMenu.broadcastChanges();
        }

        ci.cancel();
    }

    @Unique
    private boolean swt$handleInventoryAccessoryQuickMove(Slot slot, Player player, int targetIndex) {
        ItemStack stack = slot.getItem();
        if (stack.isEmpty()) {
            return false;
        }

        SWTCreativeAccessorySlot accessorySlot = swt$findAccessoryQuickMoveTarget(stack);
        if (accessorySlot == null) {
            return false;
        }

        ItemStack movedStack = stack.copyWithCount(1);
        accessorySlot.set(movedStack);
        swt$playAccessoryEquipSound(player, accessorySlot, movedStack);
        swt$syncAccessorySlot(accessorySlot);

        ItemStack remainingStack = stack.copy();
        remainingStack.shrink(1);
        slot.set(remainingStack);
        swt$syncInventorySlot(targetIndex, remainingStack);
        player.inventoryMenu.broadcastChanges();
        return true;
    }

    @Unique
    private void swt$handleAccessoryQuickMove(SWTCreativeAccessorySlot slot, Player player) {
        ItemStack stack = slot.getItem();
        if (stack.isEmpty()) {
            return;
        }

        int freePlayerSlot = player.getInventory().getFreeSlot();
        if (freePlayerSlot < 0) {
            return;
        }

        int menuSlot = swt$toInventoryMenuSlot(freePlayerSlot);
        if (menuSlot < 0 || menuSlot >= player.inventoryMenu.slots.size()) {
            return;
        }

        ItemStack movedStack = stack.copy();
        player.inventoryMenu.getSlot(menuSlot).set(movedStack);
        swt$syncInventorySlot(menuSlot, movedStack);

        slot.set(ItemStack.EMPTY);
        swt$syncAccessorySlot(slot);
        player.inventoryMenu.broadcastChanges();
    }

    @Unique
    private static void swt$renderEntityInInventoryWithoutScissor(
        GuiGraphics guiGraphics,
        int x1,
        int y1,
        int x2,
        int y2,
        int scale,
        float yOffset,
        float mouseX,
        float mouseY,
        LivingEntity entity
    ) {
        float centerX = (float) (x1 + x2) / 2.0F;
        float centerY = (float) (y1 + y2) / 2.0F;
        float angleX = (float) Math.atan((centerX - mouseX) / 40.0F);
        float angleY = (float) Math.atan((centerY - mouseY) / 40.0F);

        Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf camera = new Quaternionf().rotateX(angleY * 20.0F * (float) (Math.PI / 180.0));
        pose.mul(camera);

        float bodyRot = entity.yBodyRot;
        float yRot = entity.getYRot();
        float xRot = entity.getXRot();
        float headRotO = entity.yHeadRotO;
        float headRot = entity.yHeadRot;

        entity.yBodyRot = 180.0F + angleX * 20.0F;
        entity.setYRot(180.0F + angleX * 40.0F);
        entity.setXRot(-angleY * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();

        float entityScale = entity.getScale();
        Vector3f translate = new Vector3f(0.0F, entity.getBbHeight() / 2.0F + yOffset * entityScale, 0.0F);
        float renderScale = (float) scale / entityScale;

        InventoryScreen.renderEntityInInventory(guiGraphics, centerX, centerY, renderScale, translate, pose, camera, entity);

        entity.yBodyRot = bodyRot;
        entity.setYRot(yRot);
        entity.setXRot(xRot);
        entity.yHeadRotO = headRotO;
        entity.yHeadRot = headRot;
    }

    @Unique
    private void swt$addAccessorySlot(Player player, @Nullable io.wispforest.accessories.api.slot.SlotTypeReference slotReference, int x, int y, Component emptyTooltip) {
        if (slotReference != null) {
            this.menu.slots.add(new SWTCreativeAccessorySlot(player, slotReference, x, y, emptyTooltip));
        }
    }

    @Unique
    private static int swt$getTargetIndex(@Nullable Slot slot) {
        if (slot instanceof SWTCreativeInventorySlot creativeInventorySlot) {
            return creativeInventorySlot.getTargetIndex();
        }
        return -1;
    }

    @Unique
    private void swt$handleAccessorySlotClick(SWTCreativeAccessorySlot slot, Player player, int mouseButton, ClickType type) {
        if (type == ClickType.PICKUP) {
            swt$handleAccessoryPickup(slot);
            return;
        }
        if (type == ClickType.THROW && slot.hasItem()) {
            ItemStack removedStack = slot.remove(mouseButton == 0 ? 1 : slot.getItem().getCount());
            player.drop(removedStack, true);
            this.minecraft.gameMode.handleCreativeModeItemDrop(removedStack);
            swt$syncAccessorySlot(slot);
        } else if (type == ClickType.THROW && !this.menu.getCarried().isEmpty()) {
            player.drop(this.menu.getCarried(), true);
            this.minecraft.gameMode.handleCreativeModeItemDrop(this.menu.getCarried());
            this.menu.setCarried(ItemStack.EMPTY);
            swt$syncAccessorySlot(slot);
        }
    }

    @Unique
    private void swt$handleAccessoryPickup(SWTCreativeAccessorySlot slot) {
        ItemStack carried = this.menu.getCarried();
        ItemStack slotStack = slot.getItem();

        if (carried.isEmpty()) {
            if (!slotStack.isEmpty()) {
                this.menu.setCarried(slotStack.copy());
                slot.set(ItemStack.EMPTY);
                swt$syncAccessorySlot(slot);
            }
            return;
        }

        if (!slot.mayPlace(carried)) {
            return;
        }

        if (slotStack.isEmpty()) {
            ItemStack newCarried = carried.copy();
            newCarried.shrink(1);
            this.menu.setCarried(newCarried);
            slot.set(carried.copyWithCount(1));
            swt$playAccessoryEquipSound(this.minecraft.player, slot, slot.getItem());
            swt$syncAccessorySlot(slot);
            return;
        }

        if (carried.getCount() != 1) {
            return;
        }

        this.menu.setCarried(slotStack.copy());
        slot.set(carried.copy());
        swt$playAccessoryEquipSound(this.minecraft.player, slot, slot.getItem());
        swt$syncAccessorySlot(slot);
    }

    @Unique
    private void swt$syncAccessorySlot(SWTCreativeAccessorySlot slot) {
        PacketDistributor.sendToServer(new CreativeAccessorySlotPacket(
            SWAetherSlotRefs.location(slot.getSlotReference()),
            slot.getItem().copy(),
            this.menu.getCarried().copy()
        ));
    }

    @Unique
    private void swt$playAccessoryEquipSound(Player player, SWTCreativeAccessorySlot slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        AccessoriesAPI.getOrDefaultAccessory(stack).onEquipFromUse(stack, SlotReference.of(player, slot.getSlotReference().slotName(), 0));
    }

    @Unique
    private void swt$syncInventorySlot(int slotIndex, ItemStack stack) {
        this.minecraft.gameMode.handleCreativeModeItemAdd(stack.copy(), slotIndex);
    }

    @Unique
    @Nullable
    private SWTCreativeAccessorySlot swt$findAccessoryQuickMoveTarget(ItemStack stack) {
        for (Slot menuSlot : this.menu.slots) {
            if (menuSlot instanceof SWTCreativeAccessorySlot accessorySlot && !accessorySlot.hasItem() && accessorySlot.mayPlace(stack)) {
                return accessorySlot;
            }
        }

        return null;
    }

    @Unique
    private static int swt$toInventoryMenuSlot(int playerInventorySlot) {
        if (playerInventorySlot < 0 || playerInventorySlot > 35) {
            return -1;
        }

        return playerInventorySlot < 9 ? 36 + playerInventorySlot : playerInventorySlot;
    }

    @Unique
    private static int swt$getSlotX(int slotIndex) {
        if (slotIndex >= 5 && slotIndex < 9) {
            int armorIndex = slotIndex - 5;
            return 54 + (armorIndex / 2) * 54;
        }
        if (slotIndex == 45) {
            return 35;
        }
        if (slotIndex < 9) {
            return -2000;
        }
        int inventoryIndex = slotIndex - 9;
        return 9 + inventoryIndex % 9 * 18;
    }

    @Unique
    private static int swt$getSlotY(int slotIndex) {
        if (slotIndex >= 5 && slotIndex < 9) {
            int armorIndex = slotIndex - 5;
            return 6 + (armorIndex % 2) * 27;
        }
        if (slotIndex == 45) {
            return 20;
        }
        if (slotIndex < 9) {
            return -2000;
        }
        return slotIndex >= 36 ? 112 : 54 + (slotIndex - 9) / 9 * 18;
    }
}
