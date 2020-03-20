package hybolic.meadery.common.blocks;

import hybolic.meadery.common.tile.FermentationStationTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class DemijohnLargeBlock extends DemijohnBlock {

	public DemijohnLargeBlock(String id) {
		super(id);
	}

	@Override
	public TileEntity createTileEntity_(BlockState state, IBlockReader world) {
		if(hasTileEntity_(state))
			return new FermentationStationTileEntity(8);
		return null;
	}


}
