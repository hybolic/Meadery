package hybolic.meadery.common;

import java.util.ArrayList;
import java.util.List;

import hybolic.meadery.common.items.ModItems;
import net.minecraft.item.ItemStack;

public class CulturableFoods
{
	private static List<ItemStack> list = new ArrayList<ItemStack>();
	
	public static boolean isCulturableFood(ItemStack stack)
	{
		if(list.size() > 0)
			for (ItemStack list_stack : list) {
				if(ItemStack.areItemsEqual(list_stack, stack))
					return true;
			}
		return stack.getItem() == ModItems.Honey;
	}
}
