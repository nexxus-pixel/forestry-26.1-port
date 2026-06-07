package forestry.core.entities;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleSmoke extends Particle {
	public ParticleSmoke(ClientLevel world, double x, double y, double z) {
		super(world, x, y, z, 0, 0, 0);
		this.xd *= 0.8;
		this.yd *= 0.8;
		this.zd *= 0.8;
		this.yd = this.random.nextFloat() * 0.2F + 0.05F;
		this.lifetime = (int) (16.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	protected int getLightCoords(float partialTick) {
		int i = super.getLightCoords(partialTick);
		int skyLight = i >> 16 & 255;
		return 240 | skyLight << 16;
	}

	@Override
	public ParticleRenderType getGroup() {
		return ParticleRenderType.SINGLE_QUADS;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
		}

		float f = (float) this.age / (float) this.lifetime;

		if (this.random.nextFloat() > f * 2) {
			this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
		}

		this.yd -= 0.03D;
		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.999D;
		this.yd *= 0.999D;
		this.zd *= 0.999D;

		if (this.onGround) {
			this.xd *= 0.7;
			this.zd *= 0.7;
		}
	}
}
