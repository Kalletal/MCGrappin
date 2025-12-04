package com.mcgrappin.common;

import com.mcgrappin.MCGrappin;
import com.mcgrappin.controllers.GrapplePhysics;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Server-side event handlers for the grappling hook mod.
 */
@EventBusSubscriber(modid = MCGrappin.MOD_ID)
public class CommonEventHandlers {

    /**
     * Called every tick for each player.
     * Updates the grapple physics if the player is being pulled.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // Tick the grapple physics for this player
            if (GrapplePhysics.isPulling(serverPlayer)) {
                GrapplePhysics.tickPull(serverPlayer);
            }
        }
    }
}
