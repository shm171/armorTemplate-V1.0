package com.codex.armortemplatejson.plugin;

import com.codex.armortemplatejson.text.LocalizedText;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

public record PluginDefinition(int level, ResourceLocation icon, LocalizedText name) {
    private static final Codec<Integer> LEVEL_CODEC = Codec.INT.flatXmap(PluginDefinition::validateLevel, DataResult::success);

    public static final Codec<PluginDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LEVEL_CODEC.fieldOf("level").forGetter(PluginDefinition::level),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(PluginDefinition::icon),
            LocalizedText.CODEC.fieldOf("name").forGetter(PluginDefinition::name)
    ).apply(instance, PluginDefinition::new));

    public PluginDefinition {
        Objects.requireNonNull(icon, "icon");
        Objects.requireNonNull(name, "name");
    }

    private static DataResult<Integer> validateLevel(int level) {
        return level >= 0 ? DataResult.success(level) : DataResult.error(() -> "plugin level must be greater than or equal to 0");
    }
}
