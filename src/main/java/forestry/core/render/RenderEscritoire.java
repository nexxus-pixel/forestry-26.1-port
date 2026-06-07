package forestry.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.api.ForestryConstants;
import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileEscritoire;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RenderEscritoire implements BlockEntityRenderer<TileEscritoire, EscritoireRenderState> {
	private static final Identifier TEXTURE = ForestryConstants.forestry(Constants.TEXTURE_PATH_BLOCK + "/escritoire.png");

	private final SpriteGetter sprites;
	private final ItemModelResolver itemModelResolver;
	private final ModelPart root;

	public RenderEscritoire(BlockEntityRendererProvider.Context ctx) {
		this.sprites = ctx.sprites();
		this.itemModelResolver = ctx.itemModelResolver();
		this.root = ctx.bakeLayer(ForestryModelLayers.ESCRITOIRE_LAYER);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("desk", CubeListBuilder.create().texOffs(0, 0)
			.addBox(0, 0, 0, 16, 2, 15).mirror(), PartPose.offsetAndRotation(0, 9.5f, 0.4f, 0.0872665f, 0, 0));
		partdefinition.addOrReplaceChild("standrb", CubeListBuilder.create().texOffs(38, 18)
			.addBox(0f, 0f, 0f, 2, 6, 2).mirror(), PartPose.offset(13, 4, 13));
		partdefinition.addOrReplaceChild("standrf", CubeListBuilder.create().texOffs(38, 18)
			.addBox(0f, 0f, 0f, 2, 6, 2).mirror(), PartPose.offset(13, 4, 1));
		partdefinition.addOrReplaceChild("standlb", CubeListBuilder.create().texOffs(38, 18)
			.addBox(0f, 0f, 0f, 2, 6, 2).mirror(), PartPose.offset(1, 4, 1));
		partdefinition.addOrReplaceChild("standlf", CubeListBuilder.create().texOffs(38, 18)
			.addBox(0f, 0f, 0f, 2, 6, 2).mirror(), PartPose.offset(1, 4, 13));
		partdefinition.addOrReplaceChild("drawers", CubeListBuilder.create().texOffs(0, 18)
			.addBox(0f, 0f, 0f, 15, 5, 3).mirror(), PartPose.offset(0.5f, 11, 0.5f));
		partdefinition.addOrReplaceChild("standlowrb", CubeListBuilder.create().texOffs(0, 26)
			.addBox(0f, 0f, 0f, 1, 4, 1).mirror(), PartPose.offset(13.5f, 0, 13.5f));
		partdefinition.addOrReplaceChild("standlowrf", CubeListBuilder.create().texOffs(0, 26)
			.addBox(0f, 0f, 0f, 1, 4, 1).mirror(), PartPose.offset(13.5f, 0, 1.5f));
		partdefinition.addOrReplaceChild("standlowlb", CubeListBuilder.create().texOffs(0, 26)
			.addBox(0f, 0f, 0f, 1, 4, 1).mirror(), PartPose.offset(1.5f, 0, 1.5f));
		partdefinition.addOrReplaceChild("standlowlf", CubeListBuilder.create().texOffs(0, 26)
			.addBox(0f, 0f, 0f, 1, 4, 1).mirror(), PartPose.offset(1.5f, 0, 13.5f));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public EscritoireRenderState createRenderState() {
		return new EscritoireRenderState();
	}

	@Override
	public void extractRenderState(TileEscritoire escritoire, EscritoireRenderState state, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(escritoire, state, partialTick, cameraPos, crumblingOverlay);
		state.orientation = escritoire.getBlockState().getValue(BlockBase.FACING);

		ItemStack displayStack = escritoire.getIndividualOnDisplay();
		state.hasDisplayItem = !displayStack.isEmpty();
		if (state.hasDisplayItem) {
			BerItemRenderHelper.updateDisplayItem(this.itemModelResolver, state.displayItem, displayStack, escritoire);
			state.displayAnimationTime = escritoire.hasLevel() ? escritoire.getLevel().getGameTime() + partialTick : partialTick;
		} else {
			state.displayItem.clear();
		}
	}

	@Override
	public void submit(EscritoireRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraState) {
		int light = state.lightCoords;
		int overlay = 0;

		stack.pushPose();
		BerRenderHelper.rotateByHorizontalDirection(stack, state.orientation);
		BerRenderHelper.submitPart(collector, this.root, stack, this.sprites, TEXTURE, light, overlay);

		if (state.hasDisplayItem) {
			stack.pushPose();
			stack.translate(0.5, 0.65, 0.5);
			stack.scale(0.75f, 0.75f, 0.75f);
			BerItemRenderHelper.submitBobbingItem(state.displayItem, stack, collector, light, state.displayAnimationTime);
			stack.popPose();
		}

		stack.popPose();
	}
}
