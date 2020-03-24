package hybolic.meadery.common.recipe;

import com.google.gson.JsonObject;

import hybolic.meadery.MeaderyMod;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BrewSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>>  implements IRecipeSerializer<Brew>, IRecipeType<Brew> {

	public static final BrewSerializer INSTANCE = new BrewSerializer();
	
	public static ResourceLocation this_registry = new ResourceLocation(MeaderyMod.MODID, "brew");

    
	public static ResourceLocation getRegistry() {
		return this_registry;
	}

	@Override
	public Brew read(ResourceLocation recipeId, JsonObject json)
	{
		final FermentationIngredient[] list = FermentationIngredient.deserializer(json.getAsJsonObject("ingredients"));
		final String name = json.get("name").getAsString();
		final boolean needs_water = json.get("needs_water").getAsBoolean();
		return new Brew(recipeId,name,list,needs_water);
	}

	@Override
	public Brew read(ResourceLocation recipeId, PacketBuffer buffer)
	{
		final FermentationIngredient[] list = FermentationIngredient.deserializer(buffer);
		final String name = buffer.readString();
		final boolean needs_water = buffer.readBoolean();
		return new Brew(recipeId,name,list,needs_water);
	}

	@Override
	public void write(PacketBuffer buffer, Brew recipe) {
		Brew.write(buffer, recipe);
	}
	
}