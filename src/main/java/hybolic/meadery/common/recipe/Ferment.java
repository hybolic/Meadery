package hybolic.meadery.common.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import hybolic.meadery.common.util.RecipeGrabber;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class Ferment implements IRecipe<IInventory>
{
	public ResourceLocation id;
	public FermentationType fermentation_type;
	public Ingredient ingredient;
	public int sugar;
	
	public Ferment(ResourceLocation id, FermentationType fermentation_type, Ingredient ingredient, int sugar)
	{
		this.id = id;
		this.fermentation_type = fermentation_type;
		this.ingredient = ingredient;
		this.sugar = sugar;
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
		return FermentSerializer.INSTANCE;
	}
	@Override
	public IRecipeType<?> getType() {
		return FermentSerializer.INSTANCE;
	}

    @Nullable
    public static List<Ferment> getFermentData (RecipeManager manager) {
    	List<Ferment> list = new ArrayList<Ferment>();
        if (manager != null) {
        	for(IRecipe<?> s : RecipeGrabber.getFerment().getRecipes(manager).values())
        	{
        		list.add((Ferment) s);
        	}
        }
        
        return list;
    }
    
    public static List<Ferment> getFermList(World world)
    {
    	return getFermentData(world.getRecipeManager());
    }
    
    public static Ferment getFromItem(World world, ItemStack item)
    {
    	List<Ferment> list = getFermList(world);
    	for(Ferment ferm : list)
    	{
    		if(ferm.ingredient.test(item))
    			return ferm;
    	}
    	return null;
    }
}
