package com.mcgrappin.network;

import com.mcgrappin.MCGrappin;
import com.mcgrappin.entities.GrappleState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GrappleHitPacket(
        int hookEntityId,
        byte hitType, // 0 = BLOCK, 1 = ENTITY
        double hitX,
        double hitY,
        double hitZ,
        int attachedEntityId // -1 if block
) implements CustomPacketPayload {

    public static final byte HIT_BLOCK = 0;
    public static final byte HIT_ENTITY = 1;

    public static final Type<GrappleHitPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MCGrappin.MOD_ID, "grapple_hit")
    );

    public static final StreamCodec<FriendlyByteBuf, GrappleHitPacket> STREAM_CODEC = StreamCodec.of(
            GrappleHitPacket::encode,
            GrappleHitPacket::decode
    );

    private static void encode(FriendlyByteBuf buf, GrappleHitPacket packet) {
        buf.writeInt(packet.hookEntityId);
        buf.writeByte(packet.hitType);
        buf.writeDouble(packet.hitX);
        buf.writeDouble(packet.hitY);
        buf.writeDouble(packet.hitZ);
        buf.writeInt(packet.attachedEntityId);
    }

    private static GrappleHitPacket decode(FriendlyByteBuf buf) {
        return new GrappleHitPacket(
                buf.readInt(),
                buf.readByte(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readInt()
        );
    }

    public static void handle(GrappleHitPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Client-side handling
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && mc.player != null) {
                if (packet.hitType == HIT_BLOCK) {
                    // Start pulling animation/state
                    GrappleState.setClientPulling(true, packet.hitX, packet.hitY, packet.hitZ);
                    MCGrappin.LOGGER.debug("Client: Grapple hit block at ({}, {}, {})",
                            packet.hitX, packet.hitY, packet.hitZ);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
