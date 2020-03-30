package hybolic.meadery.common.tile;

import javax.annotation.Nullable;

import hybolic.meadery.MeaderyMod;
import hybolic.meadery.common.blocks.CultureStationBlock;
import hybolic.meadery.common.items.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ObjectHolder;

public class CultureStationTileEntity extends TileEntity implements IInventory, ITickableTileEntity {
	
	@ObjectHolder("meadery:culture")
    public static TileEntityType<?> TILE = null;
	
	public long TICK = 0;
	
	private NonNullList<ItemStack> INVENTORY = NonNullList.withSize(3, ItemStack.EMPTY);

	private CultureStationTileEntity(TileEntityType<?> tileentity) {
		super(tileentity);
	}

	public CultureStationTileEntity() {
		this(TILE);
	}

	@Override
	public void clear() {
		INVENTORY.clear();
	}

	@Override
	public int getSizeInventory() {
		return INVENTORY.size();
	}

	@Override
	public boolean isEmpty() {
	      for(ItemStack itemstack : this.INVENTORY) {
	          if (!itemstack.isEmpty()) {
	             return false;
	          }
	       }

	       return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return INVENTORY.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(INVENTORY, index, count);
		if (!itemstack.isEmpty()) {
			this.markDirty();
		}

		return itemstack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(INVENTORY, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		INVENTORY.set(index, stack);
		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}

		this.markDirty();
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
	      if (this.world.getTileEntity(this.pos) != this) {
	          return false;
	       } else {
	          return !(player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
	       }
	}
	
	public void read(CompoundNBT compound) {
		ItemStackHelper.loadAllItems(compound.getCompound("items"), INVENTORY);
		if(compound.contains("ticks"))
			TICK = compound.getLong("ticks");
		super.read(compound);
	}

	public CompoundNBT write(CompoundNBT compound) {
		compound.put("items",ItemStackHelper.saveAllItems(new CompoundNBT(), INVENTORY, true));
		compound.putLong("ticks", TICK);
		super.write(compound);
		return compound;
	}

	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT tag = pkt.getNbtCompound();
		read(tag);
	}
	
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}
	static ResourceLocation fruit = new ResourceLocation(MeaderyMod.MODID,"fruit");
	static ResourceLocation berry = new ResourceLocation(MeaderyMod.MODID,"berry");
	@Override
	public void tick() {
		if(world.isRemote())
		{
			if(canProcess() && world.getRandom().nextInt(25) == 0)
				world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX()+0.34375, pos.getY()+1, pos.getZ()+0.34375, 0, 0.1, 0);
			if(!INVENTORY.get(2).isEmpty())
				for(int i = 0 ; i < Math.min(INVENTORY.get(2).getCount(), 15); i++)
					if(world.getRandom().nextInt(15) == 0)
						world.addParticle(ParticleTypes.BUBBLE_POP, pos.getX()+0.53125, pos.getY()+1, pos.getZ()+0.65625, 0, 0.1, 0);
		}
		if(canProcess())
			{
				if(TICK > 333)
				{
					decrStackSize(0,1);
					decrStackSize(1,1);
					if(INVENTORY.get(2).isEmpty())
						INVENTORY.set(2, new ItemStack(ModItems.Yeast_Culture, 0));
					INVENTORY.get(2).grow(1);
					TICK = 0;
					CultureStationBlock.setCorrectState(getWorld(), this);
				}
				else
					TICK++;
			}
	}
	
	private boolean canProcess() {
		return INVENTORY.get(2).getCount() < INVENTORY.get(2).getMaxStackSize()
				&& !INVENTORY.get(0).isEmpty()
				&& INVENTORY.get(1).getItem() == Items.GLASS_BOTTLE;
	}

	public boolean canAddItem(ItemStack stack, int slot)
	{
		if(stack.isEmpty())
			return false;
		if(slot == 0 && isFruitOrBerry(stack))
			return true;
		if(slot == 1 && stack.getItem() == Items.GLASS_BOTTLE)
			return true;
		return false;
	}
	
	public boolean addItem(ItemStack item, int slot)
	{
		if(slot == -1)
			return false;
		if(INVENTORY.get(slot).isEmpty() || item.getItem() == INVENTORY.get(slot).getItem())
		{
			if(INVENTORY.get(slot).isEmpty())
			{
				INVENTORY.set(slot, item.copy());
				item.setCount(0);;
				return true;
			}
			else if (item.getCount() + INVENTORY.get(slot).getCount() < INVENTORY.get(slot).getMaxStackSize()) {
				INVENTORY.get(slot).grow(item.getCount());
				return true;
			} else {
				int stacks = (item.getCount() + INVENTORY.get(slot).getCount())
						/ INVENTORY.get(slot).getMaxStackSize();
				int over = (item.getCount() + INVENTORY.get(slot).getCount())
						% INVENTORY.get(slot).getMaxStackSize();
				if (stacks == 1) {
					INVENTORY.get(slot).grow(INVENTORY.get(slot).getMaxStackSize());
					item.setCount(over);
					return true;
				}
			}
		}
		return false;
	}
	
	public int getSlotForStack(ItemStack stack)
	{
		if(stack.isEmpty())
			return -1;
		if(isFruitOrBerry(stack))
			return 0;
		if(stack.getItem() == Items.GLASS_BOTTLE)
			return 1;
		return -1;
	}
	
	public boolean isFruitOrBerry(ItemStack item)
	{
		return ItemTags.getCollection().getOrCreate(fruit).contains(item.getItem()) || ItemTags.getCollection().getOrCreate(berry).contains(item.getItem());
	}

	public void remove() {
		InventoryHelper.dropInventoryItems(getWorld(), getPos(), this);
		super.remove();
	}
}
