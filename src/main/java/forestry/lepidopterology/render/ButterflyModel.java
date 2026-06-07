package forestry.lepidopterology.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class ButterflyModel extends EntityModel<ButterflyRenderState> {
	private final ModelPart leftWing;
	private final ModelPart rightWing;

	public ButterflyModel(ModelPart root) {
		super(root);
		this.leftWing = root.getChild("left_wing");
		this.rightWing = root.getChild("right_wing");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();

		root.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(40, 0)
				.addBox(0f, 0f, -4f, 1, 1, 6),
			PartPose.rotation(0f, 0f, 0.7853982f));
		root.addOrReplaceChild("left_wing", CubeListBuilder.create()
				.texOffs(0, 14)
				.addBox(0f, 0f, -6f, 7, 1, 13),
			PartPose.offset(0.5f, 0.5f, 0f));
		root.addOrReplaceChild("right_wing", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-7f, 0f, -6f, 7, 1, 13),
			PartPose.offset(-0.5f, 0.5f, 0f));
		root.addOrReplaceChild("left_eye", CubeListBuilder.create()
				.texOffs(40, 7)
				.addBox(0f, 0f, 0f, 1, 1, 1),
			PartPose.offset(0.1f, -0.5f, -4.5f));
		root.addOrReplaceChild("right_eye", CubeListBuilder.create()
				.texOffs(40, 9)
				.addBox(0f, 0f, 0f, 1, 1, 1),
			PartPose.offset(-1.1f, -0.5f, -4.5f));

		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public void setupAnim(ButterflyRenderState state) {
		float wingAngle = Mth.cos(state.ageInTicks * 1.3f) * Mth.PI * 0.25f;
		this.leftWing.zRot = wingAngle;
		this.rightWing.zRot = -wingAngle;
	}
}
