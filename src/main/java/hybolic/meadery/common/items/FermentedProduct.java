package hybolic.meadery.common.items;

import java.util.List;

import javax.annotation.Nullable;

import hybolic.meadery.MeaderyMod;
import hybolic.meadery.common.damage.CustomDamage;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FermentedProduct extends Item {

	private boolean isLiquid = false;
	private boolean isFood = false;

	public FermentedProduct(String id, Food food) {
		this(id, food, Group.DRINK);
	}

	public FermentedProduct(String id, Food food, ItemGroup group) {
		super(new Item.Properties().group(group).food(food));
		this.setRegistryName(MeaderyMod.MODID, id);
	}

	public Item setIsLiquid() {
		isLiquid = true;
		return this;
	}

	public boolean isLiquid() {
		return isLiquid;
	}

	public Item setIsFood() {
		isFood = true;
		return this;
	}

	public boolean isFood() {
		return isFood;
	}

	public float getABV(ItemStack item) {
		if (item.hasTag() && item.getTag().contains("ABV")) {
			return item.getTag().getFloat("ABV");
		}
		return 0f;
	}

	public int getEffectLength(ItemStack item) {
		float abv = getABV(item);
		if (abv == 0f)
			return 0;
		return (int) (50 * ((abv * abv) / 20));
	}

	public int getUseDuration(ItemStack stack) {
		if (this.isLiquid)
			return (int) (32f * (getABV(stack) / 20f));
		return 32;
	}

	public UseAction getUseAction(ItemStack stack) {
		if (this.isLiquid)
			return UseAction.DRINK;
		if (this.isFood)
			return UseAction.EAT;
		return UseAction.NONE;
	}

	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip,
			ITooltipFlag flags) {
		if (this.isLiquid)
			tooltip.add(new StringTextComponent("ABV: " + getABV(stack) + "%"));
		if(PotionUtils.getEffectsFromStack(stack).size() > 0)
			PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
	}

	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		if (this.isLiquid) {
			PlayerEntity playerentity = entityLiving instanceof PlayerEntity ? (PlayerEntity) entityLiving : null;
			if (playerentity == null || !playerentity.abilities.isCreativeMode) {
				stack.shrink(1);
			}

			if (playerentity instanceof ServerPlayerEntity) {
				CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) playerentity, stack);
			}

			if (!worldIn.isRemote) {
				// add nausea for that total realism
				MeaderyMod.LOGGER.info(getEffectLength(stack));
				if(getABV(stack) >= 20f)
					entityLiving.addPotionEffect(new EffectInstance(Effects.NAUSEA, getEffectLength(stack)));
				if(getABV(stack) >= 40f)
					entityLiving.addPotionEffect(new EffectInstance(Effects.BLINDNESS, getEffectLength(stack) * 2));
				if(getABV(stack) >= 60f)
					entityLiving.addPotionEffect(new EffectInstance(Effects.SLOWNESS, getEffectLength(stack) * 8, 10));
				if(getABV(stack) >= 80f)
					entityLiving.addPotionEffect(new EffectInstance(Effects.WITHER, getEffectLength(stack) / 2, 2));
				if(getABV(stack) >= 99.9f)
				{
					entityLiving.attackEntityFrom(CustomDamage.ALCOHOLISM, Float.MAX_VALUE);
					entityLiving.entityDropItem(new ItemStack(Items.GLASS_BOTTLE));
				}
				for (EffectInstance effectinstance : PotionUtils.getEffectsFromStack(stack)) {
					if (effectinstance.getPotion().isInstant()) {
						effectinstance.getPotion().affectEntity(playerentity, playerentity, entityLiving,
								effectinstance.getAmplifier(), 1.0D);
					} else {
						entityLiving.addPotionEffect(new EffectInstance(effectinstance));
					}
				}
			}

			if (playerentity == null || !playerentity.abilities.isCreativeMode) {
				if (stack.isEmpty()) {
					return new ItemStack(Items.GLASS_BOTTLE);
				}

				if (playerentity != null) {
					playerentity.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
				}
			}
		}
		return stack;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		playerIn.setActiveHand(handIn);
		return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
	}

	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (this.isInGroup(group) && this.isLiquid) {
			for(int i = 1; i <= 2; i++) {
				ItemStack itemstack = new ItemStack(this);
				itemstack.serializeNBT();
				itemstack.setTag(new CompoundNBT());
				itemstack.getTag().putFloat("ABV", 10f * i);
				items.add(itemstack);
			}
			if(this == ModItems.dragon_liquor)
			{
				ItemStack itemstack = new ItemStack(this);
				itemstack.serializeNBT();
				itemstack.setTag(new CompoundNBT());
				itemstack.getTag().putFloat("ABV", 100f);
				items.add(itemstack);
			}
		} else
			items.add(new ItemStack(this));

	}
}