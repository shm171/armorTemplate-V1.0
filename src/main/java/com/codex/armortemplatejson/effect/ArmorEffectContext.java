package com.codex.armortemplatejson.effect;

import com.codex.armortemplatejson.item.ArmorTemplateBinding;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record ArmorEffectContext(
        LivingEntity wearer,
        int level,
        List<ItemStack> armorStacks,
        List<ArmorTemplateBinding> bindings,
        Set<ResourceLocation> effectIds
) {
    public ArmorEffectContext {
        Objects.requireNonNull(wearer, "wearer");
        armorStacks = List.copyOf(Objects.requireNonNull(armorStacks, "armorStacks"));
        bindings = List.copyOf(Objects.requireNonNull(bindings, "bindings"));
        effectIds = Set.copyOf(Objects.requireNonNull(effectIds, "effectIds"));
    }

    public boolean hasEffect(ResourceLocation effectId) {
        return effectIds.contains(effectId);
    }
}
