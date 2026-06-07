package forestry.core.utils;

import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.model.SimpleModelState;

/**
 * Util methods used at the installation of the game or at the reloading or baking of resources like models or
 * textures.
 */
public class ResourceUtil {

	public static TextureAtlasSprite getMissingTexture() {
		return getSprite(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
	}

	public static TextureAtlasSprite getSprite(Identifier atlas, Identifier sprite) {
		return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(atlas).getSprite(sprite);
	}

	public static TextureAtlasSprite getBlockSprite(Identifier location) {
		return getSprite(TextureAtlas.LOCATION_BLOCKS, location);
	}

	public static TextureAtlasSprite getBlockSprite(String location) {
		return getBlockSprite(Identifier.parse(location));
	}

	public static SimpleModelState loadTransform(Identifier location) {
		// todo
		return new SimpleModelState(Transformation.IDENTITY);
	}
}
