package hybolic.meadery.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;

public abstract class DemijohnBlock extends AbstractFermnetationBlock {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty DECORATIVE = BooleanProperty.create("decorative");

	public DemijohnBlock(String id) {
		super(Material.GLASS, MaterialColor.LIGHT_GRAY, id, 0.3f);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(DECORATIVE, true));
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
		builder.add(FACING);
		builder.add(DECORATIVE);
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		ItemStack item = context.getItem();
		BlockState state = this.getDefaultState();
		if (item.hasTag()) {
			state = state.with(DECORATIVE, item.getTag().getBoolean("decorative"));
		}
		else
			state = state.with(DECORATIVE, false);
		return state.with(FACING, context.getPlacementHorizontalFacing());
	}

	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		ItemStack decrotive = new ItemStack(this);
		CompoundNBT DETAILS = new CompoundNBT();
		DETAILS.putBoolean("decorative", true);
		decrotive.setTag(DETAILS);
		items.add(decrotive);
	}
}
