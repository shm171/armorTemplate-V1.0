package com.codex.armortemplatejson.plugin;

import com.codex.armortemplatejson.ArmorTemplateJsonMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ArmorTemplateJsonMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<PluginMenu>> PLUGIN_MENU =
            MENUS.register("plugin_menu", () -> IMenuTypeExtension.create(PluginMenu::new));

    private ModMenus() {
    }
}
