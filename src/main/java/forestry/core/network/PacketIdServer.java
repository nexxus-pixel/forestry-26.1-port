package forestry.core.network;

import forestry.api.ForestryConstants;
import net.minecraft.resources.Identifier;

/**
 * Packets sent to the server from the client
 */
public class PacketIdServer {
	// Core Gui
	public static final Identifier GUI_SELECTION_REQUEST = ForestryConstants.forestry("gui_selection_request");
	public static final Identifier PIPETTE_CLICK = ForestryConstants.forestry("pipette_click");
	public static final Identifier CHIPSET_CLICK = ForestryConstants.forestry("chipset_click");
	public static final Identifier SOLDERING_IRON_CLICK = ForestryConstants.forestry("soldering_iron_click");
	// Climate
	public static final Identifier SELECT_CLIMATE_TARGETED = ForestryConstants.forestry("select_climate_targeted");
	public static final Identifier CLIMATE_LISTENER_UPDATE_REQUEST = ForestryConstants.forestry("climate_listener_update_request");
	// Database
	public static final Identifier INSERT_ITEM = ForestryConstants.forestry("insert_item");
	public static final Identifier EXTRACT_ITEM = ForestryConstants.forestry("extract_item");
	// Sorting
	public static final Identifier FILTER_CHANGE_RULE = ForestryConstants.forestry("filter_change_rule");
	public static final Identifier FILTER_CHANGE_GENOME = ForestryConstants.forestry("filter_change_genome");
	// JEI
	public static final Identifier WORKTABLE_RECIPE_REQUEST = ForestryConstants.forestry("worktable_recipe_request");
	public static final Identifier RECIPE_TRANSFER_REQUEST = ForestryConstants.forestry("recipe_transfer_request");
	// Mail
	public static final Identifier LETTER_INFO_REQUEST = ForestryConstants.forestry("letter_info_request");
	public static final Identifier TRADING_ADDRESS_REQUEST = ForestryConstants.forestry("trading_address_request");
	public static final Identifier LETTER_TEXT_SET = ForestryConstants.forestry("letter_text_set");
}
