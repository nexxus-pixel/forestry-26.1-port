package forestry.arboriculture.blocks;

import forestry.modules.features.RegistrationContext;

import forestry.api.arboriculture.IWoodType;
import forestry.api.arboriculture.WoodBlockKind;
import forestry.arboriculture.ForestryWoodType;
import forestry.arboriculture.IWoodTyped;
import forestry.arboriculture.VanillaWoodType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class BlockForestryFenceGate extends FenceGateBlock implements IWoodTyped {
	private final boolean fireproof;
	private final IWoodType woodType;

	public BlockForestryFenceGate(boolean fireproof, IWoodType woodType) {
		super(getMinecraftWoodType(woodType), RegistrationContext.of(p -> p.strength(woodType.getHardness(), woodType.getHardness() * 1.5F).sound(SoundType.WOOD)));
		this.fireproof = fireproof;
		this.woodType = woodType;
	}

	@Override
	public boolean isFireproof() {
		return this.fireproof;
	}

	@Override
	public IWoodType getWoodType() {
		return this.woodType;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (this.fireproof) {
			return 0;
		}
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		if (this.fireproof) {
			return 0;
		}
		return 5;
	}

	@Override
	public WoodBlockKind getBlockKind() {
		return WoodBlockKind.FENCE_GATE;
	}

	private static WoodType getMinecraftWoodType(IWoodType woodType) {
		if (woodType instanceof ForestryWoodType forestryWoodType) {
			return forestryWoodType.getWoodType();
		}
		if (woodType instanceof VanillaWoodType vanillaWoodType) {
			return vanillaWoodType.getWoodType();
		}
		throw new IllegalArgumentException("Unknown wood type: " + woodType);
	}
}
