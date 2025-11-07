#!/bin/bash
# Script to help generate JSON files from MineFantasyKnowledgeList.java
# This is a helper to migrate the hardcoded knowledge entries to JSON format

# Note: This script extracts the structure and will need manual adjustments
# for item/block references and special cases

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
KNOWLEDGE_DIR="$SCRIPT_DIR/src/main/resources/assets/minefantasyreforged/knowledge_entries/minefantasyreforged"
ARTEFACT_DIR="$SCRIPT_DIR/src/main/resources/assets/minefantasyreforged/knowledge_artefacts/minefantasyreforged"

mkdir -p "$KNOWLEDGE_DIR"
mkdir -p "$ARTEFACT_DIR"

echo "Knowledge entry directories created at:"
echo "  - $KNOWLEDGE_DIR"
echo "  - $ARTEFACT_DIR"
echo ""
echo "Please manually create JSON files based on MineFantasyKnowledgeList.java"
echo "Use the examples in the directories as templates."
echo ""
echo "JSON Schema for knowledge_entries:"
echo '{
  "name": "entry_id",
  "x": 0,
  "y": 0,
  "artefacts": 0,
  "icon": {
    "type": "item",  // or "block"
    "id": "modid:item_name",
    "meta": 0  // optional
  },
  "parent": "parent_entry_id",  // optional
  "page": "artisanry",  // optional: artisanry, construction, engineering, provisioning, mastery
  "unlocked": true,  // optional
  "special": true,  // optional
  "perk": true,  // optional
  "skills": [  // optional
    {
      "skill": "artisanry",
      "level": 10
    }
  ],
  "descriptValues": ["value1", "value2"]  // optional
}'
echo ""
echo "JSON Schema for knowledge_artefacts:"
echo '{
  "knowledge_entry": "entry_id",
  "artefacts": [
    {
      "type": "item",  // or "block" or "itemstack"
      "id": "modid:item_name",
      "meta": 0  // optional
    }
  ]
}'

