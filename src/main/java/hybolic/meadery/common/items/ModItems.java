package hybolic.meadery.common.items;

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
    @ObjectHolder("meadery:cider")
	public static FermentedProduct Cider;
    @ObjectHolder("meadery:beer")
	public static FermentedProduct Beer;
    @ObjectHolder("meadery:sake")
	public static FermentedProduct Sake;
    @ObjectHolder("meadery:dargon_liquor")
	public static FermentedProduct Dargon_Liquor;
}
