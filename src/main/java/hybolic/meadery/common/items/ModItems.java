package hybolic.meadery.common.items;

import net.minecraft.item.Food;
import net.minecraftforge.registries.ObjectHolder;

public class ModItems {


    @ObjectHolder("meadery:honey_comb")
	public static BasicItem HoneyComb;
    @ObjectHolder("meadery:honey")
	public static HoneyBottleItem Honey;
    
    @ObjectHolder("meadery:yeast_culture")
	public static BasicItem Yeast_Culture;
    
    public static Food food_Mead = (new Food.Builder()).hunger(5).saturation(.3f).build();
    public static Food food_Wine = (new Food.Builder()).hunger(5).saturation(.3f).build();
    public static Food food_Cider = (new Food.Builder()).hunger(5).saturation(.3f).build();
    public static Food food_Beer = (new Food.Builder()).hunger(5).saturation(.3f).build();
    public static Food food_Sake = (new Food.Builder()).hunger(5).saturation(.3f).build();
    public static Food food_Dargon_Liquor = (new Food.Builder()).hunger(5).saturation(.3f).build();

	public static Food food_Rice = (new Food.Builder()).hunger(5).saturation(.3f).build();
	public static Food food_Other = (new Food.Builder()).hunger(5).saturation(.3f).build();
}
