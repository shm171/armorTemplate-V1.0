package com.codex.armortemplatejson.plugin;

import com.codex.armortemplatejson.component.ModDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PluginMenu extends AbstractContainerMenu {
    public static final int IMAGE_WIDTH = 176;
    public static final int IMAGE_HEIGHT = 166;

    private static final int PLAYER_INVENTORY_START_Y = 84;
    private static final int HOTBAR_START_Y = 142;

    private final SimpleContainer pluginContainer;
    private final int pluginSlotCount;
    private final int lockedHotbarSlot;
    private final ItemStack editedArmorStack;

    public PluginMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, ItemStack.EMPTY, ClientMenuData.read(extraData));
    }

    public PluginMenu(int containerId, Inventory playerInventory, ItemStack editedArmorStack, int pluginSlotCount) {
        this(containerId, playerInventory, editedArmorStack, new ClientMenuData(pluginSlotCount, playerInventory.selected));
    }

    private PluginMenu(int containerId, Inventory playerInventory, ItemStack editedArmorStack, ClientMenuData menuData) {
        super(ModMenus.PLUGIN_MENU.get(), containerId);
        this.pluginSlotCount = Math.max(0, Math.min(menuData.pluginSlotCount(), PluginContainerComponent.MAX_PLUGIN_SLOTS));
        this.lockedHotbarSlot = menuData.lockedHotbarSlot();
        this.editedArmorStack = editedArmorStack;
        this.pluginContainer = createPluginContainer(editedArmorStack, this.pluginSlotCount);
        this.pluginContainer.startOpen(playerInventory.player);

        addPluginSlots();
        addPlayerInventorySlots(playerInventory);
    }

    public int pluginSlotCount() {
        return pluginSlotCount;
    }

    @Override
    public boolean stillValid(Player player) {
        if (editedArmorStack.isEmpty()) {
            return true;
        }
        return lockedHotbarSlot >= 0 && lockedHotbarSlot < Inventory.getSelectionSize()
                && player.getInventory().getItem(lockedHotbarSlot) == editedArmorStack;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (clickType == ClickType.SWAP && button == lockedHotbarSlot) {
            return;
        }
        if (slotId >= 0 && slotId < this.slots.size() && this.slots.get(slotId) instanceof LockedPlayerSlot) {
            return;
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem() || slot instanceof LockedPlayerSlot) {
            return result;
        }

        ItemStack source = slot.getItem();
        result = source.copy();
        if (index < pluginSlotCount) {
            if (!this.moveItemStackTo(source, pluginSlotCount, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (PluginSlot.isPluginStack(source)) {
            if (!this.moveItemStackTo(source, 0, pluginSlotCount, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (source.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            if (!editedArmorStack.isEmpty() && stillValid(player)) {
                editedArmorStack.set(ModDataComponents.PLUGIN_CONTAINER.get(), PluginContainerComponent.fromContainer(pluginContainer, pluginSlotCount));
            } else {
                this.clearContainer(player, pluginContainer);
            }
        }
        pluginContainer.stopOpen(player);
    }

    private void addPluginSlots() {
        int startX = 8 + (9 - pluginSlotCount) * 9;
        for (int i = 0; i < pluginSlotCount; i++) {
            this.addSlot(new PluginSlot(pluginContainer, i, startX + i * 18, 20));
        }
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, PLAYER_INVENTORY_START_Y + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            Slot slot = column == lockedHotbarSlot
                    ? new LockedPlayerSlot(playerInventory, column, 8 + column * 18, HOTBAR_START_Y)
                    : new Slot(playerInventory, column, 8 + column * 18, HOTBAR_START_Y);
            this.addSlot(slot);
        }
    }

    private static SimpleContainer createPluginContainer(ItemStack armorStack, int pluginSlotCount) {
        SimpleContainer container = new SimpleContainer(pluginSlotCount);
        PluginContainerComponent component = armorStack.get(ModDataComponents.PLUGIN_CONTAINER.get());
        var stacks = component == null
                ? PluginContainerComponent.empty(pluginSlotCount).copyForSize(pluginSlotCount)
                : component.copyForSize(pluginSlotCount);
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            container.setItem(i, PluginSlot.isPluginStack(stack) ? stack : ItemStack.EMPTY);
        }
        return container;
    }

    private record ClientMenuData(int pluginSlotCount, int lockedHotbarSlot) {
        static ClientMenuData read(RegistryFriendlyByteBuf extraData) {
            if (extraData == null) {
                return new ClientMenuData(0, -1);
            }
            return new ClientMenuData(extraData.readVarInt(), extraData.readVarInt());
        }
    }

    private static class LockedPlayerSlot extends Slot {
        LockedPlayerSlot(Inventory inventory, int slot, int x, int y) {
            super(inventory, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
    }
}
