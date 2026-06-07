package forestry.core.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class NaturalistChestRenderState extends BlockEntityRenderState {
	public Direction orientation = Direction.NORTH;
	public float lidRotation;
}
