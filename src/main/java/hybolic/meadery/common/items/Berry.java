package hybolic.meadery.common.items;

import hybolic.meadery.MeaderyMod;
import hybolic.meadery.common.blocks.BerryBush;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class Berry extends BlockNamedItem {

	public Berry(BerryBush blockIn, String id, ItemGroup group, Food food) {
		super(blockIn, new Item.Properties().group(group).food(food));
		this.setRegistryName(MeaderyMod.MODID, id);
		blockIn.item = this;
	}

}
