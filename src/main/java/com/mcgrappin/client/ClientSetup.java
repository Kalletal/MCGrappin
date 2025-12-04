package com.mcgrappin.client;

import com.mcgrappin.MCGrappin;
import com.mcgrappin.client.renderer.GrappleHookRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientSetup {

    public static void init() {
        MCGrappin.LOGGER.info("MCGrappin ClientSetup initialized");
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MCGrappin.GRAPPLE_HOOK_ENTITY.get(), GrappleHookRenderer::new);
        MCGrappin.LOGGER.info("MCGrappin entity renderers registered");
    }
}
