package forestry.plugin;



import forestry.api.arboriculture.ITreeSpecies;

import forestry.api.arboriculture.genetics.IFruit;

import forestry.api.core.IProduct;

import forestry.api.farming.ForestryFarmTypes;

import forestry.api.genetics.alleles.ForestryAlleles;

import forestry.api.genetics.alleles.IValueAllele;

import forestry.api.genetics.alleles.TreeChromosomes;

import forestry.api.plugin.IFarmTypeBuilder;

import forestry.api.plugin.IFarmingRegistration;

import forestry.core.features.CoreBlocks;

import forestry.core.features.CoreItems;

import forestry.core.items.ItemFruit;

import forestry.core.utils.SpeciesUtil;

import forestry.farming.logic.*;

import forestry.farming.logic.farmables.*;

import net.minecraft.world.item.Items;

import net.minecraft.world.level.block.BeetrootBlock;

import net.minecraft.world.level.block.Blocks;

import net.minecraft.world.level.block.CropBlock;

import net.minecraft.world.level.block.NetherWartBlock;

import java.util.List;

public class DefaultFarms {

	public static void registerFarmTypes(IFarmingRegistration farming) {

		// Trees

		// TODO separate compatibility plugins that add all the extra junk items (ex. drops from mods like Delightful, Twig)

		IFarmTypeBuilder arboreal = farming.createFarmType(ForestryFarmTypes.ARBOREAL, FarmLogicArboreal::new, Blocks.OAK_SAPLING.asItem())

			.setFertilizerConsumption(10)

			.setWaterConsumption(hydrationModifier -> (int) (10 * hydrationModifier))

			.addSoilItem(Blocks.DIRT.asItem(), CoreBlocks.HUMUS.defaultState())

			.addSoil(CoreBlocks.HUMUS.item(), CoreBlocks.HUMUS.defaultState());

		addTreeFarmables(arboreal);



		// Crops

		IFarmTypeBuilder crops = farming.createFarmType(ForestryFarmTypes.CROPS, FarmLogicCrops::new, Items.WHEAT)

			.setWaterConsumption(hydrationModifier -> (int) (20 * hydrationModifier))

			.setFertilizerConsumption(5)

			.addSoilItem(Blocks.DIRT.asItem(), Blocks.FARMLAND.defaultBlockState());

		addCropFarmables(crops);



		// Gourd (Pumpkin and Melon)

		IFarmTypeBuilder gourd = farming.createFarmType(ForestryFarmTypes.GOURD, FarmLogicGourd::new, Items.MELON)

			.setFertilizerConsumption(10)

			.setWaterConsumption(hydrationModifier -> (int) (40 * hydrationModifier))

			.addSoilItem(Blocks.DIRT.asItem(), Blocks.FARMLAND.defaultBlockState());

		addGourdFarmables(gourd);



		// Mushroom

		IFarmTypeBuilder shroom = farming.createFarmType(ForestryFarmTypes.SHROOM, FarmLogicMushroom::new, Blocks.RED_MUSHROOM.asItem())

			.setFertilizerConsumption(20)

			.setWaterConsumption(hydrationModifier -> (int) (80 * hydrationModifier))

			.addSoil(Blocks.MYCELIUM)

			.addSoil(Blocks.PODZOL);

		shroom.addFarmable(new FarmableMushroom(Items.BROWN_MUSHROOM, Blocks.BROWN_MUSHROOM.defaultBlockState()));

		shroom.addFarmable(new FarmableMushroom(Items.RED_MUSHROOM, Blocks.RED_MUSHROOM.defaultBlockState()));



		// Nether Wart

		IFarmTypeBuilder infernal = farming.createFarmType(ForestryFarmTypes.INFERNAL, FarmLogicInfernal::new, Items.NETHER_WART)

			.setFertilizerConsumption(20)

			.setWaterConsumption(0)

			.addSoil(Blocks.SOUL_SAND);

		infernal.addFarmable(new FarmableAgingCrop(Items.NETHER_WART, Blocks.NETHER_WART, NetherWartBlock.AGE, 3));



		// Sugarcane

		IFarmTypeBuilder poales = farming.createFarmType(ForestryFarmTypes.POALES, FarmLogicReeds::new, Items.SUGAR_CANE)

			.setFertilizerConsumption(10)

			.setWaterConsumption(hydrationModifier -> (int) (20 * hydrationModifier))

			.addSoil(Blocks.SAND)

			.addSoil(Blocks.DIRT);

		poales.addFarmable(new FarmableStacked(Items.SUGAR_CANE, Blocks.SUGAR_CANE, 3));



		// Cactus

		IFarmTypeBuilder cactus = farming.createFarmType(ForestryFarmTypes.SUCCULENTES, FarmLogicSucculent::new, Items.GREEN_DYE)

			.setFertilizerConsumption(10)

			.setWaterConsumption(1)

			.addSoil(Blocks.SAND);

		cactus.addFarmable(new FarmableStacked(Blocks.CACTUS.asItem(), Blocks.CACTUS, 3));



		// Chorus Fruit

		IFarmTypeBuilder ender = farming.createFarmType(ForestryFarmTypes.ENDER, FarmLogicEnder::new, Items.ENDER_EYE)

			.setFertilizerConsumption(20)

			.setWaterConsumption(0)

			.addSoil(Blocks.END_STONE);

		ender.addFarmable(FarmableChorus.INSTANCE);



		// Peat (???)

		IFarmTypeBuilder peat = farming.createFarmType(ForestryFarmTypes.PEAT, FarmLogicPeat::new, CoreItems.PEAT.item())

			.setWaterConsumption((hydrationModifier) -> (int) (20 * hydrationModifier))

			.setFertilizerConsumption(2)

			.addSoil(CoreBlocks.BOG_EARTH.item(), CoreBlocks.BOG_EARTH.defaultState())

			.addProduct(CoreItems.PEAT.item())

			.addProduct(Blocks.DIRT.asItem());



		// Fruit Trees

		IFarmTypeBuilder orchard = farming.createFarmType(ForestryFarmTypes.ORCHARD, FarmLogicOrchard::new, CoreItems.FRUITS.item(ItemFruit.EnumFruit.CHERRY))

			.setFertilizerConsumption(10)

			.setWaterConsumption(hydrationModifier -> (int) (40 * hydrationModifier));

		for (ITreeSpecies species : SpeciesUtil.TREE_TYPE.get().getAllSpecies()) {

			IValueAllele<IFruit> fruitAllele = species.getDefaultGenome().getActiveAllele(TreeChromosomes.FRUIT);



			if (fruitAllele != ForestryAlleles.FRUIT_NONE) {

				IFruit fruit = fruitAllele.value();

				for (IProduct product : fruit.getProducts()) {

					orchard.addProduct(product.item());

				}

				for (IProduct product : fruit.getSpecialty()) {

					orchard.addProduct(product.item());

				}

			}

		}



		/*BlockState plantedBrownMushroom = FarmingBlocks.MUSHROOM.with(BlockMushroom.VARIANT, BlockMushroom.MushroomType.BROWN);

		registry.registerFarmables(ForestryFarmIdentifier.SHROOM, new FarmableVanillaMushroom(new ItemStack(Blocks.BROWN_MUSHROOM), plantedBrownMushroom, Blocks.BROWN_MUSHROOM_BLOCK));



		BlockState plantedRedMushroom = FarmingBlocks.MUSHROOM.with(BlockMushroom.VARIANT, BlockMushroom.MushroomType.RED);

		registry.registerFarmables(ForestryFarmIdentifier.SHROOM, new FarmableVanillaMushroom(new ItemStack(Blocks.RED_MUSHROOM), plantedRedMushroom, Blocks.RED_MUSHROOM_BLOCK));*/



		// Cocoa

		IFarmTypeBuilder cocoa = farming.createFarmType(ForestryFarmTypes.COCOA, FarmLogicCocoa::new, Items.COCOA_BEANS)

			.setFertilizerConsumption(120)

			.setWaterConsumption(hydrationModifier -> (int) (20 * hydrationModifier))

			.addGermling(Items.COCOA_BEANS)

			.addProduct(Items.COCOA_BEANS);

	}



	private static void addGourdFarmables(IFarmTypeBuilder gourd) {

		gourd.addFarmable(new FarmableGourd(Items.PUMPKIN_SEEDS, Blocks.PUMPKIN_STEM, Blocks.PUMPKIN));

		gourd.addFarmable(new FarmableGourd(Items.MELON_SEEDS, Blocks.MELON_STEM, Blocks.MELON));

	}



	private static void addTreeFarmables(IFarmTypeBuilder arboreal) {

		arboreal.addWindfallFarmable(Items.OAK_SAPLING, FarmableSapling::new, builder -> builder.addWindfall(List.of(Items.APPLE, Items.STICK)));

		arboreal.addWindfallFarmable(Items.BIRCH_SAPLING, FarmableSapling::new, builder -> builder.addWindfall(Items.STICK));

		arboreal.addWindfallFarmable(Items.SPRUCE_SAPLING, FarmableSapling::new, builder -> builder.addWindfall(Items.STICK));

		arboreal.addWindfallFarmable(Items.JUNGLE_SAPLING, FarmableSapling::new, builder -> builder.addWindfall(List.of(Items.STICK, Items.COCOA_BEANS)));

		arboreal.addWindfallFarmable(Items.DARK_OAK_SAPLING, FarmableSapling::new, builder -> builder.addWindfall(Items.STICK));

		arboreal.addWindfallFarmable(Items.ACACIA_SAPLING, FarmableSapling::new, builder -> builder.addWindfall(Items.STICK));

		//arboreal.addWindfallFarmable(Items.AZALEA, FarmableSapling::new, builder -> builder.addWindfall(Items.STICK));

		//arboreal.addWindfallFarmable(Items.FLOWERING_AZALEA, FarmableSapling::new, builder -> builder.addWindfall(List.of(Items.STICK, Items.AZALEA)));

		arboreal.addWindfallFarmable(Items.MANGROVE_PROPAGULE, FarmableMangroveTree::new, builder -> builder.addWindfall(List.of(Items.STICK, Items.MOSS_CARPET)));

		arboreal.addWindfallFarmable(Items.CHERRY_SAPLING, FarmableSapling::new, builder -> builder.addWindfall(Items.STICK));

		arboreal.addFarmable(new FarmableGE());

	}



	private static void addCropFarmables(IFarmTypeBuilder crops) {

		crops.addFarmable(new FarmableAgingCrop(Items.WHEAT_SEEDS, Blocks.WHEAT, Items.WHEAT, CropBlock.AGE, 7, 0));

		crops.addFarmable(new FarmableAgingCrop(Items.POTATO, Blocks.POTATOES, Items.POTATO, CropBlock.AGE, 7, 0));

		crops.addFarmable(new FarmableAgingCrop(Items.CARROT, Blocks.CARROTS, Items.CARROT, CropBlock.AGE, 7, 0));

		crops.addFarmable(new FarmableAgingCrop(Items.BEETROOT_SEEDS, Blocks.BEETROOTS, Items.BEETROOT, BeetrootBlock.AGE, 3, 0));

	}

}

