package forestry.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import forestry.api.ForestryConstants;
import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileAnalyzer;
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
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// todo replace with static block model and just render the item
public class RenderAnalyzer implements BlockEntityRenderer<TileAnalyzer, AnalyzerRenderState> {
	private static final String TOWER2 = "tower2";
	private static final String TOWER1 = "tower1";
	private static final String COVER = "cover";
	private static final String PEDESTAL = "pedestal";

	private static final Identifier TEXTURE0 = ForestryConstants.forestry(Constants.TEXTURE_PATH_BLOCK + "/analyzer_pedestal.png");
	private static final Identifier TEXTURE1 = ForestryConstants.forestry(Constants.TEXTURE_PATH_BLOCK + "/analyzer_tower1.png");
	private static final Identifier TEXTURE2 = ForestryConstants.forestry(Constants.TEXTURE_PATH_BLOCK + "/analyzer_tower2.png");

	private final SpriteGetter sprites;
	private final ItemModelResolver itemModelResolver;
	private final ModelPart pedestal;
	private final ModelPart cover;
	private final ModelPart tower1;
	private final ModelPart tower2;

	public RenderAnalyzer(BlockEntityRendererProvider.Context ctx) {
		this.sprites = ctx.sprites();
		this.itemModelResolver = ctx.itemModelResolver();

		ModelPart root = ctx.bakeLayer(ForestryModelLayers.ANALYZER_LAYER);
		this.pedestal = root.getChild(PEDESTAL);
		this.cover = root.getChild(COVER);
		this.tower1 = root.getChild(TOWER1);
		this.tower2 = root.getChild(TOWER2);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild(PEDESTAL, CubeListBuilder.create().texOffs(0, 0)
			.addBox(0, 0, 0, 16, 1, 16), PartPose.offset(0, 0, 0));
		partdefinition.addOrReplaceChild(COVER, CubeListBuilder.create().texOffs(0, 0)
			.addBox(0, 0, 0, 16, 1, 16), PartPose.offsetAndRotation(16, 16, 0, 0, 0, Mth.PI));
		partdefinition.addOrReplaceChild(TOWER1, CubeListBuilder.create().texOffs(0, 0)
			.addBox(0, 0, 0, 2, 14, 14), PartPose.offset(0, 1, 1));
		partdefinition.addOrReplaceChild(TOWER2, CubeListBuilder.create().texOffs(0, 0)
			.addBox(0, 0, 0, 2, 14, 14), PartPose.offset(14, 1, 1));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}

	@Override
	public AnalyzerRenderState createRenderState() {
		return new AnalyzerRenderState();
	}

	@Override
	public void extractRenderState(TileAnalyzer analyzer, AnalyzerRenderState state, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(analyzer, state, partialTick, cameraPos, crumblingOverlay);
		state.orientation = analyzer.getBlockState().getValue(BlockBase.FACING);

		ItemStack displayStack = analyzer.getIndividualOnDisplay();
		state.hasDisplayItem = !displayStack.isEmpty();
		if (state.hasDisplayItem) {
			BerItemRenderHelper.updateDisplayItem(this.itemModelResolver, state.displayItem, displayStack, analyzer);
			state.displayAnimationTime = analyzer.hasLevel() ? analyzer.getLevel().getGameTime() + partialTick : partialTick;
		} else {
			state.displayItem.clear();
		}
	}

	@Override
	public void submit(AnalyzerRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraState) {
		int light = state.lightCoords;
		int overlay = 0;

		stack.pushPose();
		BerRenderHelper.rotateByHorizontalDirection(stack, state.orientation);

		BerRenderHelper.submitPart(collector, this.pedestal, stack, this.sprites, TEXTURE0, light, overlay);
		BerRenderHelper.submitPart(collector, this.cover, stack, this.sprites, TEXTURE0, light, overlay);
		BerRenderHelper.submitPart(collector, this.tower1, stack, this.sprites, TEXTURE1, light, overlay);
		BerRenderHelper.submitPart(collector, this.tower2, stack, this.sprites, TEXTURE2, light, overlay);

		stack.popPose();

		if (state.hasDisplayItem) {
			stack.pushPose();
			stack.translate(0.5f, 0.2f, 0.5f);
			BerItemRenderHelper.submitBobbingItem(state.displayItem, stack, collector, light, state.displayAnimationTime);
			stack.popPose();
		}
	}
}
