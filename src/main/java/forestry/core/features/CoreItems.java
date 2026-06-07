package forestry.core.features;

import forestry.api.modules.ForestryModuleIds;
import forestry.apiculture.items.ItemBeesWax;
import forestry.apiculture.items.ItemRefractoryWax;
import forestry.core.circuits.EnumCircuitBoardType;
import forestry.core.circuits.ItemCircuitBoard;
import forestry.core.items.*;
import forestry.core.items.definitions.EnumCraftingMaterial;
import forestry.core.items.definitions.EnumElectronTube;
import forestry.modules.features.*;
import net.minecraft.world.item.Item;

@FeatureProvider
public class CoreItems {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.CORE);

	/* Foresters' Manual (ForestersManualItem pending Phase 5) */
	public static final FeatureItem<Item> FORESTERS_MANUAL = REGISTRY.item("foresters_manual");

	/* Fertilizer */
	public static final FeatureItem<ItemFertilizer> COMPOST = REGISTRY.item(ItemFertilizer::new, "fertilizer_bio");
	public static final FeatureItem<ItemFertilizer> FERTILIZER_COMPOUND = REGISTRY.item(ItemFertilizer::new, "fertilizer_compound");

	/* Gems and raw ores */
	public static final FeatureItem<ItemForestry> APATITE = REGISTRY.item(ItemForestry::new, "apatite");
	public static final FeatureItem<ItemForestry> RAW_TIN = REGISTRY.item(ItemForestry::new, "raw_tin");
	public static final FeatureItem<ItemForestry> AMBER = REGISTRY.item(ItemForestry::new, "amber");

	/* Research (ItemResearchNote pending Phase 5) */
	public static final FeatureItem<Item> RESEARCH_NOTE = REGISTRY.item("research_note");

	/* Alyzer (ItemAlyzer pending Phase 5) */
	public static final FeatureItem<Item> PORTABLE_ALYZER = REGISTRY.item("portable_alyzer");

	/* Ingots */
	public static final FeatureItem<ItemForestry> INGOT_TIN = REGISTRY.item(ItemForestry::new, "ingot_tin");
	public static final FeatureItem<ItemForestry> INGOT_BRONZE = REGISTRY.item(ItemForestry::new, "ingot_bronze");

	/* Tools */
	public static final FeatureItem<ItemWrench> WRENCH = REGISTRY.item(ItemWrench::new, "wrench");
	public static final FeatureItem<Item> PIPETTE = REGISTRY.item("pipette");

	/* Packaged Tools (HasRemnants bronze tools pending Phase 5) */
	public static final FeatureItem<ItemForestry> CARTON = REGISTRY.item(ItemForestry::new, "carton");
	public static final FeatureItem<ItemForestry> BROKEN_BRONZE_PICKAXE = REGISTRY.item(ItemForestry::new, "broken_bronze_pickaxe");
	public static final FeatureItem<ItemForestry> BROKEN_BRONZE_SHOVEL = REGISTRY.item(ItemForestry::new, "broken_bronze_shovel");
	public static final FeatureItem<ItemForestry> BROKEN_BRONZE_AXE = REGISTRY.item(ItemForestry::new, "broken_axe");
	public static final FeatureItem<ItemForestry> BROKEN_BRONZE_SWORD = REGISTRY.item(ItemForestry::new, "broken_sword");
	public static final FeatureItem<ItemForestry> BROKEN_BRONZE_HOE = REGISTRY.item(ItemForestry::new, "broken_hoe");
	public static final FeatureItem<Item> BRONZE_PICKAXE = REGISTRY.item("bronze_pickaxe");
	public static final FeatureItem<Item> BRONZE_SHOVEL = REGISTRY.item("bronze_shovel");
	public static final FeatureItem<Item> BRONZE_AXE = REGISTRY.item("bronze_axe");
	public static final FeatureItem<Item> BRONZE_SWORD = REGISTRY.item("bronze_sword");
	public static final FeatureItem<Item> BRONZE_HOE = REGISTRY.item("bronze_hoe");
	public static final FeatureItem<Item> KIT_SHOVEL = REGISTRY.item("kit_shovel");
	public static final FeatureItem<Item> KIT_PICKAXE = REGISTRY.item("kit_pickaxe");
	public static final FeatureItem<Item> KIT_AXE = REGISTRY.item("axe_kit");
	public static final FeatureItem<Item> KIT_SWORD = REGISTRY.item("sword_kit");
	public static final FeatureItem<Item> KIT_HOE = REGISTRY.item("hoe_kit");

	/* Machine Parts */
	public static final FeatureItem<ItemForestry> STURDY_CASING = REGISTRY.item(ItemForestry::new, "sturdy_machine");
	public static final FeatureItem<ItemForestry> HARDENED_CASING = REGISTRY.item(ItemForestry::new, "hardened_machine");
	public static final FeatureItem<ItemForestry> IMPREGNATED_CASING = REGISTRY.item(ItemForestry::new, "impregnated_casing");
	public static final FeatureItem<ItemForestry> FLEXIBLE_CASING = REGISTRY.item(ItemForestry::new, "flexible_casing");
	public static final FeatureItem<ItemForestry> GEAR_BRONZE = REGISTRY.item(ItemForestry::new, "gear_bronze");
	public static final FeatureItem<ItemForestry> GEAR_COPPER = REGISTRY.item(ItemForestry::new, "gear_copper");
	public static final FeatureItem<ItemForestry> GEAR_TIN = REGISTRY.item(ItemForestry::new, "gear_tin");

	/* Soldering (ItemSolderingIron pending Phase 5) */
	public static final FeatureItem<Item> SOLDERING_IRON = REGISTRY.item("soldering_iron");
	public static final FeatureItemGroup<ItemCircuitBoard, EnumCircuitBoardType> CIRCUITBOARDS = REGISTRY.itemGroup(ItemCircuitBoard::new, "circuit_board", EnumCircuitBoardType.values());
	public static final FeatureItemGroup<ItemElectronTube, EnumElectronTube> ELECTRON_TUBES = REGISTRY.itemGroup(ItemElectronTube::new, "electron_tube", EnumElectronTube.values());

	/* Armor (ItemSpectacles pending Phase 5) */
	public static final FeatureItem<Item> SPECTACLES = REGISTRY.item("naturalist_helmet");

	/* Peat */
	public static final FeatureItem<ItemForestry> PEAT = REGISTRY.item(() -> new ItemForestry(new ItemProperties().burnTime(2000)), "peat");
	public static final FeatureItem<ItemForestry> ASH = REGISTRY.item(ItemForestry::new, "ash");
	public static final FeatureItem<ItemForestry> BITUMINOUS_PEAT = REGISTRY.item(() -> new ItemForestry(new ItemProperties().burnTime(4200)), "bituminous_peat");

	/* Moistener */
	public static final FeatureItem<ItemForestry> MOULDY_WHEAT = REGISTRY.item(ItemForestry::new, "mouldy_wheat");
	public static final FeatureItem<ItemForestry> DECAYING_WHEAT = REGISTRY.item(ItemForestry::new, "decaying_wheat");
	public static final FeatureItem<ItemFertilizer> MULCH = REGISTRY.item(ItemFertilizer::new, "mulch");

	/* Rainmaker */
	public static final FeatureItem<ItemForestry> IODINE_CHARGE = REGISTRY.item(ItemForestry::new, "iodine_capsule");
	public static final FeatureItem<ItemForestry> DISSIPATION_CHARGE = REGISTRY.item(ItemForestry::new, "dissipation_charge");

	/* Misc */
	public static final FeatureItemGroup<ItemCraftingMaterial, EnumCraftingMaterial> CRAFTING_MATERIALS = REGISTRY.itemGroup(ItemCraftingMaterial::new, EnumCraftingMaterial.values()).create();
	public static final FeatureItemGroup<ItemFruit, ItemFruit.EnumFruit> FRUITS = REGISTRY.itemGroup(ItemFruit::new, "fruit", ItemFruit.EnumFruit.values());
	public static final FeatureItem<ItemBeesWax> BEESWAX = REGISTRY.item(ItemBeesWax::new, "beeswax");
	public static final FeatureItem<ItemRefractoryWax> REFRACTORY_WAX = REGISTRY.item(ItemRefractoryWax::new, "refractory_wax");
}
