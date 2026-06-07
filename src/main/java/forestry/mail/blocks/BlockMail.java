package forestry.mail.blocks;

import forestry.modules.features.RegistrationContext;

import forestry.core.blocks.BlockBase;
import net.minecraft.world.level.block.Block;

public class BlockMail extends BlockBase<BlockTypeMail> {
	public BlockMail(BlockTypeMail blockType) {
		super(blockType, RegistrationContext.blockProperties());
	}
}
