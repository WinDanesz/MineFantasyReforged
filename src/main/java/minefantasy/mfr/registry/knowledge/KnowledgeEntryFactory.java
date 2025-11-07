package minefantasy.mfr.registry.knowledge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.init.MineFantasyBlocks;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.mechanics.knowledge.InformationBase;
import minefantasy.mfr.mechanics.knowledge.InformationList;
import minefantasy.mfr.mechanics.knowledge.InformationPage;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Factory for creating InformationBase instances from JSON data.
 */
public class KnowledgeEntryFactory {

	public InformationBase parse(JsonContext context, JsonObject json) {
		String name = JsonUtils.getString(json, "name");
		int x = JsonUtils.getInt(json, "x");
		int y = JsonUtils.getInt(json, "y");
		int artefacts = JsonUtils.getInt(json, "artefacts", 0);
		
		// Parse icon
		ItemStack icon = parseIcon(json);
		
		// Parse parent (optional)
		InformationBase parent = null;
		if (json.has("parent")) {
			String parentName = JsonUtils.getString(json, "parent");
			parent = InformationList.nameMap.get(parentName);
			if (parent == null) {
				// Parent might not be loaded yet - will be null
				// Could add delayed resolution here if needed
			}
		}
		
		// Create the entry
		InformationBase entry = new InformationBase(name, x, y, artefacts, icon, parent);
		
		// Parse page (optional)
		if (json.has("page")) {
			String pageName = JsonUtils.getString(json, "page");
			InformationPage page = getPageByName(pageName);
			if (page != null) {
				entry.setPage(page);
			}
		}
		
		// Parse unlocked status
		if (JsonUtils.getBoolean(json, "unlocked", false)) {
			entry.setUnlocked();
		}
		
		// Parse special status
		if (JsonUtils.getBoolean(json, "special", false)) {
			entry.setSpecial();
		}
		
		// Parse perk status
		if (JsonUtils.getBoolean(json, "perk", false)) {
			entry.setPerk();
		}
		
		// Parse skill requirements
		if (json.has("skills")) {
			JsonArray skills = JsonUtils.getJsonArray(json, "skills");
			for (JsonElement skillElement : skills) {
				JsonObject skillObj = skillElement.getAsJsonObject();
				String skillName = JsonUtils.getString(skillObj, "skill");
				int level = JsonUtils.getInt(skillObj, "level");
				
				Skill skill = Skill.valueOf(skillName.toUpperCase());
				entry.addSkill(skill, level);
			}
		}
		
		// Parse description values (optional)
		if (json.has("descriptValues")) {
			JsonArray values = JsonUtils.getJsonArray(json, "descriptValues");
			Object[] descriptValues = new Object[values.size()];
			for (int i = 0; i < values.size(); i++) {
				descriptValues[i] = values.get(i).getAsString();
			}
			entry.setDescriptValues(descriptValues);
		}
		
		return entry;
	}
	
	private ItemStack parseIcon(JsonObject json) {
		if (!json.has("icon")) {
			return new ItemStack(Items.BOOK);
		}
		
		JsonObject iconObj = JsonUtils.getJsonObject(json, "icon");
		String type = JsonUtils.getString(iconObj, "type", "item");
		String id = JsonUtils.getString(iconObj, "id");
		int meta = JsonUtils.getInt(iconObj, "meta", 0);
		
		if ("block".equals(type)) {
			Block block = ForgeRegistries.BLOCKS.getValue(new net.minecraft.util.ResourceLocation(id));
			if (block != null && block != Blocks.AIR) {
				return new ItemStack(block, 1, meta);
			}
		} else {
			Item item = ForgeRegistries.ITEMS.getValue(new net.minecraft.util.ResourceLocation(id));
			if (item != null && item != Items.AIR) {
				return new ItemStack(item, 1, meta);
			}
		}
		
		return new ItemStack(Items.BOOK);
	}
	
	private InformationPage getPageByName(String name) {
		switch (name.toLowerCase()) {
			case "artisanry":
				return InformationList.artisanry;
			case "construction":
				return InformationList.construction;
			case "engineering":
				return InformationList.engineering;
			case "provisioning":
				return InformationList.provisioning;
			case "mastery":
				return InformationList.mastery;
			default:
				return null;
		}
	}
}
