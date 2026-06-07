package forestry.storage;

import forestry.api.ForestryTags;
import forestry.api.genetics.ForestrySpeciesTypes;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.api.storage.IBackpackInterface;
import forestry.core.ForestryColors;
import forestry.core.config.ForestryConfig;
import forestry.modules.BlankForestryModule;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;


@ForestryModule
public class ModuleStorage extends BlankForestryModule {
	public static final IBackpackInterface BACKPACK_INTERFACE = new BackpackInterface();

	public static final BackpackDefinition APIARIST = new BackpackDefinition(0xc4923d, ForestryColors.WHITE, BACKPACK_INTERFACE.createNaturalistBackpackFilter(ForestrySpeciesTypes.BEE));
	public static final BackpackDefinition ARBORIST = new BackpackDefinition(0x657e3a, ForestryColors.WHITE, BACKPACK_INTERFACE.createNaturalistBackpackFilter(ForestrySpeciesTypes.TREE));
	public static final BackpackDefinition LEPIDOPTERIST = new BackpackDefinition(0x995b31, ForestryColors.WHITE, BACKPACK_INTERFACE.createNaturalistBackpackFilter(ForestrySpeciesTypes.BUTTERFLY));
	public static final BackpackDefinition MINER = new BackpackDefinition(0x36187d, ForestryColors.WHITE, new BackpackFilter(ForestryTags.Items.MINER_ALLOW, ForestryTags.Items.MINER_REJECT));
	public static final BackpackDefinition DIGGER = new BackpackDefinition(0x363cc5, ForestryColors.WHITE, new BackpackFilter(ForestryTags.Items.DIGGER_ALLOW, ForestryTags.Items.DIGGER_REJECT));
	public static final BackpackDefinition FORESTER = new BackpackDefinition(0x347427, ForestryColors.WHITE, new BackpackFilter(ForestryTags.Items.FORESTER_ALLOW, ForestryTags.Items.FORESTER_REJECT));
	public static final BackpackDefinition HUNTER = new BackpackDefinition(0x412215, ForestryColors.WHITE, new BackpackFilter(ForestryTags.Items.HUNTER_ALLOW, ForestryTags.Items.HUNTER_REJECT));
	public static final BackpackDefinition ADVENTURER = new BackpackDefinition(0x7fb8c2, ForestryColors.WHITE, new BackpackFilter(ForestryTags.Items.ADVENTURER_ALLOW, ForestryTags.Items.ADVENTURER_REJECT));
	public static final BackpackDefinition BUILDER = new BackpackDefinition(0xdd3a3a, ForestryColors.WHITE, new BackpackFilter(ForestryTags.Items.BUILDER_ALLOW, ForestryTags.Items.BUILDER_REJECT));

	@Override
	public Identifier getId() {
		return ForestryModuleIds.STORAGE;
	}

	@Override
	public void registerEvents(BusGroup modBusGroup) {
		EntityItemPickupEvent.BUS.addListener(ModuleStorage::onItemPickup);
		TickEvent.LevelTickEvent.Post.BUS.addListener(ModuleStorage::onLevelTick);
	}

	private static void onLevelTick(TickEvent.LevelTickEvent.Post event) {
		if (ForestryConfig.SERVER.enableBackpackResupply.get()) {
			for (Player player : event.level().players()) {
				BackpackResupplyHandler.resupply(player);
			}
		}
	}

	private static void onItemPickup(EntityItemPickupEvent event) {
		if (event.getResult() == Result.ALLOW) {
			return;
		}
		BackpackPickupHandler.onItemPickup(event.getEntity(), event.getItem());
	}

}
