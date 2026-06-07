package forestry.core.blocks;

import forestry.modules.features.RegistrationContext;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockCore extends BlockBase<BlockTypeCoreTesr> {
	public BlockCore(BlockTypeCoreTesr blockType) {
		super(blockType, RegistrationContext.of(p -> p.sound(SoundType.WOOD).noOcclusion()));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}
}
