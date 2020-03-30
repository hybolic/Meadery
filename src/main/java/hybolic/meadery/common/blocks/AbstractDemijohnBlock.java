package hybolic.meadery.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class AbstractDemijohnBlock extends AbstractFermentationBlock {

	public static final DirectionProperty FACING       = HorizontalBlock.HORIZONTAL_FACING;

	public AbstractDemijohnBlock(String id) {
		super(Material.GLASS, MaterialColor.LIGHT_GRAY, id, 0f);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(DECORATIVE, true).with(SEALED, true).with(WATER_LOGGED, false));
	}

	public BlockState postBlockPlace(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		return state;
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		ItemStack item = context.getItem();
		BlockState state = this.getDefaultState();
		if (item.hasTag()) {
			state = state.with(FACING, context.getPlacementHorizontalFacing());
		}
		else
		{
			state = state.with(FACING, context.getPlacementHorizontalFacing());
		}
		return state;
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

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
	    return getShape(state,worldIn,pos,context);
	}
	
}
