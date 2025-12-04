# Network Packets Contract: Grappin

**Feature Branch**: `001-grappin-craftable`
**Date**: 2025-12-04

## Vue d'ensemble

Le mod utilise des packets réseau personnalisés pour synchroniser l'état du grappin entre client et serveur. Ceci est essentiel pour le multijoueur.

## Channel Principal

```
Channel ID: mcgrappin:main
Version: 1.0.0
```

---

## Packets

### 1. GrappleLaunchPacket

**Direction**: Client → Serveur
**Trigger**: Joueur fait clic droit avec Grappin en main

| Champ | Type | Description |
|-------|------|-------------|
| `targetX` | double | Position X visée |
| `targetY` | double | Position Y visée |
| `targetZ` | double | Position Z visée |
| `hand` | InteractionHand | Main utilisée (MAIN_HAND ou OFF_HAND) |

**Réponse serveur**:
- Valide la requête (cooldown, durabilité, portée)
- Si valide: spawn `GrappleHookEntity`
- Si invalide: aucune action

---

### 2. GrappleHitPacket

**Direction**: Serveur → Client
**Trigger**: `GrappleHookEntity` touche un bloc ou une entité

| Champ | Type | Description |
|-------|------|-------------|
| `hookEntityId` | int | ID de l'entité crochet |
| `hitType` | byte | 0=BLOCK, 1=ENTITY |
| `hitX` | double | Position X du point d'impact |
| `hitY` | double | Position Y du point d'impact |
| `hitZ` | double | Position Z du point d'impact |
| `attachedEntityId` | int | ID entité touchée (-1 si bloc) |

**Comportement client**:
- Met à jour l'état visuel du crochet
- Démarre l'animation de traction si BLOCK

---

### 3. GrapplePullPacket

**Direction**: Serveur → Client
**Trigger**: À chaque tick pendant la traction

| Champ | Type | Description |
|-------|------|-------------|
| `playerId` | UUID | ID du joueur tracté |
| `velocityX` | double | Vélocité X à appliquer |
| `velocityY` | double | Vélocité Y à appliquer |
| `velocityZ` | double | Vélocité Z à appliquer |
| `progress` | float | Progression 0.0-1.0 |

**Comportement client**:
- Applique la vélocité au joueur local
- Met à jour les effets visuels

---

### 4. GrappleReleasePacket

**Direction**: Bidirectionnel
**Trigger**:
- Client→Serveur: Joueur relâche le clic ou appuie sur sneak
- Serveur→Client: Traction terminée ou interrompue

| Champ | Type | Description |
|-------|------|-------------|
| `hookEntityId` | int | ID de l'entité crochet |
| `reason` | byte | 0=MANUAL, 1=ARRIVED, 2=BROKEN, 3=OUT_OF_RANGE |

**Comportement**:
- Supprime l'entité crochet
- Nettoie le `GrappleState`
- Restaure le mouvement normal du joueur

---

### 5. GrappleDamagePacket

**Direction**: Serveur → Client
**Trigger**: Grappin touche une entité en mode arme

| Champ | Type | Description |
|-------|------|-------------|
| `targetEntityId` | int | ID de l'entité touchée |
| `damage` | float | Dégâts infligés |
| `hookEntityId` | int | ID de l'entité crochet |

**Comportement client**:
- Affiche les effets visuels de dégâts
- Joue le son d'impact

---

## Diagramme de Séquence

```
┌──────┐                    ┌──────┐                    ┌──────┐
│Client│                    │Server│                    │Target│
└──┬───┘                    └──┬───┘                    └──┬───┘
   │                           │                           │
   │  GrappleLaunchPacket      │                           │
   │──────────────────────────►│                           │
   │                           │                           │
   │                           │ spawn GrappleHookEntity   │
   │                           │───────────────────────────│
   │                           │                           │
   │                           │        collision          │
   │                           │◄──────────────────────────│
   │                           │                           │
   │   GrappleHitPacket        │                           │
   │◄──────────────────────────│                           │
   │                           │                           │
   │   GrapplePullPacket (x N) │                           │
   │◄──────────────────────────│                           │
   │                           │                           │
   │   GrappleReleasePacket    │                           │
   │◄─────────────────────────►│                           │
   │                           │                           │
```

---

## Gestion des Erreurs

| Erreur | Comportement |
|--------|--------------|
| Packet malformé | Log warning, ignorer |
| Joueur non trouvé | Ignorer silencieusement |
| Entité crochet non trouvée | Envoyer GrappleReleasePacket(BROKEN) |
| Désynchronisation | Forcer re-sync position joueur |
