package com.codex.armortemplatejson.plugin;

import com.codex.armortemplatejson.item.ArmorTemplateBinding;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;

public final class PluginMenuProvider {
    private PluginMenuProvider() {
    }

    public static void open(ServerPlayer player, ItemStack armorStack, ArmorTemplateBinding binding) {
        int lockedSlot = player.getInventory().selected;
        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, playerInventory, ignored) -> new PluginMenu(containerId, playerInventory, armorStack, binding.pluginSlots()),
                        Component.translatable("container.armortemplatejson.plugin_slots")
                ),
                buffer -> {
                    buffer.writeVarInt(binding.pluginSlots());
                    buffer.writeVarInt(lockedSlot);
                }
        );
    }
}
