# Data Model: Grappin Craftable Multi-Matériaux

**Feature Branch**: `001-grappin-craftable`
**Date**: 2025-12-04

## Entités Principales

### 1. GrappleItem (Item Minecraft)

Représente l'item Grappin dans l'inventaire du joueur.

| Attribut | Type | Description | Contraintes |
|----------|------|-------------|-------------|
| `materialType` | Enum | Type de matériau (WOOD, IRON) | Non-null |
| `durability` | int | Points de durabilité restants | > 0 |
| `maxDurability` | int | Durabilité maximale | WOOD=100, IRON=250 |
| `damage` | float | Dégâts infligés en mode arme | WOOD=3.0, IRON=5.0 |
| `range` | int | Portée maximale en blocs | WOOD=25, IRON=35 |
| `cooldownTicks` | int | Temps de recharge en ticks | WOOD=30, IRON=20 |

**Comportements**:
- `use()`: Lance le crochet vers la cible
- `onHit()`: Décrémente la durabilité
- `canUse()`: Vérifie durabilité > 0 et cooldown écoulé

**Relations**:
- Crée une `GrappleHookEntity` lors de l'utilisation
- Appartient à un `Player`

---

### 2. GrappleHookEntity (Entité Projectile)

Représente le crochet projeté dans le monde.

| Attribut | Type | Description | Contraintes |
|----------|------|-------------|-------------|
| `owner` | Player | Joueur propriétaire | Non-null |
| `state` | Enum | État du crochet | FLYING, ATTACHED, RETRACTING |
| `position` | Vec3d | Position dans le monde | Valide |
| `velocity` | Vec3d | Vélocité de déplacement | - |
| `attachPoint` | Vec3d | Point d'ancrage (si attaché) | Null si FLYING |
| `attachedBlock` | BlockPos | Position du bloc accroché | Null si FLYING |
| `materialType` | Enum | Hérité de l'item parent | WOOD ou IRON |

**États et Transitions**:

```
[IDLE] --use()--> [FLYING] --hitBlock()--> [ATTACHED]
                     |                          |
                     v                          v
              hitEntity()               pullComplete()
                     |                          |
                     v                          v
               [RETRACT] <----release()---- [PULLING]
                     |
                     v
                 [IDLE]
```

**Comportements**:
- `tick()`: Mise à jour position/vélocité
- `onHitBlock()`: Transition vers ATTACHED, commence la traction
- `onHitEntity()`: Inflige dégâts, transition vers RETRACTING
- `onMiss()`: Si aucune collision après range max, rétracte

---

### 3. GrappleState (État de Traction)

État temporaire stocké pendant la traction du joueur.

| Attribut | Type | Description |
|----------|------|-------------|
| `playerId` | UUID | Identifiant du joueur |
| `hookEntity` | GrappleHookEntity | Entité crochet active |
| `startPosition` | Vec3d | Position de départ du joueur |
| `targetPosition` | Vec3d | Point d'ancrage cible |
| `startTime` | long | Timestamp de début (ticks) |
| `pullSpeed` | double | Vitesse de traction |

**Invariants**:
- Un seul `GrappleState` actif par joueur
- Nettoyé automatiquement à la fin de la traction

---

### 4. GrappleRecipe (Recette de Craft)

Définition des patterns de craft.

| Recette | Pattern | Ingrédients |
|---------|---------|-------------|
| Grappin Bois | `"  H"`, `" P "`, `"BI "` | H=tripwire_hook, P=planks, B=iron_block, I=iron_ingot |
| Grappin Fer | `"  H"`, `" I "`, `"BI "` | H=tripwire_hook, I=iron_ingot, B=iron_block |

**Positions grille 3x3**:
```
[1][2][3]  ->  [ ][ ][H]
[4][5][6]  ->  [ ][*][ ]   * = P (bois) ou I (fer)
[7][8][9]  ->  [B][I][ ]
```

---

### 5. GrappleConfig (Configuration)

Paramètres configurables par l'utilisateur.

| Paramètre | Type | Défaut Bois | Défaut Fer | Description |
|-----------|------|-------------|------------|-------------|
| `durability` | int | 100 | 250 | Durabilité initiale |
| `damage` | float | 3.0 | 5.0 | Dégâts en combat |
| `range` | int | 25 | 35 | Portée max (blocs) |
| `pullSpeed` | double | 0.5 | 0.6 | Vitesse de traction |
| `cooldownTicks` | int | 30 | 20 | Temps recharge |
| `allowWaterUse` | boolean | false | false | Utilisation sous l'eau |

---

## Diagramme de Relations

```
┌─────────────┐         ┌──────────────────┐
│   Player    │         │   GrappleItem    │
│             │ holds   │                  │
│  - inventory├────────►│ - materialType   │
│  - position │         │ - durability     │
│             │         │ - damage/range   │
└──────┬──────┘         └────────┬─────────┘
       │                         │
       │ has active              │ spawns
       │                         │
       ▼                         ▼
┌──────────────┐         ┌──────────────────┐
│ GrappleState │◄───────►│ GrappleHookEntity│
│              │         │                  │
│ - targetPos  │ tracks  │ - state          │
│ - pullSpeed  │         │ - attachPoint    │
│ - startTime  │         │ - velocity       │
└──────────────┘         └──────────────────┘
                                 │
                                 │ collides with
                                 ▼
                         ┌──────────────────┐
                         │ Block / Entity   │
                         │                  │
                         │ - solid blocks   │
                         │ - living entities│
                         └──────────────────┘
```

---

## Règles de Validation

### FR-001/FR-002: Craft valide
- Pattern doit correspondre exactement
- Tous les ingrédients présents
- Résultat: 1 item Grappin du type correspondant

### FR-004/FR-005: Utilisation valide
- Joueur doit tenir le Grappin en main
- Durabilité > 0
- Cooldown écoulé
- Cible à portée et bloc solide

### FR-006: Mode arme
- Si collision avec entité vivante
- Dégâts = `damage` de l'item
- Durabilité décrémentée

### FR-007: Différentiation matériaux
- Fer > Bois sur: durabilité (+150%), dégâts (+67%), portée (+40%)
- Fer < Bois sur: cooldown (-33%)

### FR-008: Durabilité
- Décrémentée de 1 à chaque utilisation (lancer + accroche)
- Item détruit quand durabilité = 0
