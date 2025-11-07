package minefantasy.mfr.mechanics.knowledge;

import minefantasy.mfr.constants.Skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InformationList {
	public static InformationPage artisanry = new InformationPage("infoPage.artisanry", Skill.ARTISANRY).registerInfoPage();
	public static InformationPage construction = new InformationPage("infoPage.construction", Skill.CONSTRUCTION).registerInfoPage();
	public static InformationPage provisioning = new InformationPage("infoPage.provisioning", Skill.PROVISIONING).registerInfoPage();
	public static InformationPage engineering = new InformationPage("infoPage.engineering", Skill.ENGINEERING).registerInfoPage();
	public static InformationPage mastery = new InformationPage("infoPage.mastery", Skill.NONE).registerInfoPage();

	/**
	 * Is the smallest column used to display a achievement on the GUI.
	 */
	public static int minDisplayColumn;
	/**
	 * Is the smallest row used to display a achievement on the GUI.
	 */
	public static int minDisplayRow;
	/**
	 * Is the biggest column used to display a achievement on the GUI.
	 */
	public static int maxDisplayColumn;
	/**
	 * Is the biggest row used to display a achievement on the GUI.
	 */
	public static int maxDisplayRow;
	/**
	 * Holds a list of all registered achievements.
	 */
	public static List<InformationBase> knowledgeList = new ArrayList<>();
	public static HashMap<String, InformationBase> nameMap = new HashMap<>();

	/**
	 * A stub functions called to make the static initializer for this class run.
	 */
	public static void init() {
	}
	
	/**
	 * Get a knowledge entry by name.
	 * @param name The name/id of the knowledge entry
	 * @return The InformationBase object, or null if not found
	 */
	public static InformationBase getEntry(String name) {
		return nameMap.get(name);
	}
}