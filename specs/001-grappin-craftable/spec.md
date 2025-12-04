# Feature Specification: Grappin Craftable Multi-Matériaux

**Feature Branch**: `001-grappin-craftable`
**Created**: 2025-12-04
**Status**: Draft
**Input**: User description: "Le Grappin doit avoir les mêmes fonctionnalités qu'un grappin classique et le joueur doit pouvoir s'en servir d'arme à distance également. Dans le jeu, il doit être craftable dans divers matériaux comme le bois et le fer."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Crafter un Grappin en Bois (Priority: P1)

En tant que joueur, je veux pouvoir crafter un Grappin en bois sur la table de craft afin d'obtenir un outil de mobilité accessible en début de partie.

**Why this priority**: Le Grappin en bois est la version la plus accessible, utilisant des ressources de début de jeu. C'est l'entrée de base pour découvrir la mécanique du grappin.

**Independent Test**: Peut être testé en plaçant les ingrédients corrects sur une table de craft et en vérifiant que le Grappin en bois apparaît comme résultat.

**Acceptance Scenarios**:

1. **Given** le joueur est devant une table de craft avec les ressources nécessaires (1 crochet en fer, 1 planche de bois, 1 bloc de fer, 1 lingot de fer), **When** il place les ingrédients selon le pattern défini (crochet en fer en haut à droite, planche de bois au milieu, bloc de fer en bas à gauche, lingot de fer en bas au milieu), **Then** un Grappin en bois apparaît dans la case de résultat.

2. **Given** le joueur a crafté un Grappin en bois, **When** il le récupère dans son inventaire, **Then** l'item s'affiche avec son nom "Grappin en Bois" et son icône distincte.

---

### User Story 2 - Crafter un Grappin en Fer (Priority: P1)

En tant que joueur, je veux pouvoir crafter un Grappin en fer sur la table de craft afin d'obtenir un outil de mobilité plus résistant.

**Why this priority**: Le Grappin en fer est la version améliorée, nécessitant plus de ressources mais offrant de meilleures caractéristiques. Priorité égale car les deux versions sont essentielles au gameplay.

**Independent Test**: Peut être testé en plaçant les ingrédients corrects sur une table de craft et en vérifiant que le Grappin en fer apparaît comme résultat.

**Acceptance Scenarios**:

1. **Given** le joueur est devant une table de craft avec les ressources nécessaires (1 crochet en fer, 2 lingots de fer, 1 bloc de fer), **When** il place les ingrédients selon le pattern défini (crochet en fer en haut à droite, lingot de fer au milieu, bloc de fer en bas à gauche, lingot de fer en bas au milieu), **Then** un Grappin en fer apparaît dans la case de résultat.

2. **Given** le joueur a crafté un Grappin en fer, **When** il le récupère dans son inventaire, **Then** l'item s'affiche avec son nom "Grappin en Fer" et son icône distincte.

---

### User Story 3 - Utiliser le Grappin pour se déplacer (Priority: P1)

En tant que joueur, je veux pouvoir utiliser le Grappin pour m'accrocher à des surfaces et me tracter vers elles afin de me déplacer rapidement dans l'environnement.

**Why this priority**: La fonctionnalité de mobilité est le coeur du grappin. Sans elle, l'item n'a pas de raison d'être.

**Independent Test**: Peut être testé en visant un bloc solide, en lançant le grappin, et en vérifiant que le joueur est tracté vers le point d'accroche.

**Acceptance Scenarios**:

1. **Given** le joueur tient un Grappin (bois ou fer) dans sa main, **When** il fait un clic droit en visant un bloc solide à portée, **Then** le crochet du grappin se lance vers le bloc ciblé.

2. **Given** le crochet du grappin est accroché à un bloc, **When** le joueur est tracté, **Then** le joueur se déplace vers le point d'accroche de manière fluide.

3. **Given** le joueur est tracté par le grappin, **When** il atteint le point d'accroche ou relâche le grappin, **Then** le mouvement de traction s'arrête et le joueur retrouve le contrôle normal de ses mouvements.

---

### User Story 4 - Utiliser le Grappin comme arme à distance (Priority: P2)

En tant que joueur, je veux pouvoir utiliser le Grappin pour attaquer des entités à distance afin de disposer d'une option de combat supplémentaire.

**Why this priority**: La fonctionnalité d'arme est secondaire par rapport à la mobilité, mais ajoute de la polyvalence à l'outil.

**Independent Test**: Peut être testé en visant une entité (mob ou joueur), en lançant le grappin, et en vérifiant que des dégâts sont infligés.

**Acceptance Scenarios**:

1. **Given** le joueur tient un Grappin dans sa main, **When** il fait un clic droit en visant une entité hostile à portée, **Then** le crochet se lance et inflige des dégâts à l'entité touchée.

2. **Given** le grappin touche une entité, **When** les dégâts sont calculés, **Then** les dégâts correspondent au type de grappin utilisé (bois inflige moins de dégâts que fer).

---

### Edge Cases

- Que se passe-t-il si le joueur vise un bloc hors de portée du grappin ?
- Que se passe-t-il si le bloc ciblé est détruit pendant la traction ?
- Que se passe-t-il si le joueur est en mode accroupi (sneak) pendant l'utilisation ?
- Que se passe-t-il si le grappin manque sa cible (aucun bloc ou entité touchée) ?
- Que se passe-t-il si le joueur utilise le grappin sous l'eau ?
- Comment le grappin interagit-il avec les blocs non-solides (eau, lave, feuillage) ?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Le système DOIT permettre de crafter un Grappin en Bois avec le pattern suivant : crochet en fer (position 3 - haut droite), planche de bois (position 5 - milieu), bloc de fer (position 7 - bas gauche), lingot de fer (position 8 - bas milieu).

- **FR-002**: Le système DOIT permettre de crafter un Grappin en Fer avec le pattern suivant : crochet en fer (position 3 - haut droite), lingot de fer (position 5 - milieu), bloc de fer (position 7 - bas gauche), lingot de fer (position 8 - bas milieu).

- **FR-003**: Les deux types de Grappin DOIVENT être craftables uniquement sur une table de craft (grille 3x3).

- **FR-004**: Le Grappin DOIT permettre au joueur de lancer un crochet vers un bloc solide ciblé.

- **FR-005**: Le Grappin DOIT tracter le joueur vers le point d'accroche lorsque le crochet est accroché.

- **FR-006**: Le Grappin DOIT pouvoir être utilisé comme arme à distance, infligeant des dégâts aux entités touchées.

- **FR-007**: Le Grappin en Fer DOIT avoir de meilleures caractéristiques que le Grappin en Bois (durabilité, dégâts, ou portée supérieurs).

- **FR-008**: Le Grappin DOIT avoir une durabilité qui diminue à chaque utilisation.

- **FR-009**: Le système DOIT afficher le Grappin avec un nom et une icône distincts selon le matériau (bois ou fer).

### Key Entities

- **Grappin en Bois**: Item craftable représentant la version basique du grappin. Attributs : nom, icône, durabilité, dégâts, portée.

- **Grappin en Fer**: Item craftable représentant la version améliorée du grappin. Attributs : nom, icône, durabilité, dégâts, portée (supérieurs au bois).

- **Crochet projeté**: Entité temporaire représentant le crochet du grappin en vol. Attributs : position, vélocité, propriétaire, état (en vol, accroché, rétracté).

- **Recette de craft**: Définition du pattern de craft pour chaque type de grappin. Attributs : pattern 3x3, ingrédients, résultat.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Les joueurs peuvent crafter les deux types de Grappin (bois et fer) avec un taux de réussite de 100% lorsque le pattern correct est utilisé.

- **SC-002**: Le temps entre le lancer du grappin et l'accroche au bloc ne dépasse pas 1 seconde pour une distance de 20 blocs.

- **SC-003**: Les joueurs peuvent utiliser le grappin pour atteindre des hauteurs inaccessibles autrement (test : atteindre un bloc à 15 blocs de hauteur en moins de 5 secondes).

- **SC-004**: Le Grappin en Fer offre au moins 50% de durabilité supplémentaire par rapport au Grappin en Bois.

- **SC-005**: Les joueurs peuvent toucher une cible mobile (mob) avec le grappin en mode arme avec un taux de réussite supérieur à 70% à une distance de 10 blocs.

## Assumptions

- Le crochet en fer (Tripwire Hook) existe déjà dans Minecraft vanilla et sera utilisé comme ingrédient.
- La portée par défaut du grappin est assumée être d'environ 20-30 blocs (standard pour ce type de mod).
- Le grappin ne peut s'accrocher qu'aux blocs solides (pas aux liquides ni aux blocs traversables).
- La traction du joueur suit une trajectoire directe vers le point d'accroche.
- Le grappin a un temps de recharge (cooldown) entre deux utilisations pour équilibrer le gameplay.
