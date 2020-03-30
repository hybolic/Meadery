package hybolic.meadery.common.recipe;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class Brew implements IRecipe<IInventory>
{
	public ResourceLocation id;
	public String name;
	public FermentationIngredient[] ingredients;
	public boolean needs_water;
	public ItemStack result;
	
	public Brew(ResourceLocation recipeId, String name, FermentationIngredient[] list, boolean needs_water, ItemStack result)
	{
		id = recipeId;
		this.name = name;
		this.ingredients = list;
		this.needs_water = needs_water;
		this.result = result;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return false;
	}
	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return ItemStack.EMPTY;
	}
	@Override
	public boolean canFit(int width, int height) {
		return false;
	}
	@Override
	public ItemStack getRecipeOutput() {
		return result;
	}
	@Override
	public ResourceLocation getId() {
		return id;
	}
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return BrewSerializer.INSTANCE;
	}
	@Override
	public IRecipeType<?> getType() {
		return BrewSerializer.INSTANCE;
	}
	
	//super rough recipe checker
	public boolean equalWithMultiply(Brew current, int bottles)
	{
		int x = 0;
		for(FermentationIngredient in : ingredients)
		{
			boolean found = false;
			int needed = in.count * bottles;
			for(FermentationIngredient other : current.ingredients)
			{
				if(other == null || found)
					;
				else if(in.fermentation_type.equals(other.fermentation_type))
				{
					needed -= other.count;
					if(needed < 0)
					{
						found = true;
						x++;;
					}
				}
			}
			if(!found)
				return false;
		}
		return x == ingredients.length;
	}

	public long sugarCount(List<Ferment> list)
	{
		long count = 0;
		for(int i = 0; i < ingredients.length; i++)
		{
			count += ingredients[i].count;
		}
		return count;
	}
}
