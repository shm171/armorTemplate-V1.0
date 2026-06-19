package com.codex.armortemplatejson.config;

public enum ConfigErrorType {
    INVALID_JSON("invalid_json"),
    MISSING_FIELD("missing_field"),
    INVALID_ATTRIBUTE("invalid_attribute"),
    INVALID_VALUE("invalid_value"),
    REGISTRY_LOAD_FAILED("registry_load_failed");

    private final String serializedName;

    ConfigErrorType(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return serializedName;
    }
}
