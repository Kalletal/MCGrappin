package com.mcgrappin.network;

import com.mcgrappin.MCGrappin;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class GrappleNetwork {
    public static final String PROTOCOL_VERSION = "1.0";

    public static void register() {
        MCGrappin.LOGGER.info("GrappleNetwork registered");
    }

    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MCGrappin.MOD_ID).versioned(PROTOCOL_VERSION);

        // Client -> Server packets
        registrar.playToServer(
                GrappleLaunchPacket.TYPE,
                GrappleLaunchPacket.STREAM_CODEC,
                GrappleLaunchPacket::handle
        );

        registrar.playToServer(
                GrappleCancelPacket.TYPE,
                GrappleCancelPacket.STREAM_CODEC,
                GrappleCancelPacket::handle
        );

        // Server -> Client packets
        registrar.playToClient(
                GrappleHitPacket.TYPE,
                GrappleHitPacket.STREAM_CODEC,
                GrappleHitPacket::handle
        );

        registrar.playToClient(
                GrapplePullPacket.TYPE,
                GrapplePullPacket.STREAM_CODEC,
                GrapplePullPacket::handle
        );

        registrar.playToClient(
                GrappleDamagePacket.TYPE,
                GrappleDamagePacket.STREAM_CODEC,
                GrappleDamagePacket::handle
        );

        registrar.playToClient(
                GrappleReleasePacket.TYPE,
                GrappleReleasePacket.STREAM_CODEC,
                GrappleReleasePacket::handle
        );

        MCGrappin.LOGGER.info("GrappleNetwork payloads registered");
    }

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        PacketDistributor.sendToAllPlayers(payload);
    }
}
