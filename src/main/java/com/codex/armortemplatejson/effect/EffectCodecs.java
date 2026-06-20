package com.codex.armortemplatejson.effect;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public final class EffectCodecs {
    public static final Codec<List<ResourceLocation>> EFFECT_LIST = Codec.either(
            ResourceLocation.CODEC,
            ResourceLocation.CODEC.listOf()
    ).xmap(
            either -> either.map(List::of, List::copyOf),
            effects -> effects.size() == 1 ? Either.left(effects.getFirst()) : Either.right(effects)
    );

    private EffectCodecs() {
    }
}
