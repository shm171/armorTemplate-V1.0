package com.codex.armortemplatejson;

import com.codex.armortemplatejson.command.ArmorTemplateCommands;
import com.codex.armortemplatejson.component.ModDataComponents;
import com.codex.armortemplatejson.config.ConfigExceptionDisplay;
import com.codex.armortemplatejson.effect.ArmorEffectEvents;
import com.codex.armortemplatejson.effect.ModArmorEffects;
import com.codex.armortemplatejson.item.ModItems;
import com.codex.armortemplatejson.plugin.ModMenus;
import com.codex.armortemplatejson.template.ModArmorTemplateRegistries;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(ArmorTemplateJsonMod.MOD_ID)
public final class ArmorTemplateJsonMod {
    public static final String MOD_ID = "armortemplatejson";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArmorTemplateJsonMod(IEventBus modEventBus) {
        ModArmorEffects.bootstrap();
        ModDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        modEventBus.addListener(ModArmorTemplateRegistries::registerDataPackRegistries);
        NeoForge.EVENT_BUS.addListener(ConfigExceptionDisplay::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(ArmorEffectEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(ArmorEffectEvents::onLivingDamagePost);
        NeoForge.EVENT_BUS.addListener(ArmorTemplateCommands::register);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
