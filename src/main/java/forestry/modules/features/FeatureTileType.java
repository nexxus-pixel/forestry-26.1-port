package forestry.modules.features;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class FeatureTileType<T extends BlockEntity> extends ModFeature implements ITileTypeFeature<T> {
	private final RegistryObject<BlockEntityType<T>> blockEntityObject;

	public FeatureTileType(IFeatureRegistry registry, Identifier moduleId, String name, BlockEntityType.BlockEntitySupplier<T> constructorTileEntity, Supplier<Collection<? extends Block>> validBlocks) {
		super(moduleId, name);
		this.blockEntityObject = registry.getRegistry(Registries.BLOCK_ENTITY_TYPE).register(name, () -> {
			Set<Block> blocks = Set.copyOf(validBlocks.get());
			return new BlockEntityType<>(constructorTileEntity, blocks);
		});
	}

	@Override
	public ResourceKey<? extends Registry<?>> getRegistry() {
		return Registries.BLOCK_ENTITY_TYPE;
	}

	@Override
	public BlockEntityType<T> tileType() {
		return this.blockEntityObject.get();
	}
}
