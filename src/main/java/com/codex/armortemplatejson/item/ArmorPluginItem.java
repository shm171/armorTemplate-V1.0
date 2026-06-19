package com.codex.armortemplatejson.item;

import com.codex.armortemplatejson.component.ModDataComponents;
import com.codex.armortemplatejson.plugin.PluginBinding;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ArmorPluginItem extends Item {
    public ArmorPluginItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        PluginBinding binding = stack.get(ModDataComponents.PLUGIN.get());
        return binding == null ? super.getName(stack) : binding.name().toComponent();
    }
}
