package com.codex.armortemplatejson.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record VisualDefinition(Optional<ResourceLocation> texture, Optional<ResourceLocation> model, Optional<Integer> customModelData) {
    public static final VisualDefinition EMPTY = new VisualDefinition(Optional.empty(), Optional.empty(), Optional.empty());

    private static final Codec<Integer> CUSTOM_MODEL_DATA_CODEC = Codec.INT.flatXmap(VisualDefinition::validateCustomModelData, DataResult::success);

    public static final Codec<VisualDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(VisualDefinition::texture),
            ResourceLocation.CODEC.optionalFieldOf("model").forGetter(VisualDefinition::model),
            CUSTOM_MODEL_DATA_CODEC.optionalFieldOf("custom_model_data").forGetter(VisualDefinition::customModelData)
    ).apply(instance, VisualDefinition::new));

    public VisualDefinition {
        texture = Objects.requireNonNull(texture, "texture");
        model = Objects.requireNonNull(model, "model");
        customModelData = Objects.requireNonNull(customModelData, "customModelData");
    }

    private static DataResult<Integer> validateCustomModelData(int customModelData) {
        return customModelData >= 0
                ? DataResult.success(customModelData)
                : DataResult.error(() -> "custom_model_data must be greater than or equal to 0");
    }
}
