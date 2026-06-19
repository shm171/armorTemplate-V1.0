package com.codex.armortemplatejson.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ArmorTemplate(ArmorTemplateProperties properties) {
    public static final Codec<ArmorTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ArmorTemplateProperties.CODEC.fieldOf("properties").forGetter(ArmorTemplate::properties)
    ).apply(instance, ArmorTemplate::new));
}
