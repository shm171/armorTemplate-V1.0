package com.codex.armortemplatejson.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

public record LevelEffectDefinition(int level, List<ResourceLocation> effects) {
    private static final Codec<Integer> LEVEL_CODEC = Codec.INT.flatXmap(LevelEffectDefinition::validateLevel, DataResult::success);

    public static final Codec<LevelEffectDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LEVEL_CODEC.fieldOf("level").forGetter(LevelEffectDefinition::level),
            EffectCodecs.EFFECT_LIST.fieldOf("effect").forGetter(LevelEffectDefinition::effects)
    ).apply(instance, LevelEffectDefinition::new));

    public LevelEffectDefinition {
        effects = List.copyOf(Objects.requireNonNull(effects, "effects"));
    }

    private static DataResult<Integer> validateLevel(int level) {
        return level >= 0
                ? DataResult.success(level)
                : DataResult.error(() -> "unlock effect level must be greater than or equal to 0");
    }
}
