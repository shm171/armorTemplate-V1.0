package com.codex.armortemplatejson.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public record VanillaAttributeDefinition(ResourceLocation type, AttributeValueOperation operation, double value) {
    private static final Codec<ResourceLocation> VANILLA_ATTRIBUTE_CODEC =
            ResourceLocation.CODEC.flatXmap(VanillaAttributeDefinition::validateVanillaAttribute, DataResult::success);

    private static final Codec<VanillaAttributeDefinition> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VANILLA_ATTRIBUTE_CODEC.fieldOf("type").forGetter(VanillaAttributeDefinition::type),
            AttributeValueOperation.CODEC.fieldOf("operation").forGetter(VanillaAttributeDefinition::operation),
            Codec.DOUBLE.fieldOf("value").forGetter(VanillaAttributeDefinition::value)
    ).apply(instance, VanillaAttributeDefinition::new));

    public static final Codec<VanillaAttributeDefinition> CODEC =
            BASE_CODEC.flatXmap(VanillaAttributeDefinition::validateDefinition, DataResult::success);

    private static DataResult<ResourceLocation> validateVanillaAttribute(ResourceLocation id) {
        if (!ResourceLocation.DEFAULT_NAMESPACE.equals(id.getNamespace())) {
            return DataResult.error(() -> "attribute must use the minecraft namespace: " + id);
        }
        if (!BuiltInRegistries.ATTRIBUTE.containsKey(id)) {
            return DataResult.error(() -> "unknown vanilla attribute: " + id);
        }
        return DataResult.success(id);
    }

    private static DataResult<VanillaAttributeDefinition> validateDefinition(VanillaAttributeDefinition definition) {
        if (!Double.isFinite(definition.value)) {
            return DataResult.error(() -> "attribute value must be finite: " + definition.value);
        }

        return BuiltInRegistries.ATTRIBUTE.getHolder(definition.type)
                .map(holder -> {
                    double sanitized = holder.value().sanitizeValue(definition.value);
                    if (Double.compare(sanitized, definition.value) != 0) {
                        // 用原版 Attribute 的 sanitizeValue 做边界判定，避免 JSON 写入 MC 不接受的属性值。
                        return DataResult.<VanillaAttributeDefinition>error(() -> "attribute value " + definition.value
                                + " is outside vanilla range for " + definition.type);
                    }
                    return DataResult.success(definition);
                })
                .orElseGet(() -> DataResult.error(() -> "unknown vanilla attribute: " + definition.type));
    }
}
