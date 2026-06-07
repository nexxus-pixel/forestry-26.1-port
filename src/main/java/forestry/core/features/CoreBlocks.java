package forestry.core.features;

import forestry.modules.features.RegistrationContext;

import forestry.api.modules.ForestryModuleIds;
import forestry.apiculture.blocks.NaturalistChestBlockType;
import forestry.core.blocks.*;
import forestry.core.items.ItemBlockForestry;
import forestry.modules.features.*;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

@FeatureProvider
public class CoreBlocks {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.CORE);

	public static final FeatureBlockGroup<BlockCore, BlockTypeCoreTesr> BASE = REGISTRY.blockGroup(BlockCore::new, BlockTypeCoreTesr.values()).item(ItemBlockForestry::new).create();
	public static final FeatureBlock<BlockBogEarth, ItemBlockForestry<?>> BOG_EARTH = REGISTRY.block(BlockBogEarth::new, ItemBlockForestry::new, "bog_earth");
	public static final FeatureBlock<Block, ItemBlockForestry<?>> PEAT = REGISTRY.block(() -> new Block(RegistrationContext.blockProperties()
		.strength(0.5f)
		.sound(SoundType.GRAVEL)), "peat");
	public static final FeatureBlock<BlockHumus, ItemBlockForestry<?>> HUMUS = REGISTRY.block(BlockHumus::new, ItemBlockForestry::new, "humus");
	public static final FeatureBlockGroup<BlockResourceStorage, EnumResourceType> RESOURCE_STORAGE = REGISTRY.blockGroup(BlockResourceStorage::new, EnumResourceType.values()).item(ItemBlockForestry::new).identifier("resource_storage").create();
	public static final FeatureBlock<Block, BlockItem> APATITE_ORE = REGISTRY.block(() -> new DropExperienceBlock(UniformInt.of(0, 4), RegistrationContext.withBlockId(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE))), ItemBlockForestry::new, "apatite_ore");
	public static final FeatureBlock<Block, BlockItem> DEEPSLATE_APATITE_ORE = REGISTRY.block(() -> new DropExperienceBlock(UniformInt.of(0, 4), RegistrationContext.withBlockId(BlockBehaviour.Properties.ofFullCopy(APATITE_ORE.block()).mapColor(MapColor.DEEPSLATE).strength(4.5f, 3.0f).sound(SoundType.DEEPSLATE))), ItemBlockForestry::new, "deepslate_apatite_ore");
	public static final FeatureBlock<Block, BlockItem> TIN_ORE = REGISTRY.block(() -> new Block(RegistrationContext.withBlockId(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_ORE))), ItemBlockForestry::new, "tin_ore");
	public static final FeatureBlock<Block, BlockItem> DEEPSLATE_TIN_ORE = REGISTRY.block(() -> new Block(RegistrationContext.withBlockId(BlockBehaviour.Properties.ofFullCopy(TIN_ORE.block()).mapColor(MapColor.DEEPSLATE).strength(4.5f, 3.0f).sound(SoundType.DEEPSLATE))), ItemBlockForestry::new, "deepslate_tin_ore");
	public static final FeatureBlock<Block, BlockItem> RAW_TIN_BLOCK = REGISTRY.block(() -> new Block(RegistrationContext.withBlockId(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_COPPER_BLOCK))), ItemBlockForestry::new, "raw_tin_block");

	public static final FeatureBlockGroup<BlockTesr<NaturalistChestBlockType>, NaturalistChestBlockType> NATURALIST_CHEST = REGISTRY.blockGroup(type -> {
		return new BlockTesr<>(type, RegistrationContext.of(p -> p.sound(SoundType.WOOD)));
	}, NaturalistChestBlockType.values()).item(ItemBlockForestry::new).create();
}
