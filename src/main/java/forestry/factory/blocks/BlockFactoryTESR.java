package forestry.factory.blocks;

import forestry.modules.features.RegistrationContext;

import forestry.core.blocks.BlockBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class BlockFactoryTESR extends BlockBase<BlockTypeFactoryTesr> {
	public BlockFactoryTESR(BlockTypeFactoryTesr type) {
		super(type, RegistrationContext.blockProperties());
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}
}
