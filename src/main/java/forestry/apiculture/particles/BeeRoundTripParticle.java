package forestry.apiculture.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class BeeRoundTripParticle extends SingleQuadParticle {
	private final Vec3 origin;
	private final BlockPos destination;

	public BeeRoundTripParticle(ClientLevel world, double x, double y, double z, BlockPos destination, int color, SpriteSet sprites) {
		super(world, x, y, z, sprites.first());
		this.origin = new Vec3(x, y, z);

		this.destination = destination;
		this.xd = (destination.getX() + 0.5 - this.x) * 0.02 + 0.1 * this.random.nextFloat();
		this.yd = (destination.getY() + 0.5 - this.y) * 0.015 + 0.1 * this.random.nextFloat();
		this.zd = (destination.getZ() + 0.5 - this.z) * 0.02 + 0.1 * this.random.nextFloat();

		this.setColor((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F);

		this.setSize(0.1F, 0.1F);
		this.quadSize *= 0.2F;
		this.lifetime = (int) (80.0D / (Math.random() * 0.8D + 0.2D));

		this.xd *= 0.9D;
		this.yd *= 0.015D;
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
			this.xd = (this.origin.x - this.x) * 0.03 + 0.1 * this.random.nextFloat();
			this.yd = (this.origin.y - this.y) * 0.03 + 0.1 * this.random.nextFloat();
			this.zd = (this.origin.z - this.z) * 0.03 + 0.1 * this.random.nextFloat();
		}

		if (this.age < this.lifetime * 0.25) {
			this.xd *= 0.92 + 0.2D * this.random.nextFloat();
			this.yd = (this.yd + 0.3 * (-0.5 + this.random.nextFloat())) / 2;
			this.zd *= 0.92 + 0.2D * this.random.nextFloat();
		} else if (this.age < this.lifetime * 0.5) {
			this.xd = (this.destination.getX() + 0.5 - this.x) * 0.03;
			this.yd = (this.destination.getY() + 0.5 - this.y) * 0.1;
			this.yd = (this.yd + 0.2 * (-0.5 + this.random.nextFloat())) / 2;
			this.zd = (this.destination.getZ() + 0.5 - this.z) * 0.03;
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
