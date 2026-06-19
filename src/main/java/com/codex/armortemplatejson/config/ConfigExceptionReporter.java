package com.codex.armortemplatejson.config;

import com.codex.armortemplatejson.ArmorTemplateJsonMod;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ConfigExceptionReporter {
    private static final List<ArmorTemplateConfigException> ERRORS = new CopyOnWriteArrayList<>();

    private ConfigExceptionReporter() {
    }

    public static void report(ArmorTemplateConfigException error) {
        ERRORS.add(error);
        ArmorTemplateJsonMod.LOGGER.warn("Armor template configuration error: {}", error.toChatComponent().getString(), error);
    }

    public static List<ArmorTemplateConfigException> snapshot() {
        return List.copyOf(ERRORS);
    }

    public static void clear() {
        ERRORS.clear();
    }
}
