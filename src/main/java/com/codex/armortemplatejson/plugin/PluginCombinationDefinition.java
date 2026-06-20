package com.codex.armortemplatejson.plugin;

import com.codex.armortemplatejson.text.LocalizedText;
import com.codex.armortemplatejson.effect.TieredEffectDefinition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public record PluginCombinationDefinition(
        LocalizedText name,
        int minLevel,
        List<List<PluginRequirement>> combinations,
        TieredEffectDefinition effects
) {
    private static final Codec<Integer> MIN_LEVEL_CODEC = Codec.INT.flatXmap(PluginCombinationDefinition::validateMinLevel, DataResult::success);

    private static final Codec<PluginCombinationDefinition> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LocalizedText.CODEC.fieldOf("name").forGetter(definition -> definition.name),
            MIN_LEVEL_CODEC.optionalFieldOf("min_level", 0).forGetter(definition -> definition.minLevel),
            PluginRequirement.CODEC.listOf().listOf().fieldOf("combinations").forGetter(definition -> definition.combinations),
            TieredEffectDefinition.CODEC.optionalFieldOf("effects", TieredEffectDefinition.EMPTY).forGetter(definition -> definition.effects)
    ).apply(instance, PluginCombinationDefinition::new));

    public static final Codec<PluginCombinationDefinition> CODEC =
            BASE_CODEC.flatXmap(PluginCombinationDefinition::validate, DataResult::success);

    public PluginCombinationDefinition {
        Objects.requireNonNull(name, "name");
        combinations = copyCombinations(combinations);
        Objects.requireNonNull(effects, "effects");
    }

    public boolean matches(List<PluginBinding> activePlugins) {
        return !activeEffects(activePlugins).isEmpty();
    }

    public List<ResourceLocation> activeEffects(List<PluginBinding> activePlugins) {
        Set<ResourceLocation> active = new LinkedHashSet<>();
        for (List<PluginRequirement> combination : combinations) {
            int matchedLevel = matchedLevel(combination, activePlugins);
            if (matchedLevel >= minLevel) {
                active.addAll(effects.activeEffects(matchedLevel));
            }
        }
        return List.copyOf(active);
    }

    private static int matchedLevel(List<PluginRequirement> combination, List<PluginBinding> activePlugins) {
        boolean[] used = new boolean[activePlugins.size()];
        int minMatchedLevel = Integer.MAX_VALUE;
        for (PluginRequirement requirement : combination) {
            int matchedIndex = firstUnusedMatch(requirement, activePlugins, used);
            if (matchedIndex < 0) {
                return -1;
            }
            used[matchedIndex] = true;
            minMatchedLevel = Math.min(minMatchedLevel, activePlugins.get(matchedIndex).level());
        }
        return minMatchedLevel == Integer.MAX_VALUE ? -1 : minMatchedLevel;
    }

    private static int firstUnusedMatch(PluginRequirement requirement, List<PluginBinding> activePlugins, boolean[] used) {
        for (int i = 0; i < activePlugins.size(); i++) {
            if (!used[i] && requirement.matches(activePlugins.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static DataResult<PluginCombinationDefinition> validate(PluginCombinationDefinition definition) {
        if (definition.combinations.isEmpty()) {
            return DataResult.error(() -> "plugin combination must contain at least one supported combination");
        }
        if (definition.combinations.stream().anyMatch(List::isEmpty)) {
            return DataResult.error(() -> "supported plugin combination entries must not be empty");
        }
        return DataResult.success(definition);
    }

    private static DataResult<Integer> validateMinLevel(int minLevel) {
        return minLevel >= 0
                ? DataResult.success(minLevel)
                : DataResult.error(() -> "min_level must be greater than or equal to 0");
    }

    private static List<List<PluginRequirement>> copyCombinations(List<List<PluginRequirement>> combinations) {
        Objects.requireNonNull(combinations, "combinations");
        List<List<PluginRequirement>> copied = new ArrayList<>(combinations.size());
        for (List<PluginRequirement> combination : combinations) {
            copied.add(List.copyOf(Objects.requireNonNull(combination, "combination")));
        }
        return List.copyOf(copied);
    }
}
