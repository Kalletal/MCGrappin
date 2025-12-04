package com.mcgrappin.network;

import com.mcgrappin.MCGrappin;
import com.mcgrappin.controllers.GrapplePhysics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet sent from client to server to cancel an active grapple pull.
 */
public record GrappleCancelPacket() implements CustomPacketPayload {

    public static final Type<GrappleCancelPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MCGrappin.MOD_ID, "grapple_cancel")
    );

    public static final StreamCodec<FriendlyByteBuf, GrappleCancelPacket> STREAM_CODEC = StreamCodec.of(
            GrappleCancelPacket::encode,
            GrappleCancelPacket::decode
    );

    private static void encode(FriendlyByteBuf buf, GrappleCancelPacket packet) {
        // No data to encode
    }

    private static GrappleCancelPacket decode(FriendlyByteBuf buf) {
        return new GrappleCancelPacket();
    }

    public static void handle(GrappleCancelPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                // Cancel the grapple pull on the server
                if (GrapplePhysics.isPulling(player)) {
                    GrapplePhysics.releaseGrapple(player);
                    MCGrappin.LOGGER.debug("Grapple cancelled by player {}", player.getName().getString());
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
