package forestry.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import it.unimi.dsi.fastutil.HashCommon;

public final class BerItemRenderHelper {
	private BerItemRenderHelper() {
	}

	public static void updateDisplayItem(ItemModelResolver resolver, ItemStackRenderState itemState, ItemStack stack, BlockEntity blockEntity) {
		resolver.updateForTopItem(
			itemState,
			stack,
			ItemDisplayContext.GROUND,
			blockEntity.getLevel(),
			new BlockEntityItemOwner(blockEntity),
			HashCommon.long2int(blockEntity.getBlockPos().asLong())
		);
	}

	public static void submitBobbingItem(ItemStackRenderState itemState, PoseStack stack, SubmitNodeCollector collector, int light, float animationTime) {
		if (itemState.isEmpty()) {
			return;
		}

		stack.pushPose();

		float bob = Mth.sin(animationTime / 10.0f) * 0.1f + 0.1f;
		AABB bounds = itemState.getModelBoundingBox();
		float groundScale = (float) bounds.getYsize();
		stack.translate(0, bob + 0.25f * groundScale, 0);
		stack.mulPose(Axis.YP.rotation(animationTime / 20f));

		itemState.submit(stack, collector, light, OverlayTexture.NO_OVERLAY, 0);
		stack.popPose();
	}
}
