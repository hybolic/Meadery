package hybolic.meadery.common.items;

import hybolic.meadery.MeaderyMod;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class FermentedProduct extends Item {
	private boolean isLiquid = false;
	public FermentedProduct(String id, Food food) {
		super(new Item.Properties().group(BasicItem.MeaderyGroup).food(food));
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
