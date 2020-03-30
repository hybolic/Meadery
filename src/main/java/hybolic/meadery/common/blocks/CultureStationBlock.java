package hybolic.meadery.common.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import hybolic.meadery.MeaderyMod;
import hybolic.meadery.common.tile.CultureStationTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CultureStationBlock extends Block {

	public CultureStationBlock(String id) {
		super(Block.Properties.create(Material.WOOD, MaterialColor.GOLD).tickRandomly().hardnessAndResistance(0.3f));
		this.setRegistryName(MeaderyMod.MODID, id);
		this.setDefaultState(this.stateContainer.getBaseState().with(CULTURE, false).with(BOTTLE, false).with(OUT, false));
	}

    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }
    
    @Nullable
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
    	return new CultureStationTileEntity();
    }
    
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		CultureStationTileEntity tile = ((CultureStationTileEntity)world.getTileEntity(pos));
		int slot = -1;
		if(player.getHeldItem(hand).isEmpty())
		{
			if(tile.getStackInSlot(2).getCount() > 0)
			{
				ItemStack dropped = ((CultureStationTileEntity)world.getTileEntity(pos)).decrStackSize(2, 64);
				if(player.inventory.addItemStackToInventory(dropped) == false)
					player.entityDropItem(dropped);
				world.setBlockState(pos, getCorrectState(state, tile));
				return true;
			}
		}
		else if((slot = tile.getSlotForStack(player.getHeldItem(hand))) != -1)
		{
			boolean doesSomething = tile.addItem(player.getHeldItem(hand), slot);
			if(doesSomething)
			{
				if(player.getHeldItem(hand).getCount() == 0)
					player.setHeldItem(hand, ItemStack.EMPTY);
				world.setBlockState(pos, getCorrectState(state, tile));
			}
			return doesSomething;
		}
		return false;
	}
	
	private static BlockState getCorrectState(BlockState state, boolean cult, boolean bott, boolean out)
	{
		return state.with(CULTURE, cult).with(BOTTLE, bott).with(OUT, out);
	}
	
	public static BlockState getCorrectState(BlockState state, IInventory tile)
	{
		return getCorrectState(state, !tile.getStackInSlot(0).isEmpty(), !tile.getStackInSlot(1).isEmpty(), !tile.getStackInSlot(2).isEmpty());
	}

	public static BooleanProperty CULTURE     = BooleanProperty.create("culture");
	public static BooleanProperty BOTTLE = BooleanProperty.create("bottle");
	public static BooleanProperty OUT = BooleanProperty.create("output");
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(CULTURE);
		builder.add(BOTTLE);
		builder.add(OUT);
	}

	public static void setCorrectState(World world, CultureStationTileEntity tile) {
		world.setBlockState(tile.getPos(), getCorrectState(tile.getBlockState(), tile));
	}

	public static VoxelShape SHAPE = generateShape();
	private static VoxelShape generateShape()
	{
	    List<VoxelShape> shapes = new ArrayList<>();

	    shapes.add(Block.makeCuboidShape(0, 10, 0, 16, 12, 16));
	    shapes.add(Block.makeCuboidShape(14, 0, 14, 16, 10, 16));
	    shapes.add(Block.makeCuboidShape(0, 0, 0, 2, 10, 2));
	    shapes.add(Block.makeCuboidShape(0, 0, 14, 2, 10, 16));
	    shapes.add(Block.makeCuboidShape(14, 0, 0, 16, 10, 2));
	    
	    VoxelShape result = VoxelShapes.empty();
	    
	    for(VoxelShape shape : shapes)
	    {
	        result = VoxelShapes.combine(result, shape, IBooleanFunction.OR);
	    }
	    
	    return result.simplify();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
	    return getShape(state,worldIn,pos,context);
	}

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
    	super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    	CultureStationBlock.setCorrectState(worldIn, (CultureStationTileEntity) worldIn.getTileEntity(pos));
    }

}
