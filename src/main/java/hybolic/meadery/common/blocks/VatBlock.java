package hybolic.meadery.common.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import hybolic.meadery.common.tile.VatTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class VatBlock extends AbstractFermentationBlock implements IBlockColor {
	
	public static EnumProperty<DoubleBlockHalf> DoubleBlock_Half = EnumProperty.create("block_part", DoubleBlockHalf.class);

	
	public VatBlock(String id) {
		super(Material.IRON, MaterialColor.IRON, id, 3f);
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(DECORATIVE, true)
				.with(SEALED, true)
				.with(DoubleBlock_Half, DoubleBlockHalf.LOWER)
				.with(WATER_LOGGED, false));
	}

	public BlockState postBlockPlace(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		return state;
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		if((!context.getWorld().getBlockState(context.getPos().up()).isReplaceable(BlockItemUseContext.func_221536_a(context, context.getPos().up(), context.getNearestLookingDirection())) ) && Block.func_220055_a(context.getWorld(), context.getPos().down(), Direction.UP))
			return null;
		return this.getDefaultState();
	}

	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state,
			@Nullable TileEntity te, ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
	}
	
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf doubleblockhalf = state.get(DoubleBlock_Half);
	      BlockPos blockpos = doubleblockhalf == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
	      BlockState blockstate = worldIn.getBlockState(blockpos);
	      if (blockstate.getBlock() == this && blockstate.get(DoubleBlock_Half) != doubleblockhalf) {
	         worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
	         worldIn.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
	      }
	      super.onBlockHarvested(worldIn, pos, state, player);
	}

	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockState(pos.up(), state.with(DoubleBlock_Half, DoubleBlockHalf.UPPER));
	}
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if(state.get(DoubleBlock_Half) == DoubleBlockHalf.UPPER)
		{
			return onBlockActivated(world.getBlockState(pos.down()),world,pos.down(),player,hand,hit);
		}
		else
		{
			boolean temp = super.onBlockActivated(state,world,pos,player,hand,hit);
			world.setBlockState(pos.up(), world.getBlockState(pos).with(DoubleBlock_Half, DoubleBlockHalf.UPPER));
			return temp;
		}
	}

	@Override
	public void setBlockstateDecorative(World world, BlockPos pos, BlockState state)
	{
		BlockPos blockpos = state.get(DoubleBlock_Half) == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
		BlockState blockstate = world.getBlockState(blockpos);
		world.setBlockState(pos, state.with(DECORATIVE, false));
		world.setBlockState(blockpos, blockstate.with(DECORATIVE, false));
	}

	@Override
	public void toggleBlockstateSealed(World world, BlockPos pos, BlockState state)
	{
		BlockPos blockpos = state.get(DoubleBlock_Half) == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
		BlockState blockstate = world.getBlockState(blockpos);
		boolean newValue = !state.get(SEALED);
		world.setBlockState(pos, state.with(SEALED, newValue));
		world.setBlockState(blockpos, blockstate.with(SEALED, newValue));
	}

	@Override
	public boolean hasTileEntity_(BlockState state)
	{
		return !state.get(DECORATIVE) && state.get(DoubleBlock_Half) == DoubleBlockHalf.LOWER;
	}

	@Override
	public TileEntity createTileEntity_(BlockState state, IBlockReader world) {
		if(hasTileEntity(state))
			return new VatTileEntity();
		return null;
	}

	public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(DoubleBlock_Half);
	}

	public static VoxelShape TOP 			= generateShape(true, false);
	public static VoxelShape BOTTOM 		= generateShape(false, false );
	public static VoxelShape TOP_OPEN 		= generateShape(true, true);
	public static VoxelShape BOTTOM_OPEN 	= generateShape(false, true );

	private static VoxelShape generateShape(boolean top, boolean open)
	{
	    List<VoxelShape> shapes = new ArrayList<>();
	    if(top)
	    {
	    	if(open)
	    	{
	    		shapes.add(Block.makeCuboidShape(0, -14, 0, 16, 14, 1));
	    		shapes.add(Block.makeCuboidShape(0, -14, 0, 1, 14, 16));
	    		shapes.add(Block.makeCuboidShape(15, -14, 0, 16, 14, 16));
	    		shapes.add(Block.makeCuboidShape(0, -14, 15, 16, 14, 16));

	    		shapes.add(Block.makeCuboidShape(0, -16, 0, 2, -14, 2));
	    		shapes.add(Block.makeCuboidShape(14, -16, 0, 16, -14, 2));
	    		shapes.add(Block.makeCuboidShape(0, -16, 14, 2, -14, 16));
	    		shapes.add(Block.makeCuboidShape(14, -16, 14, 16, -14, 16));
	    	}
	    	else
	    	{
			    shapes.add(Block.makeCuboidShape(0, -14, 0, 16, 14, 16));
			    shapes.add(Block.makeCuboidShape(0, 14.1, 0, 16, 16, 16));

	    		shapes.add(Block.makeCuboidShape(0, -16, 0, 2, -14, 2));
	    		shapes.add(Block.makeCuboidShape(14, -16, 0, 16, -14, 2));
	    		shapes.add(Block.makeCuboidShape(0, -16, 14, 2, -14, 16));
	    		shapes.add(Block.makeCuboidShape(14, -16, 14, 16, -14, 16));
	    	}
	    }
	    else
	    {
	    	if(open)
	    	{
	    		shapes.add(Block.makeCuboidShape(0, 2, 0, 16, 30, 1));
	    		shapes.add(Block.makeCuboidShape(0, 2, 0, 1, 30, 16));
	    		shapes.add(Block.makeCuboidShape(15, 2, 0, 16, 30, 16));
	    		shapes.add(Block.makeCuboidShape(0, 2, 15, 16, 30, 16));
	    		shapes.add(Block.makeCuboidShape(0, 2, 0, 16, 3, 16));

	    		shapes.add(Block.makeCuboidShape(0, 0, 0, 2, 2, 2));
	    		shapes.add(Block.makeCuboidShape(14, 0, 0, 16, 2, 2));
	    		shapes.add(Block.makeCuboidShape(0, 0, 14, 2, 2, 16));
	    		shapes.add(Block.makeCuboidShape(14, 0, 14, 16, 2, 16));
	    	}
	    	else
	    	{
	    		shapes.add(Block.makeCuboidShape(0, 2, 0, 16, 32, 16));
	    		shapes.add(Block.makeCuboidShape(0, 0, 0, 2, 2, 2));
	    		shapes.add(Block.makeCuboidShape(14, 0, 0, 16, 2, 2));
	    		shapes.add(Block.makeCuboidShape(0, 0, 14, 2, 2, 16));
	    		shapes.add(Block.makeCuboidShape(14, 0, 14, 16, 2, 16));
	    	}
	    }
	    VoxelShape result = VoxelShapes.empty();
	    
	    for(VoxelShape shape : shapes)
	    {
	        result = VoxelShapes.combine(result, shape, IBooleanFunction.OR);
	    }
	    
	    return result.simplify();
	}
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		switch(state.get(DoubleBlock_Half))
		{
			case UPPER:
				if(state.get(SEALED))
				    return TOP;
			    return TOP_OPEN;
			case LOWER:
				if(state.get(SEALED))
				    return BOTTOM;
			    return BOTTOM_OPEN;
			default:
				return VoxelShapes.fullCube();
		}
	}

	@Override
	public int getColor(BlockState state, IEnviromentBlockReader p_getColor_2_, BlockPos pos, int render_layer) {
		if(state.get(DoubleBlock_Half) == DoubleBlockHalf.UPPER && !state.get(SEALED) && render_layer == 1)
			return 0x4444FF;
		return 0;
	}

	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.down();
		BlockState blockstate = worldIn.getBlockState(blockpos);
		if (state.get(DoubleBlock_Half) == DoubleBlockHalf.LOWER) {
			return blockstate.func_224755_d(worldIn, blockpos, Direction.UP);
		} else {
			return blockstate.getBlock() == this;
		}
	}
}
