package com.codex.armortemplatejson.item;

import com.codex.armortemplatejson.text.LocalizedText;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

public final class ItemTextComponents {
    private ItemTextComponents() {
    }

    public static void applyLore(ItemStack stack, Optional<LocalizedText> description) {
        // 1.21.1 的 lore 是原版 ItemLore 数据组件；这里仅写入静态文本，不做客户端侧动态查询。
        description.ifPresent(text -> stack.set(DataComponents.LORE, new ItemLore(List.of(text.toComponent()))));
    }
}
