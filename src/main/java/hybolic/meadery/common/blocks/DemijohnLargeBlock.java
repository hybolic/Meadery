package hybolic.meadery.common.blocks;

import java.util.ArrayList;
import java.util.List;

import hybolic.meadery.common.tile.DemijohnLargeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class DemijohnLargeBlock extends AbstractDemijohnBlock {

	public DemijohnLargeBlock(String id) {
		super(id);
	}

	@Override
	public TileEntity createTileEntity_(BlockState state, IBlockReader world) {
		if(hasTileEntity_(state))
			return new DemijohnLargeTileEntity();
		return null;
	}


	
	
	
	public static VoxelShape ESHAPE = generateShape(Direction.EAST, false);
	public static VoxelShape WSHAPE = generateShape(Direction.WEST, false);
	public static VoxelShape NSHAPE = generateShape(Direction.NORTH, false);
	public static VoxelShape SSHAPE = generateShape(Direction.SOUTH, false);
	public static VoxelShape ELSHAPE = generateShape(Direction.EAST, true);
	public static VoxelShape WLSHAPE = generateShape(Direction.WEST, true);
	public static VoxelShape NLSHAPE = generateShape(Direction.NORTH, true);
	public static VoxelShape SLSHAPE = generateShape(Direction.SOUTH, true );

	private static VoxelShape generateShape(Direction direction, boolean locked)
	{
	    List<VoxelShape> shapes = new ArrayList<>();
	    switch(direction)
	    {
			case EAST:
			    shapes.add(Block.makeCuboidShape(2, 0, 1, 16, 12, 15)); // BOTTLE_CENTER
			    shapes.add(Block.makeCuboidShape(0, 6, 7, 2, 11, 9)); // HANDLE
			    shapes.add(Block.makeCuboidShape(4, 12, 3, 14, 13, 13)); // TOP
			    if(locked)
				    shapes.add(Block.makeCuboidShape(8, 12, 7, 10, 14, 9)); // STOPPER
				break;
			case NORTH:
			    shapes.add(Block.makeCuboidShape(1, 0, 0, 15, 12, 14)); // BOTTLE_CENTER
			    shapes.add(Block.makeCuboidShape(7, 6, 14, 9, 11, 16)); // HANDLE
			    shapes.add(Block.makeCuboidShape(3, 12, 2, 13, 13, 12)); // TOP
			    if(locked)
				    shapes.add(Block.makeCuboidShape(7, 12, 6, 9, 14, 8)); // STOPPER
				break;
			case SOUTH:
			    shapes.add(Block.makeCuboidShape(1, 0, 2, 15, 12, 16)); // BOTTLE_CENTER
			    shapes.add(Block.makeCuboidShape(7, 6, 0, 9, 11, 2)); // HANDLE
			    shapes.add(Block.makeCuboidShape(3, 12, 4, 13, 13, 14)); // TOP
			    if(locked)
				    shapes.add(Block.makeCuboidShape(7, 12, 8, 9, 14, 10)); // STOPPER
				break;
			case WEST:
			    shapes.add(Block.makeCuboidShape(0, 0, 1, 14, 12, 15)); // BOTTLE_CENTER
			    shapes.add(Block.makeCuboidShape(14, 6, 7, 16, 11, 9)); // HANDLE
			    shapes.add(Block.makeCuboidShape(2, 12, 3, 12, 13, 13)); // TOP
			    if(locked)
				    shapes.add(Block.makeCuboidShape(6, 12, 7, 8, 14, 9)); // STOPPER
				break;
			default:
				return VoxelShapes.fullCube();
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
		switch(state.get(FACING))
		{
			case EAST:
				if(state.get(SEALED))
				    return ELSHAPE;
			    return ESHAPE;
			case NORTH:
				if(state.get(SEALED))
				    return NLSHAPE;
			    return NSHAPE;
			case SOUTH:
				if(state.get(SEALED))
				    return SLSHAPE;
			    return SSHAPE;
			case WEST:
				if(state.get(SEALED))
				    return WLSHAPE;
			    return WSHAPE;
			default:
				return VoxelShapes.fullCube();
		}
	}
}
