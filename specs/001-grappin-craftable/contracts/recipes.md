# Crafting Recipes Contract: Grappin

**Feature Branch**: `001-grappin-craftable`
**Date**: 2025-12-04

## Vue d'ensemble

Les recettes de craft sont définies en JSON et placées dans `data/mcgrappin/recipe/`.

---

## Recette: Grappin en Bois

**Fichier**: `data/mcgrappin/recipe/wooden_grapple.json`

```json
{
  "type": "minecraft:crafting_shaped",
  "category": "equipment",
  "group": "mcgrappin:grapples",
  "pattern": [
    "  H",
    " P ",
    "BI "
  ],
  "key": {
    "H": {
      "item": "minecraft:tripwire_hook"
    },
    "P": {
      "tag": "minecraft:planks"
    },
    "B": {
      "item": "minecraft:iron_block"
    },
    "I": {
      "item": "minecraft:iron_ingot"
    }
  },
  "result": {
    "id": "mcgrappin:wooden_grapple",
    "count": 1
  }
}
```

### Visualisation grille 3x3

```
┌─────┬─────┬─────┐
│     │     │ 🪝  │  Position 3: Tripwire Hook
├─────┼─────┼─────┤
│     │ 🪵  │     │  Position 5: Any Plank
├─────┼─────┼─────┤
│ 🧱  │ 🔩  │     │  Position 7: Iron Block
└─────┴─────┴─────┘  Position 8: Iron Ingot
```

### Ingrédients requis

| Symbole | Item | Quantité | Notes |
|---------|------|----------|-------|
| H | Tripwire Hook | 1 | Item vanilla existant |
| P | Any Plank | 1 | Tag `minecraft:planks` (oak, birch, etc.) |
| B | Iron Block | 1 | 9 lingots de fer |
| I | Iron Ingot | 1 | - |

**Coût total**: 1 tripwire hook + 1 planche + 10 lingots fer

---

## Recette: Grappin en Fer

**Fichier**: `data/mcgrappin/recipe/iron_grapple.json`

```json
{
  "type": "minecraft:crafting_shaped",
  "category": "equipment",
  "group": "mcgrappin:grapples",
  "pattern": [
    "  H",
    " I ",
    "BI "
  ],
  "key": {
    "H": {
      "item": "minecraft:tripwire_hook"
    },
    "I": {
      "item": "minecraft:iron_ingot"
    },
    "B": {
      "item": "minecraft:iron_block"
    }
  },
  "result": {
    "id": "mcgrappin:iron_grapple",
    "count": 1
  }
}
```

### Visualisation grille 3x3

```
┌─────┬─────┬─────┐
│     │     │ 🪝  │  Position 3: Tripwire Hook
├─────┼─────┼─────┤
│     │ 🔩  │     │  Position 5: Iron Ingot
├─────┼─────┼─────┤
│ 🧱  │ 🔩  │     │  Position 7: Iron Block
└─────┴─────┴─────┘  Position 8: Iron Ingot
```

### Ingrédients requis

| Symbole | Item | Quantité | Notes |
|---------|------|----------|-------|
| H | Tripwire Hook | 1 | Item vanilla existant |
| I | Iron Ingot | 2 | Positions 5 et 8 |
| B | Iron Block | 1 | 9 lingots de fer |

**Coût total**: 1 tripwire hook + 11 lingots fer

---

## Comparaison des coûts

| Grappin | Tripwire Hook | Planches | Lingots Fer | Coût équivalent |
|---------|---------------|----------|-------------|-----------------|
| Bois | 1 | 1 | 10 | Moins cher |
| Fer | 1 | 0 | 11 | +1 lingot |

La différence de coût (+1 lingot) est minime mais justifiée par les meilleures stats du Grappin en Fer.

---

## Validation

### Règles de correspondance

- Le pattern doit correspondre **exactement** à la grille
- Les espaces représentent des cases vides obligatoires
- La recette n'est pas mirrorable (shaped = positionnement fixe)

### Tests d'acceptation

1. **Grappin Bois valide**:
   - Placer tous les ingrédients aux bonnes positions → Obtenir 1 Grappin en Bois

2. **Grappin Fer valide**:
   - Placer tous les ingrédients aux bonnes positions → Obtenir 1 Grappin en Fer

3. **Pattern invalide**:
   - Ingrédients aux mauvaises positions → Aucun résultat

4. **Ingrédient manquant**:
   - Omettre un ingrédient → Aucun résultat

5. **Tag planks**:
   - Utiliser n'importe quel type de planche → Grappin Bois crafté

---

## Structure des fichiers

```
data/
└── mcgrappin/
    └── recipe/
        ├── wooden_grapple.json
        └── iron_grapple.json
```
