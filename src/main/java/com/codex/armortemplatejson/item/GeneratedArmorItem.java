package com.codex.armortemplatejson.item;

import com.codex.armortemplatejson.component.ModDataComponents;
import com.codex.armortemplatejson.effect.ArmorEffectRegistry;
import com.codex.armortemplatejson.effect.ArmorSuitResolver;
import com.codex.armortemplatejson.plugin.PluginContainerComponent;
import com.codex.armortemplatejson.plugin.PluginMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public class GeneratedArmorItem extends ArmorItem {
    public GeneratedArmorItem(Type type, Item.Properties properties) {
        super(ArmorMaterials.LEATHER, type, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        ArmorTemplateBinding binding = stack.get(ModDataComponents.ARMOR_TEMPLATE.get());
        return binding == null ? super.getName(stack) : binding.name().toComponent();
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ArmorTemplateBinding binding = stack.get(ModDataComponents.ARMOR_TEMPLATE.get());
        return binding == null ? super.getDefaultAttributeModifiers() : ArmorTemplateStackFactory.buildAttributeModifiers(binding);
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return ArmorSuitResolver.activeContext(entity)
                .map(context -> ArmorEffectRegistry.anyActive(context, effect -> effect.canElytraFly(context, stack, entity)))
                .orElse(false);
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        return ArmorSuitResolver.activeContext(entity)
                .map(context -> ArmorEffectRegistry.anyActive(context, effect -> effect.elytraFlightTick(context, stack, entity, flightTicks)))
                .orElse(false);
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return ArmorSuitResolver.activeContext(wearer)
                .map(context -> ArmorEffectRegistry.anyActive(context, effect -> effect.canWalkOnPowderedSnow(context, stack, wearer)))
                .orElse(false);
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return ArmorSuitResolver.activeContext(wearer)
                .map(context -> ArmorEffectRegistry.anyActive(context, effect -> effect.makesPiglinsNeutral(context, stack, wearer)))
                .orElse(false);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        ArmorTemplateBinding binding = stack.get(ModDataComponents.ARMOR_TEMPLATE.get());
        if (binding != null && binding.pluginSlots() > 0) {
            PluginContainerComponent container = stack.get(ModDataComponents.PLUGIN_CONTAINER.get());
            stack.set(ModDataComponents.PLUGIN_CONTAINER.get(), container == null
                    ? PluginContainerComponent.empty(binding.pluginSlots())
                    : new PluginContainerComponent(container.copyForSize(binding.pluginSlots())));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ArmorTemplateBinding binding = stack.get(ModDataComponents.ARMOR_TEMPLATE.get());
        if (hand == InteractionHand.MAIN_HAND && player.isShiftKeyDown() && binding != null && binding.pluginSlots() > 0) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                PluginMenuProvider.open(serverPlayer, stack, binding);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return super.use(level, player, hand);
    }
}
