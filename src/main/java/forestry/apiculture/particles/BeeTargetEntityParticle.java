package forestry.apiculture.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class BeeTargetEntityParticle extends SingleQuadParticle {
	private final Vec3 origin;
	private final Entity entity;

	public BeeTargetEntityParticle(ClientLevel world, double x, double y, double z, Entity entity, int color, SpriteSet sprites) {
		super(world, x, y, z, sprites.first());
		this.origin = new Vec3(x, y, z);
		this.entity = entity;

		this.xd = (entity.getX() - this.x) * 0.015;
		this.yd = (entity.getY() + 1.62F - this.y) * 0.015;
		this.zd = (entity.getZ() - this.z) * 0.015;

		this.setColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F);

		this.setSize(0.1F, 0.1F);
		this.quadSize *= 0.2F;
		this.lifetime = (int) (80.0D / (Math.random() * 0.8D + 0.2D));

		this.xd *= 0.9D;
		this.yd *= 0.9D;
		this.zd *= 0.9D;
	}

	@Override
	protected Layer getLayer() {
		return Layer.OPAQUE;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.move(this.xd, this.yd, this.zd);

		if (this.age == this.lifetime / 2) {
			this.xd = (this.origin.x - this.x) * 0.03;
			this.yd = (this.origin.y - this.y) * 0.03;
			this.zd = (this.origin.z - this.z) * 0.03;
		}

		if (this.age < this.lifetime * 0.5) {
			this.xd = (this.entity.getX() - this.x) * 0.09;
			this.xd = (this.xd + 0.2 * (-0.5 + this.random.nextFloat())) / 2;
			this.yd = (this.entity.getY() + 1.62F - this.y) * 0.03;
			this.yd = (this.yd + 0.4 * (-0.5 + this.random.nextFloat())) / 4;
			this.zd = (this.entity.getZ() - this.z) * 0.09;
			this.zd = (this.zd + 0.2 * (-0.5 + this.random.nextFloat())) / 2;
		} else if (this.age < this.lifetime * 0.75) {
			this.xd *= 0.95;
			this.yd = (this.origin.y - this.y) * 0.03;
			this.yd = (this.yd + 0.2 * (-0.5 + this.random.nextFloat())) / 2;
			this.zd *= 0.95;
		} else {
			this.xd = (this.origin.x - this.x) * 0.03;
			this.yd = (this.origin.y - this.y) * 0.03;
			this.yd = (this.yd + 0.2 * (-0.5 + this.random.nextFloat())) / 2;
			this.zd = (this.origin.z - this.z) * 0.03;
		}

		if (this.age++ >= this.lifetime) {
			this.remove();
		}
	}

	@Override
	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().move(x, y, z));
		this.setLocationFromBoundingbox();
	}

	@Override
	protected int getLightCoords(float partialTick) {
		return 15728880;
	}
}
