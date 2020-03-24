package hybolic.meadery.common.items;

import hybolic.meadery.MeaderyMod;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class FermentedProduct extends Item {
	
	private boolean isLiquid = false;
	
	public FermentedProduct(String id, Food food) {
		this(id,food,Group.DRINK);
	}
	
	public FermentedProduct(String id, Food food, ItemGroup group)
	{
		super(new Item.Properties().group(group).food(food));
		this.setRegistryName(MeaderyMod.MODID, id);
	}
	
	public Item setIsLiquid() {
		isLiquid = true;
		return this;
	}
	
	public boolean isLiquid()
	{
		return isLiquid;
	}

}
