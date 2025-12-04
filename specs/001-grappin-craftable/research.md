# Research: Grappin Craftable Multi-Matériaux

**Feature Branch**: `001-grappin-craftable`
**Date**: 2025-12-04
**Status**: Complete

## 1. Mod Loader & Framework

### Decision: Minecraft Forge pour 1.21.1
**Rationale**: Conformément à la constitution du projet, Forge est le mod loader choisi. Pour Minecraft 1.21.1, NeoForge (fork de Forge) est l'option recommandée car il maintient la compatibilité avec l'écosystème Forge tout en offrant des améliorations.

**Alternatives considérées**:
- **Fabric**: Plus léger mais différent en architecture. Non retenu car la constitution spécifie Forge.
- **MCreator**: Outil visuel mais limité pour les mécaniques complexes comme la physique du grappin.

**Sources**:
- [NeoForged Documentation](https://docs.neoforged.net/docs/1.21.1/)
- [Grappling Hook Mod GitHub](https://github.com/yyon/grapplemod)

## 2. Architecture du Mod Grappin

### Decision: Structure Client/Serveur séparée avec packets réseau

Basé sur l'analyse du mod [yyon/grapplemod](https://github.com/yyon/grapplemod), la structure recommandée est:

```
com/mcgrappin/
├── client/           # Rendu, inputs, visuels côté client
│   ├── ClientSetup.java
│   ├── ClientEventHandlers.java
│   └── renderer/     # Rendu de l'entité crochet/corde
├── common/           # Logique partagée
│   ├── CommonSetup.java
│   └── CommonEventHandlers.java
├── server/           # Logique serveur pure
├── items/            # Définition des items Grappin
├── entities/         # Entité projectile du crochet
├── controllers/      # Physique de traction du joueur
├── network/          # Packets client-serveur
└── config/           # Configuration utilisateur
```

**Rationale**: Cette séparation assure:
- Pas de crash en mode serveur dédié (code client isolé)
- Synchronisation robuste via packets réseau
- Facilité de maintenance

## 3. Création d'Items Personnalisés

### Decision: Hériter de `Item` avec `DeferredRegister`

Pour créer les items Grappin (bois et fer):

```java
public static final DeferredRegister<Item> ITEMS =
    DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

public static final RegistryObject<Item> WOODEN_GRAPPLE = ITEMS.register("wooden_grapple",
    () -> new GrappleItem(new Item.Properties()
        .durability(100)
        .rarity(Rarity.COMMON)));

public static final RegistryObject<Item> IRON_GRAPPLE = ITEMS.register("iron_grapple",
    () -> new GrappleItem(new Item.Properties()
        .durability(250)  // +150% durabilité
        .rarity(Rarity.UNCOMMON)));
```

**Rationale**: `DeferredRegister` est le pattern standard Forge/NeoForge depuis 1.16+.

## 4. Entité Projectile (Crochet)

### Decision: Étendre `ThrowableItemProjectile` ou `Projectile`

Le crochet lancé doit être une entité projectile qui:
- Voyage vers la cible
- Détecte les collisions avec blocs/entités
- Notifie le serveur de l'accroche

**Pattern recommandé**:
```java
public class GrappleHookEntity extends ThrowableItemProjectile {

    public GrappleHookEntity(EntityType<? extends GrappleHookEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        // Accroche au bloc - déclencher la traction
        if (!level().isClientSide) {
            // Envoyer packet au client pour démarrer la physique
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // Infliger dégâts si mode arme
        result.getEntity().hurt(damageSources().thrown(this, getOwner()), getDamage());
    }
}
```

**Alternatives considérées**:
- **FishingHook**: Trop spécialisé, difficile à adapter
- **Arrow**: Problèmes avec la physique de retour

**Source**: [Modding Tutorials - Custom Projectile](https://moddingtutorials.org/1.16.5/arrows/)

## 5. Physique de Traction

### Decision: Modifier le motion vector du joueur côté serveur

**CRITIQUE**: Toute modification de vélocité DOIT être faite côté serveur.

```java
// Côté serveur uniquement
public void pullPlayerTowards(ServerPlayer player, Vec3 target) {
    if (!player.level().isClientSide) {
        Vec3 direction = target.subtract(player.position()).normalize();
        double speed = 0.5; // Configurable

        player.setDeltaMovement(direction.scale(speed));
        player.hurtMarked = true; // Force sync au client
    }
}
```

La physique Minecraft applique automatiquement:
- Gravité: -0.08 par tick sur Y
- Air drag: vélocité * 0.98 par tick
- Slipperiness du bloc au sol

**Source**: [Entity Movement And Physics](https://github.com/ddevault/TrueCraft/wiki/Entity-Movement-And-Physics)

## 6. Recettes de Craft JSON

### Decision: Fichiers JSON dans `data/<modid>/recipe/`

**Note importante**: Depuis Minecraft 1.21, le chemin est `recipe/` (singulier) et non `recipes/` (pluriel).

**Grappin en Bois** (`wooden_grapple.json`):
```json
{
  "type": "minecraft:crafting_shaped",
  "category": "equipment",
  "pattern": [
    "  H",
    " P ",
    "BI "
  ],
  "key": {
    "H": "minecraft:tripwire_hook",
    "P": "#minecraft:planks",
    "B": "minecraft:iron_block",
    "I": "minecraft:iron_ingot"
  },
  "result": {
    "id": "mcgrappin:wooden_grapple",
    "count": 1
  }
}
```

**Grappin en Fer** (`iron_grapple.json`):
```json
{
  "type": "minecraft:crafting_shaped",
  "category": "equipment",
  "pattern": [
    "  H",
    " I ",
    "BI "
  ],
  "key": {
    "H": "minecraft:tripwire_hook",
    "I": "minecraft:iron_ingot",
    "B": "minecraft:iron_block"
  },
  "result": {
    "id": "mcgrappin:iron_grapple",
    "count": 1
  }
}
```

**Source**: [NeoForged Recipes Documentation](https://docs.neoforged.net/docs/resources/server/recipes/builtin/)

## 7. Synchronisation Réseau

### Decision: Custom Packets avec SimpleChannel

Pour synchroniser l'état du grappin entre client et serveur:

```java
public class GrappleNetwork {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MOD_ID, "main"),
        () -> "1.0",
        s -> true,
        s -> true
    );

    public static void register() {
        CHANNEL.registerMessage(0, GrappleStatePacket.class,
            GrappleStatePacket::encode,
            GrappleStatePacket::decode,
            GrappleStatePacket::handle);
    }
}
```

**Packets nécessaires**:
1. `GrappleLaunchPacket`: Client → Serveur (demande de lancer)
2. `GrappleHitPacket`: Serveur → Client (crochet accroché, position)
3. `GrappleReleasePacket`: Bidirectionnel (arrêt de la traction)

## 8. Rendu du Crochet et de la Corde

### Decision: EntityRenderer personnalisé avec ligne dynamique

Le rendu doit inclure:
- Le modèle/texture du crochet
- Une ligne (corde) du joueur au crochet

```java
public class GrappleHookRenderer extends EntityRenderer<GrappleHookEntity> {
    @Override
    public void render(GrappleHookEntity entity, float yaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int light) {
        // Rendu du crochet
        // Rendu de la ligne vers le propriétaire
    }
}
```

**Source**: [Forge Tutorials - Rendering Custom Item Entity](https://github.com/coolAlias/Forge_Tutorials/blob/master/RenderingCustomItemEntity.java)

## 9. Configuration

### Decision: Fichier TOML avec ForgeConfigSpec

```java
public class GrappleConfig {
    public static final ForgeConfigSpec.IntValue WOODEN_DURABILITY;
    public static final ForgeConfigSpec.IntValue IRON_DURABILITY;
    public static final ForgeConfigSpec.DoubleValue PULL_SPEED;
    public static final ForgeConfigSpec.IntValue MAX_RANGE;
    public static final ForgeConfigSpec.IntValue COOLDOWN_TICKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        // Définition des valeurs...
    }
}
```

**Rationale**: Conforme au principe IV de la constitution (Configurabilité).

## 10. Durabilité et Dégâts

### Decision: Valeurs équilibrées basées sur les matériaux Minecraft

| Propriété | Grappin Bois | Grappin Fer | Ratio |
|-----------|--------------|-------------|-------|
| Durabilité | 100 | 250 | +150% (> 50% requis) |
| Dégâts | 3.0 | 5.0 | +67% |
| Portée | 25 blocs | 35 blocs | +40% |
| Cooldown | 30 ticks | 20 ticks | -33% |

**Rationale**: Le fer est un matériau mid-game, les stats reflètent cette progression.

## Résumé des Technologies

| Composant | Technologie |
|-----------|-------------|
| Langage | Java 17+ |
| Mod Loader | Forge/NeoForge 1.21.1 |
| Build Tool | Gradle |
| Recettes | JSON (data-driven) |
| Config | TOML (ForgeConfigSpec) |
| Réseau | SimpleChannel |
| Tests | JUnit + tests en jeu |
