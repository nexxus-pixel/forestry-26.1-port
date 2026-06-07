package forestry.factory.recipes;

import forestry.core.utils.ItemStackUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.ForestryConstants;
import forestry.api.core.Product;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class RecipeSerializers {
	static final Identifier UNBOUND_ID = ForestryConstants.forestry("unbound");

	public static final Codec<FluidStack> FLUID_STACK_CODEC = Codec.of(
		new Encoder<>() {
			@Override
			public <T> DataResult<T> encode(FluidStack value, DynamicOps<T> ops, T prefix) {
				return DataResult.success(JsonOps.INSTANCE.convertTo(ops, serializeFluid(value)));
			}
		},
		new Decoder<>() {
			@Override
			public <T> DataResult<Pair<FluidStack, T>> decode(DynamicOps<T> ops, T input) {
				JsonElement json = ops.convertTo(JsonOps.INSTANCE, input);
				if (json instanceof JsonObject object) {
					return DataResult.success(Pair.of(deserializeFluid(object), input));
				}
				return DataResult.error(() -> "Expected JSON object for FluidStack");
			}
		}
	);

	public static final Codec<Fluid> FLUID_CODEC = Identifier.CODEC.flatXmap(
		id -> Optional.ofNullable(ForgeRegistries.FLUIDS.getValue(id))
			.map(DataResult::success)
			.orElseGet(() -> DataResult.error(() -> "Unknown fluid: " + id)),
		fluid -> DataResult.success(ForgeRegistries.FLUIDS.getKey(fluid))
	);

	public static final StreamCodec<FriendlyByteBuf, FluidStack> FLUID_STACK_STREAM_CODEC = StreamCodec.of(
		(buf, stack) -> buf.writeNbt(stack.writeToNBT(new CompoundTag())),
		buf -> FluidStack.loadFluidStackFromNBT(buf.readNbt())
	);

	public static final StreamCodec<FriendlyByteBuf, Fluid> FLUID_STREAM_CODEC = StreamCodec.of(
		(buf, fluid) -> buf.writeIdentifier(ForgeRegistries.FLUIDS.getKey(fluid)),
		buf -> ForgeRegistries.FLUIDS.getValue(buf.readIdentifier())
	);

	public static final StreamCodec<FriendlyByteBuf, Product> PRODUCT_STREAM_CODEC = StreamCodec.of(
		Product::toNetwork,
		Product::fromNetwork
	);

	public static final StreamCodec<FriendlyByteBuf, List<Product>> PRODUCT_LIST_STREAM_CODEC = listStreamCodec(PRODUCT_STREAM_CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENT_LIST_STREAM_CODEC = listStreamCodec(Ingredient.CONTENTS_STREAM_CODEC);

	public static final MapCodec<CraftingRecipe> CRAFTING_RECIPE_CODEC = ShapedRecipe.MAP_CODEC.xmap(recipe -> recipe, recipe -> (ShapedRecipe) recipe);

	public static final StreamCodec<RegistryFriendlyByteBuf, CraftingRecipe> CRAFTING_RECIPE_STREAM_CODEC = Recipe.STREAM_CODEC.map(
		recipe -> {
			if (recipe instanceof CraftingRecipe crafting) {
				return crafting;
			}
			throw new IllegalStateException("Expected a crafting recipe");
		},
		recipe -> recipe
	);

	public static final MapCodec<ShapedRecipe> SHAPED_RECIPE_CODEC = ShapedRecipe.MAP_CODEC;

	public static final StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> SHAPED_RECIPE_STREAM_CODEC = ShapedRecipe.STREAM_CODEC;

	public static <T extends Recipe<?>> RecipeSerializer<T> of(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
		return new RecipeSerializer<>(codec, streamCodec);
	}

	public static <B extends FriendlyByteBuf, T> StreamCodec<B, List<T>> listStreamCodec(StreamCodec<? super B, T> elementCodec) {
		return StreamCodec.of(
			(buf, list) -> {
				buf.writeVarInt(list.size());
				for (T element : list) {
					elementCodec.encode(buf, element);
				}
			},
			buf -> {
				int size = buf.readVarInt();
				ArrayList<T> list = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					list.add(elementCodec.decode(buf));
				}
				return list;
			}
		);
	}

	public static <B extends FriendlyByteBuf> StreamCodec<B, Optional<FluidStack>> optionalFluidStreamCodec() {
		return StreamCodec.of(
			(buf, optional) -> {
				buf.writeBoolean(optional.isPresent());
				optional.ifPresent(stack -> FLUID_STACK_STREAM_CODEC.encode(buf, stack));
			},
			buf -> buf.readBoolean()
				? Optional.of(FLUID_STACK_STREAM_CODEC.decode(buf))
				: Optional.empty()
		);
	}

	public static <B extends RegistryFriendlyByteBuf> StreamCodec<B, Optional<ItemStack>> optionalItemStreamCodec() {
		return StreamCodec.of(
			(buf, optional) -> {
				buf.writeBoolean(optional.isPresent());
				optional.ifPresent(stack -> ItemStack.STREAM_CODEC.encode(buf, stack));
			},
			buf -> buf.readBoolean()
				? Optional.of(ItemStack.STREAM_CODEC.decode(buf))
				: Optional.empty()
		);
	}

	static <E> void write(FriendlyByteBuf buffer, List<E> list, BiConsumer<FriendlyByteBuf, E> consumer) {
		buffer.writeVarInt(list.size());

		for (E e : list) {
			consumer.accept(buffer, e);
		}
	}

	static <E> List<E> read(FriendlyByteBuf buffer, Function<FriendlyByteBuf, E> reader) {
		int size = buffer.readVarInt();

		ArrayList<E> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			list.add(reader.apply(buffer));
		}

		return list;
	}

	public static FluidStack deserializeFluid(JsonObject object) {
		return FluidStack.loadFluidStackFromNBT((CompoundTag) com.mojang.serialization.Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, object));
	}

	public static JsonObject serializeFluid(FluidStack fluid) {
		return (JsonObject) com.mojang.serialization.Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, fluid.writeToNBT(new CompoundTag()));
	}

	public static ItemStack item(JsonObject object) {
		return ItemStackUtil.loadFromTag((CompoundTag) com.mojang.serialization.Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, object));
	}

	public static JsonObject item(ItemStack stack) {
		CompoundTag tag = new CompoundTag();
		ItemStackUtil.saveToTag(stack, tag);
		return (JsonObject) com.mojang.serialization.Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, tag);
	}

	public static Ingredient deserialize(JsonElement resource) {
		if (resource.isJsonArray() && resource.getAsJsonArray().isEmpty()) {
			return Ingredient.of(java.util.stream.Stream.empty());
		}

		return Ingredient.CODEC.parse(JsonOps.INSTANCE, resource).result().orElseGet(() -> Ingredient.of(java.util.stream.Stream.empty()));
	}

	private RecipeSerializers() {
	}

	public static <T1, T2, T3, R> Function3<T1, T2, T3, R> bindId(Function4<Identifier, T1, T2, T3, R> factory) {
		return (a, b, c) -> factory.apply(UNBOUND_ID, a, b, c);
	}

	public static <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> bindId(Function5<Identifier, T1, T2, T3, T4, R> factory) {
		return (a, b, c, d) -> factory.apply(UNBOUND_ID, a, b, c, d);
	}

	public static <T1, T2, T3, T4, T5, R> Function5<T1, T2, T3, T4, T5, R> bindId(Function6<Identifier, T1, T2, T3, T4, T5, R> factory) {
		return (a, b, c, d, e) -> factory.apply(UNBOUND_ID, a, b, c, d, e);
	}
}
