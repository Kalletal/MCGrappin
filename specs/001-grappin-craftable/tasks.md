# Tasks: Grappin Craftable Multi-Matériaux

**Input**: Design documents from `/specs/001-grappin-craftable/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: Tests manuels en jeu uniquement (pas de tests unitaires demandés).

**Organization**: Tasks groupées par user story pour permettre une implémentation et des tests indépendants.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Peut être exécuté en parallèle (fichiers différents, pas de dépendances)
- **[Story]**: User story concernée (US1, US2, US3, US4)
- Chemins exacts inclus dans les descriptions

## Path Conventions (Minecraft Mod)

```
src/main/java/com/mcgrappin/     # Code Java
src/main/resources/              # Ressources (JSON, textures)
```

---

## Phase 1: Setup (Infrastructure Projet)

**Purpose**: Initialisation du projet Forge/NeoForge et structure de base

- [x] T001 Initialiser le projet Forge MDK pour Minecraft 1.21.1 avec Gradle
- [x] T002 Créer la structure de packages dans src/main/java/com/mcgrappin/ (client/, common/, items/, entities/, network/, controllers/, config/)
- [x] T003 [P] Configurer le fichier src/main/resources/META-INF/mods.toml avec les métadonnées du mod
- [x] T004 [P] Créer la classe principale MCGrappin.java dans src/main/java/com/mcgrappin/MCGrappin.java
- [x] T005 [P] Créer GrappleConfig.java dans src/main/java/com/mcgrappin/config/GrappleConfig.java avec ForgeConfigSpec

---

## Phase 2: Foundational (Prérequis Bloquants)

**Purpose**: Infrastructure réseau et enregistrements qui DOIVENT être complets avant toute user story

**⚠️ CRITIQUE**: Aucune implémentation de user story ne peut commencer avant cette phase

- [x] T006 Créer GrappleNetwork.java dans src/main/java/com/mcgrappin/network/GrappleNetwork.java avec SimpleChannel
- [x] T007 [P] Créer le packet GrappleLaunchPacket.java dans src/main/java/com/mcgrappin/network/GrappleLaunchPacket.java
- [x] T008 [P] Créer le packet GrappleHitPacket.java dans src/main/java/com/mcgrappin/network/GrappleHitPacket.java
- [x] T009 [P] Créer le packet GrapplePullPacket.java dans src/main/java/com/mcgrappin/network/GrapplePullPacket.java
- [x] T010 [P] Créer le packet GrappleReleasePacket.java dans src/main/java/com/mcgrappin/network/GrappleReleasePacket.java
- [x] T011 [P] Créer le packet GrappleDamagePacket.java dans src/main/java/com/mcgrappin/network/GrappleDamagePacket.java
- [x] T012 Enregistrer tous les packets dans GrappleNetwork.register() appelé depuis MCGrappin.java
- [x] T013 [P] Créer CommonSetup.java dans src/main/java/com/mcgrappin/common/CommonSetup.java
- [x] T014 [P] Créer ClientSetup.java dans src/main/java/com/mcgrappin/client/ClientSetup.java

**Checkpoint**: Infrastructure réseau prête - l'implémentation des user stories peut commencer

---

## Phase 3: User Story 1 - Crafter un Grappin en Bois (Priority: P1) 🎯 MVP

**Goal**: Le joueur peut crafter un Grappin en Bois avec le pattern défini

**Independent Test**: Placer les ingrédients (tripwire hook, planche, bloc fer, lingot fer) sur table de craft → obtenir Grappin en Bois

### Implementation for User Story 1

- [ ] T015 [P] [US1] Créer l'enum GrappleMaterial.java dans src/main/java/com/mcgrappin/items/GrappleMaterial.java (WOOD, IRON avec stats)
- [ ] T016 [P] [US1] Créer la classe abstraite GrappleItem.java dans src/main/java/com/mcgrappin/items/GrappleItem.java
- [ ] T017 [US1] Créer WoodenGrappleItem.java dans src/main/java/com/mcgrappin/items/WoodenGrappleItem.java étendant GrappleItem
- [ ] T018 [US1] Enregistrer wooden_grapple dans le registre des items de MCGrappin.java via DeferredRegister
- [ ] T019 [P] [US1] Créer le modèle JSON src/main/resources/assets/mcgrappin/models/item/wooden_grapple.json
- [ ] T020 [P] [US1] Créer la texture 16x16 src/main/resources/assets/mcgrappin/textures/item/wooden_grapple.png
- [ ] T021 [P] [US1] Créer la recette src/main/resources/data/mcgrappin/recipe/wooden_grapple.json selon le pattern défini
- [ ] T022 [P] [US1] Ajouter les traductions dans src/main/resources/assets/mcgrappin/lang/en_us.json
- [ ] T023 [P] [US1] Ajouter les traductions dans src/main/resources/assets/mcgrappin/lang/fr_fr.json

**Checkpoint**: Grappin en Bois craftable et visible dans l'inventaire

---

## Phase 4: User Story 2 - Crafter un Grappin en Fer (Priority: P1)

**Goal**: Le joueur peut crafter un Grappin en Fer avec le pattern défini

**Independent Test**: Placer les ingrédients (tripwire hook, 2 lingots fer, bloc fer) sur table de craft → obtenir Grappin en Fer

### Implementation for User Story 2

- [ ] T024 [US2] Créer IronGrappleItem.java dans src/main/java/com/mcgrappin/items/IronGrappleItem.java étendant GrappleItem
- [ ] T025 [US2] Enregistrer iron_grapple dans le registre des items de MCGrappin.java via DeferredRegister
- [ ] T026 [P] [US2] Créer le modèle JSON src/main/resources/assets/mcgrappin/models/item/iron_grapple.json
- [ ] T027 [P] [US2] Créer la texture 16x16 src/main/resources/assets/mcgrappin/textures/item/iron_grapple.png
- [ ] T028 [P] [US2] Créer la recette src/main/resources/data/mcgrappin/recipe/iron_grapple.json selon le pattern défini
- [ ] T029 [US2] Mettre à jour les traductions en_us.json et fr_fr.json pour iron_grapple

**Checkpoint**: Grappin en Fer craftable, stats supérieures au Grappin en Bois (durabilité +150%, portée +40%)

---

## Phase 5: User Story 3 - Utiliser le Grappin pour se déplacer (Priority: P1)

**Goal**: Le joueur peut lancer le grappin vers un bloc solide et être tracté vers le point d'accroche

**Independent Test**: Tenir un Grappin, clic droit vers un bloc solide → être tracté vers le bloc

### Implementation for User Story 3

- [ ] T030 [US3] Créer GrappleHookEntity.java dans src/main/java/com/mcgrappin/entities/GrappleHookEntity.java étendant ThrowableItemProjectile
- [ ] T031 [US3] Enregistrer GrappleHookEntity dans le registre des EntityTypes de MCGrappin.java
- [ ] T032 [US3] Créer GrappleState.java dans src/main/java/com/mcgrappin/entities/GrappleState.java pour stocker l'état de traction
- [ ] T033 [US3] Créer GrapplePhysics.java dans src/main/java/com/mcgrappin/controllers/GrapplePhysics.java pour calculer le mouvement
- [ ] T034 [US3] Implémenter la méthode use() dans GrappleItem.java pour lancer GrappleHookEntity au clic droit
- [ ] T035 [US3] Implémenter onHitBlock() dans GrappleHookEntity.java pour déclencher l'accroche
- [ ] T036 [US3] Implémenter la logique de traction dans GrapplePhysics.pullPlayerTowards() (côté serveur uniquement)
- [ ] T037 [US3] Implémenter l'envoi de GrappleHitPacket depuis le serveur vers le client
- [ ] T038 [US3] Implémenter l'envoi de GrapplePullPacket à chaque tick pendant la traction
- [ ] T039 [US3] Implémenter GrappleReleasePacket pour arrêter la traction (arrivée ou relâchement)
- [ ] T040 [US3] Créer GrappleHookRenderer.java dans src/main/java/com/mcgrappin/client/renderer/GrappleHookRenderer.java
- [ ] T041 [US3] Enregistrer le renderer dans ClientSetup.java
- [ ] T042 [US3] Ajouter le rendu de la corde entre joueur et crochet dans GrappleHookRenderer

**Checkpoint**: Le grappin fonctionne pour la mobilité - lancer, accrocher, tracter, relâcher

---

## Phase 6: User Story 4 - Utiliser le Grappin comme arme (Priority: P2)

**Goal**: Le joueur peut infliger des dégâts aux entités touchées par le grappin

**Independent Test**: Lancer le grappin vers un mob → le mob reçoit des dégâts correspondant au type de grappin

### Implementation for User Story 4

- [ ] T043 [US4] Implémenter onHitEntity() dans GrappleHookEntity.java pour détecter la collision avec entités
- [ ] T044 [US4] Calculer les dégâts basés sur GrappleMaterial (bois=3.0, fer=5.0) dans GrappleHookEntity
- [ ] T045 [US4] Appeler entity.hurt() avec le DamageSource approprié dans onHitEntity()
- [ ] T046 [US4] Envoyer GrappleDamagePacket au client pour les effets visuels
- [ ] T047 [US4] Ajouter la gestion du son d'impact dans ClientSetup ou via le packet

**Checkpoint**: Le grappin fonctionne comme arme à distance avec dégâts différenciés par matériau

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Améliorations affectant toutes les user stories

- [ ] T048 [P] Implémenter la gestion de la durabilité dans GrappleItem.java (décrémenter à chaque utilisation)
- [ ] T049 [P] Implémenter le cooldown entre utilisations dans GrappleItem.canUse()
- [ ] T050 [P] Gérer le cas hors portée - rétraction automatique si distance max dépassée
- [ ] T051 [P] Gérer le cas bloc détruit pendant traction - envoyer GrappleReleasePacket(BROKEN)
- [ ] T052 [P] Ajouter les edge cases: utilisation sous l'eau, blocs non-solides
- [ ] T053 [P] Créer CommonEventHandlers.java dans src/main/java/com/mcgrappin/common/CommonEventHandlers.java
- [ ] T054 [P] Créer ClientEventHandlers.java dans src/main/java/com/mcgrappin/client/ClientEventHandlers.java
- [ ] T055 Tester en multijoueur: vérifier synchronisation client-serveur sans désync
- [ ] T056 Validation finale avec quickstart.md: lancer ./gradlew runClient et tester tous les scénarios

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1 (Setup)          → Aucune dépendance
Phase 2 (Foundational)   → Dépend de Phase 1
Phase 3-6 (User Stories) → Dépendent de Phase 2
Phase 7 (Polish)         → Dépend de Phase 3-6
```

### User Story Dependencies

- **US1 (Grappin Bois)**: Indépendante après Phase 2
- **US2 (Grappin Fer)**: Dépend de US1 (réutilise GrappleItem, GrappleMaterial)
- **US3 (Mobilité)**: Indépendante après Phase 2, peut être parallèle à US1/US2
- **US4 (Arme)**: Dépend de US3 (réutilise GrappleHookEntity)

### Ordre d'exécution recommandé

```
1. Setup (T001-T005)
2. Foundational (T006-T014)
3. US1 + US2 en parallèle (T015-T029) - crafting des items
4. US3 (T030-T042) - mécanique de mobilité
5. US4 (T043-T047) - mécanique de combat
6. Polish (T048-T056)
```

### Parallel Opportunities

**Phase 2 - Packets en parallèle**:
```
T007, T008, T009, T010, T011 peuvent être exécutés simultanément
```

**Phase 3 - Ressources US1 en parallèle**:
```
T019, T020, T021, T022, T023 peuvent être exécutés simultanément
```

**Phase 4 - Ressources US2 en parallèle**:
```
T026, T027, T028 peuvent être exécutés simultanément
```

---

## Implementation Strategy

### MVP First (User Stories 1 + 2 + 3)

1. Complete Phase 1: Setup Forge
2. Complete Phase 2: Infrastructure réseau
3. Complete Phase 3: Grappin Bois craftable
4. Complete Phase 4: Grappin Fer craftable
5. Complete Phase 5: Mécanique de mobilité
6. **STOP and VALIDATE**: Tester crafting et mobilité en jeu
7. Les items sont utilisables sans la fonctionnalité arme

### Full Feature

1. Complete MVP (US1-3)
2. Add US4: Mode arme
3. Add Polish: Durabilité, cooldown, edge cases
4. Test multijoueur

---

## Summary

| Phase | User Story | Tasks | Parallelizable |
|-------|------------|-------|----------------|
| Setup | - | 5 | 3 |
| Foundational | - | 9 | 7 |
| US1 | Craft Bois | 9 | 6 |
| US2 | Craft Fer | 6 | 3 |
| US3 | Mobilité | 13 | 0 |
| US4 | Arme | 5 | 0 |
| Polish | - | 9 | 7 |
| **Total** | | **56** | **26** |

---

## Notes

- [P] = fichiers différents, pas de dépendances
- [USx] = appartient à la user story x
- Chaque user story devrait être testable indépendamment
- Commit après chaque tâche ou groupe logique
- Côté serveur UNIQUEMENT pour les modifications de vélocité du joueur
- Le Grappin en Fer a des stats 50%+ supérieures au Grappin en Bois (constitution SC-004)
