# Quickstart Guide: MCGrappin Development

**Feature Branch**: `001-grappin-craftable`
**Date**: 2025-12-04

## Prérequis

- **Java 17+** (JDK, pas JRE)
- **IDE**: IntelliJ IDEA recommandé (ou Eclipse avec Buildship)
- **Git**
- **Minecraft 1.21.1** pour les tests

## Setup Initial

### 1. Cloner et configurer

```bash
# Cloner le repository
git clone <repo-url>
cd MCGrappin

# Checkout la branche feature
git checkout 001-grappin-craftable
```

### 2. Générer le workspace IDE

```bash
# Pour IntelliJ IDEA
./gradlew genIntellijRuns

# Pour Eclipse
./gradlew genEclipseRuns
```

### 3. Importer dans l'IDE

- **IntelliJ**: Open → Sélectionner le dossier → Import as Gradle project
- **Eclipse**: Import → Existing Gradle Project

## Structure du Projet

```
MCGrappin/
├── src/main/java/com/mcgrappin/
│   ├── MCGrappin.java           # Classe principale du mod
│   ├── client/                  # Code côté client uniquement
│   │   ├── ClientSetup.java
│   │   └── renderer/
│   ├── common/                  # Code partagé
│   │   └── CommonSetup.java
│   ├── items/                   # Définition des items
│   │   └── GrappleItem.java
│   ├── entities/                # Entités (projectile crochet)
│   │   └── GrappleHookEntity.java
│   ├── network/                 # Packets réseau
│   │   └── GrappleNetwork.java
│   └── config/                  # Configuration
│       └── GrappleConfig.java
│
├── src/main/resources/
│   ├── META-INF/
│   │   └── mods.toml            # Métadonnées du mod
│   ├── assets/mcgrappin/
│   │   ├── lang/                # Traductions
│   │   ├── models/item/         # Modèles JSON des items
│   │   └── textures/item/       # Textures des items
│   └── data/mcgrappin/
│       └── recipe/              # Recettes de craft
│
├── build.gradle                 # Configuration build
└── gradle.properties            # Propriétés (version MC, Forge, etc.)
```

## Commandes Gradle Essentielles

```bash
# Lancer Minecraft client avec le mod
./gradlew runClient

# Lancer un serveur dédié avec le mod
./gradlew runServer

# Compiler le mod (sans lancer)
./gradlew build

# Nettoyer les fichiers générés
./gradlew clean

# Générer les data (recettes, etc.)
./gradlew runData
```

## Workflow de Développement

### Cycle recommandé

1. **Modifier le code** dans `src/main/java/`
2. **Lancer** `./gradlew runClient`
3. **Tester** en jeu
4. **Itérer**

### Rechargement à chaud

En développement, certains changements peuvent être rechargés sans redémarrer:
- Modifications de ressources (textures, JSON)
- Certaines modifications de code (avec DCEVM ou JRebel)

Pour la plupart des changements de code Java, un redémarrage de `runClient` est nécessaire.

## Premier Test

### Obtenir les items en jeu

1. Lancer `./gradlew runClient`
2. Créer un monde en mode Créatif
3. Ouvrir l'inventaire créatif
4. Chercher "grapple" ou "grappin"
5. Les deux variantes (bois et fer) doivent apparaître

### Tester le craft

1. Créer un monde en mode Survie
2. Obtenir les ressources:
   - 1 Tripwire Hook (craft: fer + stick + planche)
   - 10-11 Iron Ingots
   - 1 Planche (pour version bois)
3. Ouvrir une table de craft
4. Placer selon le pattern documenté
5. Récupérer le Grappin

## Debugging

### Logs

Les logs sont affichés dans la console IDE. Pour plus de détails:

```java
// Dans le code
LOGGER.debug("Message de debug");
LOGGER.info("Message d'info");
LOGGER.error("Message d'erreur", exception);
```

### Breakpoints

1. Placer un breakpoint dans l'IDE
2. Lancer en mode Debug: `./gradlew runClient --debug-jvm`
3. Attacher le debugger de l'IDE (port 5005)

## Ressources Utiles

- [NeoForge Documentation](https://docs.neoforged.net/)
- [Minecraft Wiki - Data Packs](https://minecraft.wiki/w/Data_pack)
- [Forge Community Wiki](https://forge.gemwire.uk/wiki/)
- [Grappling Hook Mod Source](https://github.com/yyon/grapplemod) (référence)

## Checklist Premier Lancement

- [ ] Java 17+ installé (`java -version`)
- [ ] Gradle wrapper exécutable (`chmod +x gradlew` sur Linux/Mac)
- [ ] `./gradlew genIntellijRuns` exécuté sans erreur
- [ ] Projet importé dans l'IDE
- [ ] `./gradlew runClient` lance Minecraft avec le mod
- [ ] Items visibles dans l'inventaire créatif
