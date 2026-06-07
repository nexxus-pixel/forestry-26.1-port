package forestry.core.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class EscritoireRenderState extends BlockEntityRenderState {
	public Direction orientation = Direction.NORTH;
	public final ItemStackRenderState displayItem = new ItemStackRenderState();
	public boolean hasDisplayItem;
	public float displayAnimationTime;
}
