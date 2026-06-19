package com.codex.armortemplatejson.item;

import com.codex.armortemplatejson.ArmorTemplateJsonMod;
import com.codex.armortemplatejson.component.ModDataComponents;
import com.codex.armortemplatejson.plugin.PluginContainerComponent;
import com.codex.armortemplatejson.template.ArmorTemplate;
import com.codex.armortemplatejson.template.AttributeValueOperation;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public final class ArmorTemplateStackFactory {
    private ArmorTemplateStackFactory() {
    }

    public static ItemStack createStack(ResourceLocation templateId, ArmorTemplate template) {
        ArmorTemplateBinding binding = ArmorTemplateBinding.from(templateId, template);
        ItemStack stack = new ItemStack(ModItems.forSlot(binding.slot()).get());
        stack.set(ModDataComponents.ARMOR_TEMPLATE.get(), binding);
        stack.set(DataComponents.ITEM_NAME, binding.name().toComponent());
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, buildAttributeModifiers(binding));
        if (binding.pluginSlots() > 0) {
            stack.set(ModDataComponents.PLUGIN_CONTAINER.get(), PluginContainerComponent.empty(binding.pluginSlots()));
        }
        return stack;
    }

    public static ItemAttributeModifiers buildAttributeModifiers(ArmorTemplateBinding binding) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(binding.slot().equipmentSlot());
        for (int i = 0; i < binding.vanillaAttributes().size(); i++) {
            int modifierIndex = i;
            var definition = binding.vanillaAttributes().get(i);
            Optional<ModifierSpec> modifier = modifierSpec(definition.operation(), definition.value());
            if (modifier.isEmpty()) {
                ArmorTemplateJsonMod.LOGGER.warn("Skipped unsupported armor attribute operation '{}' on {}", definition.operation().serializedName(), binding.templateId());
                continue;
            }

            BuiltInRegistries.ATTRIBUTE.getHolder(definition.type()).ifPresent(attribute -> builder.add(
                    attribute,
                    new AttributeModifier(modifierId(binding.templateId(), modifierIndex), modifier.get().amount(), modifier.get().operation()),
                    slotGroup
            ));
        }
        return builder.build();
    }

    private static Optional<ModifierSpec> modifierSpec(AttributeValueOperation operation, double value) {
        return switch (operation) {
            case ADD -> Optional.of(new ModifierSpec(value, AttributeModifier.Operation.ADD_VALUE));
            case SUBTRACT -> Optional.of(new ModifierSpec(-value, AttributeModifier.Operation.ADD_VALUE));
            case MULTIPLY -> Optional.of(new ModifierSpec(value - 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            case DIVIDE -> value == 0.0D
                    ? Optional.empty()
                    : Optional.of(new ModifierSpec((1.0D / value) - 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            case MODULO -> Optional.empty();
        };
    }

    private static ResourceLocation modifierId(ResourceLocation templateId, int index) {
        return ArmorTemplateJsonMod.id("armor_template/" + templateId.getNamespace() + "/" + templateId.getPath() + "/" + index);
    }

    private record ModifierSpec(double amount, AttributeModifier.Operation operation) {
    }
}
