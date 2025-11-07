# Knowledge Entries - JSON Data-Driven System

This directory contains JSON files that define knowledge entries (research) for the MineFantasy Reforged knowledge/research system.

## Overview

Knowledge entries are now data-driven using JSON files, allowing mod pack creators and addon developers to easily add, modify, or remove knowledge entries without modifying Java code.

## Directory Structure

```
knowledge_entries/
└── minefantasyreforged/
    ├── carpenter.json
    ├── anvil.json
    ├── bloomery.json
    └── ...

knowledge_artefacts/
└── minefantasyreforged/
    ├── smelt_bronze.json
    ├── blackpowder.json
    └── ...
```

## Knowledge Entry JSON Schema

Each knowledge entry is defined in a separate JSON file with the following structure:

```json
{
  "name": "entry_id",
  "x": 0,
  "y": 0,
  "artefacts": 0,
  "icon": {
    "type": "item",
    "id": "modid:item_name",
    "meta": 0
  },
  "parent": "parent_entry_id",
  "page": "artisanry",
  "unlocked": true,
  "special": true,
  "perk": true,
  "skills": [
    {
      "skill": "artisanry",
      "level": 10
    }
  ],
  "descriptValues": ["value1", "value2"]
}
```

### Field Descriptions

- **name** (required): Unique identifier for the knowledge entry
- **x** (required): X position on the knowledge tree grid
- **y** (required): Y position on the knowledge tree grid
- **artefacts** (required): Number of artefacts required to unlock (0 for auto-unlock entries)
- **icon** (required): Display icon for the entry
  - **type**: "item" or "block"
  - **id**: Resource location (e.g., "minecraft:iron_ingot" or "minefantasyreforged:steel_ingot")
  - **meta** (optional): Item metadata/damage value
- **parent** (optional): ID of the parent entry (creates dependency line in knowledge tree)
- **page** (optional): Knowledge page category. Valid values:
  - "artisanry"
  - "construction"
  - "engineering"
  - "provisioning"
  - "mastery"
- **unlocked** (optional): If true, entry starts unlocked (default: false)
- **special** (optional): If true, displays with special frame (default: false)
- **perk** (optional): If true, marks as a perk entry (default: false)
- **skills** (optional): Array of skill requirements
  - **skill**: Skill name (artisanry, construction, engineering, provisioning, combat)
  - **level**: Required skill level
- **descriptValues** (optional): Array of values to interpolate into the localized description text

## Artefact Mapping JSON Schema

Artefact mappings define which items/blocks provide clues for unlocking knowledge entries:

```json
{
  "knowledge_entry": "entry_id",
  "artefacts": [
    {
      "type": "item",
      "id": "modid:item_name",
      "meta": 0
    },
    {
      "type": "block",
      "id": "modid:block_name"
    }
  ]
}
```

### Field Descriptions

- **knowledge_entry** (required): ID of the knowledge entry these artefacts unlock
- **artefacts** (required): Array of artefact items/blocks
  - **type**: "item", "block", or "itemstack"
  - **id**: Resource location
  - **meta** (optional): Item metadata/damage value

## Adding New Knowledge Entries

### Method 1: Create JSON Files Manually

1. Create a new JSON file in the appropriate directory:
   - For mod entries: `src/main/resources/assets/yourmod/knowledge_entries/yourmod/entry_name.json`
   - For data pack entries: `data/yourmod/knowledge_entries/yourmod/entry_name.json`

2. Define the entry following the schema above

3. Optionally create an artefact mapping file:
   - `src/main/resources/assets/yourmod/knowledge_artefacts/yourmod/entry_name.json`

4. Add localization entries in your language files:
   ```
   knowledge.entry_name=Entry Display Name
   knowledge.entry_name.desc=Entry description text
   ```

### Method 2: Use Custom Config Directory

Place custom JSON files in:
```
config/minefantasyreforged/custom/knowledge_entries/minefantasyreforged/
config/minefantasyreforged/custom/knowledge_artefacts/minefantasyreforged/
```

These will be loaded after mod-provided entries and can override them.

## Examples

### Simple Unlocked Entry
```json
{
  "name": "getting_started",
  "x": 0,
  "y": 0,
  "artefacts": 0,
  "icon": {
    "type": "item",
    "id": "minecraft:book"
  },
  "unlocked": true
}
```

### Entry with Parent and Page
```json
{
  "name": "anvil",
  "x": -1,
  "y": 0,
  "artefacts": 0,
  "icon": {
    "type": "block",
    "id": "minefantasyreforged:anvil_iron"
  },
  "parent": "forge",
  "page": "artisanry",
  "unlocked": true,
  "special": true
}
```

### Entry with Skill Requirements
```json
{
  "name": "smelt_steel",
  "x": 4,
  "y": 5,
  "artefacts": 1,
  "icon": {
    "type": "item",
    "id": "minefantasyreforged:steel_ingot"
  },
  "parent": "smelt_pig_iron",
  "page": "artisanry",
  "skills": [
    {
      "skill": "artisanry",
      "level": 25
    }
  ]
}
```

### Perk Entry
```json
{
  "name": "toughness",
  "x": -1,
  "y": 0,
  "artefacts": 0,
  "icon": {
    "type": "item",
    "id": "minefantasyreforged:standard_plate_helmet"
  },
  "page": "mastery",
  "perk": true,
  "skills": [
    {
      "skill": "combat",
      "level": 10
    }
  ]
}
```

## Migration from Hardcoded Java

The repository includes helper scripts for converting hardcoded Java entries:

- `generate_knowledge_jsons.py`: Generates knowledge entry JSON files
- `generate_artefact_jsons.py`: Generates artefact mapping JSON files

These were used to migrate the original 148 knowledge entries and 74 artefact mappings from Java to JSON.

## Important Notes

1. **Parent Dependencies**: Entries with parents must be loaded AFTER their parent entries. Since files are loaded in alphabetical order, you can ensure proper ordering by:
   - Naming parent files alphabetically before child files (e.g., `a_parent.json`, `b_child.json`)
   - Using numeric prefixes (e.g., `01_parent.json`, `02_child.json`)
   - If a parent is not found, the entry will have null parent and a warning will be logged

2. **Item/Block References**: Ensure all referenced items and blocks are registered before knowledge entries are loaded (during postInit phase).

3. **Localization**: JSON files only define the data structure. Display names and descriptions still come from language files.

4. **Compatibility**: The system maintains backward compatibility with existing save data.

## Troubleshooting

If a knowledge entry doesn't load:

1. Check the logs for parsing errors
2. Verify JSON syntax is valid
3. Ensure item/block IDs are correct and registered
4. Verify parent entry exists and loads before the child (alphabetically earlier filename)
5. Check skill names are valid (artisanry, construction, engineering, provisioning, combat)

### Parent Not Found Warning

If you see warnings like "Parent 'xyz' not found for entry 'abc'":
- The parent entry hasn't been loaded yet
- Rename the child file to load alphabetically after the parent
- Example: If parent is `bloomery.json` and child is `advanced_bloomery.json`, this will work (advanced < bloomery alphabetically is wrong!)
- Better: Rename to `bloomery.json` and `bloomery_advanced.json` or use prefixes like `01_bloomery.json` and `02_bloomery_advanced.json`
