package hybolic.meadery.common.tile;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import hybolic.meadery.MeaderyMod;
import hybolic.meadery.common.blocks.AbstractFermentationBlock;
import hybolic.meadery.common.recipe.Brew;
import hybolic.meadery.common.recipe.Ferment;
import hybolic.meadery.common.recipe.FermentationIngredient;
import hybolic.meadery.common.recipe.FermentationType;
import hybolic.meadery.common.util.RecipeGrabber;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public abstract class FermentationTileEntity extends TileEntity implements IInventory, IFluidTank, ITickableTileEntity {
	public static Random random = new Random();
	
	/*
	 * slot 0: additions
	 * slot 1: ^
	 * slot 2: ^
	 * slot 3: ^
	 * slot 4: culture
	 * slot 5: output
	 */
	public NonNullList<ItemStack> INVENTORY = NonNullList.withSize(6, ItemStack.EMPTY);
	public FluidStack FLUID = FluidStack.EMPTY;
	public int CAPACITY = 0;
	/*
	 * age in ticks
	 */
	public long AGE = 0;

	// 250mb is one bottle
	public FermentationTileEntity(TileEntityType<?> tile, int capInBottles) {
		super(tile);
		CAPACITY = 250 * capInBottles;
	}
	
	public FermentationTileEntity() {
		this(null, 0);
	}

	public void toggleLock() {
		world.setBlockState(pos, this.getBlockState().with(AbstractFermentationBlock.SEALED, !this.getBlockState().get(AbstractFermentationBlock.SEALED)));
		tryConvertInventory();
	}

	private void tryConvertInventory() {
		Collection<Brew> map = RecipeGrabber.getBrew().getRecipes(world.getRecipeManager()).values();
		
		
		Brew current = new Brew(null, null, getList(), !FLUID.isEmpty(), null);
		for(Brew brew : map)
		{
			if(brew.equalWithMultiply(current,getCapacityInBottles()))
			{
				MeaderyMod.LOGGER.info("BREW FOUND!");
				//this.clear();
				//ItemStack agedItem = new ItemStack(brew.result, getCapacityInBottles())
				//this.setInventorySlotContents(5, agedItem);
			}
		}
	}
	FermentationIngredient[] list = new FermentationIngredient[4];
	private FermentationIngredient[] getList()
	{
		if(rebuildFerm)
		{
			list = new FermentationIngredient[4];
			rebuildFerm = false;
			Ferment in_1 = RecipeGrabber.getFerment().getRecipeStream(world.getRecipeManager()).filter(recipe -> recipe.ingredient.test(getStackInSlot(0))).findFirst().orElse(null);
			Ferment in_2 = RecipeGrabber.getFerment().getRecipeStream(world.getRecipeManager()).filter(recipe -> recipe.ingredient.test(getStackInSlot(1))).findFirst().orElse(null);
			Ferment in_3 = RecipeGrabber.getFerment().getRecipeStream(world.getRecipeManager()).filter(recipe -> recipe.ingredient.test(getStackInSlot(2))).findFirst().orElse(null);
			Ferment in_4 = RecipeGrabber.getFerment().getRecipeStream(world.getRecipeManager()).filter(recipe -> recipe.ingredient.test(getStackInSlot(3))).findFirst().orElse(null);
			if(in_1 != null)
				list[0] = new FermentationIngredient(in_1.fermentation_type, getStackInSlot(0).getCount());
			if(in_2 != null)
				list[1] = new FermentationIngredient(in_2.fermentation_type, getStackInSlot(0).getCount());
			if(in_3 != null)
				list[2] = new FermentationIngredient(in_3.fermentation_type, getStackInSlot(0).getCount());
			if(in_4 != null)
				list[3] = new FermentationIngredient(in_4.fermentation_type, getStackInSlot(0).getCount());
			list = Arrays.stream(list).filter(new_ferment -> (new_ferment != null && new_ferment.fermentation_type != FermentationType.NULL)).toArray(FermentationIngredient[]::new);
		}
		return list;
	}

	//// IInventory////
	public boolean canAddItem(ItemStack stack)
	{
		if(stack.isEmpty())
			return false;
		if(Ferment.getFromItem(getWorld(), stack) != null)
		{
			for(int i = 0; i < 4; i++)
			{
				if(this.getStackInSlot(i).isEmpty() || ItemStack.areItemsEqual(stack, this.getStackInSlot(i)))
				{
					if(this.getStackInSlot(i).getCount() + 1 < this.getStackInSlot(i).getMaxStackSize())
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public ItemStack addItem(ItemStack stack)
	{
		if(canAddItem(stack))
		{
			for(int i = 0; i < 4; i++)
			{
				if(this.getStackInSlot(i).isEmpty() || ItemStack.areItemsEqual(stack, this.getStackInSlot(i)))
				{
					if(this.getStackInSlot(i).getCount() + 1 < this.getStackInSlot(i).getMaxStackSize())
					{
						if(this.getStackInSlot(i).isEmpty())
						{
							ItemStack newStack = stack.copy();
							newStack.setCount(0);
							this.setInventorySlotContents(i, newStack);
						}
						getStackInSlot(i).setCount(getStackInSlot(i).getCount() + 1);
						stack.setCount(stack.getCount() - 1);
						if(stack.getCount() > 0)
							return stack;
						return ItemStack.EMPTY;
					}
				}
			}
		}
		return stack;
	}
	
	@Override
	public void clear() {
		INVENTORY.clear();
		FLUID = FluidStack.EMPTY;
		AGE = 0;
		this.markDirty();
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
		if (this.getBlockState().get(AbstractFermentationBlock.SEALED))
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
	public int getCapacityInBottles()
	{
		return CAPACITY / 250;
	}
	@Override
	public boolean isFluidValid(FluidStack stack) {
		return stack.getFluid() == Fluids.WATER;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (this.getBlockState().get(AbstractFermentationBlock.SEALED))
			return 0;
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
		this.markDirty();
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
		if (resource.isEmpty() || !resource.isFluidEqual(FLUID) || this.getBlockState().get(AbstractFermentationBlock.SEALED)) {
			return FluidStack.EMPTY;
		}
		this.markDirty();
		return drain(resource.getAmount(), action);
	}

	@Nonnull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (this.getBlockState().get(AbstractFermentationBlock.SEALED))
			return FluidStack.EMPTY;
		int drained = maxDrain;
		if (FLUID.getAmount() < drained) {
			drained = FLUID.getAmount();
		}
		FluidStack stack = new FluidStack(FLUID, drained);
		if (action.execute() && drained > 0) {
			FLUID.shrink(drained);
		}
		this.markDirty();
		return stack;
	}

	/// ITickableTileEntity
	@Override
	public void tick() {
		if(this.getStackInSlot(4).isEmpty() == false && this.getFluid().isEmpty() == false && world.isRemote())
		{
			if(0 == random.nextInt(10))
				pushBubbles();
		}
		if(!world.isRemote()) {
			if (this.getBlockState().get(AbstractFermentationBlock.SEALED) && !this.getStackInSlot(4).isEmpty())
			{
				int i = ((world.getGameTime() % 20 == 0) ? 1 : 0);
				if(i>0)
					this.AGE = AGE + i;
			}
			if(FLUID.getAmount() == this.getCapacity())
			{
				//check inventory
			}
		}
	}
	abstract void pushBubbles();
	
	// IForgeTileEntity
	public void read(CompoundNBT compound) {
		FLUID = FluidStack.loadFluidStackFromNBT(compound);
		ItemStackHelper.loadAllItems(compound.getCompound("items"), INVENTORY);
		if(compound.contains("capacity"))
			CAPACITY = compound.getInt("capacity");
		if(compound.contains("age"))
			AGE = compound.getLong("age");
		super.read(compound);
	}

	public CompoundNBT write(CompoundNBT compound) {
		FLUID.writeToNBT(compound);
		compound.put("items",ItemStackHelper.saveAllItems(new CompoundNBT(), INVENTORY, true));
		compound.putInt("capacity", CAPACITY);
		compound.putLong("age", AGE);
		super.write(compound);
		return compound;
	}

	   @Nullable
	   public SUpdateTileEntityPacket getUpdatePacket() {
		      return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
	   }

	   /**
	    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
	    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
	    */
	   public CompoundNBT getUpdateTag() {
	      return write(new CompoundNBT());
	   }

	boolean rebuildFerm = false;

	public void markDirty() {
		this.rebuildFerm = true;
		super.markDirty();
	}
}
