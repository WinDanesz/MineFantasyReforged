#!/usr/bin/env python3
"""
Script to convert hardcoded knowledge entries from MineFantasyKnowledgeList.java to JSON files.
This automates the migration process.
"""

import json
import re
import os
from pathlib import Path

# Read the Java file
java_file = Path("src/main/java/minefantasy/mfr/init/MineFantasyKnowledgeList.java")
knowledge_dir = Path("src/main/resources/assets/minefantasyreforged/knowledge_entries/minefantasyreforged")
artefact_dir = Path("src/main/resources/assets/minefantasyreforged/knowledge_artefacts/minefantasyreforged")

# Create directories
knowledge_dir.mkdir(parents=True, exist_ok=True)
artefact_dir.mkdir(parents=True, exist_ok=True)

# Map of common item/block patterns to their resource locations
ITEM_MAP = {
    'MineFantasyItems.': 'minefantasyreforged:',
    'MineFantasyBlocks.': 'minefantasyreforged:',
    'Items.': 'minecraft:',
    'Blocks.': 'minecraft:',
    'LeatherArmourListMFR.': 'minefantasyreforged:'
}

def convert_item_reference(ref):
    """Convert Java item reference to resource location."""
    ref = ref.strip()
    
    # Handle simple cases
    for java_ref, mod_id in ITEM_MAP.items():
        if ref.startswith(java_ref):
            item_name = ref[len(java_ref):].lower()
            # Convert UPPER_CASE to lower_case
            item_name = re.sub(r'([A-Z])', r'_\1', item_name).lower().strip('_')
            return {
                "type": "block" if "Blocks." in java_ref or "MineFantasyBlocks." in java_ref else "item",
                "id": mod_id + item_name
            }
    
    return {"type": "item", "id": "minecraft:book"}  # fallback

def parse_knowledge_entry(statement):
    """Parse a knowledge entry initialization statement (may span multiple lines)."""
    # Pattern: entry_name = (new InformationBase("name", x, y, artefacts, icon, parent))
    pattern = r'(\w+)\s*=\s*\(new InformationBase\("(\w+)",\s*(-?\d+),\s*(-?\d+),\s*(\d+),\s*([^,]+?),\s*([^)]*?)\)\)'
    match = re.search(pattern, statement, re.MULTILINE | re.DOTALL)
    
    if not match:
        return None
    
    var_name, name, x, y, artefacts, icon_ref, parent = match.groups()
    
    entry = {
        "name": name,
        "x": int(x),
        "y": int(y),
        "artefacts": int(artefacts)
    }
    
    # Parse icon
    icon_ref = icon_ref.strip()
    if icon_ref and icon_ref != "null":
        entry["icon"] = convert_item_reference(icon_ref)
    
    # Parse parent
    parent = parent.strip()
    if parent and parent != "null":
        entry["parent"] = parent
    
    return var_name, entry, statement

def parse_modifiers(statement, entry_data):
    """Parse method calls on the entry (setPage, addSkill, etc.)."""
    # Parse chained methods in the statement
    
    # Page
    m = re.search(r'\.setPage\((\w+)\)', statement)
    if m:
        page_name = m.group(1).lower()
        if page_name in ['artisanry', 'construction', 'engineering', 'provisioning', 'mastery']:
            entry_data["page"] = page_name
    
    # Unlocked
    if re.search(r'\.setUnlocked\(\)', statement):
        entry_data["unlocked"] = True
    
    # Special
    if re.search(r'\.setSpecial\(\)', statement):
        entry_data["special"] = True
    
    # Perk
    if re.search(r'\.setPerk\(\)', statement):
        entry_data["perk"] = True
    
    # Skills
    for m in re.finditer(r'\.addSkill\(Skill\.(\w+),\s*(\d+)\)', statement):
        skill_name = m.group(1).lower()
        skill_level = int(m.group(2))
        if "skills" not in entry_data:
            entry_data["skills"] = []
        entry_data["skills"].append({
            "skill": skill_name,
            "level": skill_level
        })

def main():
    """Main conversion function."""
    if not java_file.exists():
        print(f"Error: {java_file} not found")
        return
    
    with open(java_file, 'r') as f:
        content = f.read()
    
    # Dictionary to store entries temporarily
    entries = {}
    
    # Parse entries - find complete statements across multiple lines
    # Look for patterns like: var_name = (new InformationBase(...))... ;
    statements = re.split(r';\s*\n', content)
    
    for statement in statements:
        # Try to parse as entry initialization
        result = parse_knowledge_entry(statement)
        if result:
            var_name, entry_data, full_statement = result
            
            # Parse method chains in the same statement
            parse_modifiers(full_statement, entry_data)
            
            entries[var_name] = entry_data
    
    # Generate JSON files
    for var_name, entry_data in entries.items():
        name = entry_data["name"]
        json_file = knowledge_dir / f"{name}.json"
        
        with open(json_file, 'w') as f:
            json.dump(entry_data, f, indent=2)
        
        print(f"Created: {json_file}")
    
    print(f"\nGenerated {len(entries)} knowledge entry JSON files")
    print(f"Please review and manually adjust:")
    print(f"  - Item/block references in icon fields")
    print(f"  - Parent references")
    print(f"  - Special cases (ItemStack with metadata, etc.)")

if __name__ == "__main__":
    main()
