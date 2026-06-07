package forestry.worktable.blocks;

import forestry.core.blocks.BlockBase;
import net.minecraft.world.level.block.SoundType;

public class WorktableBlock extends BlockBase<WorktableBlockType> {
	public WorktableBlock(WorktableBlockType blockType) {
		super(blockType, Properties.of().sound(SoundType.WOOD));
	}
}
