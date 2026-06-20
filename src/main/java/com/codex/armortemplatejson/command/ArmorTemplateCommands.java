package com.codex.armortemplatejson.command;

import com.codex.armortemplatejson.ArmorTemplateJsonMod;
import com.codex.armortemplatejson.item.ArmorTemplateStackFactory;
import com.codex.armortemplatejson.item.PluginStackFactory;
import com.codex.armortemplatejson.plugin.PluginDefinition;
import com.codex.armortemplatejson.template.ArmorTemplate;
import com.codex.armortemplatejson.template.ModArmorTemplateRegistries;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ArmorTemplateCommands {
    private static final ResourceLocation[] SAMPLE_ARMOR = {
            ArmorTemplateJsonMod.id("sample_helmet"),
            ArmorTemplateJsonMod.id("sample_chestplate"),
            ArmorTemplateJsonMod.id("sample_leggings"),
            ArmorTemplateJsonMod.id("sample_boots")
    };
    private static final ResourceLocation[] SAMPLE_PLUGINS = {
            ArmorTemplateJsonMod.id("guard_plate"),
            ArmorTemplateJsonMod.id("flight_core")
    };

    private ArmorTemplateCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(ArmorTemplateJsonMod.MOD_ID)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("give_sample")
                        .executes(ArmorTemplateCommands::giveSample)));
    }

    private static int giveSample(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception exception) {
            source.sendFailure(Component.translatable("command.armortemplatejson.players_only"));
            return 0;
        }

        Registry<ArmorTemplate> armorTemplates = source.registryAccess().registryOrThrow(ModArmorTemplateRegistries.ARMOR_TEMPLATES);
        Registry<PluginDefinition> plugins = source.registryAccess().registryOrThrow(ModArmorTemplateRegistries.PLUGINS);
        int given = 0;

        for (ResourceLocation armorId : SAMPLE_ARMOR) {
            ArmorTemplate template = armorTemplates.get(armorId);
            if (template == null) {
                source.sendFailure(Component.translatable("command.armortemplatejson.missing_sample", armorId.toString()));
                return given;
            }
            giveOrDrop(player, ArmorTemplateStackFactory.createStack(armorId, template));
            given++;
        }

        for (ResourceLocation pluginId : SAMPLE_PLUGINS) {
            PluginDefinition definition = plugins.get(pluginId);
            if (definition == null) {
                source.sendFailure(Component.translatable("command.armortemplatejson.missing_sample", pluginId.toString()));
                return given;
            }
            giveOrDrop(player, PluginStackFactory.createStack(pluginId, definition));
            given++;
        }

        int finalGiven = given;
        source.sendSuccess(() -> Component.translatable("command.armortemplatejson.give_sample.success", finalGiven), true);
        return Command.SINGLE_SUCCESS;
    }

    private static void giveOrDrop(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
