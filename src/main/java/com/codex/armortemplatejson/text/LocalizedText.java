package com.codex.armortemplatejson.text;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LocalizedText(Optional<String> translate, Optional<String> literal, Optional<String> fallback) {
    private static final Codec<String> NON_BLANK_STRING = Codec.STRING.flatXmap(LocalizedText::validateString, DataResult::success);

    private static final Codec<LocalizedText> OBJECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NON_BLANK_STRING.optionalFieldOf("translate").forGetter(LocalizedText::translate),
            NON_BLANK_STRING.optionalFieldOf("literal").forGetter(LocalizedText::literal),
            NON_BLANK_STRING.optionalFieldOf("fallback").forGetter(LocalizedText::fallback)
    ).apply(instance, LocalizedText::new));

    public static final Codec<LocalizedText> CODEC = Codec.either(NON_BLANK_STRING, OBJECT_CODEC)
            .xmap(
                    either -> either.map(LocalizedText::translation, Function.identity()),
                    text -> text.isSimpleTranslation()
                            ? Either.left(text.translate.orElseThrow())
                            : Either.right(text)
            )
            .flatXmap(LocalizedText::validate, DataResult::success);

    public static final StreamCodec<RegistryFriendlyByteBuf, LocalizedText> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public LocalizedText {
        translate = normalize(translate);
        literal = normalize(literal);
        fallback = normalize(fallback);
    }

    public static LocalizedText translation(String key) {
        return new LocalizedText(Optional.of(key), Optional.empty(), Optional.empty());
    }

    public Component toComponent() {
        if (translate.isPresent()) {
            return Component.translatableWithFallback(translate.get(), fallback.orElse(null));
        }
        return Component.literal(literal.orElse(""));
    }

    private boolean isSimpleTranslation() {
        return translate.isPresent() && literal.isEmpty() && fallback.isEmpty();
    }

    private static DataResult<LocalizedText> validate(LocalizedText text) {
        if (text.translate.isPresent() == text.literal.isPresent()) {
            return DataResult.error(() -> "localized text must define exactly one of translate or literal");
        }
        if (text.fallback.isPresent() && text.translate.isEmpty()) {
            return DataResult.error(() -> "fallback can only be used with translate");
        }
        return DataResult.success(text);
    }

    private static DataResult<String> validateString(String value) {
        return value == null || value.isBlank()
                ? DataResult.error(() -> "text value must not be blank")
                : DataResult.success(value);
    }

    private static Optional<String> normalize(Optional<String> value) {
        return value.flatMap(raw -> raw.isBlank() ? Optional.empty() : Optional.of(raw));
    }
}
