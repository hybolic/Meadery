package hybolic.meadery.common.recipe;

import com.google.gson.JsonObject;

import hybolic.meadery.MeaderyMod;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FermentSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>>  implements IRecipeSerializer<Ferment>, IRecipeType<Ferment> {

	public static FermentSerializer INSTANCE;

	public static ResourceLocation this_registry = new ResourceLocation(MeaderyMod.MODID, "fermentation_ingredient");

	public FermentSerializer()
	{
		super();
		this.setRegistryName(getRegistry());
	}
    
	public static ResourceLocation getRegistry() {
		return this_registry;
	}
	public void write(PacketBuffer buffer, Ferment recipe) {
		recipe.ingredient.write(buffer);
		buffer.writeString(recipe.fermentation_type.getName());
		buffer.writeInt(recipe.sugar);
	}

	@Override
	public Ferment read(ResourceLocation recipeId, JsonObject json) {
		final Ingredient ing = Ingredient.deserialize(json.get("ingredient"));
		final int sugar = json.get("sugar").getAsInt();
		final FermentationType type = FermentationType.valueOf_(json.get("fermentation_type").getAsString());
		return new Ferment(recipeId,type,ing,sugar);
	}

	@Override
	public Ferment read(ResourceLocation recipeId, PacketBuffer buffer) {
		final Ingredient ing = Ingredient.read(buffer);
		final FermentationType type = FermentationType.valueOf_(buffer.readString());
		final int sugar = buffer.readInt();
		return new Ferment(recipeId,type,ing,sugar);
	}
}
