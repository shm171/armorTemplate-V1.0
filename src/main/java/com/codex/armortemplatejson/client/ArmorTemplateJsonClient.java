package com.codex.armortemplatejson.client;

import com.codex.armortemplatejson.ArmorTemplateJsonMod;
import com.codex.armortemplatejson.plugin.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ArmorTemplateJsonMod.MOD_ID, value = Dist.CLIENT)
public final class ArmorTemplateJsonClient {
    private ArmorTemplateJsonClient() {
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.PLUGIN_MENU.get(), PluginScreen::new);
    }
}
