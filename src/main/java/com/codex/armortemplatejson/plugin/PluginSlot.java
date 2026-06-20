package com.codex.armortemplatejson.plugin;

import com.codex.armortemplatejson.item.ArmorTemplateBinding;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PluginSlot extends Slot {
    private final ArmorTemplateBinding armorBinding;

    public PluginSlot(Container container, int slot, int x, int y, ArmorTemplateBinding armorBinding) {
        super(container, slot, x, y);
        this.armorBinding = armorBinding;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return PluginRules.canPlacePlugin(stack, armorBinding);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
