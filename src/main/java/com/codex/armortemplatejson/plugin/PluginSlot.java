package com.codex.armortemplatejson.plugin;

import com.codex.armortemplatejson.component.ModDataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PluginSlot extends Slot {
    public PluginSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return isPluginStack(stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    public static boolean isPluginStack(ItemStack stack) {
        return !stack.isEmpty() && stack.has(ModDataComponents.PLUGIN.get());
    }
}
