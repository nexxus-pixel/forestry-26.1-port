package forestry.core.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import forestry.api.ForestryConstants;
import forestry.core.config.Constants;
import forestry.core.tiles.TemperatureState;
import forestry.energy.blocks.EngineBlock;
import forestry.energy.tiles.EngineBlockEntity;
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
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RenderEngine implements BlockEntityRenderer<EngineBlockEntity, EngineRenderState> {
	private static final float[] ANGLE_MAP = new float[6];

	private enum Textures {
		BASE, PISTON, EXTENSION, TRUNK_HIGHEST, TRUNK_HIGHER, TRUNK_HIGH, TRUNK_MEDIUM, TRUNK_LOW
	}

	private final SpriteGetter sprites;
	private final Identifier[] textures;
	private final ModelPart boiler;
	private final ModelPart trunk;
	private final ModelPart piston;
	private final ModelPart extension;

	static {
		ANGLE_MAP[Direction.EAST.ordinal()] = -Mth.HALF_PI;
		ANGLE_MAP[Direction.NORTH.ordinal()] = -Mth.HALF_PI;
		ANGLE_MAP[Direction.WEST.ordinal()] = Mth.HALF_PI;
		ANGLE_MAP[Direction.SOUTH.ordinal()] = Mth.HALF_PI;
		ANGLE_MAP[Direction.UP.ordinal()] = 0;
		ANGLE_MAP[Direction.DOWN.ordinal()] = Mth.PI;
	}

	public RenderEngine(BlockEntityRendererProvider.Context ctx, String baseTexture) {
		this.sprites = ctx.sprites();
		ModelPart root = ctx.bakeLayer(ForestryModelLayers.ENGINE_LAYER);

		this.boiler = root.getChild("boiler");
		this.trunk = root.getChild("trunk");
		this.piston = root.getChild("piston");
		this.extension = root.getChild("extension");

		this.textures = new Identifier[]{
			ForestryConstants.forestry(baseTexture + "base.png"),
			ForestryConstants.forestry(baseTexture + "piston.png"),
			ForestryConstants.forestry(baseTexture + "extension.png"),
			ForestryConstants.forestry(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_highest.png"),
			ForestryConstants.forestry(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_higher.png"),
			ForestryConstants.forestry(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_high.png"),
			ForestryConstants.forestry(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_medium.png"),
			ForestryConstants.forestry(Constants.TEXTURE_PATH_BLOCK + "/engine_trunk_low.png"),
		};
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		root.addOrReplaceChild("boiler", CubeListBuilder.create().texOffs(0, 0)
			.addBox(0, 0, 0, 16, 6, 16), PartPose.offset(0, 0, 0));
		root.addOrReplaceChild("trunk", CubeListBuilder.create().texOffs(0, 0)
			.addBox(0, 0, 0, 8, 12, 8), PartPose.offset(4, 4, 4));
		root.addOrReplaceChild("piston", CubeListBuilder.create().texOffs(0, 0)
			.addBox(0, 0, 0, 12, 4, 12), PartPose.offset(2, 6, 2));
		root.addOrReplaceChild("extension", CubeListBuilder.create().texOffs(0, 0)
			.addBox(0, 0, 0, 10, 2, 10), PartPose.offset(3, 5, 3));

		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public EngineRenderState createRenderState() {
		return new EngineRenderState();
	}

	@Override
	public void extractRenderState(EngineBlockEntity engine, EngineRenderState state, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(engine, state, partialTick, cameraPos, crumblingOverlay);
		state.orientation = engine.getBlockState().getValue(EngineBlock.VERTICAL_FACING);
		state.temperature = engine.hasLevel() ? engine.getTemperatureState() : TemperatureState.COOL;
		state.pistonStep = getPistonStep(engine, partialTick);
	}

	@Override
	public void submit(EngineRenderState state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState cameraState) {
		int light = state.lightCoords;
		int overlay = 0;

		stack.pushPose();

		stack.translate(0.5, 0.5, 0.5);
		Direction orientation = state.orientation;
		switch (orientation) {
			case EAST, WEST, DOWN -> stack.mulPose(Axis.ZP.rotation(ANGLE_MAP[orientation.ordinal()]));
			default -> stack.mulPose(Axis.XP.rotation(ANGLE_MAP[orientation.ordinal()]));
		}
		stack.translate(-0.5, -0.5, -0.5);

		BerRenderHelper.submitPart(collector, this.boiler, stack, this.sprites, this.textures[Textures.BASE.ordinal()], light, overlay);

		float step = state.pistonStep;
		float tfactor = step / 16;
		stack.translate(0, tfactor, 0);
		BerRenderHelper.submitPart(collector, this.piston, stack, this.sprites, this.textures[Textures.PISTON.ordinal()], light, overlay);
		stack.translate(0, -tfactor, 0);

		Identifier texture = switch (state.temperature) {
			case OVERHEATING -> this.textures[Textures.TRUNK_HIGHEST.ordinal()];
			case RUNNING_HOT -> this.textures[Textures.TRUNK_HIGHER.ordinal()];
			case OPERATING_TEMPERATURE -> this.textures[Textures.TRUNK_HIGH.ordinal()];
			case WARMED_UP -> this.textures[Textures.TRUNK_MEDIUM.ordinal()];
			default -> this.textures[Textures.TRUNK_LOW.ordinal()];
		};
		BerRenderHelper.submitPart(collector, this.trunk, stack, this.sprites, texture, light, overlay);

		float chamberf = 2F / 16F;
		if (step > 0) {
			for (int i = 0; i <= step + 2; i += 2) {
				BerRenderHelper.submitPart(collector, this.extension, stack, this.sprites, this.textures[Textures.EXTENSION.ordinal()], light, overlay);
				stack.translate(0, chamberf, 0);
			}
		}

		stack.popPose();
	}

	private static float getPistonStep(EngineBlockEntity engine, float partialTick) {
		float progress;

		if (engine.hasLevel()) {
			progress = engine.progress;
			if (engine.stagePiston != 0) {
				float smoothing = engine.pistonSpeedServer * partialTick;
				progress = (progress + smoothing);
			}
		} else {
			progress = 0.25f;
		}

		if (progress > 0.5f) {
			return 6f - (progress - 0.5f) * 2f * 6F;
		} else {
			return progress * 2f * 6f;
		}
	}
}
