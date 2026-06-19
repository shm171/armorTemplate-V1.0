package com.codex.armortemplatejson.config;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class ConfigExceptionDisplay {
    private static final int MAX_CHAT_ERRORS = 8;

    private ConfigExceptionDisplay() {
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        List<ArmorTemplateConfigException> errors = ConfigExceptionReporter.snapshot();
        if (errors.isEmpty()) {
            return;
        }

        // 配置错误来自服务器加载阶段，玩家进服后只做聊天提示，不在客户端保存任何状态。
        event.getEntity().sendSystemMessage(Component.literal("Armor Template JSON found " + errors.size() + " configuration error(s).")
                .withStyle(ChatFormatting.RED));
        errors.stream()
                .limit(MAX_CHAT_ERRORS)
                .forEach(error -> event.getEntity().sendSystemMessage(error.toChatComponent()));

        if (errors.size() > MAX_CHAT_ERRORS) {
            event.getEntity().sendSystemMessage(Component.literal("More errors were omitted from chat; see the server log.")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
