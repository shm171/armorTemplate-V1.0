package com.codex.armortemplatejson.effect;

import com.codex.armortemplatejson.component.ModDataComponents;
import com.codex.armortemplatejson.item.ArmorTemplateBinding;
import com.codex.armortemplatejson.template.ArmorTemplateSlot;
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
        }

        if (effectIds.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new ArmorEffectContext(wearer, level, stacks, bindings, effectIds));
    }

    private record SlotRule(EquipmentSlot equipmentSlot, ArmorTemplateSlot templateSlot) {
    }
}
