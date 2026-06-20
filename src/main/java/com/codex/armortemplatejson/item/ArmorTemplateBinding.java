package com.codex.armortemplatejson.item;

import com.codex.armortemplatejson.consumption.ConsumptionState;
import com.codex.armortemplatejson.template.ArmorTemplate;
import com.codex.armortemplatejson.template.ArmorTemplateSlot;
import com.codex.armortemplatejson.template.ChestplateSettings;
import com.codex.armortemplatejson.template.VanillaAttributeDefinition;
import com.codex.armortemplatejson.text.LocalizedText;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ArmorTemplateBinding(
        ResourceLocation templateId,
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
    public static final Codec<ArmorTemplateBinding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template_id").forGetter(ArmorTemplateBinding::templateId),
            Codec.INT.fieldOf("level").forGetter(ArmorTemplateBinding::level),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(ArmorTemplateBinding::icon),
            LocalizedText.CODEC.fieldOf("name").forGetter(ArmorTemplateBinding::name),
            LocalizedText.CODEC.optionalFieldOf("description").forGetter(ArmorTemplateBinding::description),
            VisualDefinition.CODEC.optionalFieldOf("visual", VisualDefinition.EMPTY).forGetter(ArmorTemplateBinding::visual),
            ArmorTemplateSlot.CODEC.fieldOf("slot").forGetter(ArmorTemplateBinding::slot),
            ResourceLocation.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(ArmorTemplateBinding::effects),
            ChestplateSettings.CODEC.optionalFieldOf("chestplate", ChestplateSettings.EMPTY).forGetter(ArmorTemplateBinding::chestplate),
            VanillaAttributeDefinition.CODEC.listOf().optionalFieldOf("vanilla_attributes", List.of()).forGetter(ArmorTemplateBinding::vanillaAttributes)
    ).apply(instance, ArmorTemplateBinding::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArmorTemplateBinding> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public ArmorTemplateBinding {
        Objects.requireNonNull(templateId, "templateId");
        Objects.requireNonNull(icon, "icon");
        Objects.requireNonNull(name, "name");
        description = Objects.requireNonNull(description, "description");
        Objects.requireNonNull(visual, "visual");
        Objects.requireNonNull(slot, "slot");
        effects = List.copyOf(Objects.requireNonNull(effects, "effects"));
        Objects.requireNonNull(chestplate, "chestplate");
        vanillaAttributes = List.copyOf(Objects.requireNonNull(vanillaAttributes, "vanillaAttributes"));
    }

    public static ArmorTemplateBinding from(ResourceLocation templateId, ArmorTemplate template) {
        var properties = template.properties();
        return new ArmorTemplateBinding(
                templateId,
                properties.level(),
                properties.icon(),
                properties.name(),
                properties.description(),
                properties.visual(),
                properties.slot(),
                properties.effects(),
                properties.chestplate(),
                properties.vanillaAttributes()
        );
    }

    public int pluginSlots() {
        return slot == ArmorTemplateSlot.BODY ? chestplate.pluginSlots() : 0;
    }

    public int maxPluginLevel() {
        return slot == ArmorTemplateSlot.BODY ? chestplate.maxPluginLevel() : 0;
    }

    public List<ResourceLocation> pluginCategories() {
        return slot == ArmorTemplateSlot.BODY ? chestplate.pluginCategories() : List.of();
    }

    public List<ConsumptionState> consumption() {
        return slot == ArmorTemplateSlot.BODY ? chestplate.consumption() : List.of();
    }

    public ArmorTemplateBinding withConsumption(List<ConsumptionState> newConsumption) {
        return new ArmorTemplateBinding(
                templateId,
                level,
                icon,
                name,
                description,
                visual,
                slot,
                effects,
                slot == ArmorTemplateSlot.BODY
                        ? new ChestplateSettings(pluginSlots(), pluginCategories(), maxPluginLevel(), newConsumption)
                        : ChestplateSettings.EMPTY,
                vanillaAttributes
        );
    }
}
