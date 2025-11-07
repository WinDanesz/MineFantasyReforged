package minefantasy.mfr.registry.knowledge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.mechanics.knowledge.InformationBase;
import minefantasy.mfr.mechanics.knowledge.InformationList;
import minefantasy.mfr.mechanics.knowledge.InformationPage;
import minefantasy.mfr.util.FileUtils;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads knowledge entries from JSON files in a data-driven manner.
 * Similar to how recipes are loaded via CraftingManagerBase.
 */
public class KnowledgeEntryLoader {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String KNOWLEDGE_FOLDER_PATH = Constants.ASSET_DIRECTORY + "/knowledge_entries/";
	private static final String CONFIG_KNOWLEDGE_DIRECTORY = "config/" + Constants.CONFIG_DIRECTORY + "/custom/knowledge_entries/";

	private final KnowledgeEntryFactory factory;

	public KnowledgeEntryLoader() {
		this.factory = new KnowledgeEntryFactory();
	}

	/**
	 * Load all knowledge entries from JSON files.
	 * This should be called during post-initialization after items and blocks are registered.
	 * 
	 * Note: Parent entries should be loaded before their children. Since InformationBase.parent
	 * is final and set in the constructor, parent references that don't exist at load time
	 * will remain null. Files are loaded in alphabetical order, so naming conventions can help
	 * ensure proper ordering (e.g., prefix with numbers: "01_parent.json", "02_child.json").
	 */
	public void loadKnowledgeEntries() {
		ModContainer modContainer = Loader.instance().activeModContainer();

		FileUtils.createCustomDataDirectory(CONFIG_KNOWLEDGE_DIRECTORY);
		
		// Load from custom config directory first
		loadKnowledgeEntriesFromDirectory(modContainer, new File(CONFIG_KNOWLEDGE_DIRECTORY), "");
		
		// Load from mod assets
		Loader.instance().getActiveModList().forEach(m ->
				loadKnowledgeEntriesFromDirectory(m, m.getSource(), String.format(KNOWLEDGE_FOLDER_PATH, m.getModId())));

		Loader.instance().setActiveModContainer(modContainer);
	}

	private void loadKnowledgeEntriesFromDirectory(ModContainer mod, File source, String base) {
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

				ResourceLocation key = new ResourceLocation(ctx.getModId(), fileName);

				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(file);
					JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);

					if (json.has("modIds")) {
						JsonArray modIds = JsonUtils.getJsonArray(json, "modIds");
						for (JsonElement modId : modIds) {
							if (!Loader.isModLoaded(modId.getAsString())) {
								return;
							}
						}
					}

					if (CraftingHelper.processConditions(json, "conditions", ctx)) {
						InformationBase entry = factory.parse(ctx, json);
						addKnowledgeEntry(entry, key);
					}
				}
				catch (JsonParseException e) {
					MineFantasyReforged.LOG.error("Parsing error loading knowledge entry {}", key, e);
				}
				catch (IOException e) {
					MineFantasyReforged.LOG.error("Couldn't read knowledge entry {} from {}", key, file, e);
				}
				finally {
					IOUtils.closeQuietly(reader);
				}
			}
		});
	}

	private void addKnowledgeEntry(InformationBase entry, ResourceLocation key) {
		if (entry != null) {
			entry.registerStat();
			MineFantasyReforged.LOG.debug("Loaded knowledge entry: {}", key);
		}
	}

	public KnowledgeEntryFactory getFactory() {
		return factory;
	}
}
