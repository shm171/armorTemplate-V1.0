package com.codex.armortemplatejson.effect;

import com.codex.armortemplatejson.ArmorTemplateJsonMod;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;

public final class ArmorEffectRegistry {
    private static final Map<ResourceLocation, ArmorTemplateEffect> EFFECTS = new LinkedHashMap<>();
    private static final Set<ResourceLocation> WARNED_UNKNOWN_EFFECTS = ConcurrentHashMap.newKeySet();

    private ArmorEffectRegistry() {
    }

    public static synchronized void register(ResourceLocation id, ArmorTemplateEffect effect) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(effect, "effect");
        if (EFFECTS.containsKey(id)) {
            throw new IllegalArgumentException("Armor effect is already registered: " + id);
        }
        EFFECTS.put(id, effect);
    }

    public static synchronized Optional<ArmorTemplateEffect> get(ResourceLocation id) {
        return Optional.ofNullable(EFFECTS.get(id));
    }

    public static synchronized Map<ResourceLocation, ArmorTemplateEffect> registeredEffects() {
        return Map.copyOf(EFFECTS);
    }

    public static boolean anyActive(ArmorEffectContext context, Predicate<ArmorTemplateEffect> predicate) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(predicate, "predicate");
        for (ResourceLocation id : context.effectIds()) {
            ArmorTemplateEffect effect = getOrWarn(id);
            if (effect != null && predicate.test(effect)) {
                return true;
            }
        }
        return false;
    }

    public static void forEachActive(ArmorEffectContext context, Consumer<ArmorTemplateEffect> consumer) {
        Objects.requireNonNull(context, "context");
        Objects.requireNonNull(consumer, "consumer");
        for (ResourceLocation id : context.effectIds()) {
            ArmorTemplateEffect effect = getOrWarn(id);
            if (effect != null) {
                consumer.accept(effect);
            }
        }
    }

    private static ArmorTemplateEffect getOrWarn(ResourceLocation id) {
        ArmorTemplateEffect effect = get(id).orElse(null);
        if (effect == null && WARNED_UNKNOWN_EFFECTS.add(id)) {
            ArmorTemplateJsonMod.LOGGER.warn("Armor template effect '{}' is referenced but has not been registered.", id);
        }
        return effect;
    }
}
