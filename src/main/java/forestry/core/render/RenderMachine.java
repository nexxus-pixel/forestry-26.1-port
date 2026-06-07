package forestry.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import forestry.api.ForestryConstants;
import forestry.core.blocks.BlockBase;
import forestry.core.tiles.IRenderableTile;
import forestry.core.tiles.TileBase;
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
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Locale;

public class RenderMachine implements BlockEntityRenderer<TileBase, MachineRenderState> {
	private static final String BASE_FRONT = "basefront";
	private static final String BASE_BACK = "baseback";
	private static final String RESOURCE_TANK = "resourceTank";
	private static final String PRODUCT_TANK = "productTank";

	private final SpriteGetter sprites;
	private final ModelPart basefront;
	private final ModelPart baseback;
	private final ModelPart resourceTank;
	private final ModelPart productTank;

	private final Identifier textureBase;
	private final Identifier textureResourceTank;
	private final Identifier textureProductTank;

	private final EnumMap<EnumTankLevel, Identifier> texturesTankLevels = new EnumMap<>(EnumTankLevel.class);

	public RenderMachine(BlockEntityRendererProvider.Context ctx, String baseTexture) {
		this.sprites = ctx.sprites();
		ModelPart root = ctx.bakeLayer(ForestryModelLayers.MACHINE_LAYER);

		this.basefront = root.getChild(BASE_FRONT);
		this.baseback = root.getChild(BASE_BACK);
		this.resourceTank = root.getChild(RESOURCE_TANK);
		this.productTank = root.getChild(PRODUCT_TANK);

		this.textureBase = ForestryConstants.forestry(baseTexture + "base.png");
		this.textureProductTank = ForestryConstants.forestry(baseTexture + "tank_product_empty.png");
		this.textureResourceTank = ForestryConstants.forestry(baseTexture + "tank_resource_empty.png");

		for (EnumTankLevel tankLevel : EnumTankLevel.values()) {
			if (tankLevel == EnumTankLevel.EMPTY) {
				continue;
			}
			String tankLevelString = tankLevel.toString().toLowerCase(Locale.ENGLISH);
			this.texturesTankLevels.put(tankLevel, ForestryConstants.forestry("textures/block/machine_tank_" + tankLevelString + ".png"));
		}
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		root.addOrReplaceChild(BASE_FRONT, CubeListBuilder.create().texOffs(0, 0)
			.addBox(0f, 0f, 0f, 16, 4, 16), PartPose.offset(0, 0, 0));
		root.addOrReplaceChild(BASE_BACK, CubeListBuilder.create().texOffs(0, 0)
			.addBox(0f, 0f, 0f, 16, 4, 16), PartPose.offset(0, 12, 0));
		root.addOrReplaceChild(RESOURCE_TANK, CubeListBuilder.create().texOffs(0, 0)
			.addBox(0f, 0f, 0f, 12, 16, 6), PartPose.offset(2, 0, 2));
		root.addOrReplaceChild(PRODUCT_TANK, CubeListBuilder.create().texOffs(0, 0)
			.addBox(0f, 0f, 0f, 12, 16, 6), PartPose.offset(2, 0, 8));

		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public MachineRenderState createRenderState() {
		return new MachineRenderState();
	}

	@Override
	public void extractRenderState(TileBase machine, MachineRenderState state, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(machine, state, partialTick, cameraPos, crumblingOverlay);
		state.orientation = machine.getBlockState().getValue(BlockBase.FACING);
		if (machine instanceof IRenderableTile renderable) {
			state.resourceTankInfo = renderable.getResourceTankInfo();
			state.productTankInfo = renderable.getProductTankInfo();
		} else {
			state.resourceTankInfo = TankRenderInfo.EMPTY;
			state.productTankInfo = TankRenderInfo.EMPTY;
		}
	}

	@Override
	public void submit(MachineRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraState) {
		int light = state.lightCoords;
		int overlay = 0;

		stack.pushPose();
		BerRenderHelper.rotateByHorizontalDirection(stack, state.orientation);
		stack.translate(0.5, 0.5, 0.5);
		stack.mulPose(Axis.XP.rotation(-Mth.HALF_PI));
		stack.translate(-0.5, -0.5, -0.5);

		BerRenderHelper.submitPart(collector, this.basefront, stack, this.sprites, this.textureBase, light, overlay);
		BerRenderHelper.submitPart(collector, this.baseback, stack, this.sprites, this.textureBase, light, overlay);

		stack.translate(0.5, 0.5, 0.5);
		stack.mulPose(Axis.YP.rotation(-Mth.HALF_PI));
		stack.translate(-0.5, -0.5, -0.5);

		submitTank(collector, stack, this.resourceTank, this.textureResourceTank, state.resourceTankInfo, light, overlay);
		submitTank(collector, stack, this.productTank, this.textureProductTank, state.productTankInfo, light, overlay);

		stack.popPose();
	}

	private void submitTank(SubmitNodeCollector collector, PoseStack stack, ModelPart tankModel, Identifier textureBase, TankRenderInfo renderInfo, int light, int overlay) {
		BerRenderHelper.submitPart(collector, tankModel, stack, this.sprites, textureBase, light, overlay);

		Identifier textureResourceTankLevel = this.texturesTankLevels.get(renderInfo.getLevel());
		if (textureResourceTankLevel == null) {
			return;
		}

		int color = FluidRenderHelper.getFluidColor(renderInfo.getFluidStack().getFluid());
		BerRenderHelper.submitTintedPart(collector, tankModel, stack, this.sprites, textureResourceTankLevel, light, overlay, color);
	}
}
