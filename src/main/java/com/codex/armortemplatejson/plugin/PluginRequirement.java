package com.codex.armortemplatejson.plugin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

public record PluginRequirement(ResourceLocation category, int minLevel) {
    public static final int DEFAULT_MIN_LEVEL = 0;
    private static final Codec<Integer> MIN_LEVEL_CODEC = Codec.INT.flatXmap(PluginRequirement::validateMinLevel, DataResult::success);

    public static final Codec<PluginRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("category").forGetter(PluginRequirement::category),
            MIN_LEVEL_CODEC.optionalFieldOf("min_level", DEFAULT_MIN_LEVEL).forGetter(PluginRequirement::minLevel)
    ).apply(instance, PluginRequirement::new));

    public PluginRequirement {
        Objects.requireNonNull(category, "category");
        if (minLevel < 0) {
            throw new IllegalArgumentException("min_level must be greater than or equal to 0");
        }
    }

    public boolean matches(PluginBinding binding) {
        return category.equals(binding.category()) && binding.level() >= minLevel;
    }

    private static DataResult<Integer> validateMinLevel(int minLevel) {
        return minLevel >= 0
                ? DataResult.success(minLevel)
                : DataResult.error(() -> "min_level must be greater than or equal to 0");
    }
}
