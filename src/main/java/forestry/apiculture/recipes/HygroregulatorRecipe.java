package forestry.apiculture.recipes;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.recipes.IHygroregulatorRecipe;
import forestry.api.recipes.IForestryRecipe;
import forestry.factory.features.FactoryRecipeTypes;
import forestry.factory.recipes.RecipeSerializers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;

// recipes used by Alveary Hygroregulator
public class HygroregulatorRecipe implements IHygroregulatorRecipe {
	public static final MapCodec<HygroregulatorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		RecipeSerializers.FLUID_STACK_CODEC.fieldOf("liquid").forGetter(HygroregulatorRecipe::getInputFluid),
		Codec.INT.fieldOf("time").forGetter(HygroregulatorRecipe::getRetainTime),
		Codec.BYTE.fieldOf("humidity_steps").forGetter(HygroregulatorRecipe::getHumiditySteps),
		Codec.BYTE.fieldOf("temperature_steps").forGetter(HygroregulatorRecipe::getTemperatureSteps)
	).apply(instance, RecipeSerializers.bindId(HygroregulatorRecipe::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, HygroregulatorRecipe> STREAM_CODEC = StreamCodec.composite(
		RecipeSerializers.FLUID_STACK_STREAM_CODEC, HygroregulatorRecipe::getInputFluid,
		ByteBufCodecs.VAR_INT, HygroregulatorRecipe::getRetainTime,
		ByteBufCodecs.BYTE, HygroregulatorRecipe::getHumiditySteps,
		ByteBufCodecs.BYTE, HygroregulatorRecipe::getTemperatureSteps,
		RecipeSerializers.bindId(HygroregulatorRecipe::new)
	);

	public static final RecipeSerializer<HygroregulatorRecipe> SERIALIZER = RecipeSerializers.of(CODEC, STREAM_CODEC);

	private final Identifier id;
	private final FluidStack liquid;
	private final byte humiditySteps;
	private final byte temperatureSteps;
	private final int retainTime;

	public HygroregulatorRecipe(Identifier id, FluidStack liquid, int retainTime, byte humiditySteps, byte temperatureSteps) {
		Preconditions.checkNotNull(id, "Recipe identifier cannot be null");
		Preconditions.checkNotNull(liquid);
		this.id = id;
		this.liquid = liquid;
		this.retainTime = retainTime;
		this.humiditySteps = humiditySteps;
		this.temperatureSteps = temperatureSteps;
	}

	@Override
	public FluidStack getInputFluid() {
		return this.liquid;
	}

	@Override
	public int getRetainTime() {
		return this.retainTime;
	}

	@Override
	public byte getHumiditySteps() {
		return this.humiditySteps;
	}

	@Override
	public byte getTemperatureSteps() {
		return this.temperatureSteps;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<? extends IForestryRecipe> getSerializer() {
		return FactoryRecipeTypes.HYGROREGULATOR.serializer();
	}

	@Override
	public RecipeType<? extends IForestryRecipe> getType() {
		return FactoryRecipeTypes.HYGROREGULATOR.type();
	}
}
