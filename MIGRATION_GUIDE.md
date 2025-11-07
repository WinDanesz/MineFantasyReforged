# Knowledge Entries Migration Guide

## Overview

This document explains the migration from hardcoded Java knowledge entries to data-driven JSON files.

## What Changed

### Before (Java Code)
```java
public static InformationBase bloomery;

public static void init() {
    bloomery = (new InformationBase("bloomery", 4, -2, 0, MineFantasyBlocks.BLOOMERY, crucible))
        .registerStat()
        .setPage(artisanry)
        .setUnlocked()
        .setSpecial();
}
```

### After (JSON Data)
**knowledge_entries/minefantasyreforged/bloomery.json**
```json
{
  "name": "bloomery",
  "x": 4,
  "y": -2,
  "artefacts": 0,
  "icon": {
    "type": "block",
    "id": "minefantasyreforged:bloomery"
  },
  "parent": "crucible",
  "page": "artisanry",
  "unlocked": true,
  "special": true
}
```

## Benefits

1. **No Code Required**: Add/modify entries without Java knowledge
2. **Hot-Reloadable**: Change entries without recompiling (in theory, with right mods)
3. **Data Pack Compatible**: Can be distributed as data packs
4. **Modpack Friendly**: Easy customization for modpack creators
5. **Maintainable**: Easier to review and update
6. **Consistent**: Same pattern as recipes

## Migration Process

### Automated Migration

The migration was done automatically using Python scripts:

1. **generate_knowledge_jsons.py**: Converted 148 Java entries to JSON
2. **generate_artefact_jsons.py**: Converted 74 artefact mappings to JSON

To re-run (if needed):
```bash
python3 generate_knowledge_jsons.py
python3 generate_artefact_jsons.py
```

### Manual Review Required

Some entries may need manual adjustment:

#### 1. Complex Icon References
Entries using method calls or special constructors:
```java
// Before
LeatherArmourListMFR.armour(LeatherArmourListMFR.LEATHER, 0, 1)

// After - needs manual adjustment
{
  "icon": {
    "type": "item",
    "id": "minefantasyreforged:leather_helmet"
  }
}
```

#### 2. ItemStack with Metadata
```java
// Before
new ItemStack(Items.DYE, 1, 3)

// After
{
  "icon": {
    "type": "itemstack",
    "id": "minecraft:dye",
    "meta": 3
  }
}
```

#### 3. OreDictionary Artefacts
```java
// Before
for (ItemStack copper : OreDictionary.getOres("ingotCopper")) {
    for (ItemStack tin : OreDictionary.getOres("ingotTin")) {
        add(smelt_bronze, copper, tin);
    }
}

// After - Currently not supported, needs feature addition
// Workaround: List specific items
{
  "knowledge_entry": "smelt_bronze",
  "artefacts": [
    {"type": "item", "id": "minefantasyreforged:copper_ingot"},
    {"type": "item", "id": "minefantasyreforged:tin_ingot"}
  ]
}
```

## Backward Compatibility

### Save Data
- No changes to save data format
- Existing player knowledge is preserved
- Entry IDs remain the same

### Code References
Static fields in `MineFantasyKnowledgeList` are preserved but may be null if not loaded from JSON. Access entries via:
```java
InformationBase entry = InformationList.nameMap.get("entry_name");
```

## Testing Checklist

After migration, verify:

- [ ] All 148 knowledge entries load without errors
- [ ] All 74 artefact mappings load correctly
- [ ] Knowledge tree displays correctly in game
- [ ] Parent dependencies work properly
- [ ] Skill requirements function correctly
- [ ] Artefacts unlock entries as expected
- [ ] Localization displays correctly
- [ ] Save/load preserves player knowledge
- [ ] Custom entries can be added
- [ ] Config overrides work

## Common Issues

### Issue: Parent Not Found
**Symptom**: Warning in logs about missing parent entry
**Cause**: Parent entry loaded after child
**Solution**: Ensure parent JSON files are named alphabetically before children, or load in separate passes

### Issue: Icon Not Displaying
**Symptom**: Entry shows with book icon
**Cause**: Invalid item/block ID
**Solution**: Check item/block is registered and ID is correct

### Issue: Entry Not Loading
**Symptom**: Entry missing from knowledge tree
**Cause**: JSON parse error or invalid data
**Solution**: Check logs for errors, validate JSON syntax

## Future Enhancements

Potential improvements to the system:

1. **OreDictionary Support**: Add "oredict" type for artefacts
2. **Conditional Loading**: Support for conditions (mod loaded, config value, etc.)
3. **Multi-Pass Loading**: Automatic dependency resolution for parents
4. **Validation**: JSON schema validation on load
5. **Hot Reload**: Integration with resource reload for development

## Rollback Plan

If issues are found, rollback is simple:

1. Comment out JSON loaders in `MineFantasyReforged.postInit()`:
```java
// knowledgeLoader.loadKnowledgeEntries();
// artefactLoader.loadArtefacts();
```

2. Uncomment old init calls:
```java
MineFantasyKnowledgeList.init();
MineFantasyKnowledgeList.ArtefactListMFR.init();
```

3. The old Java code is preserved and functional

## Resources

- **README.md**: Schema documentation and examples
- **EXAMPLE_*.json**: Template files for new entries
- **generate_*.py**: Migration scripts
- **Original Java**: `MineFantasyKnowledgeList.java` (preserved)

## Questions?

For questions or issues:
1. Check logs for error messages
2. Review README.md for schema details
3. Examine existing JSON files for examples
4. Consult this migration guide
