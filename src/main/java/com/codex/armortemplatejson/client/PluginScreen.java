package com.codex.armortemplatejson.client;

import com.codex.armortemplatejson.plugin.PluginMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class PluginScreen extends AbstractContainerScreen<PluginMenu> {
    private static final int BACKGROUND = 0xFFCEC3AF;
    private static final int BORDER_DARK = 0xFF3F352A;
    private static final int SLOT_DARK = 0xFF6B5F50;
    private static final int SLOT_LIGHT = 0xFFE8DDC9;
    private static final int SLOT_FILL = 0xFFB8AB96;

    public PluginScreen(PluginMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = PluginMenu.IMAGE_WIDTH;
        this.imageHeight = PluginMenu.IMAGE_HEIGHT;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;
        guiGraphics.fill(left, top, left + this.imageWidth, top + this.imageHeight, BACKGROUND);
        guiGraphics.fill(left, top, left + this.imageWidth, top + 1, SLOT_LIGHT);
        guiGraphics.fill(left, top, left + 1, top + this.imageHeight, SLOT_LIGHT);
        guiGraphics.fill(left, top + this.imageHeight - 1, left + this.imageWidth, top + this.imageHeight, BORDER_DARK);
        guiGraphics.fill(left + this.imageWidth - 1, top, left + this.imageWidth, top + this.imageHeight, BORDER_DARK);

        for (Slot slot : this.menu.slots) {
            drawSlotFrame(guiGraphics, left + slot.x - 1, top + slot.y - 1);
        }
    }

    private static void drawSlotFrame(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 18, y + 18, SLOT_DARK);
        guiGraphics.fill(x + 1, y + 1, x + 18, y + 18, SLOT_LIGHT);
        guiGraphics.fill(x + 1, y + 1, x + 17, y + 17, SLOT_FILL);
    }
}
