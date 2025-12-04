package com.mcgrappin.controllers;

import com.mcgrappin.MCGrappin;
import com.mcgrappin.entities.GrappleHookEntity;
import com.mcgrappin.entities.GrappleState;
import com.mcgrappin.network.GrappleNetwork;
import com.mcgrappin.network.GrapplePullPacket;
import com.mcgrappin.network.GrappleReleasePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the physics of pulling players towards grapple hook attach points.
 * All velocity modifications are done server-side only.
 */
public class GrapplePhysics {

    // Active pull states per player
    private static final Map<UUID, PullState> activePulls = new ConcurrentHashMap<>();

    // Distance threshold to consider player arrived
    private static final double ARRIVAL_DISTANCE = 1.5;

    // Maximum pull duration in ticks (safety limit)
    private static final int MAX_PULL_TICKS = 200;

    /**
     * Starts pulling a player towards the attach point.
     */
    public static void startPull(ServerPlayer player, GrappleHookEntity hook, Vec3 attachPoint) {
        double pullSpeed = hook.getMaterial().getPullSpeed();

        PullState state = new PullState(
                hook,
                attachPoint,
                pullSpeed,
                player.position()
        );

        activePulls.put(player.getUUID(), state);

        MCGrappin.LOGGER.debug("Started pulling {} towards {} at speed {}",
                player.getName().getString(), attachPoint, pullSpeed);
    }

    /**
     * Called every tick to update player velocity during a pull.
     * Should be called from a server tick event.
     */
    public static void tickPull(ServerPlayer player) {
        PullState state = activePulls.get(player.getUUID());
        if (state == null) {
            return;
        }

        state.tickCount++;

        // Safety check: max duration
        if (state.tickCount > MAX_PULL_TICKS) {
            stopPull(player, GrappleReleasePacket.REASON_OUT_OF_RANGE);
            return;
        }

        // Check if hook is still valid
        if (state.hook.isRemoved() || state.hook.getState() != GrappleState.ATTACHED) {
            stopPull(player, GrappleReleasePacket.REASON_BROKEN);
            return;
        }

        Vec3 playerPos = player.position();
        Vec3 targetPos = state.attachPoint;

        double distance = playerPos.distanceTo(targetPos);

        // Check if arrived
        if (distance < ARRIVAL_DISTANCE) {
            stopPull(player, GrappleReleasePacket.REASON_ARRIVED);
            return;
        }

        // Calculate pull direction and velocity
        Vec3 direction = targetPos.subtract(playerPos).normalize();
        Vec3 velocity = direction.scale(state.pullSpeed);

        // Apply velocity to player (server-side)
        player.setDeltaMovement(velocity);
        player.hurtMarked = true; // Force sync to client

        // Prevent fall damage during pull
        player.resetFallDistance();

        // Calculate progress (0.0 to 1.0)
        double startDistance = state.startPosition.distanceTo(targetPos);
        float progress = (float) (1.0 - (distance / startDistance));

        // Send pull packet to client for visual feedback
        GrappleNetwork.sendToPlayer(player, new GrapplePullPacket(
                player.getUUID(),
                velocity.x,
                velocity.y,
                velocity.z,
                progress
        ));
    }

    /**
     * Stops pulling a player.
     */
    public static void stopPull(ServerPlayer player, byte reason) {
        PullState state = activePulls.remove(player.getUUID());
        if (state == null) {
            return;
        }

        // Discard the hook entity
        if (!state.hook.isRemoved()) {
            state.hook.discard();
        }

        // Send release packet
        GrappleNetwork.sendToPlayer(player, new GrappleReleasePacket(
                state.hook.getId(),
                reason
        ));

        String reasonStr = switch (reason) {
            case GrappleReleasePacket.REASON_ARRIVED -> "arrived";
            case GrappleReleasePacket.REASON_MANUAL -> "manual";
            case GrappleReleasePacket.REASON_BROKEN -> "broken";
            case GrappleReleasePacket.REASON_OUT_OF_RANGE -> "out_of_range";
            default -> "unknown";
        };

        MCGrappin.LOGGER.debug("Stopped pulling {} (reason: {})",
                player.getName().getString(), reasonStr);
    }

    /**
     * Check if a player is currently being pulled.
     */
    public static boolean isPulling(Player player) {
        return activePulls.containsKey(player.getUUID());
    }

    /**
     * Release grapple for a player (called when player releases the button).
     */
    public static void releaseGrapple(ServerPlayer player) {
        if (isPulling(player)) {
            stopPull(player, GrappleReleasePacket.REASON_MANUAL);
        }
    }

    /**
     * Internal state for tracking a pull operation.
     */
    private static class PullState {
        final GrappleHookEntity hook;
        final Vec3 attachPoint;
        final double pullSpeed;
        final Vec3 startPosition;
        int tickCount = 0;

        PullState(GrappleHookEntity hook, Vec3 attachPoint, double pullSpeed, Vec3 startPosition) {
            this.hook = hook;
            this.attachPoint = attachPoint;
            this.pullSpeed = pullSpeed;
            this.startPosition = startPosition;
        }
    }
}
