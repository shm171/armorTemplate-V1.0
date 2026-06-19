package com.codex.armortemplatejson.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public interface ArmorTemplateEffect {
    default boolean canElytraFly(ArmorEffectContext context, ItemStack stack, LivingEntity entity) {
        return false;
    }

    default boolean elytraFlightTick(ArmorEffectContext context, ItemStack stack, LivingEntity entity, int flightTicks) {
        return canElytraFly(context, stack, entity);
    }

    default boolean canWalkOnPowderedSnow(ArmorEffectContext context, ItemStack stack, LivingEntity wearer) {
        return false;
    }

    default boolean makesPiglinsNeutral(ArmorEffectContext context, ItemStack stack, LivingEntity wearer) {
        return false;
    }

    default void onPlayerTick(ArmorEffectContext context, PlayerTickEvent.Post event) {
    }

    default void onLivingDamagePost(ArmorEffectContext context, LivingDamageEvent.Post event) {
    }
}
