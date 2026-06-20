package com.codex.armortemplatejson.plugin;

import com.codex.armortemplatejson.component.ModDataComponents;
import com.codex.armortemplatejson.item.ArmorTemplateBinding;
import com.codex.armortemplatejson.template.ArmorTemplateSlot;
import net.minecraft.world.item.ItemStack;

public final class PluginRules {
    private PluginRules() {
    }

    public static boolean isPluginStack(ItemStack stack) {
        return !stack.isEmpty() && stack.has(ModDataComponents.PLUGIN.get());
    }

    public static boolean canPlacePlugin(ItemStack stack, ArmorTemplateBinding armorBinding) {
        PluginBinding pluginBinding = stack.get(ModDataComponents.PLUGIN.get());
        if (pluginBinding == null) {
            return false;
        }
        if (armorBinding == null) {
            return true;
        }
        if (armorBinding.slot() != ArmorTemplateSlot.BODY) {
            return false;
        }
        if (pluginBinding.level() > armorBinding.maxPluginLevel()) {
            return false;
        }
        return armorBinding.pluginCategories().isEmpty() || armorBinding.pluginCategories().contains(pluginBinding.category());
    }
}
