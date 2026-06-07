package forestry.apiculture.blocks;

import forestry.modules.features.RegistrationContext;

import forestry.core.blocks.BlockBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class BlockApiculture extends BlockBase<BlockTypeApiculture> {
	public BlockApiculture(BlockTypeApiculture type) {
		super(type, RegistrationContext.of(p -> p.sound(SoundType.WOOD)));
	}
}
