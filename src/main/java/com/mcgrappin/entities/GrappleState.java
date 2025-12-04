package com.mcgrappin.entities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the state of a grappling hook entity.
 * Also manages client-side pulling state for rendering and movement.
 */
public enum GrappleState {
    /**
     * The hook is flying through the air towards its target.
     */
    FLYING,

    /**
     * The hook has attached to a solid block.
     */
    ATTACHED,

    /**
     * The hook is retracting back to the player.
     */
    RETRACTING;

    // Server-side: track active grapple states per player
    private static final Map<UUID, ActiveGrappleState> activeStates = new ConcurrentHashMap<>();

    // Client-side state tracking
    private static boolean clientPulling = false;
    private static Vec3 clientTargetPosition = null;

    /**
     * Active grapple state data stored server-side.
     */
    public record ActiveGrappleState(
            GrappleHookEntity hookEntity,
            Vec3 targetPosition,
            long startTime,
            double pullSpeed
    ) {}

    /**
     * Stores an active grapple state for a player (server-side).
     */
    public static void setState(Player player, ActiveGrappleState state) {
        activeStates.put(player.getUUID(), state);
    }

    /**
     * Gets the active grapple state for a player (server-side).
     */
    public static ActiveGrappleState getState(Player player) {
        return activeStates.get(player.getUUID());
    }

    /**
     * Removes the active grapple state for a player (server-side).
     */
    public static void removeState(Player player) {
        activeStates.remove(player.getUUID());
    }

    /**
     * Checks if a player has an active grapple state (server-side).
     */
    public static boolean hasState(Player player) {
        return activeStates.containsKey(player.getUUID());
    }

    /**
     * Sets the client-side pulling state.
     * Called when receiving a GrappleHitPacket from the server.
     */
    public static void setClientPulling(boolean pulling, double x, double y, double z) {
        clientPulling = pulling;
        if (pulling) {
            clientTargetPosition = new Vec3(x, y, z);
        } else {
            clientTargetPosition = null;
        }
    }

    /**
     * Clears the client-side pulling state.
     */
    public static void clearClientPulling() {
        clientPulling = false;
        clientTargetPosition = null;
    }

    /**
     * Returns whether the client is currently being pulled.
     */
    public static boolean isClientPulling() {
        return clientPulling;
    }

    /**
     * Returns the target position for the current pull on the client.
     */
    public static Vec3 getClientTargetPosition() {
        return clientTargetPosition;
    }
}
