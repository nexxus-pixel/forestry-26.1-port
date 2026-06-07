package forestry.api.fuels;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * todo data driven
 *
 * @param item     Rain substrate capable of activating the rainmaker.
 * @param duration Duration of the rain shower triggered by this substrate in Minecraft ticks.
 * @param speed    Speed of activation sequence triggered.
 * @param reverse  Whether the substrate stops rain instead of creating rain.
 */
public record RainSubstrate(Item item, int duration, float speed, boolean reverse) {
	public RainSubstrate(Item item, float speed) {
		this(item, 0, speed, true);
	}

	public RainSubstrate(Item item, int duration, float speed) {
		this(item, duration, speed, false);
	}

	public ItemStack itemStack() {
		return new ItemStack(this.item);
	}
}
