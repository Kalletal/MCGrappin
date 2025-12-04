package com.mcgrappin.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class GrappleConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // Wooden Grapple Config
    public static final ModConfigSpec.IntValue WOODEN_DURABILITY;
    public static final ModConfigSpec.DoubleValue WOODEN_DAMAGE;
    public static final ModConfigSpec.IntValue WOODEN_RANGE;
    public static final ModConfigSpec.DoubleValue WOODEN_PULL_SPEED;
    public static final ModConfigSpec.IntValue WOODEN_COOLDOWN;

    // Iron Grapple Config
    public static final ModConfigSpec.IntValue IRON_DURABILITY;
    public static final ModConfigSpec.DoubleValue IRON_DAMAGE;
    public static final ModConfigSpec.IntValue IRON_RANGE;
    public static final ModConfigSpec.DoubleValue IRON_PULL_SPEED;
    public static final ModConfigSpec.IntValue IRON_COOLDOWN;

    // General Config
    public static final ModConfigSpec.BooleanValue ALLOW_WATER_USE;

    static {
        BUILDER.comment("MCGrappin Configuration").push("grapple");

        // Wooden Grapple
        BUILDER.comment("Wooden Grapple Settings").push("wooden");
        WOODEN_DURABILITY = BUILDER.comment("Durability of the wooden grapple")
                .defineInRange("durability", 100, 1, 10000);
        WOODEN_DAMAGE = BUILDER.comment("Damage dealt by wooden grapple when hitting entities")
                .defineInRange("damage", 3.0, 0.0, 100.0);
        WOODEN_RANGE = BUILDER.comment("Maximum range of wooden grapple in blocks")
                .defineInRange("range", 25, 5, 100);
        WOODEN_PULL_SPEED = BUILDER.comment("Pull speed of wooden grapple")
                .defineInRange("pullSpeed", 0.5, 0.1, 5.0);
        WOODEN_COOLDOWN = BUILDER.comment("Cooldown in ticks between uses")
                .defineInRange("cooldown", 30, 0, 200);
        BUILDER.pop();

        // Iron Grapple
        BUILDER.comment("Iron Grapple Settings").push("iron");
        IRON_DURABILITY = BUILDER.comment("Durability of the iron grapple")
                .defineInRange("durability", 250, 1, 10000);
        IRON_DAMAGE = BUILDER.comment("Damage dealt by iron grapple when hitting entities")
                .defineInRange("damage", 5.0, 0.0, 100.0);
        IRON_RANGE = BUILDER.comment("Maximum range of iron grapple in blocks")
                .defineInRange("range", 35, 5, 100);
        IRON_PULL_SPEED = BUILDER.comment("Pull speed of iron grapple")
                .defineInRange("pullSpeed", 0.6, 0.1, 5.0);
        IRON_COOLDOWN = BUILDER.comment("Cooldown in ticks between uses")
                .defineInRange("cooldown", 20, 0, 200);
        BUILDER.pop();

        // General
        BUILDER.comment("General Settings").push("general");
        ALLOW_WATER_USE = BUILDER.comment("Allow grapple to work underwater")
                .define("allowWaterUse", false);
        BUILDER.pop();

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
