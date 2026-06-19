package com.codex.armortemplatejson.effect;

import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ArmorEffectEvents {
    private ArmorEffectEvents() {
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Optional<ArmorEffectContext> context = ArmorSuitResolver.activeContext(player);
        if (context.isEmpty() || !context.get().hasEffect(ModArmorEffects.CREATIVE_FLIGHT)) {
            ModArmorEffects.updateCreativeFlight(player, false);
        }
        context.ifPresent(activeContext -> ArmorEffectRegistry.forEachActive(
                activeContext,
                effect -> effect.onPlayerTick(activeContext, event)
        ));
    }

    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        ArmorSuitResolver.activeContext(entity).ifPresent(context -> ArmorEffectRegistry.forEachActive(
                context,
                effect -> effect.onLivingDamagePost(context, event)
        ));
    }
}
