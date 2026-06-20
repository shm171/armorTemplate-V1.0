package com.codex.armortemplatejson.plugin;

import com.codex.armortemplatejson.consumption.ConsumptionState;
import com.codex.armortemplatejson.effect.TieredEffectDefinition;
import com.codex.armortemplatejson.item.VisualDefinition;
import com.codex.armortemplatejson.text.LocalizedText;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record PluginDefinition(
        int level,
        ResourceLocation category,
        ResourceLocation icon,
        LocalizedText name,
        Optional<LocalizedText> description,
        VisualDefinition visual,
        TieredEffectDefinition effects,
        List<ConsumptionState> consumption
) {
    private static final Codec<Integer> LEVEL_CODEC = Codec.INT.flatXmap(PluginDefinition::validateLevel, DataResult::success);

    public static final Codec<PluginDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LEVEL_CODEC.fieldOf("level").forGetter(PluginDefinition::level),
            ResourceLocation.CODEC.fieldOf("category").forGetter(PluginDefinition::category),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(PluginDefinition::icon),
            LocalizedText.CODEC.fieldOf("name").forGetter(PluginDefinition::name),
            LocalizedText.CODEC.optionalFieldOf("description").forGetter(PluginDefinition::description),
            VisualDefinition.CODEC.optionalFieldOf("visual", VisualDefinition.EMPTY).forGetter(PluginDefinition::visual),
            TieredEffectDefinition.CODEC.optionalFieldOf("effects", TieredEffectDefinition.EMPTY).forGetter(PluginDefinition::effects),
            ConsumptionState.CODEC.listOf().optionalFieldOf("consumption", List.of()).forGetter(PluginDefinition::consumption)
    ).apply(instance, PluginDefinition::new));

    public PluginDefinition {
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(icon, "icon");
        Objects.requireNonNull(name, "name");
        description = Objects.requireNonNull(description, "description");
        Objects.requireNonNull(visual, "visual");
        Objects.requireNonNull(effects, "effects");
        consumption = List.copyOf(Objects.requireNonNull(consumption, "consumption"));
    }

    private static DataResult<Integer> validateLevel(int level) {
        return level >= 0 ? DataResult.success(level) : DataResult.error(() -> "plugin level must be greater than or equal to 0");
    }
}
