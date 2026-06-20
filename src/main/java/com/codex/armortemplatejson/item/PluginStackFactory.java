package com.codex.armortemplatejson.item;

import com.codex.armortemplatejson.component.ModDataComponents;
import com.codex.armortemplatejson.plugin.PluginBinding;
import com.codex.armortemplatejson.plugin.PluginDefinition;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class PluginStackFactory {
    private PluginStackFactory() {
    }

    public static ItemStack createStack(ResourceLocation pluginId, PluginDefinition definition) {
        PluginBinding binding = PluginBinding.from(pluginId, definition);
        ItemStack stack = new ItemStack(ModItems.ARMOR_PLUGIN.get());
        stack.set(ModDataComponents.PLUGIN.get(), binding);
        stack.set(DataComponents.ITEM_NAME, binding.name().toComponent());
        ItemTextComponents.applyLore(stack, binding.description());
        ItemVisuals.apply(stack, binding.visual());
        return stack;
    }
}
