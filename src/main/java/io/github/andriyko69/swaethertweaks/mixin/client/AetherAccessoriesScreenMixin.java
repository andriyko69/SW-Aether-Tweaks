package io.github.andriyko69.swaethertweaks.mixin.client;

import com.aetherteam.aether.client.gui.component.inventory.AccessoryButton;
import com.aetherteam.aether.client.gui.screen.inventory.AetherAccessoriesScreen;
import com.aetherteam.aether.inventory.menu.AetherAccessoriesMenu;
import io.github.andriyko69.swaethertweaks.mixin.accessor.ScreenAccessor;
import io.wispforest.accessories.api.menu.AccessoriesBasedSlot;
import io.wispforest.accessories.client.gui.ToggleButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AetherAccessoriesScreen.class)
public abstract class AetherAccessoriesScreenMixin extends EffectRenderingInventoryScreen<AetherAccessoriesMenu> {
    @Unique
    private static final ResourceLocation SWT_ACCESSORIES_INVENTORY = ResourceLocation.fromNamespaceAndPath("swaethertweaks", "textures/gui/inventory/accessories.png");
    @Unique
    private static final ResourceLocation SWT_ACCESSORIES_INVENTORY_CREATIVE = ResourceLocation.fromNamespaceAndPath("swaethertweaks", "textures/gui/inventory/accessories_creative.png");

    protected AetherAccessoriesScreenMixin(AetherAccessoriesMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Shadow(remap = false)
    protected abstract void updateScreenPosition();

    @Shadow(remap = false)
    public abstract RecipeBookComponent getRecipeBookComponent();

    @Final
    @Shadow(remap = false)
    private Map<AccessoriesBasedSlot, ToggleButton> cosmeticButtons;

    @Inject(method = "init", at = @At("TAIL"), remap = false)
    private void swt$removeRecipeAndPerkButtons(CallbackInfo ci) {
        if (this.getRecipeBookComponent().isVisible()) {
            this.getRecipeBookComponent().toggleVisibility();
        }
        this.swt$removeExtraElements();
        this.setFocused(null);
        this.updateScreenPosition();
    }

    @Inject(method = "render", at = @At("HEAD"), remap = false)
    private void swt$removeCosmeticButtonsEveryFrame(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        this.swt$removeExtraElements();
    }

    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$renderReducedBackground(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.player != null) {
            int left = this.leftPos;
            int top = this.topPos;
            guiGraphics.blit(this.minecraft.player.isCreative() ? SWT_ACCESSORIES_INVENTORY_CREATIVE : SWT_ACCESSORIES_INVENTORY, left, top, 0, 0, this.imageWidth, this.imageHeight);
            InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, left + 26, top + 8, left + 75, top + 78, 30, 0.0625F, mouseX, mouseY, this.minecraft.player);
            ci.cancel();
        }
    }

    @Inject(method = "renderLabels", at = @At("HEAD"), cancellable = true, remap = false)
    private void swt$renderMovedTitleAndXpLabel(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.minecraft != null && this.minecraft.player != null) {
            guiGraphics.drawString(this.font, this.title, 109, 10, 4210752, false);
            if (!this.minecraft.player.isCreative()) {
                Component xpLabel = Component.translatable("gui.swaethertweaks.stat.xp", this.minecraft.player.experienceLevel);
                guiGraphics.drawString(this.font, xpLabel, 170 - this.font.width(xpLabel), 72, 4210752, false);
            }
            ci.cancel();
        }
    }

    @Unique
    private void swt$removeExtraElements() {
        this.cosmeticButtons.clear();
        this.renderables.removeIf(AetherAccessoriesScreenMixin::swt$isExtraElement);
        this.children().removeIf(AetherAccessoriesScreenMixin::swt$isExtraElement);
        ((ScreenAccessor) this).swt$getNarratables().removeIf(AetherAccessoriesScreenMixin::swt$isExtraElement);
    }

    @Unique
    private static boolean swt$isExtraElement(Object element) {
        return element instanceof RecipeBookComponent
            || element instanceof ToggleButton
            || element instanceof ImageButton && !(element instanceof AccessoryButton);
    }
}
