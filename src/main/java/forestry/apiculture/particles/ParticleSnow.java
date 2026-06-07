package forestry.apiculture.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ParticleSnow extends SingleQuadParticle {
	public static final TextureAtlasSprite[] SPRITES = new TextureAtlasSprite[3];

	public ParticleSnow(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
		super(level, x, y, z, pickSprite(sprites));

		this.quadSize *= 0.5F;
		this.lifetime = (int) (40 / (this.random.nextDouble() * 0.8D + 0.2D));

		this.xd *= 0.01D;
		this.yd *= -0.4D;
		this.zd *= 0.01D;
	}

	private static TextureAtlasSprite pickSprite(SpriteSet sprites) {
		TextureAtlasSprite[] loaded = SPRITES;
		for (TextureAtlasSprite sprite : loaded) {
			if (sprite != null) {
				return loaded[(int) (Math.random() * loaded.length)];
			}
		}
		return sprites.first();
	}

	@Override
	protected Layer getLayer() {
		return Layer.TRANSLUCENT;
	}
}
