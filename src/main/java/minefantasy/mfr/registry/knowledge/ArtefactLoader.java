package minefantasy.mfr.registry.knowledge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.mechanics.knowledge.IArtefact;
import minefantasy.mfr.mechanics.knowledge.ResearchArtefacts;
import minefantasy.mfr.util.FileUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads artefact mappings from JSON files.
 * Artefacts are items/blocks that give clues to unlock knowledge entries.
 */
public class ArtefactLoader {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String ARTEFACT_FOLDER_PATH = Constants.ASSET_DIRECTORY + "/knowledge_artefacts/";
	private static final String CONFIG_ARTEFACT_DIRECTORY = "config/" + Constants.CONFIG_DIRECTORY + "/custom/knowledge_artefacts/";

	/**
	 * Load all artefact mappings from JSON files.
	 */
	public void loadArtefacts() {
		ModContainer modContainer = Loader.instance().activeModContainer();

		FileUtils.createCustomDataDirectory(CONFIG_ARTEFACT_DIRECTORY);
		
		// Load from custom config directory first
		loadArtefactsFromDirectory(modContainer, new File(CONFIG_ARTEFACT_DIRECTORY), "");
		
		// Load from mod assets
		Loader.instance().getActiveModList().forEach(m ->
				loadArtefactsFromDirectory(m, m.getSource(), String.format(ARTEFACT_FOLDER_PATH, m.getModId())));

		Loader.instance().setActiveModContainer(modContainer);
		
		// Also register artefacts from IArtefact items
		registerArtefactItems();
	}

	private void loadArtefactsFromDirectory(ModContainer mod, File source, String base) {
		JsonContext ctx = new JsonContext(mod.getModId());

		FileUtils.findFiles(source, base, root -> FileUtils.loadConstants(source, base, ctx), (root, file) -> {
			Path relative = root.relativize(file);
			if (relative.getNameCount() > 1) {
				String extension = FilenameUtils.getExtension(file.toString());

				if (!extension.equals(Constants.JSON_FILE_EXT)) {
					return;
				}

				String modName = relative.getName(relative.getNameCount() - 2).toString();
				String fileName = FilenameUtils.removeExtension(relative.getFileName().toString());

				if (!Loader.isModLoaded(modName) || fileName.startsWith("_")) {
					return;
				}

				Loader.instance().setActiveModContainer(mod);

				if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.toString().startsWith("_"))
					return;

				ResourceLocation key = new ResourceLocation(ctx.getModId(), fileName);

				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(file);
					JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);

					if (CraftingHelper.processConditions(json, "conditions", ctx)) {
						parseArtefacts(json, key);
					}
				}
				catch (JsonParseException e) {
					MineFantasyReforged.LOG.error("Parsing error loading artefacts {}", key, e);
				}
				catch (IOException e) {
					MineFantasyReforged.LOG.error("Couldn't read artefacts {} from {}", key, file, e);
				}
				finally {
					IOUtils.closeQuietly(reader);
				}
			}
		});
	}

	private void parseArtefacts(JsonObject json, ResourceLocation key) {
		String knowledgeEntry = JsonUtils.getString(json, "knowledge_entry");
		JsonArray artefacts = JsonUtils.getJsonArray(json, "artefacts");
		
		for (JsonElement artefactElement : artefacts) {
			JsonObject artefactObj = artefactElement.getAsJsonObject();
			Object artefact = parseArtefact(artefactObj);
			
			if (artefact != null) {
				ResearchArtefacts.addArtefact(artefact, knowledgeEntry);
				MineFantasyReforged.LOG.debug("Added artefact for knowledge entry: {}", knowledgeEntry);
			}
		}
	}
	
	private Object parseArtefact(JsonObject artefactObj) {
		String type = JsonUtils.getString(artefactObj, "type", "item");
		String id = JsonUtils.getString(artefactObj, "id");
		int meta = JsonUtils.getInt(artefactObj, "meta", 0);
		
		if ("block".equals(type)) {
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id));
			if (block != null && block != Blocks.AIR) {
				return block;
			}
		} else if ("item".equals(type)) {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
			if (item != null && item != Items.AIR) {
				if (meta > 0) {
					return new ItemStack(item, 1, meta);
				}
				return item;
			}
		} else if ("itemstack".equals(type)) {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
			if (item != null && item != Items.AIR) {
				return new ItemStack(item, 1, meta);
			}
		}
		
		return null;
	}
	
	/**
	 * Register artefacts from items implementing IArtefact interface
	 */
	private void registerArtefactItems() {
		for (Item item : ForgeRegistries.ITEMS.getValues()) {
			if (item instanceof IArtefact) {
				IArtefact artefact = (IArtefact) item;
				if (artefact.getResearches() != null) {
					for (String research : artefact.getResearches()) {
						ResearchArtefacts.addArtefact(new ItemStack(item, 1), research);
					}
				}
			}
		}
	}
}
