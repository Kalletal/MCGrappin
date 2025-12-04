package com.mcgrappin.network;

import com.mcgrappin.MCGrappin;
import com.mcgrappin.entities.GrappleState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GrappleReleasePacket(
        int hookEntityId,
        byte reason // 0=MANUAL, 1=ARRIVED, 2=BROKEN, 3=OUT_OF_RANGE
) implements CustomPacketPayload {

    public static final byte REASON_MANUAL = 0;
    public static final byte REASON_ARRIVED = 1;
    public static final byte REASON_BROKEN = 2;
    public static final byte REASON_OUT_OF_RANGE = 3;

    public static final Type<GrappleReleasePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MCGrappin.MOD_ID, "grapple_release")
    );

    public static final StreamCodec<FriendlyByteBuf, GrappleReleasePacket> STREAM_CODEC = StreamCodec.of(
            GrappleReleasePacket::encode,
            GrappleReleasePacket::decode
    );

    private static void encode(FriendlyByteBuf buf, GrappleReleasePacket packet) {
        buf.writeInt(packet.hookEntityId);
        buf.writeByte(packet.reason);
    }

    private static GrappleReleasePacket decode(FriendlyByteBuf buf) {
        return new GrappleReleasePacket(
                buf.readInt(),
                buf.readByte()
        );
    }

    public static void handle(GrappleReleasePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Client-side handling only
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && mc.player != null) {
                GrappleState.setClientPulling(false, 0, 0, 0);
                MCGrappin.LOGGER.debug("Client: Grapple released reason: {}", packet.reason);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
