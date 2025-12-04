package com.mcgrappin.items;

import com.mcgrappin.MCGrappin;
import com.mcgrappin.controllers.GrapplePhysics;
import com.mcgrappin.entities.GrappleHookEntity;
import com.mcgrappin.entities.GrappleState;
import com.mcgrappin.network.GrappleCancelPacket;
import com.mcgrappin.network.GrappleLaunchPacket;
import com.mcgrappin.network.GrappleNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public abstract class GrappleItem extends Item {

    protected final GrappleMaterial material;
    private static final int COOLDOWN_TICKS_DEFAULT = 30;

    public GrappleItem(Properties properties, GrappleMaterial material) {
        super(properties);
        this.material = material;
    }

    public GrappleMaterial getMaterial() {
        return material;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide) {
            // Client side: check if already pulling, if so cancel
            if (GrappleState.isClientPulling()) {
                GrappleNetwork.sendToServer(new GrappleCancelPacket());
                return InteractionResultHolder.success(itemStack);
            }

            // Otherwise, send launch packet
            if (!canUse(itemStack, player)) {
                return InteractionResultHolder.fail(itemStack);
            }

            HitResult hitResult = player.pick(material.getRange(), 0.0F, false);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) hitResult;
                GrappleNetwork.sendToServer(new GrappleLaunchPacket(
                        blockHit.getLocation().x,
                        blockHit.getLocation().y,
                        blockHit.getLocation().z,
                        hand
                ));
            } else {
                // Even if no block hit, send where they're looking
                var lookVec = player.getViewVector(1.0F).scale(material.getRange());
                var target = player.getEyePosition().add(lookVec);
                GrappleNetwork.sendToServer(new GrappleLaunchPacket(
                        target.x,
                        target.y,
                        target.z,
                        hand
                ));
            }
        } else {
            // Server side: check if already pulling, if so cancel
            ServerPlayer serverPlayer = (ServerPlayer) player;
            if (GrapplePhysics.isPulling(serverPlayer)) {
                GrapplePhysics.releaseGrapple(serverPlayer);
                return InteractionResultHolder.success(itemStack);
            }

            if (!canUse(itemStack, player)) {
                return InteractionResultHolder.fail(itemStack);
            }

            // Spawn the grapple entity - fast and straight like a trident
            GrappleHookEntity hook = new GrappleHookEntity(serverPlayer, level, itemStack);
            hook.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 0.0F);
            level.addFreshEntity(hook);

            // Play sound - trident-like throw sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);

            onGrappleUsed(itemStack, player);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    public boolean canUse(ItemStack stack, Player player) {
        // Check durability
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            return false;
        }

        // Check cooldown
        if (player.getCooldowns().isOnCooldown(this)) {
            return false;
        }

        return true;
    }

    public void onGrappleUsed(ItemStack stack, Player player) {
        // Apply cooldown
        player.getCooldowns().addCooldown(this, material.getCooldown());

        // Damage item
        stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));

        MCGrappin.LOGGER.debug("Grapple used by {}, remaining durability: {}",
                player.getName().getString(), stack.getMaxDamage() - stack.getDamageValue());
    }

    public float getGrappleDamage() {
        return material.getDamage();
    }

    public int getGrappleRange() {
        return material.getRange();
    }

    public double getPullSpeed() {
        return material.getPullSpeed();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
