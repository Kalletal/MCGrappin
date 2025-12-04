package com.mcgrappin;

import com.mcgrappin.client.ClientSetup;
import com.mcgrappin.common.CommonSetup;
import com.mcgrappin.config.GrappleConfig;
import com.mcgrappin.entities.GrappleHookEntity;
import com.mcgrappin.items.IronGrappleItem;
import com.mcgrappin.items.WoodenGrappleItem;
import com.mcgrappin.network.GrappleNetwork;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MCGrappin.MOD_ID)
public class MCGrappin {
    public static final String MOD_ID = "mcgrappin";
    public static final Logger LOGGER = LoggerFactory.getLogger(MCGrappin.class);

    // Registries
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);

    // Items
    public static final DeferredHolder<Item, WoodenGrappleItem> WOODEN_GRAPPLE = ITEMS.register("wooden_grapple",
            () -> new WoodenGrappleItem(new Item.Properties().durability(100)));

    public static final DeferredHolder<Item, IronGrappleItem> IRON_GRAPPLE = ITEMS.register("iron_grapple",
            () -> new IronGrappleItem(new Item.Properties().durability(250)));

    // Entity Types
    public static final DeferredHolder<EntityType<?>, EntityType<GrappleHookEntity>> GRAPPLE_HOOK_ENTITY = ENTITY_TYPES.register("grapple_hook",
            () -> EntityType.Builder.<GrappleHookEntity>of(GrappleHookEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(8)
                    .updateInterval(1)  // Update every tick for fast projectile
                    .build(ResourceLocation.fromNamespaceAndPath(MOD_ID, "grapple_hook").toString()));

    public MCGrappin(IEventBus modEventBus, ModContainer modContainer) {
        // Register deferred registers
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);

        // Register config
        modContainer.registerConfig(ModConfig.Type.COMMON, GrappleConfig.SPEC);

        // Register lifecycle events
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(GrappleNetwork::onRegisterPayloads);
        modEventBus.addListener(ClientSetup::registerRenderers);

        LOGGER.info("MCGrappin mod initialized!");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GrappleNetwork.register();
            CommonSetup.init();
        });
        LOGGER.info("MCGrappin common setup complete");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(ClientSetup::init);
        LOGGER.info("MCGrappin client setup complete");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(WOODEN_GRAPPLE.get());
            event.accept(IRON_GRAPPLE.get());
        }
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(WOODEN_GRAPPLE.get());
            event.accept(IRON_GRAPPLE.get());
        }
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
