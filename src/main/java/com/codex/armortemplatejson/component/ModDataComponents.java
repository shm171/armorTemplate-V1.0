package com.codex.armortemplatejson.component;

import com.codex.armortemplatejson.ArmorTemplateJsonMod;
import com.codex.armortemplatejson.item.ArmorTemplateBinding;
import com.codex.armortemplatejson.plugin.PluginBinding;
import com.codex.armortemplatejson.plugin.PluginContainerComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ArmorTemplateJsonMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ArmorTemplateBinding>> ARMOR_TEMPLATE =
            DATA_COMPONENT_TYPES.registerComponentType("armor_template", builder -> builder
                    .persistent(ArmorTemplateBinding.CODEC)
                    .networkSynchronized(ArmorTemplateBinding.STREAM_CODEC)
                    .cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PluginBinding>> PLUGIN =
            DATA_COMPONENT_TYPES.registerComponentType("plugin", builder -> builder
                    .persistent(PluginBinding.CODEC)
                    .networkSynchronized(PluginBinding.STREAM_CODEC)
                    .cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PluginContainerComponent>> PLUGIN_CONTAINER =
            DATA_COMPONENT_TYPES.registerComponentType("plugin_container", builder -> builder
                    .persistent(PluginContainerComponent.CODEC)
                    .networkSynchronized(PluginContainerComponent.STREAM_CODEC));

    private ModDataComponents() {
    }
}
