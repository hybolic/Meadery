package hybolic.meadery.common.blocks;

import hybolic.meadery.common.items.ModItems;
import hybolic.meadery.common.tile.BarrelTileEntity;
import hybolic.meadery.common.tile.FermentationStationTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class FermentationBarrelBlock extends AbstractFermentationBlock {

	public static BooleanProperty SEALED     = BooleanProperty.create("sealed");
	public static BooleanProperty DECORATIVE = BooleanProperty.create("decorative");
	
	public FermentationBarrelBlock(String id) {
		super(Material.WOOD, MaterialColor.WOOD, id, 0.5f);
		this.setDefaultState(this.stateContainer.getBaseState().with(SEALED, true).with(DECORATIVE, true).with(WATER_LOGGED, false));
	}

	@Override
	public boolean hasTileEntity_(BlockState state) {
		return !state.get(DECORATIVE);
	}

	@Override
	public TileEntity createTileEntity_(BlockState state, IBlockReader world) {
		if(hasTileEntity_(state))
			return new BarrelTileEntity();
		return null;
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		ItemStack item = context.getItem();
		BlockState state = this.getDefaultState();
		if (item.hasTag()) {
			state = state.with(SEALED, item.getTag().getBoolean("sealed"));
			state = state.with(DECORATIVE, item.getTag().getBoolean("decorative"));
		}
		else
		{
			state = state.with(SEALED, true);
			state = state.with(DECORATIVE, true);
		}
		return state;
	}

	
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if(state.get(DECORATIVE) == false)
		{
			FermentationStationTileEntity tile = (FermentationStationTileEntity) world.getTileEntity(pos);
			if(player.getHeldItem(hand).isEmpty())
			{
				tile.toggleLock();
				return true;
			}
			else {
				LazyOptional<FluidStack> lazy_fluid = FluidUtil.getFluidContained(player.getHeldItem(hand));
				if(!state.get(SEALED))
				{
					if(lazy_fluid.isPresent())
					{
						FluidStack stack = lazy_fluid.orElse(FluidStack.EMPTY);
						if(!stack.isEmpty())
						{
							if(tile.fill(stack, FluidAction.SIMULATE) > 0)
							{
								int removed = tile.fill(stack, FluidAction.EXECUTE);
								stack.setAmount(stack.getAmount() - removed);
								return true;
							}
						}
					}
					else if(tile.canAddItem(player.getHeldItem(hand)))
					{
						tile.addItem(player.getHeldItem(hand));
						return true;
					}
					else if(player.getHeldItem(hand).getItem() == ModItems.Yeast_Culture)
					{
						if(tile.getStackInSlot(4).isEmpty())
						{
							ItemStack culture_stack = player.getHeldItem(hand);
							if(player.getHeldItem(hand).getCount() > 1)
								player.getHeldItem(hand).split(1);
							tile.setInventorySlotContents(4, culture_stack);
							player.setHeldItem(hand, ItemStack.EMPTY);
							return true;
						}
					}
				}
			}
		}
		else if(player.getHeldItem(hand).isEmpty())
		{
			if(player.isSneaking())
				world.setBlockState(pos, state.with(DECORATIVE, false));
			else
				world.setBlockState(pos, state.with(SEALED, !state.get(SEALED)));
			return true;
		}
		return false;
	}
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this));
		ItemStack decrotive = new ItemStack(this);
		CompoundNBT DETAILS = new CompoundNBT();
		DETAILS.putBoolean("decorative", true);
		DETAILS.putBoolean("sealed", false);
		decrotive.setTag(DETAILS);
		items.add(decrotive);
	}
	public static VoxelShape SHAPE = Block.makeCuboidShape(1, 0, 1, 15, 16, 15);
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}

}
