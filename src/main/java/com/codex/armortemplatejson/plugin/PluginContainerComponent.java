package com.codex.armortemplatejson.plugin;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public record PluginContainerComponent(List<ItemStack> slots) {
    public static final int MAX_PLUGIN_SLOTS = 6;

    public static final Codec<PluginContainerComponent> CODEC =
            ItemStack.OPTIONAL_CODEC.listOf().xmap(PluginContainerComponent::new, PluginContainerComponent::slots);

    public static final StreamCodec<RegistryFriendlyByteBuf, PluginContainerComponent> STREAM_CODEC =
            ItemStack.OPTIONAL_LIST_STREAM_CODEC.map(PluginContainerComponent::new, PluginContainerComponent::slots);

    public PluginContainerComponent {
        slots = List.copyOf(normalizeSlots(slots, Math.min(slots.size(), MAX_PLUGIN_SLOTS)));
    }

    public static PluginContainerComponent empty(int slotCount) {
        return new PluginContainerComponent(normalizeSlots(List.of(), slotCount));
    }

    public static PluginContainerComponent fromContainer(Container container, int slotCount) {
        List<ItemStack> stacks = new ArrayList<>(slotCount);
        for (int i = 0; i < slotCount; i++) {
            stacks.add(normalizeStack(container.getItem(i)));
        }
        return new PluginContainerComponent(stacks);
    }

    public List<ItemStack> copyForSize(int slotCount) {
        return normalizeSlots(this.slots, slotCount);
    }

    private static List<ItemStack> normalizeSlots(List<ItemStack> source, int slotCount) {
        int safeSlotCount = Math.max(0, Math.min(MAX_PLUGIN_SLOTS, slotCount));
        List<ItemStack> normalized = new ArrayList<>(safeSlotCount);
        for (int i = 0; i < safeSlotCount; i++) {
            ItemStack stack = i < source.size() ? source.get(i) : ItemStack.EMPTY;
            normalized.add(normalizeStack(stack));
        }
        return normalized;
    }

    private static ItemStack normalizeStack(ItemStack stack) {
        return stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
    }
}
