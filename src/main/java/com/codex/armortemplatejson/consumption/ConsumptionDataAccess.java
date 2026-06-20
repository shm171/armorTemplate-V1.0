package com.codex.armortemplatejson.consumption;

import com.codex.armortemplatejson.component.ModDataComponents;
import com.codex.armortemplatejson.item.ArmorTemplateBinding;
import com.codex.armortemplatejson.plugin.PluginBinding;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;

public final class ConsumptionDataAccess {
    private ConsumptionDataAccess() {
    }

    public static List<ConsumptionState> armorConsumption(ItemStack stack) {
        ArmorTemplateBinding binding = stack.get(ModDataComponents.ARMOR_TEMPLATE.get());
        return binding == null ? List.of() : binding.consumption();
    }

    public static void setArmorConsumption(ItemStack stack, List<ConsumptionState> consumption) {
        ArmorTemplateBinding binding = stack.get(ModDataComponents.ARMOR_TEMPLATE.get());
        if (binding != null) {
            stack.set(ModDataComponents.ARMOR_TEMPLATE.get(), binding.withConsumption(consumption));
        }
    }

    public static List<ConsumptionState> pluginConsumption(ItemStack stack) {
        PluginBinding binding = stack.get(ModDataComponents.PLUGIN.get());
        return binding == null ? List.of() : binding.consumption();
    }

    public static void setPluginConsumption(ItemStack stack, List<ConsumptionState> consumption) {
        PluginBinding binding = stack.get(ModDataComponents.PLUGIN.get());
        if (binding != null) {
            stack.set(ModDataComponents.PLUGIN.get(), binding.withConsumption(Objects.requireNonNull(consumption, "consumption")));
        }
    }
}
