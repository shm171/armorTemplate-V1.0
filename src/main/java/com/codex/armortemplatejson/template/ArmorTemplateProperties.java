package com.codex.armortemplatejson.template;

import com.codex.armortemplatejson.effect.EffectCodecs;
import com.codex.armortemplatejson.item.VisualDefinition;
import com.codex.armortemplatejson.text.LocalizedText;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public record ArmorTemplateProperties(
        int level,
        ResourceLocation icon,
        LocalizedText name,
        Optional<LocalizedText> description,
        VisualDefinition visual,
        ArmorTemplateSlot slot,
        List<ResourceLocation> effects,
        ChestplateSettings chestplate,
        List<VanillaAttributeDefinition> vanillaAttributes
) {
    private static final Codec<Integer> LEVEL_CODEC = Codec.INT.flatXmap(ArmorTemplateProperties::validateLevel, DataResult::success);

    private static final Codec<ArmorTemplateProperties> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LEVEL_CODEC.fieldOf("level").forGetter(ArmorTemplateProperties::level),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(ArmorTemplateProperties::icon),
            LocalizedText.CODEC.fieldOf("name").forGetter(ArmorTemplateProperties::name),
            LocalizedText.CODEC.optionalFieldOf("description").forGetter(ArmorTemplateProperties::description),
            VisualDefinition.CODEC.optionalFieldOf("visual", VisualDefinition.EMPTY).forGetter(ArmorTemplateProperties::visual),
            ArmorTemplateSlot.CODEC.optionalFieldOf("slot", ArmorTemplateSlot.BODY).forGetter(ArmorTemplateProperties::slot),
            EffectCodecs.EFFECT_LIST.optionalFieldOf("effect", List.of()).forGetter(ArmorTemplateProperties::effects),
            ChestplateSettings.CODEC.optionalFieldOf("chestplate", ChestplateSettings.EMPTY).forGetter(ArmorTemplateProperties::chestplate),
            VanillaAttributeDefinition.CODEC.listOf().optionalFieldOf("properties", List.of()).forGetter(ArmorTemplateProperties::vanillaAttributes)
    ).apply(instance, ArmorTemplateProperties::new));

    public static final Codec<ArmorTemplateProperties> CODEC =
            BASE_CODEC.flatXmap(ArmorTemplateProperties::validateProperties, DataResult::success);

    public ArmorTemplateProperties {
        Objects.requireNonNull(icon, "icon");
        Objects.requireNonNull(name, "name");
        description = Objects.requireNonNull(description, "description");
        Objects.requireNonNull(visual, "visual");
        Objects.requireNonNull(slot, "slot");
        effects = List.copyOf(Objects.requireNonNull(effects, "effects"));
        Objects.requireNonNull(chestplate, "chestplate");
        vanillaAttributes = List.copyOf(Objects.requireNonNull(vanillaAttributes, "vanillaAttributes"));
    }

    private static DataResult<Integer> validateLevel(int level) {
        return level >= 0 ? DataResult.success(level) : DataResult.error(() -> "level must be greater than or equal to 0");
    }

    private static DataResult<ArmorTemplateProperties> validateProperties(ArmorTemplateProperties properties) {
        if (properties.slot != ArmorTemplateSlot.BODY && !properties.chestplate.equals(ChestplateSettings.EMPTY)) {
            return DataResult.error(() -> "chestplate settings can only be used when slot is body");
        }
        return DataResult.success(properties);
    }
}
