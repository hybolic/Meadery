package hybolic.meadery.common.items;

import net.minecraft.item.Food;
import net.minecraftforge.registries.ObjectHolder;

public class ModItems {


    @ObjectHolder("meadery:honey_comb")
	public static BasicItem HoneyComb;
    @ObjectHolder("meadery:honey")
	public static BasicItem Honey;
    
    @ObjectHolder("meadery:yeast_culture")
	public static BasicItem Yeast_Culture;
    
    @ObjectHolder("meadery:mead")
	public static FermentedProduct Mead;
    public static Food food_mead = (new Food.Builder()).hunger(5).saturation(.3f).build();
    @ObjectHolder("meadery:wine")
	public static FermentedProduct Wine;
    public static Food food_Wine = (new Food.Builder()).hunger(5).saturation(.3f).build();
    @ObjectHolder("meadery:cider")
	public static FermentedProduct Cider;
    public static Food food_Cider = (new Food.Builder()).hunger(5).saturation(.3f).build();
    @ObjectHolder("meadery:beer")
	public static FermentedProduct Beer;
    public static Food food_Beer = (new Food.Builder()).hunger(5).saturation(.3f).build();
    @ObjectHolder("meadery:sake")
	public static FermentedProduct Sake;
    public static Food food_Sake = (new Food.Builder()).hunger(5).saturation(.3f).build();
    @ObjectHolder("meadery:dargon_liquor")
	public static FermentedProduct Dargon_Liquor;
    public static Food food_Dargon_Liquor = (new Food.Builder()).hunger(5).saturation(.3f).build();
}
