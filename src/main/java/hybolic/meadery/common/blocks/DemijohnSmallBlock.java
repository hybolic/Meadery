package hybolic.meadery.common.blocks;

import hybolic.meadery.common.tile.FermentationStationTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class DemijohnSmallBlock extends DemijohnBlock {
	
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty DECORATIVE = BooleanProperty.create("decorative");

	public DemijohnSmallBlock(String id) {
		super(id);
	}
	@Override
	public TileEntity createTileEntity_(BlockState state, IBlockReader world) {
		if(hasTileEntity_(state))
			return new FermentationStationTileEntity(4);
		return null;
	}

}
