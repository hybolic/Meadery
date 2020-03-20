package hybolic.meadery.common.tile;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ObjectHolder;

public class FermentationStationTileEntity extends TileEntity implements IInventory, IFluidTank, ITickableTileEntity {
	
	@ObjectHolder("meadery:fermentation_station")
    public static TileEntityType<?> TILE;
	
	
	
	private NonNullList<ItemStack> INVENTORY = NonNullList.withSize(5, ItemStack.EMPTY);
	private FluidStack FLUID = FluidStack.EMPTY;
	private boolean LOCKED = false;
	private int CAPACITY = 0;
	/*
	 * age in ticks
	 */
	private int AGE = 0;

	// 250mb is one bottle
	private FermentationStationTileEntity(TileEntityType<?> tile, int capInBottles) {
		super(tile);
		CAPACITY = 250 * capInBottles;
	}
	
	public FermentationStationTileEntity(int capInBottles) {
		this(TILE, 4);
	}
	
	public FermentationStationTileEntity() {
		this(TILE, 4);
	}

	public void Unlock() {
		if (this.LOCKED)
			this.LOCKED = false;
		tryConvertInventory();
	}

	private void tryConvertInventory() {
		//find recipe.
		//check age.
		//change fluidstack
	}

	//// IInventory////
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
		for (ItemStack itemstack : this.INVENTORY) {
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
		if (this.LOCKED)
			return false;
		if (this.world.getTileEntity(this.pos) != this) {
			return false;
		} else {
			return !(player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
					(double) this.pos.getZ() + 0.5D) > 64.0D);
		}
	}

	// IFluidTank

	@Override
	public FluidStack getFluid() {
		return FLUID;
	}

	@Override
	public int getFluidAmount() {
		if (FLUID == null || FLUID == FluidStack.EMPTY)
			return 0;
		else
			return FLUID.getAmount();
	}

	@Override
	public int getCapacity() {
		return CAPACITY;
	}

	@Override
	public boolean isFluidValid(FluidStack stack) {
		return stack.getFluid() == Fluids.WATER;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty() || !isFluidValid(resource)) {
			return 0;
		}
		if (action.simulate()) {
			if (FLUID.isEmpty()) {
				return Math.min(CAPACITY, resource.getAmount());
			}
			if (!FLUID.isFluidEqual(resource)) {
				return 0;
			}
			return Math.min(CAPACITY - FLUID.getAmount(), resource.getAmount());
		}
		if (FLUID.isEmpty()) {
			FLUID = new FluidStack(resource, Math.min(CAPACITY, resource.getAmount()));
			return FLUID.getAmount();
		}
		if (!FLUID.isFluidEqual(resource)) {
			return 0;
		}
		int filled = CAPACITY - FLUID.getAmount();

		if (resource.getAmount() < filled) {
			FLUID.grow(resource.getAmount());
			filled = resource.getAmount();
		} else {
			FLUID.setAmount(CAPACITY);
		}
		return filled;
	}

	@Nonnull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty() || !resource.isFluidEqual(FLUID)) {
			return FluidStack.EMPTY;
		}
		return drain(resource.getAmount(), action);
	}

	@Nonnull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		int drained = maxDrain;
		if (FLUID.getAmount() < drained) {
			drained = FLUID.getAmount();
		}
		FluidStack stack = new FluidStack(FLUID, drained);
		if (action.execute() && drained > 0) {
			FLUID.shrink(drained);
		}
		return stack;
	}

	/// ITickableTileEntity
	@Override
	public void tick() {
		if (LOCKED)
			this.AGE++;
	}

	// IForgeTileEntity
	public void read(CompoundNBT compound) {
		FLUID = FluidStack.loadFluidStackFromNBT(compound);
		ItemStackHelper.loadAllItems(compound, INVENTORY);
		LOCKED = compound.getBoolean("locked");
		CAPACITY = compound.getInt("capacity");
		AGE = compound.getInt("age");
		super.read(compound);
	}

	public CompoundNBT write(CompoundNBT compound) {
		FLUID.writeToNBT(compound);
		ItemStackHelper.saveAllItems(compound, INVENTORY, false);
		compound.putBoolean("locked", LOCKED);
		compound.putInt("capacity", CAPACITY);
		compound.putInt("age", AGE);
		return super.write(compound);
	}
}
