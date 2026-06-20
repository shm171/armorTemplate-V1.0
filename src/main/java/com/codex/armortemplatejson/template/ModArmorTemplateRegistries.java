package com.codex.armortemplatejson.template;

import com.codex.armortemplatejson.ArmorTemplateJsonMod;
import com.codex.armortemplatejson.plugin.PluginCombinationDefinition;
import com.codex.armortemplatejson.plugin.PluginDefinition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class ModArmorTemplateRegistries {
    public static final ResourceKey<Registry<ArmorTemplate>> ARMOR_TEMPLATES =
            ResourceKey.createRegistryKey(ArmorTemplateJsonMod.id("armor_templates"));
    public static final ResourceKey<Registry<PluginDefinition>> PLUGINS =
            ResourceKey.createRegistryKey(ArmorTemplateJsonMod.id("plugins"));
    public static final ResourceKey<Registry<PluginCombinationDefinition>> PLUGIN_COMBINATIONS =
            ResourceKey.createRegistryKey(ArmorTemplateJsonMod.id("plugin_combinations"));

    private ModArmorTemplateRegistries() {
    }

    public static void registerDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ARMOR_TEMPLATES, ArmorTemplate.CODEC, ArmorTemplate.CODEC);
        event.dataPackRegistry(PLUGINS, PluginDefinition.CODEC, PluginDefinition.CODEC);
        event.dataPackRegistry(PLUGIN_COMBINATIONS, PluginCombinationDefinition.CODEC, PluginCombinationDefinition.CODEC);
    }
}
