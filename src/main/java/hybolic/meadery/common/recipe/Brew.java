package hybolic.meadery.common.recipe;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class Brew implements IRecipe<IInventory>
{
	public ResourceLocation id;
	public String name;
	public FermentationIngredient[] ingredients;
	public boolean needs_water;
	
	public Brew(ResourceLocation recipeId, String name, FermentationIngredient[] list, boolean needs_water)
	{
		id = recipeId;
		this.name = name;
		this.ingredients = list;
		this.needs_water = needs_water;
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
		return ItemStack.EMPTY;
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


	public static void write(PacketBuffer buffer, Brew recipe) {
		buffer.writeInt(recipe.ingredients.length);
		for(FermentationIngredient i : recipe.ingredients)
		{
			FermentationIngredient.serialize(buffer, i);
		}
	}
	
	

    @Nullable
    public static List<Brew> getBrewData (RecipeManager manager) {
        
        if (manager != null) {
            
            return manager.getRecipes(BrewSerializer.INSTANCE, null, null);
        }
        
        return null;
    }
    
    public static List<Brew> getBrewList(World world)
    {
    	return getBrewData(world.getRecipeManager());
    }
}
