package hybolic.meadery.common.items;

import net.minecraft.item.Food;
import net.minecraftforge.registries.ObjectHolder;

public class ModItems {


    @ObjectHolder("meadery:honey_comb")
	public static BasicItem HoneyComb;
    @ObjectHolder("meadery:honey")
	public static HoneyBottleItem Honey;
    
    @ObjectHolder("meadery:blueberry")
	public static Berry blueberry;
    @ObjectHolder("meadery:blackberry")
	public static Berry blackberry;
    @ObjectHolder("meadery:raspberry")
	public static Berry raspberry;

    @ObjectHolder("meadery:yeast_culture")
	public static BasicItem Yeast_Culture;

    @ObjectHolder("meadery:mead")
	public static FermentedProduct mead;
    @ObjectHolder("meadery:wine")
	public static FermentedProduct wine;
    @ObjectHolder("meadery:cider")
	public static FermentedProduct cider;
    @ObjectHolder("meadery:beer")
	public static FermentedProduct beer;
    @ObjectHolder("meadery:sake")
	public static FermentedProduct sake;
    @ObjectHolder("meadery:dragon_liquor")
	public static FermentedProduct dragon_liquor;
    
    public static Food food_Mead = (new Food.Builder()).hunger(5).saturation(.3f).build();
    public static Food food_Wine = (new Food.Builder()).hunger(5).saturation(.3f).build();
    public static Food food_Cider = (new Food.Builder()).hunger(5).saturation(.3f).build();
    public static Food food_Beer = (new Food.Builder()).hunger(5).saturation(.3f).build();
    public static Food food_Sake = (new Food.Builder()).hunger(5).saturation(.3f).build();
    public static Food food_Dragon_Liquor = (new Food.Builder()).hunger(5).saturation(.3f).build();

	public static Food food_Rice = (new Food.Builder()).hunger(5).saturation(.3f).build();
	public static Food food_Other = (new Food.Builder()).hunger(5).saturation(.3f).build();
}
