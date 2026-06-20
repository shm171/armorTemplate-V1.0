package com.codex.armortemplatejson.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public record TieredEffectDefinition(
        List<ResourceLocation> baseEffects,
        List<ResourceLocation> perLevelEffects,
        List<LevelEffectDefinition> unlockEffects
) {
    public static final TieredEffectDefinition EMPTY = new TieredEffectDefinition(List.of(), List.of(), List.of());

    public static final Codec<TieredEffectDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EffectCodecs.EFFECT_LIST.optionalFieldOf("effect", List.of()).forGetter(TieredEffectDefinition::baseEffects),
            EffectCodecs.EFFECT_LIST.optionalFieldOf("per_level_effect", List.of()).forGetter(TieredEffectDefinition::perLevelEffects),
            LevelEffectDefinition.CODEC.listOf().optionalFieldOf("unlock_effect", List.of()).forGetter(TieredEffectDefinition::unlockEffects)
    ).apply(instance, TieredEffectDefinition::new));

    public TieredEffectDefinition {
        baseEffects = List.copyOf(Objects.requireNonNull(baseEffects, "baseEffects"));
        perLevelEffects = List.copyOf(Objects.requireNonNull(perLevelEffects, "perLevelEffects"));
        unlockEffects = List.copyOf(Objects.requireNonNull(unlockEffects, "unlockEffects"));
    }

    public List<ResourceLocation> activeEffects(int level) {
        Set<ResourceLocation> active = new LinkedHashSet<>(baseEffects);
        // 当前预定义效果是布尔式 ID；每级提升先按“等级大于 0 时提供该效果 ID”暴露给外部效果实现。
        if (level > 0) {
            active.addAll(perLevelEffects);
        }
        for (LevelEffectDefinition unlockEffect : unlockEffects) {
            if (level >= unlockEffect.level()) {
                active.addAll(unlockEffect.effects());
            }
        }
        return List.copyOf(active);
    }
}
