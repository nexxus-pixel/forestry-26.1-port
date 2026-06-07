package forestry.core.render;

import forestry.core.tiles.TemperatureState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class EngineRenderState extends BlockEntityRenderState {
	public Direction orientation = Direction.NORTH;
	public TemperatureState temperature = TemperatureState.COOL;
	public float pistonStep;
}
