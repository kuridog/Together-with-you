package com.kuridog.twu.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PetItem extends Item {

    private static final int BUFF_DURATION = 20 * 30;   // 30秒

    public PetItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            player.sendSystemMessage(Component.literal(player.getName().getString() + "はペットを召喚した！"));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, BUFF_DURATION, 0));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, BUFF_DURATION, 0));
            player.getCooldowns().addCooldown(this, BUFF_DURATION);
            if (player.getHealth() <= player.getMaxHealth() / 2) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, BUFF_DURATION, 0));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}