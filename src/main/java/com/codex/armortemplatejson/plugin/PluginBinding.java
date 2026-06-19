package com.codex.armortemplatejson.plugin;

import com.codex.armortemplatejson.text.LocalizedText;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record PluginBinding(ResourceLocation pluginId, int level, ResourceLocation icon, LocalizedText name) {
    public static final Codec<PluginBinding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("plugin_id").forGetter(PluginBinding::pluginId),
            Codec.INT.fieldOf("level").forGetter(PluginBinding::level),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(PluginBinding::icon),
            LocalizedText.CODEC.fieldOf("name").forGetter(PluginBinding::name)
    ).apply(instance, PluginBinding::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PluginBinding> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public PluginBinding {
        Objects.requireNonNull(pluginId, "pluginId");
        Objects.requireNonNull(icon, "icon");
        Objects.requireNonNull(name, "name");
    }

    public static PluginBinding from(ResourceLocation pluginId, PluginDefinition definition) {
        return new PluginBinding(pluginId, definition.level(), definition.icon(), definition.name());
    }
}
