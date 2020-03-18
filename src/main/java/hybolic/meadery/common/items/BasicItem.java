package hybolic.meadery.common.items;

import hybolic.meadery.MeaderyMod;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BasicItem extends Item {

	public BasicItem(String id)
	{
		super(new Item.Properties().group(MeaderyGroup));
		this.setRegistryName(MeaderyMod.MODID, id);
	}
	
	
    
    public static ItemGroup MeaderyGroup = new ItemGroup("meadery")
    		{
				@Override
				public ItemStack createIcon() {
					return Items.APPLE.getDefaultInstance();
					//return Mead;
				}
    		};
    public static class ItemBlock extends BlockItem
    {

		public ItemBlock(String id, Block blockIn) {
			super(blockIn,new Item.Properties().group(MeaderyGroup));
			this.setRegistryName(MeaderyMod.MODID, id);
		}
    	
    }
}
