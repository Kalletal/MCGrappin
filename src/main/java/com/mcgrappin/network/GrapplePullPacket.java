package com.mcgrappin.network;

import com.mcgrappin.MCGrappin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record GrapplePullPacket(
        UUID playerId,
        double velocityX,
        double velocityY,
        double velocityZ,
        float progress
) implements CustomPacketPayload {

    public static final Type<GrapplePullPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MCGrappin.MOD_ID, "grapple_pull")
    );

    public static final StreamCodec<FriendlyByteBuf, GrapplePullPacket> STREAM_CODEC = StreamCodec.of(
            GrapplePullPacket::encode,
            GrapplePullPacket::decode
    );

    private static void encode(FriendlyByteBuf buf, GrapplePullPacket packet) {
        buf.writeUUID(packet.playerId);
        buf.writeDouble(packet.velocityX);
        buf.writeDouble(packet.velocityY);
        buf.writeDouble(packet.velocityZ);
        buf.writeFloat(packet.progress);
    }

    private static GrapplePullPacket decode(FriendlyByteBuf buf) {
        return new GrapplePullPacket(
                buf.readUUID(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readFloat()
        );
    }

    public static void handle(GrapplePullPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && mc.player != null) {
                Player player = mc.player;
                if (player.getUUID().equals(packet.playerId)) {
                    // Apply velocity on client for smooth movement
                    player.setDeltaMovement(packet.velocityX, packet.velocityY, packet.velocityZ);
                    player.hurtMarked = true;
                    MCGrappin.LOGGER.debug("Client: Applied pull velocity ({}, {}, {}) progress: {}",
                            packet.velocityX, packet.velocityY, packet.velocityZ, packet.progress);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
