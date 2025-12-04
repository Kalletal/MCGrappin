# MCGrappin Constitution

## Core Principles

### I. Gameplay-First
Le grappin doit offrir une expérience de jeu fluide et satisfaisante. La mécanique doit être intuitive, réactive et s'intégrer naturellement dans l'univers Minecraft. Le plaisir du joueur prime sur la complexité technique.

### II. Vanilla-Compatible
Le mod doit respecter l'esthétique et les conventions de Minecraft vanilla. Les textures, sons et animations doivent s'harmoniser avec le style existant. Aucune dépendance externe autre que le mod loader choisi (Forge/Fabric).

### III. Performance
Le grappin ne doit pas impacter négativement les performances du jeu. Les calculs de physique et de collision doivent être optimisés. Pas de lag côté client ni serveur, même en multijoueur.

### IV. Configurabilité
Les paramètres clés doivent être configurables : portée du grappin, vitesse de traction, durabilité, crafting recipe. Fichier de configuration accessible pour les joueurs et administrateurs de serveur.

### V. Multijoueur-Ready
Le mod doit fonctionner correctement en solo et en multijoueur. Synchronisation client-serveur robuste. Pas de désync ni d'exploits possibles.

## Spécifications Techniques

### Mod Loader
- Support Forge
- Version Minecraft cible : 1.21.1
- Java 17+ requis pour les versions modernes

### Fonctionnalités du Grappin
- Lancer vers une surface solide
- Traction du joueur vers le point d'ancrage
- Physique de balancement réaliste
- Durabilité et réparation
- Recipe de craft équilibré

### Structure du Code
- Séparation claire client/serveur
- Packets réseau pour la synchronisation
- Modèle de données propre pour l'item

## Qualité

### Tests
- Tests unitaires pour la logique métier
- Tests en jeu manuels pour le gameplay
- Validation multijoueur avant release

### Documentation
- README avec instructions d'installation
- Guide d'utilisation pour les joueurs
- Changelog maintenu à jour

## Governance

Cette constitution guide toutes les décisions de développement. Les modifications doivent être justifiées par une amélioration du gameplay ou de la qualité technique.

**Version**: 1.0.0 | **Ratified**: 2025-12-01 | **Last Amended**: 2025-12-01
