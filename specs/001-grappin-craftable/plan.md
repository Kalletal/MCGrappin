# Implementation Plan: Grappin Craftable Multi-Matériaux

**Branch**: `001-grappin-craftable` | **Date**: 2025-12-04 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-grappin-craftable/spec.md`

## Summary

Développement d'un mod Minecraft Forge/NeoForge 1.21.1 ajoutant un Grappin craftable en deux matériaux (bois et fer). Le Grappin permet au joueur de se tracter vers des blocs solides et peut être utilisé comme arme à distance. L'implémentation utilise une architecture client/serveur avec synchronisation par packets réseau personnalisés.

## Technical Context

**Language/Version**: Java 17+
**Primary Dependencies**: Minecraft Forge/NeoForge 1.21.1, Gradle
**Storage**: N/A (données en mémoire, persistance via NBT Minecraft)
**Testing**: JUnit pour tests unitaires, tests manuels en jeu
**Target Platform**: Minecraft Java Edition 1.21.1 (Windows, Linux, macOS)
**Project Type**: Minecraft Mod (single project)
**Performance Goals**: 60 FPS maintenu, pas de lag réseau perceptible en multijoueur
**Constraints**: Pas de désync client/serveur, physique de traction fluide
**Scale/Scope**: Mod standalone, 2 items, 1 entité projectile, ~15 classes Java

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principe | Statut | Justification |
|----------|--------|---------------|
| **I. Gameplay-First** | ✅ PASS | Mécaniques intuitives (clic droit pour lancer, traction automatique) |
| **II. Vanilla-Compatible** | ✅ PASS | Utilise les conventions Minecraft (Tripwire Hook existant, style visuel cohérent) |
| **III. Performance** | ✅ PASS | Physique simple (vecteur direction), packets légers, pas de calculs lourds |
| **IV. Configurabilité** | ✅ PASS | GrappleConfig avec tous les paramètres clés (portée, vitesse, durabilité) |
| **V. Multijoueur-Ready** | ✅ PASS | Architecture packets réseau, modifications vélocité côté serveur uniquement |

### Post-Design Re-check

| Principe | Statut | Notes |
|----------|--------|-------|
| **I. Gameplay-First** | ✅ | User stories P1 couvrent les interactions de base |
| **II. Vanilla-Compatible** | ✅ | Recettes utilisent items vanilla, tag `minecraft:planks` |
| **III. Performance** | ✅ | 5 types de packets légers, pas de polling |
| **IV. Configurabilité** | ✅ | 6+ paramètres configurables documentés |
| **V. Multijoueur-Ready** | ✅ | Séquence packets complète documentée dans contracts/ |

## Project Structure

### Documentation (this feature)

```text
specs/001-grappin-craftable/
├── plan.md              # Ce fichier
├── spec.md              # Spécification fonctionnelle
├── research.md          # Recherche technique (Phase 0)
├── data-model.md        # Modèle de données (Phase 1)
├── quickstart.md        # Guide de démarrage rapide (Phase 1)
├── contracts/           # Contrats techniques (Phase 1)
│   ├── network-packets.md
│   └── recipes.md
├── checklists/
│   └── requirements.md
└── tasks.md             # Tâches d'implémentation (/speckit.tasks)
```

### Source Code (repository root)

```text
src/main/java/com/mcgrappin/
├── MCGrappin.java              # Point d'entrée du mod, enregistrements
├── client/
│   ├── ClientSetup.java        # Initialisation côté client
│   ├── ClientEventHandlers.java
│   └── renderer/
│       └── GrappleHookRenderer.java  # Rendu entité + corde
├── common/
│   ├── CommonSetup.java        # Initialisation partagée
│   └── CommonEventHandlers.java
├── items/
│   ├── GrappleItem.java        # Item abstrait Grappin
│   ├── WoodenGrappleItem.java  # Spécialisation bois
│   └── IronGrappleItem.java    # Spécialisation fer
├── entities/
│   ├── GrappleHookEntity.java  # Projectile crochet
│   └── GrappleState.java       # État de traction
├── controllers/
│   └── GrapplePhysics.java     # Calculs de mouvement
├── network/
│   ├── GrappleNetwork.java     # Channel et registration
│   ├── GrappleLaunchPacket.java
│   ├── GrappleHitPacket.java
│   ├── GrapplePullPacket.java
│   ├── GrappleReleasePacket.java
│   └── GrappleDamagePacket.java
└── config/
    └── GrappleConfig.java      # Configuration TOML

src/main/resources/
├── META-INF/
│   └── mods.toml               # Métadonnées mod
├── assets/mcgrappin/
│   ├── lang/
│   │   ├── en_us.json          # Traductions anglais
│   │   └── fr_fr.json          # Traductions français
│   ├── models/item/
│   │   ├── wooden_grapple.json
│   │   └── iron_grapple.json
│   └── textures/item/
│       ├── wooden_grapple.png
│       └── iron_grapple.png
└── data/mcgrappin/
    └── recipe/
        ├── wooden_grapple.json
        └── iron_grapple.json

src/test/java/com/mcgrappin/
├── items/
│   └── GrappleItemTest.java
├── entities/
│   └── GrapplePhysicsTest.java
└── network/
    └── PacketSerializationTest.java
```

**Structure Decision**: Structure Minecraft Mod standard avec séparation client/common/server. Les packages `controllers` et `network` gèrent respectivement la physique et la synchronisation multijoueur, conformément aux principes III et V de la constitution.

## Complexity Tracking

> Aucune violation de la constitution identifiée. Le design reste simple et conforme.

| Aspect | Complexité | Justification |
|--------|------------|---------------|
| Packets réseau | Modérée | Nécessaire pour multijoueur (principe V) |
| Physique traction | Simple | Vecteur direction + vitesse fixe |
| Configuration | Simple | ForgeConfigSpec standard |

## Phase Outputs

### Phase 0: Research
- ✅ [research.md](./research.md) - Décisions techniques documentées

### Phase 1: Design & Contracts
- ✅ [data-model.md](./data-model.md) - Modèle de données complet
- ✅ [contracts/network-packets.md](./contracts/network-packets.md) - Protocole réseau
- ✅ [contracts/recipes.md](./contracts/recipes.md) - Recettes de craft JSON
- ✅ [quickstart.md](./quickstart.md) - Guide de démarrage développeur

### Phase 2: Tasks (à venir)
- ⏳ tasks.md - Exécuter `/speckit.tasks` pour générer

## Prochaines Étapes

1. Exécuter `/speckit.tasks` pour générer les tâches d'implémentation
2. Initialiser le projet Forge avec `./gradlew genIntellijRuns`
3. Implémenter selon l'ordre des tâches générées
