package com.mcgrappin.network;

import com.mcgrappin.MCGrappin;
import com.mcgrappin.entities.GrappleHookEntity;
import com.mcgrappin.items.GrappleItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GrappleLaunchPacket(
        double targetX,
        double targetY,
        double targetZ,
        InteractionHand hand
) implements CustomPacketPayload {

    public static final Type<GrappleLaunchPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MCGrappin.MOD_ID, "grapple_launch")
    );

    public static final StreamCodec<FriendlyByteBuf, GrappleLaunchPacket> STREAM_CODEC = StreamCodec.of(
            GrappleLaunchPacket::encode,
            GrappleLaunchPacket::decode
    );

    private static void encode(FriendlyByteBuf buf, GrappleLaunchPacket packet) {
        buf.writeDouble(packet.targetX);
        buf.writeDouble(packet.targetY);
        buf.writeDouble(packet.targetZ);
        buf.writeEnum(packet.hand);
    }

    private static GrappleLaunchPacket decode(FriendlyByteBuf buf) {
        return new GrappleLaunchPacket(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readEnum(InteractionHand.class)
        );
    }

    public static void handle(GrappleLaunchPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack heldItem = player.getItemInHand(packet.hand);

                if (heldItem.getItem() instanceof GrappleItem grappleItem) {
                    if (grappleItem.canUse(heldItem, player)) {
                        Vec3 target = new Vec3(packet.targetX, packet.targetY, packet.targetZ);

                        // Spawn the grapple hook entity
                        GrappleHookEntity hook = new GrappleHookEntity(player, player.level(), heldItem);
                        hook.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                        player.level().addFreshEntity(hook);

                        // Use durability
                        grappleItem.onGrappleUsed(heldItem, player);

                        MCGrappin.LOGGER.debug("Grapple launched by {} towards ({}, {}, {})",
                                player.getName().getString(), packet.targetX, packet.targetY, packet.targetZ);
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
