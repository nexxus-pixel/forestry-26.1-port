package forestry.api.fuels;

import net.minecraft.world.item.Item;

/**
 * todo data driven
 *
 * @param fuel          Item that is valid fuel for a peat-fired engine.
 * @param powerPerCycle Power produced by this fuel per work cycle.
 * @param burnDuration  Amount of work cycles this item lasts before being consumed.
 */
public record EngineCopperFuel(Item fuel, int powerPerCycle, int burnDuration) {
}
