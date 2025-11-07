#!/usr/bin/env python3
"""
Script to convert artefact mappings from MineFantasyKnowledgeList.ArtefactListMFR to JSON files.
"""

import json
import re
from pathlib import Path

# Read the Java file
java_file = Path("src/main/java/minefantasy/mfr/init/MineFantasyKnowledgeList.java")
artefact_dir = Path("src/main/resources/assets/minefantasyreforged/knowledge_artefacts/minefantasyreforged")

# Create directory
artefact_dir.mkdir(parents=True, exist_ok=True)

# Map of common item/block patterns to their resource locations
ITEM_MAP = {
    'MineFantasyItems.': ('item', 'minefantasyreforged:'),
    'MineFantasyBlocks.': ('block', 'minefantasyreforged:'),
    'Items.': ('item', 'minecraft:'),
    'Blocks.': ('block', 'minecraft:'),
}

def convert_item_reference(ref):
    """Convert Java item reference to resource location."""
    ref = ref.strip()
    
    # Handle simple cases
    for java_ref, (item_type, mod_id) in ITEM_MAP.items():
        if ref.startswith(java_ref):
            item_name = ref[len(java_ref):].lower()
            # Convert UPPER_CASE to lower_case
            item_name = re.sub(r'([A-Z])', r'_\1', item_name).lower().strip('_')
            return {
                "type": item_type,
                "id": mod_id + item_name
            }
    
    # Handle ItemStack
    if ref.startswith('new ItemStack('):
        # Extract item and metadata
        match = re.search(r'new ItemStack\(([^,]+)(?:,\s*\d+)?,\s*(\d+)\)', ref)
        if match:
            item_ref, meta = match.groups()
            result = convert_item_reference(item_ref)
            if result and meta and int(meta) > 0:
                result["meta"] = int(meta)
                result["type"] = "itemstack"
            return result
    
    return None

def parse_artefacts():
    """Parse the artefact add() calls from the Java file."""
    if not java_file.exists():
        print(f"Error: {java_file} not found")
        return {}
    
    with open(java_file, 'r') as f:
        content = f.read()
    
    # Dictionary to store artefacts for each knowledge entry
    artefacts_map = {}
    
    # Find all add() calls
    # Pattern: add(knowledge_entry_var, artifact1, artifact2, ...)
    pattern = r'add\((\w+),\s*([^)]+)\)'
    
    matches = re.finditer(pattern, content)
    
    for match in matches:
        entry_var = match.group(1)
        artefacts_str = match.group(2)
        
        # Split artefacts by comma, but be careful with nested structures
        artefacts_raw = re.split(r',\s*(?![^(]*\))', artefacts_str)
        
        artefacts = []
        for artefact_ref in artefacts_raw:
            artefact_ref = artefact_ref.strip()
            if not artefact_ref:
                continue
                
            # Skip OreDictionary.getOres() calls for now
            if 'OreDictionary.getOres' in artefact_ref:
                # Extract ore dict name
                ore_match = re.search(r'OreDictionary\.getOres\("([^"]+)"\)', artefact_ref)
                if ore_match:
                    ore_name = ore_match.group(1)
                    # Create a placeholder - these need manual review
                    artefacts.append({
                        "type": "oredict",
                        "id": ore_name,
                        "_comment": "OreDictionary entry - please review"
                    })
                continue
            
            converted = convert_item_reference(artefact_ref)
            if converted:
                artefacts.append(converted)
        
        if artefacts:
            if entry_var not in artefacts_map:
                artefacts_map[entry_var] = []
            artefacts_map[entry_var].extend(artefacts)
    
    return artefacts_map

def main():
    """Main conversion function."""
    artefacts_map = parse_artefacts()
    
    # Generate JSON files
    for entry_var, artefacts in artefacts_map.items():
        json_file = artefact_dir / f"{entry_var}.json"
        
        data = {
            "knowledge_entry": entry_var,
            "artefacts": artefacts
        }
        
        with open(json_file, 'w') as f:
            json.dump(data, f, indent=2)
        
        print(f"Created: {json_file}")
    
    print(f"\nGenerated {len(artefacts_map)} artefact mapping files")
    print(f"Please review and manually adjust:")
    print(f"  - OreDictionary references marked with '_comment'")
    print(f"  - Item/block references")
    print(f"  - ItemStack metadata values")

if __name__ == "__main__":
    main()
