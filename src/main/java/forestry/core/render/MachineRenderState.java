package forestry.core.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class MachineRenderState extends BlockEntityRenderState {
	public Direction orientation = Direction.NORTH;
	public TankRenderInfo resourceTankInfo = TankRenderInfo.EMPTY;
	public TankRenderInfo productTankInfo = TankRenderInfo.EMPTY;
}
