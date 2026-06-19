package com.codex.armortemplatejson.item;

import com.codex.armortemplatejson.ArmorTemplateJsonMod;
import com.codex.armortemplatejson.template.ArmorTemplateSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ArmorTemplateJsonMod.MOD_ID);

    public static final DeferredItem<GeneratedArmorItem> GENERATED_HELMET = ITEMS.register("generated_helmet",
            () -> new GeneratedArmorItem(ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(5))));
    public static final DeferredItem<GeneratedArmorItem> GENERATED_CHESTPLATE = ITEMS.register("generated_chestplate",
            () -> new GeneratedArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(5))));
    public static final DeferredItem<GeneratedArmorItem> GENERATED_LEGGINGS = ITEMS.register("generated_leggings",
            () -> new GeneratedArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(5))));
    public static final DeferredItem<GeneratedArmorItem> GENERATED_BOOTS = ITEMS.register("generated_boots",
            () -> new GeneratedArmorItem(ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(5))));

    public static final DeferredItem<ArmorPluginItem> ARMOR_PLUGIN = ITEMS.register("armor_plugin",
            () -> new ArmorPluginItem(new Item.Properties().stacksTo(1)));

    private ModItems() {
    }

    public static DeferredItem<GeneratedArmorItem> forSlot(ArmorTemplateSlot slot) {
        return switch (slot) {
            case HEAD -> GENERATED_HELMET;
            case BODY -> GENERATED_CHESTPLATE;
            case LEGGING -> GENERATED_LEGGINGS;
            case FEET -> GENERATED_BOOTS;
        };
    }
}
