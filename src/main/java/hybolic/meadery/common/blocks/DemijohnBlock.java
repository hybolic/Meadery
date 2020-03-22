package hybolic.meadery.common.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import hybolic.meadery.MeaderyMod;
import hybolic.meadery.common.items.ModItems;
import hybolic.meadery.common.tile.FermentationStationTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public abstract class DemijohnBlock extends AbstractFermentationBlock {

	public static final DirectionProperty FACING       = HorizontalBlock.HORIZONTAL_FACING;

	public DemijohnBlock(String id) {
		super(Material.GLASS, MaterialColor.LIGHT_GRAY, id, 0.3f);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(DECORATIVE, true).with(SEALED, true).with(WATER_LOGGED, false));
	}



	public static List<ItemStack> getDrops(BlockState state, ServerWorld worldIn, BlockPos pos, @Nullable TileEntity tileEntityIn) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		ItemStack demijohn = new ItemStack(state.getBlock());
		CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean("decorative", state.get(DECORATIVE));
		nbt.putBoolean("sealed", state.get(SEALED));
		if(!state.get(DECORATIVE))
		{
			if(tileEntityIn != null)
			{
				CompoundNBT tag = new CompoundNBT();
				tileEntityIn.write(tag);
				nbt.put("tile_data", tag);
			}
		}
		demijohn.setTag(nbt);
		items.add(demijohn);
		return items;
	}
	
	
	@Override
	public boolean hasTileEntity_(BlockState state) {
		return !state.get(DECORATIVE);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FACING);
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		ItemStack item = context.getItem();
		BlockState state = this.getDefaultState();
		if (item.hasTag()) {
			boolean decor = item.getTag().getBoolean("decorative");
			state = state.with(DECORATIVE, decor);
			state = state.with(FACING, context.getPlacementHorizontalFacing());
			if(!decor)
			{
				CompoundNBT tag = (CompoundNBT) item.getTag().get("tile_data");
				if(tag != null)
				{
					TileEntity tile = this.createTileEntity(state, context.getWorld());
					tile.read(tag);
					context.getWorld().setTileEntity(context.getPos(), tile);
				}
			}
				
		}
		else
		{
			state = state.with(DECORATIVE, true);
			state = state.with(FACING, context.getPlacementHorizontalFacing());
		}
		return state;
	}

	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this));
		ItemStack not_decorative = new ItemStack(this);
		CompoundNBT DETAILS = new CompoundNBT();
		DETAILS.putBoolean("decorative", false);
		not_decorative.setTag(DETAILS);
		items.add(not_decorative);
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

	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}

	public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return false;
	}

	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return false;
	}

	public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
		return false;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
	    return getShape(state,worldIn,pos,context);
	}

	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}
	
}
