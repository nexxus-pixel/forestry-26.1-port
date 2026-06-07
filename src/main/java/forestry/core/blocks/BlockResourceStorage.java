package forestry.core.blocks;

import forestry.modules.features.RegistrationContext;

import net.minecraft.world.level.block.Block;

public class BlockResourceStorage extends Block {
	private final EnumResourceType type;

	public BlockResourceStorage(EnumResourceType type) {
		super(RegistrationContext.of(p -> p.strength(3f, 5f)));
		this.type = type;
	}

	public EnumResourceType getType() {
		return this.type;
	}
}
