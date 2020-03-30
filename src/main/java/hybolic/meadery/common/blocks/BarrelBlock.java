package hybolic.meadery.common.blocks;

import hybolic.meadery.common.tile.BarrelTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BarrelBlock extends AbstractFermentationBlock {

	public static BooleanProperty SEALED     = BooleanProperty.create("sealed");
	public static BooleanProperty DECORATIVE = BooleanProperty.create("decorative");
	
	public BarrelBlock(String id) {
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
		BlockState state = this.getDefaultState();
		return state;
	}
	
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this));
		//add item full of water
//		ItemStack decrotive = new ItemStack(this);
//		CompoundNBT DETAILS = new CompoundNBT();
//		DETAILS.putBoolean("decorative", true);
//		DETAILS.putBoolean("sealed", false);
//		decrotive.setTag(DETAILS);
//		items.add(decrotive);
	}
	public static VoxelShape SHAPE = Block.makeCuboidShape(1, 0, 1, 15, 16, 15);
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}

	@Override
	BlockState postBlockPlace(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {return state;}

}
