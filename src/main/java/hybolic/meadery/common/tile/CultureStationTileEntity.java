package hybolic.meadery.common.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.ObjectHolder;

public class CultureStationTileEntity extends TileEntity implements IInventory {
	
	@ObjectHolder("meadery:fermentation_station")
    public static TileEntityType<?> TILE;
	
	private NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

	private CultureStationTileEntity(TileEntityType<?> tileentity) {
		super(tileentity);
	}

	public CultureStationTileEntity() {
		this(TILE);
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

	@Override
	public boolean isEmpty() {
	      for(ItemStack itemstack : this.inventory) {
	          if (!itemstack.isEmpty()) {
	             return false;
	          }
	       }

	       return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inventory.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(inventory, index, count);
		if (!itemstack.isEmpty()) {
			this.markDirty();
		}

		return itemstack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(inventory, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inventory.set(index, stack);
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

}
