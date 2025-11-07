package minefantasy.mfr.init;

import minefantasy.mfr.config.ConfigHardcore;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.item.ItemBomb;
import minefantasy.mfr.mechanics.knowledge.IArtefact;
import minefantasy.mfr.mechanics.knowledge.InformationBase;
import minefantasy.mfr.mechanics.knowledge.InformationList;
import minefantasy.mfr.mechanics.knowledge.InformationPage;
import minefantasy.mfr.mechanics.knowledge.ResearchArtefacts;
import minefantasy.mfr.registry.material.CustomMaterial;
import minefantasy.mfr.registry.material.CustomMaterialRegistry;
import minefantasy.mfr.util.RecipeHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;

public class MineFantasyKnowledgeList {

	// note: please follow the naming convention for static final vars, these should be all upper (https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)
	public static final IRecipe STICK_RECIPE = RecipeHelper.getMFRRecipe("sticks");
	public static final IRecipe TIMBER_RECIPE = RecipeHelper.getMFRRecipe("timber");
	public static final IRecipe FIREPIT_RECIPE = RecipeHelper.getMFRRecipe("firepit");
	public static final IRecipe STOVE_RECIPE = RecipeHelper.getMFRRecipe("stove");
	public static final IRecipe CARPENTER_RECIPE = RecipeHelper.getMFRRecipe("carpenter");
	public static final IRecipe SUGAR_POT_RECIPE = RecipeHelper.getMFRRecipe("sugar_pot");
	public static final IRecipe CHEESE_WHEEL_RECIPE = RecipeHelper.getMFRRecipe("cheese_wheel");
	public static final IRecipe PIE_MEAT_RECIPE = RecipeHelper.getMFRRecipe("pie_meat");
	public static final IRecipe PIE_SHEPARDS_RECIPE = RecipeHelper.getMFRRecipe("pie_shepards");
	public static final IRecipe PIE_APPLE_RECIPE = RecipeHelper.getMFRRecipe("pie_apple");
	public static final IRecipe PIE_BERRY_RECIPE = RecipeHelper.getMFRRecipe("pie_berry");
	public static final IRecipe PIE_PUMPKIN_RECIPE = RecipeHelper.getMFRRecipe("pie_pumpkin");
	public static final IRecipe JUG_PLANT_OIL_RECIPE = RecipeHelper.getMFRRecipe("jug_plant_oil");
	public static final IRecipe JUG_WATER_RECIPE = RecipeHelper.getMFRRecipe("jug_water");
	public static final IRecipe JUG_MILK_RECIPE = RecipeHelper.getMFRRecipe("jug_milk");
	public static final IRecipe DRYROCKS_RECIPE = ConfigHardcore.HCCallowRocks ? RecipeHelper.getMFRRecipe("dryrocks_hc") : RecipeHelper.getMFRRecipe("dryrocks");
	
	public static InformationPage artisanry = InformationList.artisanry;
	public static InformationPage construction = InformationList.construction;
	public static InformationPage engineering = InformationList.engineering;
	public static InformationPage provisioning = InformationList.provisioning;
	public static InformationPage mastery = InformationList.mastery;
	
	// Note: Static InformationBase fields have been removed.
	// Use InformationList.getEntry("entry_name") to access knowledge entries.
	// Example: InformationList.getEntry("getting_started")
	
	/**
	 * @deprecated This method is no longer used. Knowledge entries are now loaded from JSON files.
	 * See {@link minefantasy.mfr.registry.knowledge.KnowledgeEntryLoader} for the new system.
	 */
	@Deprecated
	public static void init() {
		// This method body has been removed. Use InformationList.getEntry("entry_name") instead.
	}

	private static Object getMetalTier(String string) {
		CustomMaterial mat = CustomMaterialRegistry.getMaterial(string);
		if (mat != CustomMaterialRegistry.NONE){
			return mat.getCrafterTier();
		}
		return "?";
	}

	public static class ArtefactListMFR {
	/**
	 * @deprecated This method is no longer used. Knowledge entries are now loaded from JSON files.
	 * See {@link minefantasy.mfr.registry.knowledge.KnowledgeEntryLoader} for the new system.
	 */
	@Deprecated
		public static void init() {
		// This method body has been removed. Use InformationList.getEntry("entry_name") instead.
		}

		private static void addEngineering() {
			add(blackpowder, MineFantasyItems.NITRE, MineFantasyItems.SULFUR, Items.COAL, Items.GUNPOWDER);
			add(advanced_blackpowder, Items.GLOWSTONE_DUST, Items.REDSTONE);
			add(tungsten, MineFantasyItems.ORE_TUNGSTEN, MineFantasyBlocks.TUNGSTEN_ORE);
			add(coke, Items.COAL, Items.REDSTONE);
			add(spyglass, MineFantasyItems.BRONZE_GEARS, Blocks.GLASS);
			add(parachute, Items.FEATHER, Blocks.WOOL);
			add(syringe, Items.POTIONITEM);
			add(engineering_tanner, MineFantasyItems.BRONZE_GEARS);
			add(bomb_arrow, Items.FEATHER, MineFantasyItems.BLACKPOWDER);
			add(bomb_press, MineFantasyItems.BRONZE_GEARS, Blocks.LEVER);
			add(bombs, MineFantasyItems.BLACKPOWDER, Items.REDSTONE, Items.STRING);
			add(shrapnel, Items.FLINT);
			add(firebomb, MineFantasyItems.DRAGON_HEART, Items.MAGMA_CREAM);
			add(sticky_bomb, Items.SLIME_BALL);
			add(mine_ceramic, MineFantasyItems.BLACKPOWDER, Blocks.STONE_PRESSURE_PLATE);
			add(bomb_iron, Items.IRON_INGOT);
			add(mine_iron, Items.IRON_INGOT);
			add(bomb_obsidian, Blocks.OBSIDIAN);
			add(mine_obsidian, Blocks.OBSIDIAN);
			add(bomb_crystal, Items.DIAMOND);
			add(mine_crystal, Items.DIAMOND);

			add(crossbows, Items.STRING, MineFantasyItems.TIMBER, Blocks.LEVER);
			add(crossbow_shaft_advanced, MineFantasyItems.TUNGSTEN_GEARS);
			add(crossbow_head_advanced, MineFantasyItems.TUNGSTEN_GEARS);
			add(crossbow_ammo, MineFantasyItems.TUNGSTEN_GEARS);
			add(crossbow_scope, MineFantasyItems.SPYGLASS);
			add(crossbow_bayonet, MineFantasyItems.STANDARD_DAGGER);
		}

		private static void addProvisioning() {
			add(jerky, MineFantasyItems.GENERIC_MEAT_UNCOOKED);
			add(sausage, MineFantasyItems.GENERIC_MEAT_UNCOOKED, MineFantasyItems.GUTS);
			add(sandwitch, MineFantasyItems.GENERIC_MEAT_UNCOOKED, MineFantasyItems.CHEESE_SLICE, Items.BREAD);
			add(sandwitch_big, MineFantasyItems.GENERIC_MEAT_UNCOOKED, MineFantasyItems.CHEESE_SLICE, Items.BREAD);

			add(meatpie, MineFantasyItems.GENERIC_MEAT_UNCOOKED, MineFantasyItems.PASTRY);
			add(shepard_pie, MineFantasyItems.GENERIC_MEAT_UNCOOKED, Items.POTATO, MineFantasyItems.PASTRY);
			add(berry_pie, MineFantasyItems.BERRIES, MineFantasyItems.PASTRY);
			add(apple_pie, Items.APPLE, MineFantasyItems.PASTRY);

			add(sweetroll, Items.SUGAR, MineFantasyItems.BERRIES, MineFantasyItems.SUGAR_POT);
			add(eclair, Items.EGG, new ItemStack(Items.DYE, 1, 3), MineFantasyItems.PASTRY);
			add(cheese_roll, Items.BREAD, MineFantasyItems.CHEESE_SLICE);

			add(cake, MineFantasyItems.FLOUR, Items.EGG);
			add(carrot_cake, MineFantasyItems.FLOUR, Items.EGG, Items.CARROT);
			add(chocolate_cake, MineFantasyItems.FLOUR, Items.EGG, new ItemStack(Items.DYE, 1, 3));
			add(black_forest_cake, MineFantasyItems.FLOUR, Items.EGG, new ItemStack(Items.DYE, 1, 3),
					MineFantasyItems.BERRIES_JUICY);

			add(bandage_advanced, Blocks.WOOL, Items.LEATHER);
		}

		private static void addConstruction() {
			add(refined_planks, MineFantasyItems.NAIL);
			add(clay_wall, Items.CLAY_BALL, MineFantasyItems.NAIL);
			add(paint_brush, Blocks.WOOL);
			add(decorated_stone, Items.IRON_INGOT, MineFantasyBlocks.REINFORCED_STONE);
			add(bed_roll, Items.BED);
			add(ammo_box, Blocks.CHEST);
			add(big_box, Blocks.CHEST);

		}

		private static void addArtisanry() {
			for (ItemStack copper : OreDictionary.getOres("ingotCopper")) {
				for (ItemStack tin : OreDictionary.getOres("ingotTin")) {
					add(smelt_bronze, copper, tin);
				}
			}
			add(coal_flux, Items.COAL, MineFantasyItems.FLUX);
			add(smelt_iron, Blocks.IRON_ORE);
			add(firebrick_crucible, MineFantasyItems.FIRECLAY);
			add(blast_furnace, Items.IRON_INGOT, Blocks.IRON_ORE, Blocks.FURNACE, MineFantasyBlocks.BLOOMERY,
					MineFantasyBlocks.LIMESTONE, MineFantasyItems.KAOLINITE);
			add(big_furnace, Items.IRON_INGOT, Blocks.FURNACE, MineFantasyBlocks.BLOOMERY, MineFantasyItems.KAOLINITE,
					Items.COAL);
			for (ItemStack pig : OreDictionary.getOres("ingotPigIron")) {
				add(smelt_steel, pig);
			}
			for (ItemStack steel : OreDictionary.getOres("ingotSteel")) {
				add(smelt_encrusted, steel, Items.DIAMOND);
				add(smelt_obsidian, steel, Blocks.OBSIDIAN);
				for (ItemStack bronze : OreDictionary.getOres("ingotBronze")) {
					add(smelt_black_steel, Blocks.OBSIDIAN, bronze, steel);
				}
			}
			for (ItemStack black : OreDictionary.getOres("ingotBlackSteel")) {
				for (ItemStack silver : OreDictionary.getOres("ingotSilver")) {
					add(smelt_blue_steel, Items.BLAZE_POWDER, silver, black, new ItemStack(Items.DYE, 1, 4),
							MineFantasyItems.FLUX_STRONG);
				}
				add(smelt_red_steel, Items.BLAZE_POWDER, Items.GOLD_INGOT, Items.REDSTONE, black,
						MineFantasyItems.FLUX_STRONG);
			}

			for (ItemStack silver : OreDictionary.getOres("ingotSilver")) {
				add(smelt_mithril, MineFantasyBlocks.MYTHIC_ORE, silver, MineFantasyItems.ANCIENT_JEWEL_MITHRIL);
			}
			add(smelt_adamantium, MineFantasyBlocks.MYTHIC_ORE, Items.GOLD_INGOT, MineFantasyItems.ANCIENT_JEWEL_ADAMANT);
			add(smelt_master, MineFantasyItems.ANCIENT_JEWEL_ADAMANT, MineFantasyItems.ANCIENT_JEWEL_MITHRIL, MineFantasyItems.ANCIENT_JEWEL_MASTER);

			for (ItemStack mithril : OreDictionary.getOres("ingotMithril")) {
				add(smelt_mithium, mithril, Items.GHAST_TEAR, Items.DIAMOND, MineFantasyItems.ANCIENT_JEWEL_ADAMANT);
				for (ItemStack adamant : OreDictionary.getOres("ingotAdamantium")) {
					add(smelt_ignotumite, adamant, Items.EMERALD, Items.BLAZE_POWDER);
					add(smelt_ender, adamant, mithril, Items.ENDER_PEARL);
				}
			}
			add(craft_armour_medium, Items.LEATHER);
			add(craft_armour_heavy, Items.LEATHER, MineFantasyItems.PLATE, Blocks.WOOL);
			add(smelt_dragonforged, MineFantasyItems.DRAGON_HEART);

			add(craft_ornate, new ItemStack(Items.DYE, 1, 4));

			add(arrows_bodkin, Items.FEATHER);
			add(arrows_broad, Items.FEATHER, Items.FLINT);

			add(repair_basic, Items.LEATHER, Items.FLINT, MineFantasyItems.NAIL);
			add(repair_advanced, MineFantasyBlocks.REPAIR_BASIC, Items.SLIME_BALL, Items.STRING);
			add(repair_ornate, Items.DIAMOND, Items.GOLD_INGOT, MineFantasyBlocks.REPAIR_ADVANCED);
		}

		private static void addArtefacts() {
			register(MineFantasyItems.ANCIENT_JEWEL_ADAMANT);
			register(MineFantasyItems.ANCIENT_JEWEL_MITHRIL);
			register(MineFantasyItems.ANCIENT_JEWEL_MASTER);

			register(MineFantasyBlocks.SCHEMATIC_ALLOY_ITEM);
			register(MineFantasyBlocks.SCHEMATIC_BOMB_ITEM);
			register(MineFantasyBlocks.SCHEMATIC_CROSSBOW_ITEM);
			register(MineFantasyBlocks.SCHEMATIC_FORGE_ITEM);
			register(MineFantasyBlocks.SCHEMATIC_COGWORK_ITEM);
			register(MineFantasyBlocks.SCHEMATIC_GEARS_ITEM);
		}

		private static void add(InformationBase info, Object... artifacts) {
			for (Object artifact : artifacts) {
				ResearchArtefacts.addArtefact(artifact, info);
			}
		}

		public static void register(Item item) {
			if (item instanceof IArtefact){
				if (((IArtefact) item).getResearches() != null) {
					for (String research : ((IArtefact) item).getResearches()) {
						ResearchArtefacts.addArtefact(new ItemStack(item, 1), research);
					}
				}
			}
		}
	}
}
