package com.codex.armortemplatejson.effect;

import com.codex.armortemplatejson.ArmorTemplateJsonMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ModArmorEffects {
    public static final ResourceLocation ELYTRA_FLIGHT = ArmorTemplateJsonMod.id("elytra_flight");
    public static final ResourceLocation CREATIVE_FLIGHT = ArmorTemplateJsonMod.id("creative_flight");
    public static final ResourceLocation DAMAGE_REFLECTION = ArmorTemplateJsonMod.id("damage_reflection");
    public static final ResourceLocation POWDER_SNOW_WALKING = ArmorTemplateJsonMod.id("powder_snow_walking");
    public static final ResourceLocation PIGLIN_NEUTRAL = ArmorTemplateJsonMod.id("piglin_neutral");

    private static final ResourceLocation CREATIVE_FLIGHT_MODIFIER_ID = ArmorTemplateJsonMod.id("effect/creative_flight");
    private static final AttributeModifier CREATIVE_FLIGHT_MODIFIER = new AttributeModifier(
            CREATIVE_FLIGHT_MODIFIER_ID,
            1.0D,
            AttributeModifier.Operation.ADD_VALUE
    );
    private static final ThreadLocal<Boolean> REFLECTING_DAMAGE = ThreadLocal.withInitial(() -> false);
    private static boolean bootstrapped;

    private ModArmorEffects() {
    }

    public static synchronized void bootstrap() {
        if (bootstrapped) {
            return;
        }
        ArmorEffectRegistry.register(ELYTRA_FLIGHT, new ElytraFlightEffect());
        ArmorEffectRegistry.register(CREATIVE_FLIGHT, new CreativeFlightEffect());
        ArmorEffectRegistry.register(DAMAGE_REFLECTION, new DamageReflectionEffect());
        ArmorEffectRegistry.register(POWDER_SNOW_WALKING, new PowderSnowWalkingEffect());
        ArmorEffectRegistry.register(PIGLIN_NEUTRAL, new PiglinNeutralEffect());
        bootstrapped = true;
    }

    public static void updateCreativeFlight(Player player, boolean active) {
        AttributeInstance attribute = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (attribute == null) {
            return;
        }

        if (active) {
            if (!attribute.hasModifier(CREATIVE_FLIGHT_MODIFIER_ID)) {
                attribute.addTransientModifier(CREATIVE_FLIGHT_MODIFIER);
            }
            return;
        }

        attribute.removeModifier(CREATIVE_FLIGHT_MODIFIER_ID);
        if (player.getAbilities().flying && !player.mayFly()) {
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    private static final class ElytraFlightEffect implements ArmorTemplateEffect {
        @Override
        public boolean canElytraFly(ArmorEffectContext context, ItemStack stack, LivingEntity entity) {
            return true;
        }

        @Override
        public boolean elytraFlightTick(ArmorEffectContext context, ItemStack stack, LivingEntity entity, int flightTicks) {
            return true;
        }
    }

    private static final class CreativeFlightEffect implements ArmorTemplateEffect {
        @Override
        public void onPlayerTick(ArmorEffectContext context, PlayerTickEvent.Post event) {
            updateCreativeFlight(event.getEntity(), true);
        }
    }

    private static final class DamageReflectionEffect implements ArmorTemplateEffect {
        @Override
        public void onLivingDamagePost(ArmorEffectContext context, LivingDamageEvent.Post event) {
            if (REFLECTING_DAMAGE.get() || event.getNewDamage() <= 0.0F) {
                return;
            }

            Entity sourceEntity = event.getSource().getEntity();
            if (!(sourceEntity instanceof LivingEntity attacker) || attacker == context.wearer() || attacker.isDeadOrDying()) {
                return;
            }

            // Reflected damage re-enters this event, so guard against recursive triggers.
            REFLECTING_DAMAGE.set(true);
            try {
                attacker.hurt(context.wearer().damageSources().thorns(context.wearer()), event.getNewDamage());
            } finally {
                REFLECTING_DAMAGE.set(false);
            }
        }
    }

    private static final class PowderSnowWalkingEffect implements ArmorTemplateEffect {
        @Override
        public boolean canWalkOnPowderedSnow(ArmorEffectContext context, ItemStack stack, LivingEntity wearer) {
            return true;
        }
    }

    private static final class PiglinNeutralEffect implements ArmorTemplateEffect {
        @Override
        public boolean makesPiglinsNeutral(ArmorEffectContext context, ItemStack stack, LivingEntity wearer) {
            return true;
        }
    }
}
