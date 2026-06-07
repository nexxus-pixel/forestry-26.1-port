package forestry.core.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class MillRenderState extends BlockEntityRenderState {
	public Direction orientation = Direction.SOUTH;
	public int charge;
	public float bladeStep;
}
