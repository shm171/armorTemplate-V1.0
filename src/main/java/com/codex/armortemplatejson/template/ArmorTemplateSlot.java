package com.codex.armortemplatejson.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;

public enum ArmorTemplateSlot {
    HEAD("head", ArmorItem.Type.HELMET, EquipmentSlot.HEAD),
    BODY("body", ArmorItem.Type.CHESTPLATE, EquipmentSlot.CHEST),
    LEGGING("legging", ArmorItem.Type.LEGGINGS, EquipmentSlot.LEGS),
    FEET("feet", ArmorItem.Type.BOOTS, EquipmentSlot.FEET);

    public static final Codec<ArmorTemplateSlot> CODEC = Codec.STRING.flatXmap(
            ArmorTemplateSlot::fromSerializedName,
            slot -> DataResult.success(slot.serializedName)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ArmorTemplateSlot> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);

    private final String serializedName;
    private final ArmorItem.Type armorType;
    private final EquipmentSlot equipmentSlot;

    ArmorTemplateSlot(String serializedName, ArmorItem.Type armorType, EquipmentSlot equipmentSlot) {
        this.serializedName = serializedName;
        this.armorType = armorType;
        this.equipmentSlot = equipmentSlot;
    }

    public String serializedName() {
        return serializedName;
    }

    public ArmorItem.Type armorType() {
        return armorType;
    }

    public EquipmentSlot equipmentSlot() {
        return equipmentSlot;
    }

    private static DataResult<ArmorTemplateSlot> fromSerializedName(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        if ("helmet".equals(normalized)) {
            normalized = "head";
        } else if ("chest".equals(normalized) || "chestplate".equals(normalized)) {
            normalized = "body";
        } else if ("legs".equals(normalized) || "leggings".equals(normalized)) {
            normalized = "legging";
        } else if ("boots".equals(normalized)) {
            normalized = "feet";
        }

        String finalNormalized = normalized;
        return Arrays.stream(values())
                .filter(slot -> slot.serializedName.equals(finalNormalized))
                .findFirst()
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(() -> "unknown armor slot: " + name));
    }
}
