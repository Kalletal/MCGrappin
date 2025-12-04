# MCGrappin - Mod Harpon pour Minecraft 1.21.1

Un mod NeoForge qui ajoute des harpons/grappins permettant de se déplacer rapidement en s'accrochant aux blocs.

## Fonctionnalités

- **Deux types de harpons** : Bois et Fer avec des statistiques différentes
- **Déplacement rapide** : Lancez le harpon vers un bloc et soyez tracté vers lui
- **Trajectoire directe** : Le harpon vole droit et vite comme un trident
- **Fonctionne sous l'eau** : Utilisable comme un vrai harpon aquatique
- **Mode arme** : Inflige des dégâts aux entités touchées
- **Annulation** : Second clic droit pour annuler la traction

## Statistiques

| Harpon | Durabilité | Dégâts | Portée | Cooldown |
|--------|------------|--------|--------|----------|
| Bois   | 100        | 3.0    | 25 blocs | 1.5s |
| Fer    | 250        | 5.0    | 35 blocs | 1.0s |

## Recettes de Craft

### Harpon en Bois
```
[ ] [ ] [H]
[ ] [P] [ ]
[B] [I] [ ]
```
- H = Crochet (Tripwire Hook)
- P = Planche (n'importe quel bois)
- B = Bloc de Fer
- I = Lingot de Fer

### Harpon en Fer
```
[ ] [ ] [H]
[ ] [I] [ ]
[B] [I] [ ]
```
- H = Crochet (Tripwire Hook)
- I = Lingot de Fer
- B = Bloc de Fer

## Installation

1. Installez [NeoForge](https://neoforged.net/) pour Minecraft 1.21.1
2. Téléchargez le fichier `mcgrappin-1.0.0.jar` depuis les [Releases](https://github.com/Kalletal/MCGrappin/releases)
3. Placez le fichier dans le dossier `mods/` de votre installation Minecraft
4. Lancez Minecraft avec le profil NeoForge

## Utilisation

1. Craftez un harpon (bois ou fer)
2. Tenez le harpon en main
3. **Clic droit** vers un bloc solide pour lancer le harpon
4. Vous serez automatiquement tracté vers le point d'accroche
5. **Clic droit** à nouveau pour annuler la traction

## Compilation

```bash
# Cloner le projet
git clone https://github.com/Kalletal/MCGrappin.git
cd MCGrappin

# Compiler
./gradlew build

# Le JAR sera dans build/libs/
```

## Configuration

Le mod génère un fichier de configuration permettant d'ajuster :
- Durabilité des harpons
- Dégâts infligés
- Portée maximale
- Vitesse de traction
- Temps de recharge

## Licence

Ce projet est sous licence MIT.

## Crédits

Développé avec l'assistance de [Claude Code](https://claude.ai/claude-code)
