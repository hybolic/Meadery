package hybolic.meadery.common.blocks;

import java.util.ArrayList;
import java.util.List;

import hybolic.meadery.common.tile.DemijohnSmallTileEntity;
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

public class DemijohnSmallBlock extends AbstractDemijohnBlock {

	public DemijohnSmallBlock(String id) {
		super(id);
	}
	
	@Override
	public TileEntity createTileEntity_(BlockState state, IBlockReader world) {
		if(hasTileEntity_(state))
			return new DemijohnSmallTileEntity();
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
	    shapes.add(Block.makeCuboidShape(6, 0, 6, 10, 6, 10)); // BOTTLE_CENTER
	    shapes.add(Block.makeCuboidShape(6.5, 6, 6.5, 9.5, 7, 9.5));
	    shapes.add(Block.makeCuboidShape(7, 7, 7, 9, 8, 9));
	    if(locked)
		    shapes.add(Block.makeCuboidShape(7.5, 6.5, 7.5, 8.5, 8.5, 8.5)); // STOPPER
	    switch(direction)
	    {
			case EAST:
			    shapes.add(Block.makeCuboidShape(7.5, 2, 10, 8.5, 6, 11.7));
				break;
			case NORTH:
			    shapes.add(Block.makeCuboidShape(10, 2, 7.5, 11.7, 6, 8.5));
				break;
			case SOUTH:
			    shapes.add(Block.makeCuboidShape(4.3, 2, 8.5, 8.5, 6, 7.5));
				break;
			case WEST:
			    shapes.add(Block.makeCuboidShape(7.5, 2, 4.3, 8.5, 6, 6));
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
