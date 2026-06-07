package forestry.apiculture;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.ForestryBeeSpecies;
import forestry.api.core.ForestryEvent;
import forestry.api.core.TemperatureType;
import forestry.api.genetics.ForestryTaxa;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.api.modules.IPacketRegistry;
import forestry.apiculture.commands.CommandBee;
import forestry.apiculture.features.ApicultureItems;
import forestry.apiculture.items.EnumPollenCluster;
import forestry.apiculture.network.packets.PacketAlvearyChange;
import forestry.apiculture.network.packets.PacketBeeLogicActive;
import forestry.apiculture.network.packets.PacketHabitatBiomePointer;
import forestry.core.network.PacketIdClient;
import forestry.core.utils.LootTableUtil;
import forestry.core.utils.SpeciesUtil;
import forestry.modules.BlankForestryModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.event.brewing.BrewingRecipeRegisterEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;

@ForestryModule
public class ModuleApiculture extends BlankForestryModule {
	public static int ticksPerBeeWorkCycle = 550;
	public static boolean hivesDamageOnPeaceful = false;
	public static boolean hivesDamageUnderwater = true;
	public static boolean hivesDamageOnlyPlayers = false;
	public static boolean hiveDamageOnAttack = true;
	public static boolean doSelfPollination = false;
	public static int maxFlowersSpawnedPerHive = 20;

	private static void registerBrewingRecipes(BrewingRecipeRegisterEvent event) {
		ItemStack awkwardPotion = PotionContents.createItemStack(Items.POTION, Potions.AWKWARD);
		event.addRecipe(new BrewingRecipe(
			Ingredient.of(awkwardPotion.getItem()),
			Ingredient.of(ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.NORMAL, 1).getItem()),
			PotionContents.createItemStack(Items.POTION, Potions.HEALING)
		));
		event.addRecipe(new BrewingRecipe(
			Ingredient.of(awkwardPotion.getItem()),
			Ingredient.of(ApicultureItems.POLLEN_CLUSTER.stack(EnumPollenCluster.CRYSTALLINE, 1).getItem()),
			PotionContents.createItemStack(Items.POTION, Potions.REGENERATION)
		));
	}

	private static void onNetherBeeMate(ForestryEvent.BeeMatingEvent event) {
		if (event.getPrincess().getSpecies().getGenusName().equals(ForestryTaxa.GENUS_EMBITTERED) && event.getHousing().temperature() != TemperatureType.HELLISH) {
			event.setPrincess(SpeciesUtil.getBeeSpecies(ForestryBeeSpecies.ZOMBIFIED).createIndividual());
		}
	}

	private static void modifySnifferLoot(LootTableLoadEvent event) {
		if (event.getName().equals(BuiltInLootTables.SNIFFER_DIGGING)) {
			LootTableUtil.addSupplementaryPool(event.getTable(), "forestry_amber_drone", LootItem.lootTableItem(ApicultureItems.AMBER_DRONE));
		}
	}

	// todo config
	public static double getSecondPrincessChance() {
		return (float) 0;
	}

	@Override
	public Identifier getId() {
		return ForestryModuleIds.APICULTURE;
	}

	@Override
	public void registerEvents(BusGroup modBusGroup) {
		BrewingRecipeRegisterEvent.BUS.addListener(ModuleApiculture::registerBrewingRecipes);

		ForestryEvent.BeeMatingEvent.BUS.addListener(ModuleApiculture::onNetherBeeMate);
		LootTableLoadEvent.BUS.addListener(ModuleApiculture::modifySnifferLoot);
	}

	@Override
	public void addToRootCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		command.then(CommandBee.register());
	}

	@Override
	public void setupApi() {
		BeeManager.armorApiaristHelper = new ArmorApiaristHelper();
	}

	@Override
	public void registerPackets(IPacketRegistry registry) {
		registry.clientbound(PacketIdClient.BEE_LOGIC_ACTIVE, PacketBeeLogicActive.class, PacketBeeLogicActive::decode, PacketBeeLogicActive::handle);
		registry.clientbound(PacketIdClient.HABITAT_BIOME_POINTER, PacketHabitatBiomePointer.class, PacketHabitatBiomePointer::decode, PacketHabitatBiomePointer::handle);
		registry.clientbound(PacketIdClient.ALVERAY_CONTROLLER_CHANGE, PacketAlvearyChange.class, PacketAlvearyChange::decode, PacketAlvearyChange::handle);
	}

}
