package com.codex.armortemplatejson.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.Locale;

public enum AttributeValueOperation {
    ADD("add"),
    SUBTRACT("sub"),
    MULTIPLY("mul"),
    DIVIDE("dev"),
    MODULO("mod");

    public static final Codec<AttributeValueOperation> CODEC = Codec.STRING.flatXmap(
            AttributeValueOperation::fromSerializedName,
            operation -> DataResult.success(operation.serializedName)
    );

    private final String serializedName;

    AttributeValueOperation(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return serializedName;
    }

    private static DataResult<AttributeValueOperation> fromSerializedName(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(operation -> operation.serializedName.equals(normalized))
                .findFirst()
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "unknown attribute operation: " + name));
    }
}
