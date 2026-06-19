package com.codex.armortemplatejson.template;

import com.mojang.datafixers.util.Either;
import com.codex.armortemplatejson.text.LocalizedText;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

public record ArmorTemplateProperties(
        int level,
        ResourceLocation icon,
        LocalizedText name,
        ArmorTemplateSlot slot,
        int pluginSlots,
        List<ResourceLocation> effects,
        List<VanillaAttributeDefinition> vanillaAttributes
) {
    private static final Codec<Integer> LEVEL_CODEC = Codec.INT.flatXmap(ArmorTemplateProperties::validateLevel, DataResult::success);
    private static final Codec<Integer> PLUGIN_SLOTS_CODEC = Codec.INT.flatXmap(ArmorTemplateProperties::validatePluginSlots, DataResult::success);
    private static final Codec<List<ResourceLocation>> EFFECTS_CODEC = Codec.either(
            ResourceLocation.CODEC,
            ResourceLocation.CODEC.listOf()
    ).xmap(
            either -> either.map(List::of, List::copyOf),
            effects -> effects.size() == 1 ? Either.left(effects.getFirst()) : Either.right(effects)
    );

    public static final Codec<ArmorTemplateProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LEVEL_CODEC.fieldOf("level").forGetter(ArmorTemplateProperties::level),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(ArmorTemplateProperties::icon),
            LocalizedText.CODEC.fieldOf("name").forGetter(ArmorTemplateProperties::name),
            ArmorTemplateSlot.CODEC.optionalFieldOf("slot", ArmorTemplateSlot.BODY).forGetter(ArmorTemplateProperties::slot),
            PLUGIN_SLOTS_CODEC.optionalFieldOf("plugin_numbers", 0).forGetter(ArmorTemplateProperties::pluginSlots),
            EFFECTS_CODEC.optionalFieldOf("effect", List.of()).forGetter(ArmorTemplateProperties::effects),
            VanillaAttributeDefinition.CODEC.listOf().optionalFieldOf("properties", List.of()).forGetter(ArmorTemplateProperties::vanillaAttributes)
    ).apply(instance, ArmorTemplateProperties::new));

    public ArmorTemplateProperties {
        Objects.requireNonNull(icon, "icon");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(slot, "slot");
        effects = List.copyOf(Objects.requireNonNull(effects, "effects"));
        vanillaAttributes = List.copyOf(Objects.requireNonNull(vanillaAttributes, "vanillaAttributes"));
    }

    private static DataResult<Integer> validateLevel(int level) {
        return level >= 0 ? DataResult.success(level) : DataResult.error(() -> "level must be greater than or equal to 0");
    }

    private static DataResult<Integer> validatePluginSlots(int pluginSlots) {
        return pluginSlots >= 0 && pluginSlots <= 6
                ? DataResult.success(pluginSlots)
                : DataResult.error(() -> "plugin_numbers must be between 0 and 6");
    }
}
