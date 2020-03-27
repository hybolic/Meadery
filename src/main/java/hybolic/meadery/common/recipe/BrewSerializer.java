package hybolic.meadery.common.recipe;

import com.google.gson.JsonObject;

import hybolic.meadery.MeaderyMod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BrewSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>>  implements IRecipeSerializer<Brew>, IRecipeType<Brew> {

	public static BrewSerializer INSTANCE;
	
	public static ResourceLocation this_registry = new ResourceLocation(MeaderyMod.MODID, "brew");
	
	public BrewSerializer()
	{
		super();
		this.setRegistryName(getRegistry());
	}
    
	public static ResourceLocation getRegistry() {
		return this_registry;
	}

	@Override
	public Brew read(ResourceLocation recipeId, JsonObject json)
	{
		MeaderyMod.LOGGER.info("JsonObject reader");
		MeaderyMod.LOGGER.info(json);
		final FermentationIngredient[] list = FermentationIngredient.deserializer(json.getAsJsonArray("ingredients"));
		final String name = json.get("name").getAsString();
		final boolean needs_water = json.get("needs_water").getAsBoolean();
		ItemStack.read(new CompoundNBT());
		final ItemStack result = CraftingHelper.getItemStack(json.getAsJsonObject("result"), true);
		return new Brew(recipeId,name,list,needs_water,result);
	}

	@Override
	public Brew read(ResourceLocation recipeId, PacketBuffer buffer)
	{
		MeaderyMod.LOGGER.info("PacketBuffer reader");
		final FermentationIngredient[] list = FermentationIngredient.deserializer(buffer);
		final String name = buffer.readString();
		final boolean needs_water = buffer.readBoolean();
		final ItemStack item = buffer.readItemStack();
		return new Brew(recipeId,name,list,needs_water,item);
	}

	@Override
	public void write(PacketBuffer buffer, Brew recipe) {
		buffer.writeInt(recipe.ingredients.length);
		for(FermentationIngredient i : recipe.ingredients)
		{
			FermentationIngredient.serialize(buffer, i);
		}
		buffer.writeString(recipe.name);
		buffer.writeBoolean(recipe.needs_water);
		buffer.writeItemStack(recipe.result);
	}
	
}