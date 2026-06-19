package com.codex.armortemplatejson.item;

import com.codex.armortemplatejson.template.ArmorTemplate;
import com.codex.armortemplatejson.template.ArmorTemplateSlot;
import com.codex.armortemplatejson.template.VanillaAttributeDefinition;
import com.codex.armortemplatejson.text.LocalizedText;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ArmorTemplateBinding(
        ResourceLocation templateId,
        int level,
        ResourceLocation icon,
        LocalizedText name,
        ArmorTemplateSlot slot,
        int pluginSlots,
        List<ResourceLocation> effects,
        List<VanillaAttributeDefinition> vanillaAttributes
) {
    public static final Codec<ArmorTemplateBinding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template_id").forGetter(ArmorTemplateBinding::templateId),
            Codec.INT.fieldOf("level").forGetter(ArmorTemplateBinding::level),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(ArmorTemplateBinding::icon),
            LocalizedText.CODEC.fieldOf("name").forGetter(ArmorTemplateBinding::name),
            ArmorTemplateSlot.CODEC.fieldOf("slot").forGetter(ArmorTemplateBinding::slot),
            Codec.INT.optionalFieldOf("plugin_slots", 0).forGetter(ArmorTemplateBinding::pluginSlots),
            ResourceLocation.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(ArmorTemplateBinding::effects),
            VanillaAttributeDefinition.CODEC.listOf().optionalFieldOf("vanilla_attributes", List.of()).forGetter(ArmorTemplateBinding::vanillaAttributes)
    ).apply(instance, ArmorTemplateBinding::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArmorTemplateBinding> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public ArmorTemplateBinding {
        Objects.requireNonNull(templateId, "templateId");
        Objects.requireNonNull(icon, "icon");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(slot, "slot");
        effects = List.copyOf(Objects.requireNonNull(effects, "effects"));
        vanillaAttributes = List.copyOf(Objects.requireNonNull(vanillaAttributes, "vanillaAttributes"));
        pluginSlots = Math.max(0, Math.min(pluginSlots, 6));
    }

    public static ArmorTemplateBinding from(ResourceLocation templateId, ArmorTemplate template) {
        var properties = template.properties();
        return new ArmorTemplateBinding(
                templateId,
                properties.level(),
                properties.icon(),
                properties.name(),
                properties.slot(),
                properties.pluginSlots(),
                properties.effects(),
                properties.vanillaAttributes()
        );
    }
}
