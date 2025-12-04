package com.mcgrappin.network;

import com.mcgrappin.MCGrappin;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GrappleDamagePacket(
        int targetEntityId,
        float damage,
        int hookEntityId
) implements CustomPacketPayload {

    public static final Type<GrappleDamagePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MCGrappin.MOD_ID, "grapple_damage")
    );

    public static final StreamCodec<FriendlyByteBuf, GrappleDamagePacket> STREAM_CODEC = StreamCodec.of(
            GrappleDamagePacket::encode,
            GrappleDamagePacket::decode
    );

    private static void encode(FriendlyByteBuf buf, GrappleDamagePacket packet) {
        buf.writeInt(packet.targetEntityId);
        buf.writeFloat(packet.damage);
        buf.writeInt(packet.hookEntityId);
    }

    private static GrappleDamagePacket decode(FriendlyByteBuf buf) {
        return new GrappleDamagePacket(
                buf.readInt(),
                buf.readFloat(),
                buf.readInt()
        );
    }

    public static void handle(GrappleDamagePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Client-side: Show damage effects
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                Entity target = mc.level.getEntity(packet.targetEntityId);
                if (target != null) {
                    // Visual feedback - entity hurt animation is handled automatically
                    MCGrappin.LOGGER.debug("Client: Grapple damage {} to entity {}",
                            packet.damage, target.getName().getString());
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
