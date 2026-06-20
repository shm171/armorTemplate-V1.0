package com.codex.armortemplatejson.effect;

import com.codex.armortemplatejson.component.ModDataComponents;
import com.codex.armortemplatejson.item.ArmorTemplateBinding;
import com.codex.armortemplatejson.plugin.PluginBinding;
import com.codex.armortemplatejson.plugin.PluginCombinationDefinition;
import com.codex.armortemplatejson.plugin.PluginContainerComponent;
import com.codex.armortemplatejson.plugin.PluginRules;
import com.codex.armortemplatejson.template.ArmorTemplateSlot;
import com.codex.armortemplatejson.template.ModArmorTemplateRegistries;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class ArmorSuitResolver {
    private static final SlotRule[] REQUIRED_SLOTS = {
            new SlotRule(EquipmentSlot.HEAD, ArmorTemplateSlot.HEAD),
            new SlotRule(EquipmentSlot.CHEST, ArmorTemplateSlot.BODY),
            new SlotRule(EquipmentSlot.LEGS, ArmorTemplateSlot.LEGGING),
            new SlotRule(EquipmentSlot.FEET, ArmorTemplateSlot.FEET)
    };

    private ArmorSuitResolver() {
    }

    public static Optional<ArmorEffectContext> activeContext(LivingEntity wearer) {
        List<ItemStack> stacks = new ArrayList<>(REQUIRED_SLOTS.length);
        List<ArmorTemplateBinding> bindings = new ArrayList<>(REQUIRED_SLOTS.length);
        List<PluginBinding> pluginBindings = new ArrayList<>();
        Set<ResourceLocation> effectIds = new LinkedHashSet<>();
        Integer level = null;

        for (SlotRule rule : REQUIRED_SLOTS) {
            ItemStack stack = wearer.getItemBySlot(rule.equipmentSlot());
            ArmorTemplateBinding binding = stack.get(ModDataComponents.ARMOR_TEMPLATE.get());
            if (binding == null || binding.slot() != rule.templateSlot()) {
                return Optional.empty();
            }
            if (level == null) {
                level = binding.level();
            } else if (level != binding.level()) {
                return Optional.empty();
            }

            stacks.add(stack);
            bindings.add(binding);
            effectIds.addAll(binding.effects());
            collectPluginEffects(stack, binding, pluginBindings, effectIds);
        }

        collectPluginCombinationEffects(wearer, pluginBindings, effectIds);

        if (effectIds.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new ArmorEffectContext(wearer, level, stacks, bindings, effectIds));
    }

    private static void collectPluginEffects(ItemStack armorStack, ArmorTemplateBinding armorBinding, List<PluginBinding> pluginBindings, Set<ResourceLocation> effectIds) {
        PluginContainerComponent component = armorStack.get(ModDataComponents.PLUGIN_CONTAINER.get());
        if (component == null) {
            return;
        }
        for (ItemStack pluginStack : component.copyForSize(armorBinding.pluginSlots())) {
            if (!PluginRules.canPlacePlugin(pluginStack, armorBinding)) {
                continue;
            }
            PluginBinding pluginBinding = pluginStack.get(ModDataComponents.PLUGIN.get());
            if (pluginBinding != null) {
                pluginBindings.add(pluginBinding);
                effectIds.addAll(pluginBinding.activeEffects());
            }
        }
    }

    private static void collectPluginCombinationEffects(LivingEntity wearer, List<PluginBinding> pluginBindings, Set<ResourceLocation> effectIds) {
        if (pluginBindings.isEmpty()) {
            return;
        }
        wearer.registryAccess().registry(ModArmorTemplateRegistries.PLUGIN_COMBINATIONS).ifPresent(combinations -> {
            for (ResourceLocation id : combinations.keySet()) {
                PluginCombinationDefinition combination = combinations.get(id);
                if (combination != null) {
                    effectIds.addAll(combination.activeEffects(pluginBindings));
                }
            }
        });
    }

    private record SlotRule(EquipmentSlot equipmentSlot, ArmorTemplateSlot templateSlot) {
    }
}
