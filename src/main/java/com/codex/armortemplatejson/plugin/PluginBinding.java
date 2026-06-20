package com.codex.armortemplatejson.plugin;

import com.codex.armortemplatejson.consumption.ConsumptionState;
import com.codex.armortemplatejson.effect.TieredEffectDefinition;
import com.codex.armortemplatejson.item.VisualDefinition;
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

public record PluginBinding(
        ResourceLocation pluginId,
        int level,
        ResourceLocation category,
        ResourceLocation icon,
        LocalizedText name,
        Optional<LocalizedText> description,
        VisualDefinition visual,
        TieredEffectDefinition effects,
        List<ConsumptionState> consumption
) {
    public static final Codec<PluginBinding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("plugin_id").forGetter(PluginBinding::pluginId),
            Codec.INT.fieldOf("level").forGetter(PluginBinding::level),
            ResourceLocation.CODEC.fieldOf("category").forGetter(PluginBinding::category),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(PluginBinding::icon),
            LocalizedText.CODEC.fieldOf("name").forGetter(PluginBinding::name),
            LocalizedText.CODEC.optionalFieldOf("description").forGetter(PluginBinding::description),
            VisualDefinition.CODEC.optionalFieldOf("visual", VisualDefinition.EMPTY).forGetter(PluginBinding::visual),
            TieredEffectDefinition.CODEC.optionalFieldOf("effects", TieredEffectDefinition.EMPTY).forGetter(PluginBinding::effects),
            ConsumptionState.CODEC.listOf().optionalFieldOf("consumption", List.of()).forGetter(PluginBinding::consumption)
    ).apply(instance, PluginBinding::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PluginBinding> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public PluginBinding {
        Objects.requireNonNull(pluginId, "pluginId");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(icon, "icon");
        Objects.requireNonNull(name, "name");
        description = Objects.requireNonNull(description, "description");
        Objects.requireNonNull(visual, "visual");
        Objects.requireNonNull(effects, "effects");
        consumption = List.copyOf(Objects.requireNonNull(consumption, "consumption"));
    }

    public static PluginBinding from(ResourceLocation pluginId, PluginDefinition definition) {
        return new PluginBinding(pluginId, definition.level(), definition.category(), definition.icon(), definition.name(), definition.description(), definition.visual(), definition.effects(), definition.consumption());
    }

    public List<ResourceLocation> activeEffects() {
        return effects.activeEffects(level);
    }

    public PluginBinding withConsumption(List<ConsumptionState> newConsumption) {
        return new PluginBinding(pluginId, level, category, icon, name, description, visual, effects, newConsumption);
    }
}
