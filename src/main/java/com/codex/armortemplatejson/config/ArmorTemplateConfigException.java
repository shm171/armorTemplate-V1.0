package com.codex.armortemplatejson.config;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class ArmorTemplateConfigException extends RuntimeException {
    private final ConfigErrorType type;
    private final String source;
    private final int line;
    private final int column;

    public ArmorTemplateConfigException(ConfigErrorType type, String source, int line, int column, String message) {
        super(message);
        this.type = Objects.requireNonNull(type, "type");
        this.source = source == null || source.isBlank() ? "unknown" : source;
        this.line = line;
        this.column = column;
    }

    public ConfigErrorType type() {
        return type;
    }

    public String source() {
        return source;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public Component toChatComponent() {
        String position = line > 0 ? "line " + line + (column > 0 ? ", column " + column : "") : "unknown position";
        return Component.literal("[Armor Template JSON] ")
                .withStyle(ChatFormatting.RED)
                .append(Component.literal(type.serializedName() + " in " + source + " at " + position + ": " + getMessage())
                        .withStyle(ChatFormatting.YELLOW));
    }
}
