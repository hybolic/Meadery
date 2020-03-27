package hybolic.meadery.common.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import hybolic.meadery.common.recipe.Brew;
import hybolic.meadery.common.recipe.BrewSerializer;
import hybolic.meadery.common.recipe.Ferment;
import hybolic.meadery.common.recipe.FermentSerializer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@SuppressWarnings("all")
public class RecipeGrabber<T extends IRecipe<?>> 
{
	private static RecipeGrabber<Ferment> instance_ferm;
	private static RecipeGrabber<Brew> instance_brew;
	
	public static RecipeGrabber<Brew> getBrew()
	{
		if(instance_brew == null)
			instance_brew = new RecipeGrabber<Brew>(BrewSerializer.INSTANCE);
		return instance_brew;
	}
	
	public static RecipeGrabber<Ferment> getFerment()
	{
		if(instance_ferm == null)
			instance_ferm = new RecipeGrabber<Ferment>(FermentSerializer.INSTANCE);
		return instance_ferm;
	}
	
	
    private IRecipeType<T> recipeType;

    public RecipeGrabber(IRecipeType<T> type) {
        this.recipeType = type;
    }

    private static Field recipes;

    static {
    	try {
    		Field temp = ObfuscationReflectionHelper.findField(RecipeManager.class, "field_199522_d");
        	temp.setAccessible(true);

            recipes = temp;
    	}
    	catch(Exception e)
    	{
		Field temp = ObfuscationReflectionHelper.findField(RecipeManager.class, "recipes");
    	temp.setAccessible(true);

        recipes = temp;
    	}
    }
    
    public Map<ResourceLocation, T> getRecipes(RecipeManager manager) {
        Map<IRecipeType<?>, Map<ResourceLocation, T>> recipeTypeMap = null;
        try {
            recipeTypeMap = (Map<IRecipeType<?>, Map<ResourceLocation, T>>) recipes.get(manager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return recipeTypeMap.get(recipeType);
    }
	
    public List<T> getRecipesList(RecipeManager manager) {
        return new ArrayList<T>(getRecipes(manager).values());
    }
    
	public Stream<T> getRecipeStream(RecipeManager manager) {
        return getRecipes(manager).values().stream().map(r -> (T)r);
    }

}
