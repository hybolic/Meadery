package hybolic.meadery.common.items;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nullable;

import hybolic.meadery.MeaderyMod;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BasicItem extends Item {

	public BasicItem(String id)
	{
		this(id,Group.MEADERY);
	}
	public BasicItem(String id, ItemGroup group)
	{
		super(new Item.Properties().group(group));
		this.setRegistryName(MeaderyMod.MODID, id);
	}
    public static class ItemBlock extends BlockItem
    {

		public ItemBlock(String id, Block blockIn) {
			this(id,blockIn,Group.MEADERY);
		}

		public ItemBlock(String id, Block blockIn, ItemGroup group) {
			super(blockIn,new Item.Properties().group(group));
			this.setRegistryName(MeaderyMod.MODID, id);
		}
    	
    }
    public static class TickingItemBlock extends ItemBlock
    {

		public TickingItemBlock(String id, Block blockIn, ItemGroup group) {
			super(id, blockIn, group);
		}

		public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
			if(stack.hasTag() && stack.getChildTag("BlockEntityTag") != null && stack.getChildTag("BlockEntityTag").get("age") != null)
			{
				if((worldIn.getGameTime() % 20 == 0))
				{
					ListNBT list = ((ListNBT)stack.getChildTag("BlockEntityTag").getCompound("items").get("Items"));
					boolean found_yeast = false;
					for(INBT inbt : list)
					{
						CompoundNBT tag = (CompoundNBT)inbt;
						if(tag.getByte("Slot") == (byte)4)
							if(tag.getString("id").equalsIgnoreCase("meadery:yeast_culture") && tag.getByte("Count") > (byte)0)
							{
								found_yeast = true;
								break;
							}
					}
					if(found_yeast)
						stack.getChildTag("BlockEntityTag").putLong("age", stack.getChildTag("BlockEntityTag").getLong("age") + 1);
				}
			}
		}
		
	   @OnlyIn(Dist.CLIENT)
	   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	   {
			if(stack.hasTag() && stack.getChildTag("BlockEntityTag") != null && stack.getChildTag("BlockEntityTag").get("age") != null)
			{
				DecimalFormat df2 = new DecimalFormat("#.#");
				if(stack.getChildTag("BlockEntityTag").getLong("age") >= 430000)
				{
					tooltip.add(new TranslationTextComponent("meadery.text.age_years", df2.format(stack.getChildTag("BlockEntityTag").getLong("age") / 438000f)));
				}
				else if(stack.getChildTag("BlockEntityTag").getLong("age") >= 30000)
				{
					tooltip.add(new TranslationTextComponent("meadery.text.age_months",
							df2.format(stack.getChildTag("BlockEntityTag").getLong("age") / 36000f)));
				}
				else if(stack.getChildTag("BlockEntityTag").getLong("age") >= 8000)
				{
					tooltip.add(new TranslationTextComponent("meadery.text.age_weeks",
							df2.format(stack.getChildTag("BlockEntityTag").getLong("age") / 8400f)));
				}
				else if(stack.getChildTag("BlockEntityTag").getLong("age") >= 1000){
					tooltip.add(new TranslationTextComponent("meadery.text.age_days", df2.format(stack.getChildTag("BlockEntityTag").getLong("age") / 1200f)));
				}
				else
				{
					tooltip.add(new TranslationTextComponent("meadery.text.age_hours", df2.format(stack.getChildTag("BlockEntityTag").getLong("age") / 50f)));
				}
				if(Screen.hasShiftDown())
				{
					tooltip.add(new TranslationTextComponent("meadery.text.age_detail"));
				}
			}
	   }
    }
}
