package hybolic.meadery.common.blocks;

import hybolic.meadery.common.tile.FermentationStationTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.IBlockReader;

public class FermentationBarrelBlock extends AbstractFermnetationBlock {

	public static BooleanProperty SEALED     = BooleanProperty.create("sealed");
	public static BooleanProperty DECORATIVE = BooleanProperty.create("decorative");
	
	public FermentationBarrelBlock(String id) {
		super(Material.WOOD, MaterialColor.WOOD, id, 0.5f);
		this.setDefaultState(this.stateContainer.getBaseState().with(SEALED, true).with(DECORATIVE, true));
	}

	@Override
	public boolean hasTileEntity_(BlockState state) {
		return !state.get(DECORATIVE);
	}

	@Override
	public TileEntity createTileEntity_(BlockState state, IBlockReader world) {
		if(hasTileEntity_(state))
			return new FermentationStationTileEntity(16);
		return null;
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(SEALED);
		builder.add(DECORATIVE);
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

	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this));
		ItemStack decrotive = new ItemStack(this);
		CompoundNBT DETAILS = new CompoundNBT();
		DETAILS.putBoolean("decorative", true);
		DETAILS.putBoolean("sealed", false);
		decrotive.setTag(DETAILS);
		items.add(decrotive);
	}

}
