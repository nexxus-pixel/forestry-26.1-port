package forestry.core.network;

import forestry.api.ForestryConstants;
import net.minecraft.resources.Identifier;

/**
 * Packets sent to the client from the server
 */
public class PacketIdClient {
	// Core
	public static final Identifier RECIPE_CACHE = ForestryConstants.forestry("recipe_cache");
	// Core Gui
	public static final Identifier ERROR_UPDATE = ForestryConstants.forestry("error_update");
	public static final Identifier GUI_UPDATE = ForestryConstants.forestry("gui_update");
	public static final Identifier GUI_LAYOUT_SELECT = ForestryConstants.forestry("gui_layout_select");
	public static final Identifier GUI_ENERGY = ForestryConstants.forestry("gui_energy");
	public static final Identifier SOCKET_UPDATE = ForestryConstants.forestry("socket_update");
	// Core Tile Entities
	public static final Identifier TILE_FORESTRY_UPDATE = ForestryConstants.forestry("tile_forestry_update");
	public static final Identifier ITEMSTACK_DISPLAY = ForestryConstants.forestry("itemstack_display");
	public static final Identifier TANK_LEVEL_UPDATE = ForestryConstants.forestry("tank_level_update");
	public static final Identifier REFRACTORY_WAX_ON = ForestryConstants.forestry("refractory_wax_on");
	// Core Genome
	public static final Identifier GENOME_TRACKER_UPDATE = ForestryConstants.forestry("genome_tracker_update");
	// Factory
	public static final Identifier WORKTABLE_MEMORY_UPDATE = ForestryConstants.forestry("worktable_memory_update");
	public static final Identifier WORKTABLE_CRAFTING_UPDATE = ForestryConstants.forestry("worktable_crafting_update");
	// Apiculture
	public static final Identifier TILE_FORESTRY_ACTIVE = ForestryConstants.forestry("tile_forestry_active");
	public static final Identifier BEE_LOGIC_ACTIVE = ForestryConstants.forestry("bee_logic_active");
	public static final Identifier HABITAT_BIOME_POINTER = ForestryConstants.forestry("habitat_biome_pointer");
	public static final Identifier ALVERAY_CONTROLLER_CHANGE = ForestryConstants.forestry("alveray_controller_change");
	// Arboriculture
	public static final Identifier RIPENING_UPDATE = ForestryConstants.forestry("ripening_update");
	// Mail
	public static final Identifier TRADING_ADDRESS_RESPONSE = ForestryConstants.forestry("trading_address_response");
	public static final Identifier LETTER_INFO_RESPONSE_PLAYER = ForestryConstants.forestry("letter_info_response_player");
	public static final Identifier LETTER_INFO_RESPONSE_TRADER = ForestryConstants.forestry("letter_info_response_trader");
	public static final Identifier POBOX_INFO_RESPONSE = ForestryConstants.forestry("pobox_info_response");
	// Sorting
	public static final Identifier GUI_UPDATE_FILTER = ForestryConstants.forestry("gui_update_filter");
	// JEI
	public static final Identifier RECIPE_TRANSFER_UPDATE = ForestryConstants.forestry("recipe_transfer_update");
}
