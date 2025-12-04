package com.mcgrappin.entities;

import com.mcgrappin.MCGrappin;
import com.mcgrappin.controllers.GrapplePhysics;
import com.mcgrappin.items.GrappleItem;
import com.mcgrappin.items.GrappleMaterial;
import com.mcgrappin.network.GrappleHitPacket;
import com.mcgrappin.network.GrappleNetwork;
import com.mcgrappin.network.GrappleDamagePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GrappleHookEntity extends ThrowableItemProjectile {

    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(
            GrappleHookEntity.class, EntityDataSerializers.INT);

    private ItemStack grappleStack = ItemStack.EMPTY;
    private GrappleMaterial material = GrappleMaterial.WOOD;
    private Vec3 attachPoint = null;
    private int ticksInAir = 0;

    public GrappleHookEntity(EntityType<? extends GrappleHookEntity> entityType, Level level) {
        super(entityType, level);
    }

    public GrappleHookEntity(Player owner, Level level, ItemStack stack) {
        super(MCGrappin.GRAPPLE_HOOK_ENTITY.get(), owner, level);
        this.grappleStack = stack.copy();
        if (stack.getItem() instanceof GrappleItem grappleItem) {
            this.material = grappleItem.getMaterial();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STATE, GrappleState.FLYING.ordinal());
    }

    @Override
    protected Item getDefaultItem() {
        return MCGrappin.WOODEN_GRAPPLE.get();
    }

    @Override
    protected double getDefaultGravity() {
        // No gravity - flies straight like a trident/harpoon
        return 0.0;
    }

    @Override
    public void tick() {
        // Maintain velocity even in water (harpoon behavior)
        Vec3 currentVelocity = this.getDeltaMovement();

        // Call parent tick
        tickInternal();

        // If in water, restore velocity to counteract water drag
        if (this.isInWater() && getState() == GrappleState.FLYING) {
            this.setDeltaMovement(currentVelocity);
        }
    }

    private void tickInternal() {
        if (getState() == GrappleState.FLYING && !level().isClientSide) {
            // Perform raycast BEFORE movement to detect blocks
            Vec3 currentPos = this.position();
            Vec3 velocity = this.getDeltaMovement();
            Vec3 nextPos = currentPos.add(velocity);

            BlockHitResult blockHit = level().clip(new ClipContext(
                    currentPos,
                    nextPos,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    this
            ));

            if (blockHit.getType() == HitResult.Type.BLOCK) {
                // Stop at hit location and trigger collision
                this.setPos(blockHit.getLocation());
                this.setDeltaMovement(Vec3.ZERO);
                this.onHitBlock(blockHit);
                return; // Don't call super.tick() - we handled the collision
            }
        }

        super.tick();

        ticksInAir++;

        // Check if we've exceeded max range
        if (getState() == GrappleState.FLYING) {
            Player owner = getPlayerOwner();
            if (owner != null) {
                double distance = this.position().distanceTo(owner.position());
                if (distance > material.getRange() || ticksInAir > 100) {
                    // Exceeded range - discard
                    this.discard();
                }
            }
        }

        // If attached and owner is close enough, complete the pull
        if (getState() == GrappleState.ATTACHED && attachPoint != null) {
            Player owner = getPlayerOwner();
            if (owner != null) {
                double distance = owner.position().distanceTo(attachPoint);
                if (distance < 1.5) {
                    // Player has arrived
                    this.discard();
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if (!level().isClientSide) {
            // Attach slightly in front of the block (offset by 0.5 blocks from the surface)
            Vec3 hitLocation = result.getLocation();
            Vec3 normal = Vec3.atLowerCornerOf(result.getDirection().getNormal());
            this.attachPoint = hitLocation.add(normal.scale(0.5));
            setState(GrappleState.ATTACHED);

            // Notify the owner and start pulling
            Player owner = getPlayerOwner();
            if (owner instanceof ServerPlayer serverPlayer) {
                // Send hit packet to client
                GrappleNetwork.sendToPlayer(serverPlayer, new GrappleHitPacket(
                        this.getId(),
                        GrappleHitPacket.HIT_BLOCK,
                        attachPoint.x,
                        attachPoint.y,
                        attachPoint.z,
                        -1 // No attached entity
                ));

                // Start pulling the player towards the attach point
                GrapplePhysics.startPull(serverPlayer, this, attachPoint);

                MCGrappin.LOGGER.debug("Grapple attached at {} for player {}",
                        attachPoint, owner.getName().getString());
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (!level().isClientSide) {
            // Inflict damage based on material
            float damage = material.getDamage();

            if (result.getEntity() instanceof LivingEntity target) {
                target.hurt(damageSources().thrown(this, getOwner()), damage);

                // Send damage packet for visual effects
                Player owner = getPlayerOwner();
                if (owner instanceof ServerPlayer serverPlayer) {
                    GrappleNetwork.sendToPlayer(serverPlayer, new GrappleDamagePacket(
                            target.getId(),
                            damage,
                            this.getId()
                    ));
                }

                MCGrappin.LOGGER.debug("Grapple hit entity {} for {} damage",
                        target.getName().getString(), damage);
            }

            // Discard after hitting entity (doesn't attach to entities)
            this.discard();
        }
    }

    public Player getPlayerOwner() {
        if (getOwner() instanceof Player player) {
            return player;
        }
        return null;
    }

    public GrappleState getState() {
        return GrappleState.values()[this.entityData.get(STATE)];
    }

    public void setState(GrappleState state) {
        this.entityData.set(STATE, state.ordinal());
    }

    public Vec3 getAttachPoint() {
        return attachPoint;
    }

    public GrappleMaterial getMaterial() {
        return material;
    }

    public ItemStack getGrappleStack() {
        return grappleStack;
    }
}
