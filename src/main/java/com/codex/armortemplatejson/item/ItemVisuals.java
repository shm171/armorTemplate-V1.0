package com.codex.armortemplatejson.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

public final class ItemVisuals {
    private ItemVisuals() {
    }

    public static void apply(ItemStack stack, VisualDefinition visual) {
        // MC 1.21.1 没有按堆叠指定 item_model 的组件；资源包通过 custom_model_data override 接管模型/贴图。
        visual.customModelData().ifPresent(value -> stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(value)));
    }
}
