package forestry.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import forestry.api.ForestryConstants;
import forestry.core.blocks.BlockBase;
import forestry.core.config.Constants;
import forestry.core.tiles.TileNaturalistChest;
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
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RenderNaturalistChest implements BlockEntityRenderer<TileNaturalistChest, NaturalistChestRenderState> {
	private static final String LID = "lid";
	private static final String BASE = "base";
	private static final String LOCK = "lock";

	private final SpriteGetter sprites;
	private final ModelPart lid;
	private final ModelPart base;
	private final ModelPart lock;
	private final Identifier texture;

	public RenderNaturalistChest(BlockEntityRendererProvider.Context ctx, String textureName) {
		this.sprites = ctx.sprites();
		ModelPart root = ctx.bakeLayer(ForestryModelLayers.NATURALIST_CHEST_LAYER);

		this.lid = root.getChild(LID);
		this.base = root.getChild(BASE);
		this.lock = root.getChild(LOCK);
		this.texture = ForestryConstants.forestry(Constants.TEXTURE_PATH_BLOCK + "/" + textureName + ".png");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild(BASE, CubeListBuilder.create().texOffs(0, 19)
			.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.offset(0, 0, 0));
		partdefinition.addOrReplaceChild(LID, CubeListBuilder.create().texOffs(0, 0)
			.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0, 9.0F, 1.0F));
		partdefinition.addOrReplaceChild(LOCK, CubeListBuilder.create().texOffs(0, 0)
			.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0, 8.0F, 0));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public NaturalistChestRenderState createRenderState() {
		return new NaturalistChestRenderState();
	}

	@Override
	public void extractRenderState(TileNaturalistChest chest, NaturalistChestRenderState state, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(chest, state, partialTick, cameraPos, crumblingOverlay);
		state.orientation = chest.getBlockState().getValue(BlockBase.FACING);

		float prevLidAngle = chest.prevLidAngle;
		float lidAngle = chest.lidAngle;
		float angle = prevLidAngle + (lidAngle - prevLidAngle) * partialTick;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		state.lidRotation = -(angle * Mth.HALF_PI);
	}

	@Override
	public void submit(NaturalistChestRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraState) {
		int light = state.lightCoords;
		int overlay = 0;

		stack.pushPose();
		BerRenderHelper.rotateByHorizontalDirection(stack, state.orientation);

		BerRenderHelper.submitPart(collector, this.base, stack, this.sprites, this.texture, light, overlay);

		stack.pushPose();
		stack.translate(0, 9f / 16f, 1f / 16f);
		stack.mulPose(Axis.XP.rotation(state.lidRotation));
		stack.translate(0, -9f / 16f, -1f / 16f);
		BerRenderHelper.submitPart(collector, this.lid, stack, this.sprites, this.texture, light, overlay);
		stack.popPose();

		stack.pushPose();
		stack.translate(0, 8f / 16f, 0);
		stack.mulPose(Axis.XP.rotation(state.lidRotation));
		stack.translate(0, -8f / 16f, 0);
		BerRenderHelper.submitPart(collector, this.lock, stack, this.sprites, this.texture, light, overlay);
		stack.popPose();

		stack.popPose();
	}
}
