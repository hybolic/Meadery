package hybolic.meadery.common.items;

import hybolic.meadery.common.blocks.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class Group {

	


    public static ItemGroup DRINK = new ItemGroup("meadery.ferments")
    		{
				@Override
				public ItemStack createIcon() {
					return ModItems.Yeast_Culture.getDefaultInstance();
				}
    		};
    public static ItemGroup BEES = new ItemGroup("meadery.bees")
    		{
				@Override
				public ItemStack createIcon() {
					return ModItems.HoneyComb.getDefaultInstance();
				}
    		};
    	    
    public static ItemGroup MEADERY = new ItemGroup("meadery.brewery")
    		{
				@Override
				public ItemStack createIcon() {
					return ModBlocks.Barrel.asItem().getDefaultInstance();
				}
    		};
}
