package com.mcgrappin.items;

import com.mcgrappin.config.GrappleConfig;

public enum GrappleMaterial {
    WOOD(100, 3.0f, 25, 0.5, 30),
    IRON(250, 5.0f, 35, 0.6, 20);

    private final int defaultDurability;
    private final float defaultDamage;
    private final int defaultRange;
    private final double defaultPullSpeed;
    private final int defaultCooldown;

    GrappleMaterial(int durability, float damage, int range, double pullSpeed, int cooldown) {
        this.defaultDurability = durability;
        this.defaultDamage = damage;
        this.defaultRange = range;
        this.defaultPullSpeed = pullSpeed;
        this.defaultCooldown = cooldown;
    }

    public int getDurability() {
        return switch (this) {
            case WOOD -> GrappleConfig.WOODEN_DURABILITY.get();
            case IRON -> GrappleConfig.IRON_DURABILITY.get();
        };
    }

    public float getDamage() {
        return switch (this) {
            case WOOD -> GrappleConfig.WOODEN_DAMAGE.get().floatValue();
            case IRON -> GrappleConfig.IRON_DAMAGE.get().floatValue();
        };
    }

    public int getRange() {
        return switch (this) {
            case WOOD -> GrappleConfig.WOODEN_RANGE.get();
            case IRON -> GrappleConfig.IRON_RANGE.get();
        };
    }

    public double getPullSpeed() {
        return switch (this) {
            case WOOD -> GrappleConfig.WOODEN_PULL_SPEED.get();
            case IRON -> GrappleConfig.IRON_PULL_SPEED.get();
        };
    }

    public int getCooldown() {
        return switch (this) {
            case WOOD -> GrappleConfig.WOODEN_COOLDOWN.get();
            case IRON -> GrappleConfig.IRON_COOLDOWN.get();
        };
    }

    public int getDefaultDurability() {
        return defaultDurability;
    }

    public float getDefaultDamage() {
        return defaultDamage;
    }

    public int getDefaultRange() {
        return defaultRange;
    }

    public double getDefaultPullSpeed() {
        return defaultPullSpeed;
    }

    public int getDefaultCooldown() {
        return defaultCooldown;
    }
}
