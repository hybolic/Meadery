package hybolic.meadery.common.blocks;

import javax.annotation.Nullable;

import hybolic.meadery.MeaderyMod;
import hybolic.meadery.common.items.ModItems;
import hybolic.meadery.common.recipe.Ferment;
import hybolic.meadery.common.recipe.FermentSerializer;
import hybolic.meadery.common.tile.FermentationTileEntity;
import hybolic.meadery.common.util.RecipeGrabber;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ObjectHolder;

public abstract class AbstractFermentationBlock extends Block implements IWaterLoggable, ILiquidContainer
{
	public static final BooleanProperty   WATER_LOGGED = BlockStateProperties.WATERLOGGED;
	public static BooleanProperty SEALED     = BooleanProperty.create("sealed");
	public static BooleanProperty DECORATIVE = BooleanProperty.create("decorative");
	
	public AbstractFermentationBlock(Material mat, MaterialColor color, String id, float hr) {
		super(Block.Properties.create(mat, color).tickRandomly().hardnessAndResistance(hr));
		this.setRegistryName(MeaderyMod.MODID, id);
	}
	
	public AbstractFermentationBlock(Material mat, MaterialColor color, String id, float hr, ToolType tool, int level) {
		super(Block.Properties.create(mat, color).tickRandomly().hardnessAndResistance(hr).harvestTool(tool).harvestLevel(level));
		this.setRegistryName(MeaderyMod.MODID, id);
	}
	
    public boolean hasTileEntity(BlockState state)
    {
        return hasTileEntity_(state);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
    	if(stack.getTag() != null)
    	{
			state = state.with(DECORATIVE, false);
			worldIn.setBlockState(pos, state);
			CompoundNBT tag = (CompoundNBT) stack.getTag().get("BlockEntityTag");
			if(tag != null)
			{
				TileEntity tile = worldIn.getTileEntity(pos);
				if(tile!=null)
				{
					tile.read(tag);
					tile.setPos(pos);
				}
			}
    	}
		state = postBlockPlace(worldIn, pos, state, placer, stack);
		worldIn.setBlockState(pos, state, 2);
    }
    abstract BlockState postBlockPlace(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack);
    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
    	return createTileEntity_(state,world);
    }

	@SuppressWarnings("deprecation")
	public IFluidState getFluidState(BlockState state) {
		return state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}
	
	public abstract boolean hasTileEntity_(BlockState state);
	
	public abstract TileEntity createTileEntity_(BlockState state, IBlockReader world);
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(SEALED);
		builder.add(DECORATIVE);
		builder.add(WATER_LOGGED);
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

	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

    @ObjectHolder("meadery:fermentation_ingredient")
    public static FermentSerializer FERMENT;
    
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if(state.get(WATER_LOGGED))
			return false;
		if(state.get(DECORATIVE) == false)
		{
			Ferment test = RecipeGrabber.getFerment().getRecipeStream(world.getRecipeManager()).filter(recipe -> recipe.ingredient.test(player.getHeldItem(hand))).findFirst().orElse(null);
			if(test != null)
				;
			
			FermentationTileEntity tile = (FermentationTileEntity) world.getTileEntity(pos);
			if(tile == null)
				return true;
			if(player.getHeldItem(hand).isEmpty())
			{
				if(state.get(SEALED) == false && tile.getStackInSlot(5).isEmpty() == false)
				{
					ItemStack item = tile.decrStackSize(5, 64);
					if(!player.addItemStackToInventory(item))
					{
						spawnAsEntity(world, pos, item);
					}
					if(!world.isRemote)
						world.notifyBlockUpdate(pos, state, state.with(SEALED, true), 2);
				}
				else
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
								tile.fill(stack, FluidAction.EXECUTE);
								if(player.isCreative() == false)
								{
									player.setHeldItem(hand, Items.BUCKET.getDefaultInstance().copy());
								}
								return true;
							}
						}
						else
						{
							FluidStack removed = tile.drain(1000, FluidAction.SIMULATE);
							if(removed.getAmount() == 1000)
							{
								tile.drain(1000, FluidAction.EXECUTE);
								if(player.isCreative() == false)
								{
									player.getHeldItem(hand).shrink(1);
									if (player.getHeldItem(hand).getCount() <= 0 )
										player.setHeldItem(hand, ItemStack.EMPTY);
									if (!player.inventory.addItemStackToInventory(Items.WATER_BUCKET.getDefaultInstance().copy())) {
										player.dropItem(Items.WATER_BUCKET.getDefaultInstance().copy(), false);
									}
								}
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
					return true;
				}
			}
		}
		else if(player.getHeldItem(hand).isEmpty())
		{
			if(player.isSneaking())
			{
				setBlockstateDecorative(world, pos, state);
			}
			else
			{
				toggleBlockstateSealed(world, pos, state);
			}
			return true;
		}
		return false;
	}
	
	public void setBlockstateDecorative(World world, BlockPos pos, BlockState state)
	{
		world.setBlockState(pos, state.with(DECORATIVE, false));
	}
	
	public void toggleBlockstateSealed(World world, BlockPos pos, BlockState state)
	{
		world.setBlockState(pos, state.with(SEALED, !state.get(SEALED)));
	}

	public PushReaction getPushReaction(BlockState state) {
		return state.get(DECORATIVE) ? PushReaction.NORMAL : PushReaction.BLOCK;
	}
	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity tileEntityIn, ItemStack stack) {
		ItemStack newStack = new ItemStack(state.getBlock());
		CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean("decorative", state.get(DECORATIVE));
		nbt.putBoolean("sealed", state.get(SEALED));
		if(tileEntityIn != null)
		{
			nbt.put("BlockEntityTag", tileEntityIn.serializeNBT());
			nbt.getCompound("BlockEntityTag").remove("x");;
			nbt.getCompound("BlockEntityTag").remove("y");;
			nbt.getCompound("BlockEntityTag").remove("z");;
		}
		newStack.setTag(nbt);;
		spawnAsEntity(worldIn, pos, newStack);
		player.addStat(Stats.BLOCK_MINED.get(this));
		player.addExhaustion(0.005F);

	}
}
