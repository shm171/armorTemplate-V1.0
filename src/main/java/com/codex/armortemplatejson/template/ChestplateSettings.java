package com.codex.armortemplatejson.template;

import com.codex.armortemplatejson.consumption.ConsumptionState;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

public record ChestplateSettings(
        int pluginSlots,
        List<ResourceLocation> pluginCategories,
        int maxPluginLevel,
        List<ConsumptionState> consumption
) {
    public static final ChestplateSettings EMPTY = new ChestplateSettings(0, List.of(), 0, List.of());

    private static final Codec<Integer> PLUGIN_SLOTS_CODEC = Codec.INT.flatXmap(ChestplateSettings::validatePluginSlots, DataResult::success);
    private static final Codec<Integer> MAX_PLUGIN_LEVEL_CODEC = Codec.INT.flatXmap(ChestplateSettings::validateMaxPluginLevel, DataResult::success);

    public static final Codec<ChestplateSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PLUGIN_SLOTS_CODEC.optionalFieldOf("plugin_numbers", 0).forGetter(ChestplateSettings::pluginSlots),
            ResourceLocation.CODEC.listOf().optionalFieldOf("plugin_categories", List.of()).forGetter(ChestplateSettings::pluginCategories),
            MAX_PLUGIN_LEVEL_CODEC.optionalFieldOf("max_plugin_level", 0).forGetter(ChestplateSettings::maxPluginLevel),
            ConsumptionState.CODEC.listOf().optionalFieldOf("consumption", List.of()).forGetter(ChestplateSettings::consumption)
    ).apply(instance, ChestplateSettings::new));

    public ChestplateSettings {
        pluginCategories = List.copyOf(Objects.requireNonNull(pluginCategories, "pluginCategories"));
        consumption = List.copyOf(Objects.requireNonNull(consumption, "consumption"));
    }

    private static DataResult<Integer> validatePluginSlots(int pluginSlots) {
        return pluginSlots >= 0 && pluginSlots <= 6
                ? DataResult.success(pluginSlots)
                : DataResult.error(() -> "plugin_numbers must be between 0 and 6");
    }

    private static DataResult<Integer> validateMaxPluginLevel(int maxPluginLevel) {
        return maxPluginLevel >= 0
                ? DataResult.success(maxPluginLevel)
                : DataResult.error(() -> "max_plugin_level must be greater than or equal to 0");
    }
}
