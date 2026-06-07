package forestry.core.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public record BlockEntityItemOwner(BlockEntity blockEntity) implements ItemOwner {
	@Override
	public Level level() {
		return this.blockEntity.getLevel();
	}

	@Override
	public Vec3 position() {
		BlockPos pos = this.blockEntity.getBlockPos();
		return Vec3.atCenterOf(pos);
	}

	@Override
	public float getVisualRotationYInDegrees() {
		return 0;
	}
}
